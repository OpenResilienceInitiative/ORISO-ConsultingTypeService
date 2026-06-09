package de.caritas.cob.consultingtypeservice.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.caritas.cob.consultingtypeservice.api.model.ApplicationPermissionSettingsDTO;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ApplicationPermissionSettingsConverter {

  private static final Map<String, Boolean> DEFAULT_APPLICATION_PERMISSION_SETTINGS =
      createDefaultMap();

  private final ObjectMapper objectMapper;

  public ApplicationPermissionSettingsConverter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public Map<String, Boolean> createDefaultSettings() {
    return new LinkedHashMap<>(DEFAULT_APPLICATION_PERMISSION_SETTINGS);
  }

  public void applyPatch(Map<String, Boolean> target, ApplicationPermissionSettingsDTO patch) {
    if (patch == null) {
      return;
    }
    Map<String, Boolean> patchValues =
        objectMapper.convertValue(patch, new TypeReference<Map<String, Boolean>>() {});
    patchValues.forEach(
        (key, value) -> {
          if (value != null) {
            target.put(key, value);
          }
        });
  }

  public ApplicationPermissionSettingsDTO toDto(Map<String, Boolean> settings) {
    Map<String, Boolean> source =
        settings == null || settings.isEmpty() ? DEFAULT_APPLICATION_PERMISSION_SETTINGS : settings;
    return objectMapper.convertValue(source, ApplicationPermissionSettingsDTO.class);
  }

  private static Map<String, Boolean> createDefaultMap() {
    Map<String, Boolean> defaults = new LinkedHashMap<>();
    defaults.put("featureAnonymousChatEnabled", false);
    defaults.put("featureGroupChatV2Enabled", false);
    defaults.put("featureCallsEnabled", false);
    defaults.put("featureSupervisionEnabled", false);
    defaults.put("featureSupervisionAnonymousChatsEnabled", false);
    defaults.put("featureSupervisionOneOnOneChatsEnabled", false);
    defaults.put("featureAudioCallsEnabled", false);
    defaults.put("featureAudioCallsAnonymousChatsEnabled", false);
    defaults.put("featureAudioCallsOneOnOneChatsEnabled", false);
    defaults.put("featureAudioCallsGroupChatsEnabled", false);
    defaults.put("featureAudioCallsSupervisionChatsEnabled", false);
    defaults.put("featureVideoCallsEnabled", false);
    defaults.put("featureVideoCallsAnonymousChatsEnabled", false);
    defaults.put("featureVideoCallsOneOnOneChatsEnabled", false);
    defaults.put("featureVideoCallsGroupChatsEnabled", false);
    defaults.put("featureVideoCallsSupervisionChatsEnabled", false);
    defaults.put("featureThreadsEnabled", false);
    defaults.put("featureThreadsAnonymousChatsEnabled", false);
    defaults.put("featureThreadsGroupChatsEnabled", false);
    defaults.put("featureThreadsOneOnOneEnabled", false);
    defaults.put("featureThreadsSupervisionChatsEnabled", false);
    defaults.put("featureVoiceMessagesEnabled", false);
    defaults.put("featureVoiceMessagesAnonymousChatsEnabled", false);
    defaults.put("featureVoiceMessagesOneOnOneChatsEnabled", false);
    defaults.put("featureVoiceMessagesGroupChatsEnabled", false);
    defaults.put("featureVoiceMessagesSupervisionChatsEnabled", false);
    return defaults;
  }
}
