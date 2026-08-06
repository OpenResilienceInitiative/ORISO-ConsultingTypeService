package de.caritas.cob.consultingtypeservice.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsEntity;
import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsPatchDTO;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpPassword;
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
    when(applicationSettingsService.getApplicationSettings()).thenReturn(Optional.of(entity));
    when(smtpPasswordEncryptionService.decrypt("ENC:stored")).thenReturn("plain-pass");

    // when
    var credentials = applicationSettingsServiceFacade.getGlobalSmtpCredentials();

    // then
    assertThat(credentials).isPresent();
    assertThat(credentials.get().getGlobalSmtpUsername()).isEqualTo("smtp-user");
    assertThat(credentials.get().getGlobalSmtpPassword()).isEqualTo("plain-pass");
    verify(smtpPasswordEncryptionService).decrypt("ENC:stored");
  }
}
