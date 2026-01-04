package de.caritas.cob.consultingtypeservice.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeCoreDTO;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ConsultingTypeGroupResponseDTO
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-25T23:22:19.381612101Z[Etc/UTC]")
public class ConsultingTypeGroupResponseDTO {

  @JsonProperty("title")
  private String title;

  @JsonProperty("consultingTypes")
  @Valid
  private List<ConsultingTypeCoreDTO> consultingTypes = null;

  public ConsultingTypeGroupResponseDTO title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
  */
  
  @Schema(name = "title", example = "Lorem ipsum", required = false)
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public ConsultingTypeGroupResponseDTO consultingTypes(List<ConsultingTypeCoreDTO> consultingTypes) {
    this.consultingTypes = consultingTypes;
    return this;
  }

  public ConsultingTypeGroupResponseDTO addConsultingTypesItem(ConsultingTypeCoreDTO consultingTypesItem) {
    if (this.consultingTypes == null) {
      this.consultingTypes = new ArrayList<>();
    }
    this.consultingTypes.add(consultingTypesItem);
    return this;
  }

  /**
   * Get consultingTypes
   * @return consultingTypes
  */
  @Valid 
  @Schema(name = "consultingTypes", required = false)
  public List<ConsultingTypeCoreDTO> getConsultingTypes() {
    return consultingTypes;
  }

  public void setConsultingTypes(List<ConsultingTypeCoreDTO> consultingTypes) {
    this.consultingTypes = consultingTypes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ConsultingTypeGroupResponseDTO consultingTypeGroupResponseDTO = (ConsultingTypeGroupResponseDTO) o;
    return Objects.equals(this.title, consultingTypeGroupResponseDTO.title) &&
        Objects.equals(this.consultingTypes, consultingTypeGroupResponseDTO.consultingTypes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, consultingTypes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConsultingTypeGroupResponseDTO {\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    consultingTypes: ").append(toIndentedString(consultingTypes)).append("\n");
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

