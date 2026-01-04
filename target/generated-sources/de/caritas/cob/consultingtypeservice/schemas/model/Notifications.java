
package de.caritas.cob.consultingtypeservice.schemas.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Settings for the mail notifications
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "teamSessions"
})
public class Notifications {

    /**
     * Special mail notification settings for team sessions
     * (Required)
     * 
     */
    @JsonProperty("teamSessions")
    @JsonPropertyDescription("Special mail notification settings for team sessions")
    private TeamSessions teamSessions;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Notifications() {
    }

    /**
     * 
     * @param teamSessions
     */
    public Notifications(TeamSessions teamSessions) {
        super();
        this.teamSessions = teamSessions;
    }

    /**
     * Special mail notification settings for team sessions
     * (Required)
     * 
     */
    @JsonProperty("teamSessions")
    public TeamSessions getTeamSessions() {
        return teamSessions;
    }

    /**
     * Special mail notification settings for team sessions
     * (Required)
     * 
     */
    @JsonProperty("teamSessions")
    public void setTeamSessions(TeamSessions teamSessions) {
        this.teamSessions = teamSessions;
    }

    public Notifications withTeamSessions(TeamSessions teamSessions) {
        this.teamSessions = teamSessions;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(teamSessions).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Notifications) == false) {
            return false;
        }
        Notifications rhs = ((Notifications) other);
        return new EqualsBuilder().append(teamSessions, rhs.teamSessions).isEquals();
    }

}
