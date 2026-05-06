
package de.caritas.cob.consultingtypeservice.schemas.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Settings for the welcome message
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "sendWelcomeMessage",
    "welcomeMessageText"
})
public class WelcomeMessage {

    /**
     * Indicates whether if the system should send an automatic reply message to all enquiries for this consulting type or not
     * (Required)
     * 
     */
    @JsonProperty("sendWelcomeMessage")
    @JsonPropertyDescription("Indicates whether if the system should send an automatic reply message to all enquiries for this consulting type or not")
    private Boolean sendWelcomeMessage = false;
    /**
     * The welcome message text for this consulting type -> null, if sendWelcomeMessage is false
     * (Required)
     * 
     */
    @JsonProperty("welcomeMessageText")
    @JsonPropertyDescription("The welcome message text for this consulting type -> null, if sendWelcomeMessage is false")
    private String welcomeMessageText;

    /**
     * No args constructor for use in serialization
     * 
     */
    public WelcomeMessage() {
    }

    /**
     * 
     * @param welcomeMessageText
     * @param sendWelcomeMessage
     */
    public WelcomeMessage(Boolean sendWelcomeMessage, String welcomeMessageText) {
        super();
        this.sendWelcomeMessage = sendWelcomeMessage;
        this.welcomeMessageText = welcomeMessageText;
    }

    /**
     * Indicates whether if the system should send an automatic reply message to all enquiries for this consulting type or not
     * (Required)
     * 
     */
    @JsonProperty("sendWelcomeMessage")
    public Boolean getSendWelcomeMessage() {
        return sendWelcomeMessage;
    }

    /**
     * Indicates whether if the system should send an automatic reply message to all enquiries for this consulting type or not
     * (Required)
     * 
     */
    @JsonProperty("sendWelcomeMessage")
    public void setSendWelcomeMessage(Boolean sendWelcomeMessage) {
        this.sendWelcomeMessage = sendWelcomeMessage;
    }

    public WelcomeMessage withSendWelcomeMessage(Boolean sendWelcomeMessage) {
        this.sendWelcomeMessage = sendWelcomeMessage;
        return this;
    }

    /**
     * The welcome message text for this consulting type -> null, if sendWelcomeMessage is false
     * (Required)
     * 
     */
    @JsonProperty("welcomeMessageText")
    public String getWelcomeMessageText() {
        return welcomeMessageText;
    }

    /**
     * The welcome message text for this consulting type -> null, if sendWelcomeMessage is false
     * (Required)
     * 
     */
    @JsonProperty("welcomeMessageText")
    public void setWelcomeMessageText(String welcomeMessageText) {
        this.welcomeMessageText = welcomeMessageText;
    }

    public WelcomeMessage withWelcomeMessageText(String welcomeMessageText) {
        this.welcomeMessageText = welcomeMessageText;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(sendWelcomeMessage).append(welcomeMessageText).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof WelcomeMessage) == false) {
            return false;
        }
        WelcomeMessage rhs = ((WelcomeMessage) other);
        return new EqualsBuilder().append(sendWelcomeMessage, rhs.sendWelcomeMessage).append(welcomeMessageText, rhs.welcomeMessageText).isEquals();
    }

}
