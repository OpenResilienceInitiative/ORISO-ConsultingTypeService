
package de.caritas.cob.consultingtypeservice.schemas.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Selection for the federal state of the asker.
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "isEnabled"
})
public class State {

    /**
     * If this is true, the state selection will be displayed.
     * 
     */
    @JsonProperty("isEnabled")
    @JsonPropertyDescription("If this is true, the state selection will be displayed.")
    private Boolean isEnabled = true;

    /**
     * No args constructor for use in serialization
     * 
     */
    public State() {
    }

    /**
     * 
     * @param isEnabled
     */
    public State(Boolean isEnabled) {
        super();
        this.isEnabled = isEnabled;
    }

    /**
     * If this is true, the state selection will be displayed.
     * 
     */
    @JsonProperty("isEnabled")
    public Boolean getIsEnabled() {
        return isEnabled;
    }

    /**
     * If this is true, the state selection will be displayed.
     * 
     */
    @JsonProperty("isEnabled")
    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public State withIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(isEnabled).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof State) == false) {
            return false;
        }
        State rhs = ((State) other);
        return new EqualsBuilder().append(isEnabled, rhs.isEnabled).isEquals();
    }

}
