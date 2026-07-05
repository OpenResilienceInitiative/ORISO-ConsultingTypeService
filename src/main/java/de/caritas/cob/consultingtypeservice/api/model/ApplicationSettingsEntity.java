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
