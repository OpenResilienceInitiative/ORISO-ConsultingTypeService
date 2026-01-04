package de.caritas.cob.consultingtypeservice.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
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
 * TopicGroupsDTODataItemsInner
 */

@JsonTypeName("TopicGroupsDTO_data_items_inner")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-25T23:22:20.114194580Z[Etc/UTC]")
public class TopicGroupsDTODataItemsInner {

  @JsonProperty("id")
  private Integer id;

  @JsonProperty("name")
  private String name;

  @JsonProperty("topicIds")
  @Valid
  private List<Integer> topicIds = new ArrayList<>();

  public TopicGroupsDTODataItemsInner id(Integer id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  @NotNull 
  @Schema(name = "id", example = "12132", required = true)
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public TopicGroupsDTODataItemsInner name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
  */
  @NotNull @Size(max = 100) 
  @Schema(name = "name", example = "Topic group name", required = true)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public TopicGroupsDTODataItemsInner topicIds(List<Integer> topicIds) {
    this.topicIds = topicIds;
    return this;
  }

  public TopicGroupsDTODataItemsInner addTopicIdsItem(Integer topicIdsItem) {
    this.topicIds.add(topicIdsItem);
    return this;
  }

  /**
   * Get topicIds
   * @return topicIds
  */
  @NotNull 
  @Schema(name = "topicIds", required = true)
  public List<Integer> getTopicIds() {
    return topicIds;
  }

  public void setTopicIds(List<Integer> topicIds) {
    this.topicIds = topicIds;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TopicGroupsDTODataItemsInner topicGroupsDTODataItemsInner = (TopicGroupsDTODataItemsInner) o;
    return Objects.equals(this.id, topicGroupsDTODataItemsInner.id) &&
        Objects.equals(this.name, topicGroupsDTODataItemsInner.name) &&
        Objects.equals(this.topicIds, topicGroupsDTODataItemsInner.topicIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, topicIds);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TopicGroupsDTODataItemsInner {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    topicIds: ").append(toIndentedString(topicIds)).append("\n");
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

