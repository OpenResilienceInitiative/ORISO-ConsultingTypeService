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
 * BasicConsultingTypeResponseDTOFurtherInformation
 */

@JsonTypeName("BasicConsultingTypeResponseDTO_furtherInformation")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-25T23:22:19.939702671Z[Etc/UTC]")
public class BasicConsultingTypeResponseDTOFurtherInformation {

  @JsonProperty("label")
  private String label;

  @JsonProperty("url")
  private String url;

  public BasicConsultingTypeResponseDTOFurtherInformation label(String label) {
    this.label = label;
    return this;
  }

  /**
   * Get label
   * @return label
  */
  
  @Schema(name = "label", example = "Lorem ipsum", required = false)
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public BasicConsultingTypeResponseDTOFurtherInformation url(String url) {
    this.url = url;
    return this;
  }

  /**
   * Get url
   * @return url
  */
  
  @Schema(name = "url", example = "https://www.domain.com", required = false)
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BasicConsultingTypeResponseDTOFurtherInformation basicConsultingTypeResponseDTOFurtherInformation = (BasicConsultingTypeResponseDTOFurtherInformation) o;
    return Objects.equals(this.label, basicConsultingTypeResponseDTOFurtherInformation.label) &&
        Objects.equals(this.url, basicConsultingTypeResponseDTOFurtherInformation.url);
  }

  @Override
  public int hashCode() {
    return Objects.hash(label, url);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BasicConsultingTypeResponseDTOFurtherInformation {\n");
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    url: ").append(toIndentedString(url)).append("\n");
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

