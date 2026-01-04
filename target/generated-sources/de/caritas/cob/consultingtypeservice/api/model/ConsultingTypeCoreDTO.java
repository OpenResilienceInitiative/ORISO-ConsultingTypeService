package de.caritas.cob.consultingtypeservice.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeCoreDTOTitles;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ConsultingTypeCoreDTO
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-25T23:22:19.381612101Z[Etc/UTC]")
public class ConsultingTypeCoreDTO {

  @JsonProperty("id")
  private Integer id;

  @JsonProperty("titles")
  private ConsultingTypeCoreDTOTitles titles;

  public ConsultingTypeCoreDTO id(Integer id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  
  @Schema(name = "id", example = "12345", required = false)
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public ConsultingTypeCoreDTO titles(ConsultingTypeCoreDTOTitles titles) {
    this.titles = titles;
    return this;
  }

  /**
   * Get titles
   * @return titles
  */
  @Valid 
  @Schema(name = "titles", required = false)
  public ConsultingTypeCoreDTOTitles getTitles() {
    return titles;
  }

  public void setTitles(ConsultingTypeCoreDTOTitles titles) {
    this.titles = titles;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ConsultingTypeCoreDTO consultingTypeCoreDTO = (ConsultingTypeCoreDTO) o;
    return Objects.equals(this.id, consultingTypeCoreDTO.id) &&
        Objects.equals(this.titles, consultingTypeCoreDTO.titles);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, titles);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConsultingTypeCoreDTO {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    titles: ").append(toIndentedString(titles)).append("\n");
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

