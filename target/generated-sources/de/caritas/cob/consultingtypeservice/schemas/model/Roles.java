
package de.caritas.cob.consultingtypeservice.schemas.model;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Role definitions
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "consultant"
})
public class Roles {

    /**
     * Role definitions for consultants
     * 
     */
    @JsonProperty("consultant")
    @JsonPropertyDescription("Role definitions for consultants")
    private Map<String, List<String>> consultant;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Roles() {
    }

    /**
     * 
     * @param consultant
     */
    public Roles(Map<String, List<String>> consultant) {
        super();
        this.consultant = consultant;
    }

    /**
     * Role definitions for consultants
     * 
     */
    @JsonProperty("consultant")
    public Map<String, List<String>> getConsultant() {
        return consultant;
    }

    /**
     * Role definitions for consultants
     * 
     */
    @JsonProperty("consultant")
    public void setConsultant(Map<String, List<String>> consultant) {
        this.consultant = consultant;
    }

    public Roles withConsultant(Map<String, List<String>> consultant) {
        this.consultant = consultant;
        return this;
    }

    @Override
    public java.lang.String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(consultant).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Roles) == false) {
            return false;
        }
        Roles rhs = ((Roles) other);
        return new EqualsBuilder().append(consultant, rhs.consultant).isEquals();
    }

}
