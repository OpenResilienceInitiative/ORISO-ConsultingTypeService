package de.caritas.cob.consultingtypeservice.api.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import de.caritas.cob.consultingtypeservice.schemas.model.ApplicationSettings;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "application_settings")
public class ApplicationSettingsEntity extends ApplicationSettings {

  @Id private String id;

  // Internal persistence metadata; never part of the public settings DTO.
  private Long settingsVersion;
  private long smtpRevision;
  private Long smtpPendingRevision;
  private Long smtpAppliedRevision;
  private String smtpSyncStatus;
  private int smtpSyncAttempts;
  private java.time.Instant smtpNextAttemptAt;

  @JsonIgnore
  public Long getSettingsVersion() {
    return settingsVersion;
  }

  public void setSettingsVersion(Long value) {
    settingsVersion = value;
  }

  @JsonIgnore
  public long getSmtpRevision() {
    return smtpRevision;
  }

  public void setSmtpRevision(long value) {
    smtpRevision = value;
  }

  @JsonIgnore
  public Long getSmtpPendingRevision() {
    return smtpPendingRevision;
  }

  public void setSmtpPendingRevision(Long value) {
    smtpPendingRevision = value;
  }

  @JsonIgnore
  public Long getSmtpAppliedRevision() {
    return smtpAppliedRevision;
  }

  public void setSmtpAppliedRevision(Long value) {
    smtpAppliedRevision = value;
  }

  @JsonIgnore
  public String getSmtpSyncStatus() {
    return smtpSyncStatus;
  }

  public void setSmtpSyncStatus(String value) {
    smtpSyncStatus = value;
  }

  @JsonIgnore
  public int getSmtpSyncAttempts() {
    return smtpSyncAttempts;
  }

  public void setSmtpSyncAttempts(int value) {
    smtpSyncAttempts = value;
  }

  @JsonIgnore
  public java.time.Instant getSmtpNextAttemptAt() {
    return smtpNextAttemptAt;
  }

  public void setSmtpNextAttemptAt(java.time.Instant value) {
    smtpNextAttemptAt = value;
  }

  Map<String, Object> releaseToggles = new LinkedHashMap<>();

  @JsonIgnore
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @JsonAnySetter
  public void setReleaseToggles(String key, Object value) {
    releaseToggles.put(key, value);
  }

  public Map<String, Object> getReleaseToggles() {
    return releaseToggles;
  }
}
