
package de.caritas.cob.consultingtypeservice.schemas.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Special mail notification settings for team sessions
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "newMessage"
})
public class TeamSessions {

    /**
     * Special mail notification settings for new messages
     * (Required)
     * 
     */
    @JsonProperty("newMessage")
    @JsonPropertyDescription("Special mail notification settings for new messages")
    private NewMessage newMessage;

    /**
     * No args constructor for use in serialization
     * 
     */
    public TeamSessions() {
    }

    /**
     * 
     * @param newMessage
     */
    public TeamSessions(NewMessage newMessage) {
        super();
        this.newMessage = newMessage;
    }

    /**
     * Special mail notification settings for new messages
     * (Required)
     * 
     */
    @JsonProperty("newMessage")
    public NewMessage getNewMessage() {
        return newMessage;
    }

    /**
     * Special mail notification settings for new messages
     * (Required)
     * 
     */
    @JsonProperty("newMessage")
    public void setNewMessage(NewMessage newMessage) {
        this.newMessage = newMessage;
    }

    public TeamSessions withNewMessage(NewMessage newMessage) {
        this.newMessage = newMessage;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(newMessage).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof TeamSessions) == false) {
            return false;
        }
        TeamSessions rhs = ((TeamSessions) other);
        return new EqualsBuilder().append(newMessage, rhs.newMessage).isEquals();
    }

}
