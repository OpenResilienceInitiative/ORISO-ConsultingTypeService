package de.caritas.cob.consultingtypeservice.api.consultingtypes;

import de.caritas.cob.consultingtypeservice.api.exception.UnexpectedErrorException;
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

  private Schema buildSchema() {
    var consultingTypeJsonSchema =
        new JSONObject(
            new JSONTokener(
                Objects.requireNonNull(
                    ConsultingTypeValidator.class.getResourceAsStream(
                        consultingTypeJsonSchemaFile))));
    return SchemaLoader.builder()
        .useDefaults(true)
        .schemaJson(consultingTypeJsonSchema)
        .draftV7Support()
        .build()
        .load()
        .build();
  }
}
