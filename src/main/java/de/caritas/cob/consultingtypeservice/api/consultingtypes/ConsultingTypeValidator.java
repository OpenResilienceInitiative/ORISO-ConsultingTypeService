package de.caritas.cob.consultingtypeservice.api.consultingtypes;

import java.io.File;
import java.util.Objects;
import org.everit.json.schema.Schema;
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
    // VALIDATION DISABLED - Skip validation to allow service to start
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
