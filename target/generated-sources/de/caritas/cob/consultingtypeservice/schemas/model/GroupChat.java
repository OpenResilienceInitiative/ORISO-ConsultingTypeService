
package de.caritas.cob.consultingtypeservice.schemas.model;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Group chat settings
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "isGroupChat",
    "groupChatRules"
})
public class GroupChat {

    /**
     * True, if group chats are provided for this consulting type
     * (Required)
     * 
     */
    @JsonProperty("isGroupChat")
    @JsonPropertyDescription("True, if group chats are provided for this consulting type")
    private Boolean isGroupChat = false;
    /**
     * Rules for the group chat which are displayed to the user
     * 
     */
    @JsonProperty("groupChatRules")
    @JsonPropertyDescription("Rules for the group chat which are displayed to the user")
    private List<String> groupChatRules = new ArrayList<String>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public GroupChat() {
    }

    /**
     * 
     * @param isGroupChat
     * @param groupChatRules
     */
    public GroupChat(Boolean isGroupChat, List<String> groupChatRules) {
        super();
        this.isGroupChat = isGroupChat;
        this.groupChatRules = groupChatRules;
    }

    /**
     * True, if group chats are provided for this consulting type
     * (Required)
     * 
     */
    @JsonProperty("isGroupChat")
    public Boolean getIsGroupChat() {
        return isGroupChat;
    }

    /**
     * True, if group chats are provided for this consulting type
     * (Required)
     * 
     */
    @JsonProperty("isGroupChat")
    public void setIsGroupChat(Boolean isGroupChat) {
        this.isGroupChat = isGroupChat;
    }

    public GroupChat withIsGroupChat(Boolean isGroupChat) {
        this.isGroupChat = isGroupChat;
        return this;
    }

    /**
     * Rules for the group chat which are displayed to the user
     * 
     */
    @JsonProperty("groupChatRules")
    public List<String> getGroupChatRules() {
        return groupChatRules;
    }

    /**
     * Rules for the group chat which are displayed to the user
     * 
     */
    @JsonProperty("groupChatRules")
    public void setGroupChatRules(List<String> groupChatRules) {
        this.groupChatRules = groupChatRules;
    }

    public GroupChat withGroupChatRules(List<String> groupChatRules) {
        this.groupChatRules = groupChatRules;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(isGroupChat).append(groupChatRules).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof GroupChat) == false) {
            return false;
        }
        GroupChat rhs = ((GroupChat) other);
        return new EqualsBuilder().append(isGroupChat, rhs.isGroupChat).append(groupChatRules, rhs.groupChatRules).isEquals();
    }

}
