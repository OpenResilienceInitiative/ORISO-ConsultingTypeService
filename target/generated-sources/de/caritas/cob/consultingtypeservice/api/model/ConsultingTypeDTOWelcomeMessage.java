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
 * ConsultingTypeDTOWelcomeMessage
 */

@JsonTypeName("ConsultingTypeDTO_welcomeMessage")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-25T23:22:19.381612101Z[Etc/UTC]")
public class ConsultingTypeDTOWelcomeMessage {

  @JsonProperty("sendWelcomeMessage")
  private Boolean sendWelcomeMessage;

  @JsonProperty("welcomeMessageText")
  private String welcomeMessageText;

  public ConsultingTypeDTOWelcomeMessage sendWelcomeMessage(Boolean sendWelcomeMessage) {
    this.sendWelcomeMessage = sendWelcomeMessage;
    return this;
  }

  /**
   * Get sendWelcomeMessage
   * @return sendWelcomeMessage
  */
  
  @Schema(name = "sendWelcomeMessage", example = "true", required = false)
  public Boolean getSendWelcomeMessage() {
    return sendWelcomeMessage;
  }

  public void setSendWelcomeMessage(Boolean sendWelcomeMessage) {
    this.sendWelcomeMessage = sendWelcomeMessage;
  }

  public ConsultingTypeDTOWelcomeMessage welcomeMessageText(String welcomeMessageText) {
    this.welcomeMessageText = welcomeMessageText;
    return this;
  }

  /**
   * Get welcomeMessageText
   * @return welcomeMessageText
  */
  
  @Schema(name = "welcomeMessageText", example = "Lorem ipsum", required = false)
  public String getWelcomeMessageText() {
    return welcomeMessageText;
  }

  public void setWelcomeMessageText(String welcomeMessageText) {
    this.welcomeMessageText = welcomeMessageText;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ConsultingTypeDTOWelcomeMessage consultingTypeDTOWelcomeMessage = (ConsultingTypeDTOWelcomeMessage) o;
    return Objects.equals(this.sendWelcomeMessage, consultingTypeDTOWelcomeMessage.sendWelcomeMessage) &&
        Objects.equals(this.welcomeMessageText, consultingTypeDTOWelcomeMessage.welcomeMessageText);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sendWelcomeMessage, welcomeMessageText);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConsultingTypeDTOWelcomeMessage {\n");
    sb.append("    sendWelcomeMessage: ").append(toIndentedString(sendWelcomeMessage)).append("\n");
    sb.append("    welcomeMessageText: ").append(toIndentedString(welcomeMessageText)).append("\n");
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

