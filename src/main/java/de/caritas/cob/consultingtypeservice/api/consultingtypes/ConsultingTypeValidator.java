package de.caritas.cob.consultingtypeservice.api.consultingtypes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.caritas.cob.consultingtypeservice.api.exception.UnexpectedErrorException;
import de.caritas.cob.consultingtypeservice.api.exception.httpresponses.BadRequestException;
import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeDTO;
import de.caritas.cob.consultingtypeservice.api.model.WelcomeMessageDTO;
import de.caritas.cob.consultingtypeservice.api.service.LogService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Validator for consulting type settings files against the json schema for consulting type
 * settings.
 */
@Service
public class ConsultingTypeValidator {

  @Value("${consulting.types.json.schema.file}")
  private String consultingTypeJsonSchemaFile;

  /**
   * Validate a consulting type settings file.
   *
   * @param consultingTypeJsonFile the {@link File} to be validated
   */
  public void validateConsultingTypeConfigurationJsonFile(File consultingTypeJsonFile) {
    try (var inputStream = new FileInputStream(consultingTypeJsonFile)) {
      var consultingTypeJson = new JSONObject(new JSONTokener(inputStream));
      buildSchema().validate(consultingTypeJson);
    } catch (ValidationException validationException) {
      LogService.logError(
          validationException,
          "Consulting type configuration file violates the json schema: "
              + consultingTypeJsonFile.getName());
      throw new UnexpectedErrorException();
    } catch (IOException | org.json.JSONException jsonException) {
      LogService.logError(
          jsonException,
          "Could not parse consulting type configuration file: "
              + consultingTypeJsonFile.getName());
      throw new UnexpectedErrorException();
    }
  }

  /** Validate the actual command before conversion, allocation or any Mongo operation. */
  public void validateBootstrapCommand(ConsultingTypeDTO command) {
    try {
      String json =
          new ObjectMapper()
              .setSerializationInclusion(JsonInclude.Include.NON_NULL)
              .addMixIn(WelcomeMessageDTO.class, WelcomeMessageValidation.class)
              .writeValueAsString(command);
      buildSchema(true).validate(new JSONObject(json));
    } catch (ValidationException | JsonProcessingException | org.json.JSONException invalid) {
      throw new BadRequestException(
          "Tenant bootstrap configuration violates the consulting type schema");
    }
  }

  private abstract static class WelcomeMessageValidation {
    // This required field accepts null in the persistence schema.
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public abstract String getWelcomeMessageText();
  }

  private Schema buildSchema() {
    return buildSchema(false);
  }

  private Schema buildSchema(boolean bootstrapCommand) {
    var consultingTypeJsonSchema =
        new JSONObject(
            new JSONTokener(
                Objects.requireNonNull(
                    ConsultingTypeValidator.class.getResourceAsStream(
                        consultingTypeJsonSchemaFile))));
    if (bootstrapCommand) {
      // The receiver allocates id; every other existing persistence requirement stays exact.
      var storedRequired = consultingTypeJsonSchema.getJSONArray("required");
      var commandRequired = new org.json.JSONArray();
      for (int index = 0; index < storedRequired.length(); index++) {
        String name = storedRequired.getString(index);
        if (!name.equals("id")) commandRequired.put(name);
      }
      consultingTypeJsonSchema.put("required", commandRequired);
    }
    return SchemaLoader.builder()
        .useDefaults(true)
        .schemaJson(consultingTypeJsonSchema)
        .draftV7Support()
        .build()
        .load()
        .build();
  }
}
