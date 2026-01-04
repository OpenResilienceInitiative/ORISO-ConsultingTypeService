package de.caritas.cob.consultingtypeservice.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * BasicConsultingTypeResponseDTOUrls
 */

@JsonTypeName("BasicConsultingTypeResponseDTO_urls")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-25T23:22:19.939702671Z[Etc/UTC]")
public class BasicConsultingTypeResponseDTOUrls {

  @JsonProperty("requiredAidMissingRedirectUrl")
  private String requiredAidMissingRedirectUrl;

  @JsonProperty("registrationPostcodeFallbackUrl")
  private String registrationPostcodeFallbackUrl;

  public BasicConsultingTypeResponseDTOUrls requiredAidMissingRedirectUrl(String requiredAidMissingRedirectUrl) {
    this.requiredAidMissingRedirectUrl = requiredAidMissingRedirectUrl;
    return this;
  }

  /**
   * Get requiredAidMissingRedirectUrl
   * @return requiredAidMissingRedirectUrl
  */
  
  @Schema(name = "requiredAidMissingRedirectUrl", example = "https://www.domain.tld/path", required = false)
  public String getRequiredAidMissingRedirectUrl() {
    return requiredAidMissingRedirectUrl;
  }

  public void setRequiredAidMissingRedirectUrl(String requiredAidMissingRedirectUrl) {
    this.requiredAidMissingRedirectUrl = requiredAidMissingRedirectUrl;
  }

  public BasicConsultingTypeResponseDTOUrls registrationPostcodeFallbackUrl(String registrationPostcodeFallbackUrl) {
    this.registrationPostcodeFallbackUrl = registrationPostcodeFallbackUrl;
    return this;
  }

  /**
   * Get registrationPostcodeFallbackUrl
   * @return registrationPostcodeFallbackUrl
  */
  
  @Schema(name = "registrationPostcodeFallbackUrl", example = "https://www.domain.tld/path", required = false)
  public String getRegistrationPostcodeFallbackUrl() {
    return registrationPostcodeFallbackUrl;
  }

  public void setRegistrationPostcodeFallbackUrl(String registrationPostcodeFallbackUrl) {
    this.registrationPostcodeFallbackUrl = registrationPostcodeFallbackUrl;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BasicConsultingTypeResponseDTOUrls basicConsultingTypeResponseDTOUrls = (BasicConsultingTypeResponseDTOUrls) o;
    return Objects.equals(this.requiredAidMissingRedirectUrl, basicConsultingTypeResponseDTOUrls.requiredAidMissingRedirectUrl) &&
        Objects.equals(this.registrationPostcodeFallbackUrl, basicConsultingTypeResponseDTOUrls.registrationPostcodeFallbackUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requiredAidMissingRedirectUrl, registrationPostcodeFallbackUrl);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BasicConsultingTypeResponseDTOUrls {\n");
    sb.append("    requiredAidMissingRedirectUrl: ").append(toIndentedString(requiredAidMissingRedirectUrl)).append("\n");
    sb.append("    registrationPostcodeFallbackUrl: ").append(toIndentedString(registrationPostcodeFallbackUrl)).append("\n");
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

