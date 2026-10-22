package de.caritas.cob.consultingtypeservice.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.caritas.cob.consultingtypeservice.schemas.model.ConsultingType;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "consulting_types")
public class ConsultingTypeEntity extends ConsultingType {
  private String bootstrapAttempt;
  private String bootstrapPayloadDigest;
  private Boolean bootstrapCompensated;

  @JsonIgnore
  public Boolean getBootstrapCompensated() { return bootstrapCompensated; }

  public void setBootstrapCompensated(Boolean terminal) { this.bootstrapCompensated = terminal; }


  @JsonIgnore
  public String getBootstrapAttempt() {
    return bootstrapAttempt;
  }

  public void setBootstrapAttempt(String attempt) {
    this.bootstrapAttempt = attempt;
  }

  @JsonIgnore
  public String getBootstrapPayloadDigest() {
    return bootstrapPayloadDigest;
  }

  public void setBootstrapPayloadDigest(String digest) {
    this.bootstrapPayloadDigest = digest;
  }
}
