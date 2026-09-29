package de.caritas.cob.consultingtypeservice.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsEntity;
import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsPatchDTO;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalFeatureSystemNotificationEmailsEnabled;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpEmailThemeColor;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpEnabled;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpFrom;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpHost;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpPassword;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpPort;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpSecure;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpUsername;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ApplicationSettingsServiceFacadeTest {

  @Mock ApplicationSettingsService applicationSettingsService;
  @Mock ApplicationSettingsConverter applicationSettingsConverter;
  @Mock SmtpPasswordEncryptionService smtpPasswordEncryptionService;

  @InjectMocks ApplicationSettingsServiceFacade applicationSettingsServiceFacade;

  @Test
  void patchApplicationSettings_Should_EncryptSmtpPasswordBeforeSaving() {
    // given
    var entity = new ApplicationSettingsEntity();
    entity.setGlobalSmtpPassword(new GlobalSmtpPassword().withValue("").withReadOnly(false));
    when(applicationSettingsService.getApplicationSettings()).thenReturn(Optional.of(entity));
    when(smtpPasswordEncryptionService.encrypt("plain-pass")).thenReturn("ENC:cipher");

    var patchDTO = new ApplicationSettingsPatchDTO();
    patchDTO.setGlobalSmtpPassword("plain-pass");

    // when
    applicationSettingsServiceFacade.patchApplicationSettings(patchDTO);

    // then
    verify(smtpPasswordEncryptionService).encrypt("plain-pass");
    ArgumentCaptor<ApplicationSettingsEntity> captor =
        ArgumentCaptor.forClass(ApplicationSettingsEntity.class);
    verify(applicationSettingsService).saveApplicationSettings(captor.capture());
    assertThat(captor.getValue().getGlobalSmtpPassword().getValue()).isEqualTo("ENC:cipher");
  }

  @Test
  void patchApplicationSettings_Should_MigrateLegacyPlaintextOnNextAdminUpdate() {
    var entity = new ApplicationSettingsEntity();
    entity.setGlobalSmtpPassword(
        new GlobalSmtpPassword().withValue("legacy-pass").withReadOnly(false));
    when(applicationSettingsService.getApplicationSettings()).thenReturn(Optional.of(entity));
    when(smtpPasswordEncryptionService.encrypt("legacy-pass")).thenReturn("ENC:migrated");

    var patchDTO = new ApplicationSettingsPatchDTO();
    patchDTO.setGlobalSmtpHost("smtp.example.net");
    patchDTO.setGlobalSmtpPassword("");

    applicationSettingsServiceFacade.patchApplicationSettings(patchDTO);

    assertThat(entity.getGlobalSmtpPassword().getValue()).isEqualTo("ENC:migrated");
    verify(applicationSettingsService).saveApplicationSettings(entity);
  }

  @Test
  void patchApplicationSettings_Should_NotEncrypt_When_SmtpPasswordNotPatched() {
    // given
    var entity = new ApplicationSettingsEntity();
    when(applicationSettingsService.getApplicationSettings()).thenReturn(Optional.of(entity));

    var patchDTO = new ApplicationSettingsPatchDTO();
    patchDTO.setGlobalSmtpHost("smtp.example.com");

    // when
    applicationSettingsServiceFacade.patchApplicationSettings(patchDTO);

    // then
    verify(applicationSettingsService)
        .saveApplicationSettings(any(ApplicationSettingsEntity.class));
    verify(smtpPasswordEncryptionService, never()).encrypt(any());
  }

  @Test
  void getGlobalSmtpCredentials_Should_DecryptPasswordAndReturnUsername() {
    // given
    var entity = new ApplicationSettingsEntity();
    entity.setGlobalSmtpUsername(
        new GlobalSmtpUsername().withValue("smtp-user").withReadOnly(false));
    entity.setGlobalSmtpPassword(
        new GlobalSmtpPassword().withValue("ENC:stored").withReadOnly(false));
    entity.setGlobalSmtpHost(
        new GlobalSmtpHost().withValue("smtp.example.net").withReadOnly(false));
    entity.setGlobalSmtpPort(new GlobalSmtpPort().withValue("587").withReadOnly(false));
    entity.setGlobalSmtpSecure(new GlobalSmtpSecure().withValue(false).withReadOnly(false));
    entity.setGlobalSmtpFrom(
        new GlobalSmtpFrom().withValue("sender@example.net").withReadOnly(false));
    entity.setGlobalSmtpEnabled(new GlobalSmtpEnabled().withValue(true).withReadOnly(false));
    entity.setGlobalFeatureSystemNotificationEmailsEnabled(
        new GlobalFeatureSystemNotificationEmailsEnabled().withValue(true).withReadOnly(false));
    entity.setGlobalSmtpEmailThemeColor(
        new GlobalSmtpEmailThemeColor().withValue("#123456").withReadOnly(false));
    when(applicationSettingsService.getApplicationSettings()).thenReturn(Optional.of(entity));
    when(smtpPasswordEncryptionService.decrypt("ENC:stored")).thenReturn("plain-pass");

    // when
    var credentials = applicationSettingsServiceFacade.getGlobalSmtpCredentials();

    // then
    assertThat(credentials).isPresent();
    assertThat(credentials.get().getGlobalSmtpUsername()).isEqualTo("smtp-user");
    assertThat(credentials.get().getGlobalSmtpPassword()).isEqualTo("plain-pass");
    assertThat(credentials.get().getGlobalSmtpHost()).isEqualTo("smtp.example.net");
    assertThat(credentials.get().getGlobalSmtpPort()).isEqualTo("587");
    assertThat(credentials.get().getGlobalSmtpSecure()).isFalse();
    assertThat(credentials.get().getGlobalSmtpFrom()).isEqualTo("sender@example.net");
    assertThat(credentials.get().getGlobalSmtpEnabled()).isTrue();
    assertThat(credentials.get().getGlobalFeatureSystemNotificationEmailsEnabled()).isTrue();
    assertThat(credentials.get().getGlobalSmtpEmailThemeColor()).isEqualTo("#123456");
    verify(smtpPasswordEncryptionService).decrypt("ENC:stored");
  }

  @Test
  void patchApplicationSettings_Should_PreserveStoredUsername_When_PatchedUsernameIsBlank() {
    // given
    var entity = new ApplicationSettingsEntity();
    entity.setGlobalSmtpUsername(
        new GlobalSmtpUsername().withValue("stored-user").withReadOnly(false));
    when(applicationSettingsService.getApplicationSettings()).thenReturn(Optional.of(entity));

    var patchDTO = new ApplicationSettingsPatchDTO();
    patchDTO.setGlobalSmtpUsername("");

    // when
    applicationSettingsServiceFacade.patchApplicationSettings(patchDTO);

    // then
    ArgumentCaptor<ApplicationSettingsEntity> captor =
        ArgumentCaptor.forClass(ApplicationSettingsEntity.class);
    verify(applicationSettingsService).saveApplicationSettings(captor.capture());
    assertThat(captor.getValue().getGlobalSmtpUsername().getValue()).isEqualTo("stored-user");
  }

  @Test
  void patchApplicationSettings_Should_PreserveStoredPassword_When_PatchedPasswordIsBlank() {
    // given
    var entity = new ApplicationSettingsEntity();
    entity.setGlobalSmtpPassword(
        new GlobalSmtpPassword().withValue("ENC:stored").withReadOnly(false));
    when(applicationSettingsService.getApplicationSettings()).thenReturn(Optional.of(entity));

    var patchDTO = new ApplicationSettingsPatchDTO();
    patchDTO.setGlobalSmtpPassword("");

    // when
    applicationSettingsServiceFacade.patchApplicationSettings(patchDTO);

    // then
    ArgumentCaptor<ApplicationSettingsEntity> captor =
        ArgumentCaptor.forClass(ApplicationSettingsEntity.class);
    verify(applicationSettingsService).saveApplicationSettings(captor.capture());
    assertThat(captor.getValue().getGlobalSmtpPassword().getValue()).isEqualTo("ENC:stored");
    verify(smtpPasswordEncryptionService, never()).encrypt(any());
  }

  @Test
  void
      patchApplicationSettings_Should_PreserveStoredCredentials_When_PatchedValuesAreWhitespaceOnly() {
    // given
    var entity = new ApplicationSettingsEntity();
    entity.setGlobalSmtpUsername(
        new GlobalSmtpUsername().withValue("stored-user").withReadOnly(false));
    entity.setGlobalSmtpPassword(
        new GlobalSmtpPassword().withValue("ENC:stored").withReadOnly(false));
    when(applicationSettingsService.getApplicationSettings()).thenReturn(Optional.of(entity));

    var patchDTO = new ApplicationSettingsPatchDTO();
    patchDTO.setGlobalSmtpUsername("   ");
    patchDTO.setGlobalSmtpPassword("  \t ");

    // when
    applicationSettingsServiceFacade.patchApplicationSettings(patchDTO);

    // then
    ArgumentCaptor<ApplicationSettingsEntity> captor =
        ArgumentCaptor.forClass(ApplicationSettingsEntity.class);
    verify(applicationSettingsService).saveApplicationSettings(captor.capture());
    assertThat(captor.getValue().getGlobalSmtpUsername().getValue()).isEqualTo("stored-user");
    assertThat(captor.getValue().getGlobalSmtpPassword().getValue()).isEqualTo("ENC:stored");
    verify(smtpPasswordEncryptionService, never()).encrypt(any());
  }

  @Test
  void patchApplicationSettings_Should_PreserveStoredCredentials_When_PatchedValuesAreMasked() {
    // given
    var entity = new ApplicationSettingsEntity();
    entity.setGlobalSmtpUsername(
        new GlobalSmtpUsername().withValue("stored-user").withReadOnly(false));
    entity.setGlobalSmtpPassword(
        new GlobalSmtpPassword().withValue("ENC:stored").withReadOnly(false));
    when(applicationSettingsService.getApplicationSettings()).thenReturn(Optional.of(entity));

    var patchDTO = new ApplicationSettingsPatchDTO();
    patchDTO.setGlobalSmtpUsername("***");
    patchDTO.setGlobalSmtpPassword("\u2022\u2022\u2022\u2022\u2022");

    // when
    applicationSettingsServiceFacade.patchApplicationSettings(patchDTO);

    // then
    ArgumentCaptor<ApplicationSettingsEntity> captor =
        ArgumentCaptor.forClass(ApplicationSettingsEntity.class);
    verify(applicationSettingsService).saveApplicationSettings(captor.capture());
    assertThat(captor.getValue().getGlobalSmtpUsername().getValue()).isEqualTo("stored-user");
    assertThat(captor.getValue().getGlobalSmtpPassword().getValue()).isEqualTo("ENC:stored");
    verify(smtpPasswordEncryptionService, never()).encrypt(any());
  }

  @Test
  void patchApplicationSettings_Should_StoreNewUsername_When_PatchedUsernameIsRealValue() {
    // given
    var entity = new ApplicationSettingsEntity();
    entity.setGlobalSmtpUsername(
        new GlobalSmtpUsername().withValue("old-user").withReadOnly(false));
    when(applicationSettingsService.getApplicationSettings()).thenReturn(Optional.of(entity));

    var patchDTO = new ApplicationSettingsPatchDTO();
    patchDTO.setGlobalSmtpUsername("new-user");

    // when
    applicationSettingsServiceFacade.patchApplicationSettings(patchDTO);

    // then
    ArgumentCaptor<ApplicationSettingsEntity> captor =
        ArgumentCaptor.forClass(ApplicationSettingsEntity.class);
    verify(applicationSettingsService).saveApplicationSettings(captor.capture());
    assertThat(captor.getValue().getGlobalSmtpUsername().getValue()).isEqualTo("new-user");
  }

  @Test
  void patchApplicationSettings_Should_ApplyOtherFields_When_CredentialsInSamePatchAreBlank() {
    // given
    var entity = new ApplicationSettingsEntity();
    entity.setGlobalSmtpHost(new GlobalSmtpHost().withValue("old-host").withReadOnly(false));
    entity.setGlobalSmtpUsername(
        new GlobalSmtpUsername().withValue("stored-user").withReadOnly(false));
    entity.setGlobalSmtpPassword(
        new GlobalSmtpPassword().withValue("ENC:stored").withReadOnly(false));
    when(applicationSettingsService.getApplicationSettings()).thenReturn(Optional.of(entity));

    var patchDTO = new ApplicationSettingsPatchDTO();
    patchDTO.setGlobalSmtpHost("smtp.new-host.example");
    patchDTO.setGlobalSmtpPort("2525");
    patchDTO.setGlobalSmtpUsername("");
    patchDTO.setGlobalSmtpPassword("");

    // when
    applicationSettingsServiceFacade.patchApplicationSettings(patchDTO);

    // then
    ArgumentCaptor<ApplicationSettingsEntity> captor =
        ArgumentCaptor.forClass(ApplicationSettingsEntity.class);
    verify(applicationSettingsService).saveApplicationSettings(captor.capture());
    assertThat(captor.getValue().getGlobalSmtpHost().getValue()).isEqualTo("smtp.new-host.example");
    assertThat(captor.getValue().getGlobalSmtpPort().getValue()).isEqualTo("2525");
    assertThat(captor.getValue().getGlobalSmtpUsername().getValue()).isEqualTo("stored-user");
    assertThat(captor.getValue().getGlobalSmtpPassword().getValue()).isEqualTo("ENC:stored");
    verify(smtpPasswordEncryptionService, never()).encrypt(any());
  }
}
