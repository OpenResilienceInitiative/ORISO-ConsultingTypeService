package de.caritas.cob.consultingtypeservice.api.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import de.caritas.cob.consultingtypeservice.api.exception.httpresponses.ConflictException;
import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsEntity;
import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsPatchDTO;
import de.caritas.cob.consultingtypeservice.schemas.model.*;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;

class ApplicationSettingsSmtpSynchronizationTest {
  ApplicationSettingsService settings;
  ApplicationSettingsConverter converter;
  SmtpSynchronizationService sync;
  SmtpPasswordEncryptionService crypto;
  ApplicationSettingsServiceFacade facade;
  ApplicationSettingsEntity entity;

  @BeforeEach
  void setup() {
    settings = mock(ApplicationSettingsService.class);
    converter = mock(ApplicationSettingsConverter.class);
    sync = mock(SmtpSynchronizationService.class);
    crypto = new SmtpPasswordEncryptionService("test-only-key");
    facade = new ApplicationSettingsServiceFacade(settings, converter, crypto, sync);
    entity = new ApplicationSettingsEntity();
    entity.setId("settings-id");
    entity.setGlobalSmtpHost(new GlobalSmtpHost().withValue("old-host"));
    entity.setGlobalSmtpPassword(new GlobalSmtpPassword().withValue(crypto.encrypt("secret")));
    entity.setGlobalSmtpEmailThemeColor(new GlobalSmtpEmailThemeColor().withValue("old-colour"));
    when(settings.getApplicationSettings()).thenReturn(Optional.of(entity));
    when(settings.compareAndSave(any(), isNull())).thenReturn(true);
  }

  @Test
  void effectiveChangeIsPersistedBeforeDispatch() {
    var patch = new ApplicationSettingsPatchDTO().globalSmtpHost("new-host");
    var saved = facade.patchApplicationSettings(patch).orElseThrow();
    assertThat(entity.getSmtpRevision()).isEqualTo(1);
    assertThat(entity.getSmtpPendingRevision()).isEqualTo(1L);
    assertThat(saved.getSynchronization().getStatus()).isEqualTo("SMTP_SYNC_PENDING");
    InOrder ordered = inOrder(settings, sync);
    ordered.verify(settings).getApplicationSettings();
    ordered.verify(settings).compareAndSave(entity, null);
    ordered.verify(sync).synchronizeAfterSave("settings-id", 1);
    assertThat(entity.getGlobalSmtpPassword().getValue())
        .startsWith("ENC:")
        .doesNotContain("secret");
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" ", "***", "••••", "secret"})
  void omittedMaskedOrSamePasswordDoesNotTrigger(String password) {
    var patch =
        new ApplicationSettingsPatchDTO().globalSmtpHost("old-host").globalSmtpPassword(password);
    facade.patchApplicationSettings(patch);
    assertThat(crypto.decrypt(entity.getGlobalSmtpPassword().getValue())).isEqualTo("secret");
    assertThat(entity.getSmtpRevision()).isZero();
    verifyNoInteractions(sync);
  }

  @Test
  void legacyCipherMigrationAndThemeOnlySaveAreNotSmtpChanges() {
    entity.getGlobalSmtpPassword().setValue("secret");
    facade.patchApplicationSettings(
        new ApplicationSettingsPatchDTO().globalSmtpEmailThemeColor("new-colour"));
    assertThat(entity.getGlobalSmtpPassword().getValue()).startsWith("ENC:");
    assertThat(entity.getSmtpRevision()).isZero();
    verifyNoInteractions(sync);
  }

  @Test
  void failedCasNeverDispatchesAndRetriesFreshDocumentThreeTimes() {
    when(settings.compareAndSave(any(), isNull())).thenReturn(false);
    assertThatThrownBy(
            () ->
                facade.patchApplicationSettings(
                    new ApplicationSettingsPatchDTO().globalSmtpHost("new-host")))
        .isInstanceOf(ConflictException.class);
    verify(settings, times(3)).getApplicationSettings();
    verify(settings, times(3)).compareAndSave(any(), isNull());
    verifyNoInteractions(sync);
  }

  @Test
  void failedPersistenceNeverDispatches() {
    when(settings.compareAndSave(any(), isNull()))
        .thenThrow(new IllegalStateException("DB failed"));
    assertThatThrownBy(
            () ->
                facade.patchApplicationSettings(
                    new ApplicationSettingsPatchDTO().globalSmtpHost("new-host")))
        .isInstanceOf(IllegalStateException.class);
    verifyNoInteractions(sync);
  }

  @Test
  void successfulSaveAndPendingProofRemainWhenStatusReadbackFails() {
    when(settings.getApplicationSettings())
        .thenReturn(Optional.of(entity))
        .thenThrow(new IllegalStateException("unavailable"));
    var saved =
        facade
            .patchApplicationSettings(new ApplicationSettingsPatchDTO().globalSmtpHost("new-host"))
            .orElseThrow();
    assertThat(saved.getSynchronization().getStatus()).isEqualTo("SMTP_SYNC_PENDING");
    assertThat(saved.getSynchronization().getRevision()).isEqualTo(1);
  }
}
