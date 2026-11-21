package de.caritas.cob.consultingtypeservice.api.consultingtypes;

import static de.caritas.cob.consultingtypeservice.testHelper.TestConstants.BROKEN_FILE_PATH;
import static de.caritas.cob.consultingtypeservice.testHelper.TestConstants.SRC_TEST_RESOURCES_BROKEN_CONSULTING_TYPE_SETTINGS;
import static de.caritas.cob.consultingtypeservice.testHelper.TestConstants.SRC_TEST_RESOURCES_CONSULTING_TYPE_SETTINGS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.caritas.cob.consultingtypeservice.api.exception.UnexpectedErrorException;
import de.caritas.cob.consultingtypeservice.api.tenant.TenantContext;
import de.caritas.cob.consultingtypeservice.schemas.model.ConsultingType;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

@ExtendWith(MockitoExtension.class)
class ConsultingTypeLoaderTest {

  private static final String INIT_METHOD_NAME = "init";
  private static final String CONSULTING_TYPES_FILE_PATH_NAME = "consultingTypesFilePath";

  @InjectMocks ConsultingTypeLoader consultingTypeLoader;

  @Mock ConsultingTypeRepositoryService consultingTypeRepositoryService;

  @Mock ConsultingTypeGroupRepository consultingTypeGroupRepository;
  @Mock ConsultingTypeValidator consultingTypeValidator;
  @Mock private Logger logger;

  @BeforeEach
  void setup() {
    ReflectionTestUtils.setField(
        consultingTypeLoader, "consultingTypeRepositoryService", consultingTypeRepositoryService);
  }

  @Test
  void test_Should_Fail_WhenMethodInitDoesNotHavePostConstructAnnotation()
      throws NoSuchMethodException, SecurityException {

    PostConstruct annotation =
        getInitMethodFromConsultingTypeLoader().getAnnotation(PostConstruct.class);

    assertNotNull(annotation);
  }

  @Test
  void init_Should_ThrowUnexpectedErrorException_WhenInvalidConsultingTypeSettingsPath()
      throws NoSuchMethodException {

    setConsultingTypesFilePath(BROKEN_FILE_PATH);
    Method initMethod = getInitMethodFromConsultingTypeLoader();
    assertThrows(
        UnexpectedErrorException.class,
        () -> ReflectionUtils.invokeMethod(initMethod, consultingTypeLoader));
  }

  @Test
  void init_Should_ThrowUnexpectedErrorException_WhenBrokenConsultingTypeSettings()
      throws NoSuchMethodException {

    setConsultingTypesFilePath(SRC_TEST_RESOURCES_BROKEN_CONSULTING_TYPE_SETTINGS);
    Method initMethod = getInitMethodFromConsultingTypeLoader();
    assertThrows(
        UnexpectedErrorException.class,
        () -> ReflectionUtils.invokeMethod(initMethod, consultingTypeLoader));
  }

  @AfterEach
  void clearTenant() {
    TenantContext.clear();
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(longs = 17L)
  void init_Should_AddConsultingTypesToRepository(Long previousTenant)
      throws NoSuchMethodException {
    setConsultingTypesFilePath(SRC_TEST_RESOURCES_CONSULTING_TYPE_SETTINGS);
    TenantContext.setCurrentTenant(previousTenant);
    Mockito.doAnswer(
            invocation -> {
              assertEquals(0L, TenantContext.getCurrentTenant());
              return null;
            })
        .when(consultingTypeRepositoryService)
        .addConsultingType(Mockito.any(ConsultingType.class));
    ReflectionUtils.invokeMethod(getInitMethodFromConsultingTypeLoader(), consultingTypeLoader);
    verify(consultingTypeRepositoryService, times(5))
        .addConsultingType(Mockito.any(ConsultingType.class));
    assertEquals(previousTenant, TenantContext.getCurrentTenant());
  }

  @Test
  void failedRepositoryWriteRestoresThePreviousTenant() throws NoSuchMethodException {
    setConsultingTypesFilePath(SRC_TEST_RESOURCES_CONSULTING_TYPE_SETTINGS);
    TenantContext.setCurrentTenant(17L);
    Mockito.doAnswer(
            invocation -> {
              assertEquals(0L, TenantContext.getCurrentTenant());
              throw new IllegalStateException("synthetic repository failure");
            })
        .when(consultingTypeRepositoryService)
        .addConsultingType(Mockito.any(ConsultingType.class));
    Method initMethod = getInitMethodFromConsultingTypeLoader();

    assertThrows(
        IllegalStateException.class,
        () -> ReflectionUtils.invokeMethod(initMethod, consultingTypeLoader));
    assertEquals(17L, TenantContext.getCurrentTenant());
  }

  @Test
  void init_Should_ValidateConsultingTypeSettings() throws NoSuchMethodException {

    setConsultingTypesFilePath(SRC_TEST_RESOURCES_CONSULTING_TYPE_SETTINGS);
    ReflectionUtils.invokeMethod(getInitMethodFromConsultingTypeLoader(), consultingTypeLoader);
    verify(consultingTypeValidator, times(5))
        .validateConsultingTypeConfigurationJsonFile(Mockito.any(File.class));
  }

  private Method getInitMethodFromConsultingTypeLoader() throws NoSuchMethodException {
    Class<? extends ConsultingTypeLoader> classToTest = consultingTypeLoader.getClass();
    Method methodToTest = classToTest.getDeclaredMethod(INIT_METHOD_NAME);
    methodToTest.setAccessible(true);
    return methodToTest;
  }

  private void setConsultingTypesFilePath(String consultingTypesFilePath) {
    Field fieldConsultingTypesFilePath =
        ReflectionUtils.findField(
            ConsultingTypeLoader.class, CONSULTING_TYPES_FILE_PATH_NAME, String.class);
    assert fieldConsultingTypesFilePath != null;
    fieldConsultingTypesFilePath.setAccessible(true);
    ReflectionUtils.setField(
        fieldConsultingTypesFilePath, consultingTypeLoader, consultingTypesFilePath);
  }
}
