package de.caritas.cob.consultingtypeservice.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import de.caritas.cob.consultingtypeservice.api.model.TitlesMultilingualDTO;
import java.util.HashMap;
import java.util.Map;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * TopicMultilingualDTO
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-25T23:22:20.114194580Z[Etc/UTC]")
public class TopicMultilingualDTO {

  @JsonProperty("id")
  private Long id = null;

  @JsonProperty("name")
  @Valid
  private Map<String, String> name = new HashMap<>();

  @JsonProperty("slug")
  private String slug;

  @JsonProperty("description")
  @Valid
  private Map<String, String> description = new HashMap<>();

  @JsonProperty("internalIdentifier")
  private String internalIdentifier;

  @JsonProperty("status")
  private String status;

  @JsonProperty("createDate")
  private String createDate;

  @JsonProperty("updateDate")
  private String updateDate;

  @JsonProperty("fallbackAgencyId")
  private Integer fallbackAgencyId;

  @JsonProperty("fallbackUrl")
  private String fallbackUrl;

  @JsonProperty("welcomeMessage")
  private String welcomeMessage;

  @JsonProperty("titles")
  private TitlesMultilingualDTO titles;

  public TopicMultilingualDTO id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  
  @Schema(name = "id", example = "12132", required = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public TopicMultilingualDTO name(Map<String, String> name) {
    this.name = name;
    return this;
  }

  public TopicMultilingualDTO putNameItem(String key, String nameItem) {
    this.name.put(key, nameItem);
    return this;
  }

  /**
   * Get name
   * @return name
  */
  @NotNull 
  @Schema(name = "name", required = true)
  public Map<String, String> getName() {
    return name;
  }

  public void setName(Map<String, String> name) {
    this.name = name;
  }

  public TopicMultilingualDTO slug(String slug) {
    this.slug = slug;
    return this;
  }

  /**
   * Get slug
   * @return slug
  */
  @Size(max = 100) 
  @Schema(name = "slug", example = "topic_slug", required = false)
  public String getSlug() {
    return slug;
  }

  public void setSlug(String slug) {
    this.slug = slug;
  }

  public TopicMultilingualDTO description(Map<String, String> description) {
    this.description = description;
    return this;
  }

  public TopicMultilingualDTO putDescriptionItem(String key, String descriptionItem) {
    this.description.put(key, descriptionItem);
    return this;
  }

  /**
   * Get description
   * @return description
  */
  @NotNull 
  @Schema(name = "description", required = true)
  public Map<String, String> getDescription() {
    return description;
  }

  public void setDescription(Map<String, String> description) {
    this.description = description;
  }

  public TopicMultilingualDTO internalIdentifier(String internalIdentifier) {
    this.internalIdentifier = internalIdentifier;
    return this;
  }

  /**
   * Get internalIdentifier
   * @return internalIdentifier
  */
  @Size(max = 50) 
  @Schema(name = "internalIdentifier", example = "identifier for data exports for example: alcohol", required = false)
  public String getInternalIdentifier() {
    return internalIdentifier;
  }

  public void setInternalIdentifier(String internalIdentifier) {
    this.internalIdentifier = internalIdentifier;
  }

  public TopicMultilingualDTO status(String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
  */
  @Size(max = 10) 
  @Schema(name = "status", example = "Active", required = false)
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public TopicMultilingualDTO createDate(String createDate) {
    this.createDate = createDate;
    return this;
  }

  /**
   * Get createDate
   * @return createDate
  */
  
  @Schema(name = "createDate", required = false)
  public String getCreateDate() {
    return createDate;
  }

  public void setCreateDate(String createDate) {
    this.createDate = createDate;
  }

  public TopicMultilingualDTO updateDate(String updateDate) {
    this.updateDate = updateDate;
    return this;
  }

  /**
   * Get updateDate
   * @return updateDate
  */
  
  @Schema(name = "updateDate", required = false)
  public String getUpdateDate() {
    return updateDate;
  }

  public void setUpdateDate(String updateDate) {
    this.updateDate = updateDate;
  }

  public TopicMultilingualDTO fallbackAgencyId(Integer fallbackAgencyId) {
    this.fallbackAgencyId = fallbackAgencyId;
    return this;
  }

  /**
   * Get fallbackAgencyId
   * @return fallbackAgencyId
  */
  
  @Schema(name = "fallbackAgencyId", required = false)
  public Integer getFallbackAgencyId() {
    return fallbackAgencyId;
  }

  public void setFallbackAgencyId(Integer fallbackAgencyId) {
    this.fallbackAgencyId = fallbackAgencyId;
  }

  public TopicMultilingualDTO fallbackUrl(String fallbackUrl) {
    this.fallbackUrl = fallbackUrl;
    return this;
  }

  /**
   * Get fallbackUrl
   * @return fallbackUrl
  */
  @Size(max = 200) 
  @Schema(name = "fallbackUrl", required = false)
  public String getFallbackUrl() {
    return fallbackUrl;
  }

  public void setFallbackUrl(String fallbackUrl) {
    this.fallbackUrl = fallbackUrl;
  }

  public TopicMultilingualDTO welcomeMessage(String welcomeMessage) {
    this.welcomeMessage = welcomeMessage;
    return this;
  }

  /**
   * Get welcomeMessage
   * @return welcomeMessage
  */
  @Size(max = 200) 
  @Schema(name = "welcomeMessage", required = false)
  public String getWelcomeMessage() {
    return welcomeMessage;
  }

  public void setWelcomeMessage(String welcomeMessage) {
    this.welcomeMessage = welcomeMessage;
  }

  public TopicMultilingualDTO titles(TitlesMultilingualDTO titles) {
    this.titles = titles;
    return this;
  }

  /**
   * Get titles
   * @return titles
  */
  @Valid 
  @Schema(name = "titles", required = false)
  public TitlesMultilingualDTO getTitles() {
    return titles;
  }

  public void setTitles(TitlesMultilingualDTO titles) {
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
    TopicMultilingualDTO topicMultilingualDTO = (TopicMultilingualDTO) o;
    return Objects.equals(this.id, topicMultilingualDTO.id) &&
        Objects.equals(this.name, topicMultilingualDTO.name) &&
        Objects.equals(this.slug, topicMultilingualDTO.slug) &&
        Objects.equals(this.description, topicMultilingualDTO.description) &&
        Objects.equals(this.internalIdentifier, topicMultilingualDTO.internalIdentifier) &&
        Objects.equals(this.status, topicMultilingualDTO.status) &&
        Objects.equals(this.createDate, topicMultilingualDTO.createDate) &&
        Objects.equals(this.updateDate, topicMultilingualDTO.updateDate) &&
        Objects.equals(this.fallbackAgencyId, topicMultilingualDTO.fallbackAgencyId) &&
        Objects.equals(this.fallbackUrl, topicMultilingualDTO.fallbackUrl) &&
        Objects.equals(this.welcomeMessage, topicMultilingualDTO.welcomeMessage) &&
        Objects.equals(this.titles, topicMultilingualDTO.titles);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, slug, description, internalIdentifier, status, createDate, updateDate, fallbackAgencyId, fallbackUrl, welcomeMessage, titles);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TopicMultilingualDTO {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    slug: ").append(toIndentedString(slug)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    internalIdentifier: ").append(toIndentedString(internalIdentifier)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    createDate: ").append(toIndentedString(createDate)).append("\n");
    sb.append("    updateDate: ").append(toIndentedString(updateDate)).append("\n");
    sb.append("    fallbackAgencyId: ").append(toIndentedString(fallbackAgencyId)).append("\n");
    sb.append("    fallbackUrl: ").append(toIndentedString(fallbackUrl)).append("\n");
    sb.append("    welcomeMessage: ").append(toIndentedString(welcomeMessage)).append("\n");
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

