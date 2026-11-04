package de.caritas.cob.consultingtypeservice.api.consultingtypes;

import static de.caritas.cob.consultingtypeservice.testHelper.TestConstants.FILE_BROKEN_CONSULTING_TYPE;
import static de.caritas.cob.consultingtypeservice.testHelper.TestConstants.FILE_CONSULTING_TYPE_JSON_SCHEMA;
import static de.caritas.cob.consultingtypeservice.testHelper.TestConstants.FILE_INVALID_CONSULTING_TYPE;
import static de.caritas.cob.consultingtypeservice.testHelper.TestConstants.FILE_NULL_VALUE_CONSULTING_TYPE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.caritas.cob.consultingtypeservice.api.exception.UnexpectedErrorException;
import de.caritas.cob.consultingtypeservice.api.exception.httpresponses.BadRequestException;
import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeDTO;
import de.caritas.cob.consultingtypeservice.api.model.NotificationsDTO;
import de.caritas.cob.consultingtypeservice.api.model.WelcomeMessageDTO;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.assertj.core.api.Fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

@ExtendWith(MockitoExtension.class)
class ConsultingTypeValidatorTest {

  private static final String FIELD_NAME_CONSULTING_TYPE_JSON_SCHEMA_FILE =
      "consultingTypeJsonSchemaFile";

  private ConsultingTypeValidator consultingTypeValidator;

  @Mock private Logger logger;

  @BeforeEach
  void setup() {
    consultingTypeValidator = new ConsultingTypeValidator();
    setField(
        consultingTypeValidator,
        FIELD_NAME_CONSULTING_TYPE_JSON_SCHEMA_FILE,
        FILE_CONSULTING_TYPE_JSON_SCHEMA);
  }

  @Test
  void
      validateConsultingTypeConfigurationJsonFile_Should_ThrowUnexpectedErrorException_WhenJsonViolatesSchema() {

    File invalidConsultingSettingsFile =
        new File(
            Objects.requireNonNull(
                    ConsultingTypeValidatorTest.class.getResource(FILE_INVALID_CONSULTING_TYPE))
                .getFile());

    assertThrows(
        UnexpectedErrorException.class,
        () ->
            consultingTypeValidator.validateConsultingTypeConfigurationJsonFile(
                invalidConsultingSettingsFile));
  }

  @Test
  void
      validateConsultingTypeConfigurationJsonFile_Should_ThrowUnexpectedErrorException_WhenJsonFileCouldNotBeParsed() {

    File invalidConsultingSettingsFile =
        new File(
            Objects.requireNonNull(
                    ConsultingTypeValidatorTest.class.getResource(FILE_BROKEN_CONSULTING_TYPE))
                .getFile());

    assertThrows(
        UnexpectedErrorException.class,
        () ->
            consultingTypeValidator.validateConsultingTypeConfigurationJsonFile(
                invalidConsultingSettingsFile));
  }

  @Test
  void validateConsultingTypeConfigurationJsonFile_ShouldNot_ThrowException_For_NullValues() {

    File invalidConsultingSettingsFile =
        new File(
            Objects.requireNonNull(
                    ConsultingTypeValidatorTest.class.getResource(FILE_NULL_VALUE_CONSULTING_TYPE))
                .getFile());

    try {
      consultingTypeValidator.validateConsultingTypeConfigurationJsonFile(
          invalidConsultingSettingsFile);
    } catch (Exception e) {
      Fail.fail("No exception should be thrown");
    }
  }

  @Test
  void validateBootstrapCommandAcceptsDisabledWelcomeWithRequiredNullText() throws Exception {
    var command = bootstrapCommand();
    command.setWelcomeMessage(
        new WelcomeMessageDTO().sendWelcomeMessage(false).welcomeMessageText(null));

    assertDoesNotThrow(() -> consultingTypeValidator.validateBootstrapCommand(command));
  }

  @ParameterizedTest
  @ValueSource(strings = {"slug", "roles", "registration", "urls"})
  void validateBootstrapCommandStillRejectsMissingRequiredFields(String missing) throws Exception {
    var command = bootstrapCommand();
    if (missing.equals("slug")) command.setSlug(null);
    else if (missing.equals("roles")) command.setRoles(null);
    else if (missing.equals("registration")) command.setRegistration(null);
    else command.setUrls(null);

    assertThrows(
        BadRequestException.class, () -> consultingTypeValidator.validateBootstrapCommand(command));
  }

  @Test
  void validateBootstrapCommandStillChecksPresentOptionalObjects() throws Exception {
    var command = bootstrapCommand();
    command.setNotifications(new NotificationsDTO());

    assertThrows(
        BadRequestException.class, () -> consultingTypeValidator.validateBootstrapCommand(command));
  }

  @ParameterizedTest
  @ValueSource(strings = {"missing", "invalid-type"})
  void persistenceSchemaStillRequiresNullableWelcomeTextWithItsDeclaredType(
      String invalid, @TempDir Path directory) throws Exception {
    var source =
        new org.json.JSONObject(
            new org.json.JSONTokener(
                Objects.requireNonNull(
                    getClass().getResourceAsStream(FILE_NULL_VALUE_CONSULTING_TYPE))));
    var welcome = new org.json.JSONObject().put("sendWelcomeMessage", false);
    if (invalid.equals("invalid-type")) welcome.put("welcomeMessageText", 42);
    source.put("welcomeMessage", welcome);
    var file = directory.resolve("invalid-welcome.json");
    Files.writeString(file, source.toString());

    assertThrows(
        UnexpectedErrorException.class,
        () -> consultingTypeValidator.validateConsultingTypeConfigurationJsonFile(file.toFile()));
  }

  private ConsultingTypeDTO bootstrapCommand() throws Exception {
    try (var source =
        getClass().getResourceAsStream("/bootstrap-v2-complete-consulting-type.json")) {
      return new ObjectMapper().readValue(source, ConsultingTypeDTO.class);
    }
  }
}
