package de.caritas.cob.consultingtypeservice.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.caritas.cob.consultingtypeservice.api.auth.AuthorisationService;
import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsEntity;
import de.caritas.cob.consultingtypeservice.api.service.ApplicationSettingsConverter;
import de.caritas.cob.consultingtypeservice.api.service.ApplicationSettingsService;
import de.caritas.cob.consultingtypeservice.api.service.ApplicationSettingsServiceFacade;
import de.caritas.cob.consultingtypeservice.api.service.SmtpPasswordEncryptionService;
import de.caritas.cob.consultingtypeservice.api.service.SmtpSynchronizationService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ApplicationSettingsSmtpRevisionHeaderTest {
  @Mock ApplicationSettingsService settings;
  @Mock ApplicationSettingsConverter converter;
  @Mock SmtpPasswordEncryptionService encryption;
  @Mock SmtpSynchronizationService synchronization;
  @Mock AuthorisationService authorisation;
  @InjectMocks ApplicationSettingsServiceFacade facade;

  @Test
  void legacySnapshotCarriesRevisionZeroWithoutChangingCredentialJson() {
    when(settings.getApplicationSettings())
        .thenReturn(Optional.of(new ApplicationSettingsEntity()));
    var response =
        new ApplicationSettingsController(facade, authorisation).getGlobalSmtpCredentials();
    assertThat(response.getHeaders().getFirst("X-Smtp-Revision")).isEqualTo("0");
    assertThat(response.getHeaders().getCacheControl()).isEqualTo("no-store");
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void headerAndCredentialBodyComeFromOneSavedSnapshot() {
    var entity = new ApplicationSettingsEntity();
    entity.setSmtpRevision(42);
    when(settings.getApplicationSettings()).thenReturn(Optional.of(entity));
    var response =
        new ApplicationSettingsController(facade, authorisation).getGlobalSmtpCredentials();
    assertThat(response.getHeaders().getFirst("X-Smtp-Revision")).isEqualTo("42");
    org.mockito.Mockito.verify(settings).getApplicationSettings();
  }

  @Test
  void noSettingsHasNoContentAndRevisionZero() {
    when(settings.getApplicationSettings()).thenReturn(Optional.empty());
    var response =
        new ApplicationSettingsController(facade, authorisation).getGlobalSmtpCredentials();
    assertThat(response.getStatusCode().value()).isEqualTo(204);
    assertThat(response.getHeaders().getFirst("X-Smtp-Revision")).isEqualTo("0");
    assertThat(response.getHeaders().getCacheControl()).isEqualTo("no-store");
  }
}
