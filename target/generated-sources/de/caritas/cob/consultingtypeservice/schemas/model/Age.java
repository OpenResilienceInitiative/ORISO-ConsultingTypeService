
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
 * Age of the asker.
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "isEnabled",
    "options"
})
public class Age {

    /**
     * If this is true, the age selection will be displayed.
     * (Required)
     * 
     */
    @JsonProperty("isEnabled")
    @JsonPropertyDescription("If this is true, the age selection will be displayed.")
    private Boolean isEnabled = true;
    /**
     * Individual values that can be selected.
     * (Required)
     * 
     */
    @JsonProperty("options")
    @JsonPropertyDescription("Individual values that can be selected.")
    private List<Option> options = new ArrayList<Option>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Age() {
    }

    /**
     * 
     * @param isEnabled
     * @param options
     */
    public Age(Boolean isEnabled, List<Option> options) {
        super();
        this.isEnabled = isEnabled;
        this.options = options;
    }

    /**
     * If this is true, the age selection will be displayed.
     * (Required)
     * 
     */
    @JsonProperty("isEnabled")
    public Boolean getIsEnabled() {
        return isEnabled;
    }

    /**
     * If this is true, the age selection will be displayed.
     * (Required)
     * 
     */
    @JsonProperty("isEnabled")
    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public Age withIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
        return this;
    }

    /**
     * Individual values that can be selected.
     * (Required)
     * 
     */
    @JsonProperty("options")
    public List<Option> getOptions() {
        return options;
    }

    /**
     * Individual values that can be selected.
     * (Required)
     * 
     */
    @JsonProperty("options")
    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public Age withOptions(List<Option> options) {
        this.options = options;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(isEnabled).append(options).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Age) == false) {
            return false;
        }
        Age rhs = ((Age) other);
        return new EqualsBuilder().append(isEnabled, rhs.isEnabled).append(options, rhs.options).isEquals();
    }

}
