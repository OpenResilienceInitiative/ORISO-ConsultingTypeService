
package de.caritas.cob.consultingtypeservice.schemas.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Special mail notification settings for new messages
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "allTeamConsultants"
})
public class NewMessage {

    /**
     * If true, all consultants will be informed by email
     * (Required)
     * 
     */
    @JsonProperty("allTeamConsultants")
    @JsonPropertyDescription("If true, all consultants will be informed by email")
    private Boolean allTeamConsultants;

    /**
     * No args constructor for use in serialization
     * 
     */
    public NewMessage() {
    }

    /**
     * 
     * @param allTeamConsultants
     */
    public NewMessage(Boolean allTeamConsultants) {
        super();
        this.allTeamConsultants = allTeamConsultants;
    }

    /**
     * If true, all consultants will be informed by email
     * (Required)
     * 
     */
    @JsonProperty("allTeamConsultants")
    public Boolean getAllTeamConsultants() {
        return allTeamConsultants;
    }

    /**
     * If true, all consultants will be informed by email
     * (Required)
     * 
     */
    @JsonProperty("allTeamConsultants")
    public void setAllTeamConsultants(Boolean allTeamConsultants) {
        this.allTeamConsultants = allTeamConsultants;
    }

    public NewMessage withAllTeamConsultants(Boolean allTeamConsultants) {
        this.allTeamConsultants = allTeamConsultants;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(allTeamConsultants).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof NewMessage) == false) {
            return false;
        }
        NewMessage rhs = ((NewMessage) other);
        return new EqualsBuilder().append(allTeamConsultants, rhs.allTeamConsultants).isEquals();
    }

}
