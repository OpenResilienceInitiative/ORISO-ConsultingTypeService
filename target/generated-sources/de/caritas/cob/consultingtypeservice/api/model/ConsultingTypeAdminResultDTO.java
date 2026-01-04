package de.caritas.cob.consultingtypeservice.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import de.caritas.cob.consultingtypeservice.api.model.ExtendedConsultingTypeResponseDTO;
import de.caritas.cob.consultingtypeservice.api.model.PaginationLinks;
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
 * ConsultingTypeAdminResultDTO
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-25T23:22:19.939702671Z[Etc/UTC]")
public class ConsultingTypeAdminResultDTO {

  @JsonProperty("_embedded")
  @Valid
  private List<ExtendedConsultingTypeResponseDTO> embedded = null;

  @JsonProperty("_links")
  private PaginationLinks links;

  @JsonProperty("total")
  private Integer total;

  public ConsultingTypeAdminResultDTO embedded(List<ExtendedConsultingTypeResponseDTO> embedded) {
    this.embedded = embedded;
    return this;
  }

  public ConsultingTypeAdminResultDTO addEmbeddedItem(ExtendedConsultingTypeResponseDTO embeddedItem) {
    if (this.embedded == null) {
      this.embedded = new ArrayList<>();
    }
    this.embedded.add(embeddedItem);
    return this;
  }

  /**
   * Get embedded
   * @return embedded
  */
  @Valid 
  @Schema(name = "_embedded", required = false)
  public List<ExtendedConsultingTypeResponseDTO> getEmbedded() {
    return embedded;
  }

  public void setEmbedded(List<ExtendedConsultingTypeResponseDTO> embedded) {
    this.embedded = embedded;
  }

  public ConsultingTypeAdminResultDTO links(PaginationLinks links) {
    this.links = links;
    return this;
  }

  /**
   * Get links
   * @return links
  */
  @Valid 
  @Schema(name = "_links", required = false)
  public PaginationLinks getLinks() {
    return links;
  }

  public void setLinks(PaginationLinks links) {
    this.links = links;
  }

  public ConsultingTypeAdminResultDTO total(Integer total) {
    this.total = total;
    return this;
  }

  /**
   * Get total
   * @return total
  */
  
  @Schema(name = "total", required = false)
  public Integer getTotal() {
    return total;
  }

  public void setTotal(Integer total) {
    this.total = total;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ConsultingTypeAdminResultDTO consultingTypeAdminResultDTO = (ConsultingTypeAdminResultDTO) o;
    return Objects.equals(this.embedded, consultingTypeAdminResultDTO.embedded) &&
        Objects.equals(this.links, consultingTypeAdminResultDTO.links) &&
        Objects.equals(this.total, consultingTypeAdminResultDTO.total);
  }

  @Override
  public int hashCode() {
    return Objects.hash(embedded, links, total);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConsultingTypeAdminResultDTO {\n");
    sb.append("    embedded: ").append(toIndentedString(embedded)).append("\n");
    sb.append("    links: ").append(toIndentedString(links)).append("\n");
    sb.append("    total: ").append(toIndentedString(total)).append("\n");
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

