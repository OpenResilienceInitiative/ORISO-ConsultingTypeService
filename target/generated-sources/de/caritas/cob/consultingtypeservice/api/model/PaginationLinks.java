package de.caritas.cob.consultingtypeservice.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import de.caritas.cob.consultingtypeservice.api.model.HalLink;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PaginationLinks
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-25T23:22:19.939702671Z[Etc/UTC]")
public class PaginationLinks {

  @JsonProperty("self")
  private HalLink self;

  @JsonProperty("next")
  private HalLink next;

  @JsonProperty("previous")
  private HalLink previous;

  public PaginationLinks self(HalLink self) {
    this.self = self;
    return this;
  }

  /**
   * Get self
   * @return self
  */
  @NotNull @Valid 
  @Schema(name = "self", required = true)
  public HalLink getSelf() {
    return self;
  }

  public void setSelf(HalLink self) {
    this.self = self;
  }

  public PaginationLinks next(HalLink next) {
    this.next = next;
    return this;
  }

  /**
   * Get next
   * @return next
  */
  @Valid 
  @Schema(name = "next", required = false)
  public HalLink getNext() {
    return next;
  }

  public void setNext(HalLink next) {
    this.next = next;
  }

  public PaginationLinks previous(HalLink previous) {
    this.previous = previous;
    return this;
  }

  /**
   * Get previous
   * @return previous
  */
  @Valid 
  @Schema(name = "previous", required = false)
  public HalLink getPrevious() {
    return previous;
  }

  public void setPrevious(HalLink previous) {
    this.previous = previous;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaginationLinks paginationLinks = (PaginationLinks) o;
    return Objects.equals(this.self, paginationLinks.self) &&
        Objects.equals(this.next, paginationLinks.next) &&
        Objects.equals(this.previous, paginationLinks.previous);
  }

  @Override
  public int hashCode() {
    return Objects.hash(self, next, previous);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaginationLinks {\n");
    sb.append("    self: ").append(toIndentedString(self)).append("\n");
    sb.append("    next: ").append(toIndentedString(next)).append("\n");
    sb.append("    previous: ").append(toIndentedString(previous)).append("\n");
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

