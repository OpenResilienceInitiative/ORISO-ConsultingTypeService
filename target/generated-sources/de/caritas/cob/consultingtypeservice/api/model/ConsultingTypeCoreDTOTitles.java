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
 * ConsultingTypeCoreDTOTitles
 */

@JsonTypeName("ConsultingTypeCoreDTO_titles")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-25T23:22:19.381612101Z[Etc/UTC]")
public class ConsultingTypeCoreDTOTitles {

  @JsonProperty("default")
  private String _default;

  @JsonProperty("long")
  private String _long;

  public ConsultingTypeCoreDTOTitles _default(String _default) {
    this._default = _default;
    return this;
  }

  /**
   * Get _default
   * @return _default
  */
  
  @Schema(name = "default", example = "Lorem ipsum", required = false)
  public String getDefault() {
    return _default;
  }

  public void setDefault(String _default) {
    this._default = _default;
  }

  public ConsultingTypeCoreDTOTitles _long(String _long) {
    this._long = _long;
    return this;
  }

  /**
   * Get _long
   * @return _long
  */
  
  @Schema(name = "long", example = "Lorem ipsum", required = false)
  public String getLong() {
    return _long;
  }

  public void setLong(String _long) {
    this._long = _long;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ConsultingTypeCoreDTOTitles consultingTypeCoreDTOTitles = (ConsultingTypeCoreDTOTitles) o;
    return Objects.equals(this._default, consultingTypeCoreDTOTitles._default) &&
        Objects.equals(this._long, consultingTypeCoreDTOTitles._long);
  }

  @Override
  public int hashCode() {
    return Objects.hash(_default, _long);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConsultingTypeCoreDTOTitles {\n");
    sb.append("    _default: ").append(toIndentedString(_default)).append("\n");
    sb.append("    _long: ").append(toIndentedString(_long)).append("\n");
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

