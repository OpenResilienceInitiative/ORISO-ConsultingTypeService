package de.caritas.cob.consultingtypeservice.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.caritas.cob.consultingtypeservice.api.model.ApplicationPermissionSettingsDTO;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ApplicationPermissionSettingsConverterTest {

  private final ApplicationPermissionSettingsConverter converter =
      new ApplicationPermissionSettingsConverter(new ObjectMapper());

  @Test
  void applyPatch_Should_MergeOnlyProvidedValues() {
    Map<String, Boolean> settings = converter.createDefaultSettings();

    converter.applyPatch(
        settings, new ApplicationPermissionSettingsDTO().featureAnonymousChatEnabled(true));

    assertThat(settings.get("featureAnonymousChatEnabled")).isTrue();
    assertThat(settings.get("featureGroupChatV2Enabled")).isFalse();
  }

  @Test
  void toDto_Should_ReturnDefaultsWhenSettingsAreEmpty() {
    ApplicationPermissionSettingsDTO dto = converter.toDto(new LinkedHashMap<>());

    assertThat(dto.getFeatureAnonymousChatEnabled()).isFalse();
    assertThat(dto.getFeatureVoiceMessagesSupervisionChatsEnabled()).isFalse();
  }
}
