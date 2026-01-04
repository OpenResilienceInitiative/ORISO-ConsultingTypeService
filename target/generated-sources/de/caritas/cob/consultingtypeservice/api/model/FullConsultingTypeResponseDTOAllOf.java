package de.caritas.cob.consultingtypeservice.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import de.caritas.cob.consultingtypeservice.api.model.WelcomeScreenDTO;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * FullConsultingTypeResponseDTOAllOf
 */

@JsonTypeName("FullConsultingTypeResponseDTO_allOf")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-25T23:22:19.381612101Z[Etc/UTC]")
public class FullConsultingTypeResponseDTOAllOf {

  @JsonProperty("requiredComponents")
  private Object requiredComponents;

  @JsonProperty("welcomeScreen")
  private WelcomeScreenDTO welcomeScreen;

  public FullConsultingTypeResponseDTOAllOf requiredComponents(Object requiredComponents) {
    this.requiredComponents = requiredComponents;
    return this;
  }

  /**
   * Get requiredComponents
   * @return requiredComponents
  */
  
  @Schema(name = "requiredComponents", required = false)
  public Object getRequiredComponents() {
    return requiredComponents;
  }

  public void setRequiredComponents(Object requiredComponents) {
    this.requiredComponents = requiredComponents;
  }

  public FullConsultingTypeResponseDTOAllOf welcomeScreen(WelcomeScreenDTO welcomeScreen) {
    this.welcomeScreen = welcomeScreen;
    return this;
  }

  /**
   * Get welcomeScreen
   * @return welcomeScreen
  */
  @Valid 
  @Schema(name = "welcomeScreen", required = false)
  public WelcomeScreenDTO getWelcomeScreen() {
    return welcomeScreen;
  }

  public void setWelcomeScreen(WelcomeScreenDTO welcomeScreen) {
    this.welcomeScreen = welcomeScreen;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FullConsultingTypeResponseDTOAllOf fullConsultingTypeResponseDTOAllOf = (FullConsultingTypeResponseDTOAllOf) o;
    return Objects.equals(this.requiredComponents, fullConsultingTypeResponseDTOAllOf.requiredComponents) &&
        Objects.equals(this.welcomeScreen, fullConsultingTypeResponseDTOAllOf.welcomeScreen);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requiredComponents, welcomeScreen);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FullConsultingTypeResponseDTOAllOf {\n");
    sb.append("    requiredComponents: ").append(toIndentedString(requiredComponents)).append("\n");
    sb.append("    welcomeScreen: ").append(toIndentedString(welcomeScreen)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

