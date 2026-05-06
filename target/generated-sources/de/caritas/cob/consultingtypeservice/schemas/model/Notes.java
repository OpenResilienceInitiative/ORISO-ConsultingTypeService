
package de.caritas.cob.consultingtypeservice.schemas.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Additional info to display in agency selection and password accordion (optional)
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "agencySelection",
    "password"
})
public class Notes {

    /**
     * Additional info to display in agency selection
     * 
     */
    @JsonProperty("agencySelection")
    @JsonPropertyDescription("Additional info to display in agency selection")
    private String agencySelection = null;
    /**
     * Additional info to display in password accordion
     * 
     */
    @JsonProperty("password")
    @JsonPropertyDescription("Additional info to display in password accordion")
    private String password = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Notes() {
    }

    /**
     * 
     * @param password
     * @param agencySelection
     */
    public Notes(String agencySelection, String password) {
        super();
        this.agencySelection = agencySelection;
        this.password = password;
    }

    /**
     * Additional info to display in agency selection
     * 
     */
    @JsonProperty("agencySelection")
    public String getAgencySelection() {
        return agencySelection;
    }

    /**
     * Additional info to display in agency selection
     * 
     */
    @JsonProperty("agencySelection")
    public void setAgencySelection(String agencySelection) {
        this.agencySelection = agencySelection;
    }

    public Notes withAgencySelection(String agencySelection) {
        this.agencySelection = agencySelection;
        return this;
    }

    /**
     * Additional info to display in password accordion
     * 
     */
    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    /**
     * Additional info to display in password accordion
     * 
     */
    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
    }

    public Notes withPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(agencySelection).append(password).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Notes) == false) {
            return false;
        }
        Notes rhs = ((Notes) other);
        return new EqualsBuilder().append(agencySelection, rhs.agencySelection).append(password, rhs.password).isEquals();
    }

}
