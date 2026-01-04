
package de.caritas.cob.consultingtypeservice.schemas.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "value",
    "label"
})
public class Option {

    /**
     * Identifier for the option.
     * (Required)
     * 
     */
    @JsonProperty("value")
    @JsonPropertyDescription("Identifier for the option.")
    private String value;
    /**
     * Human readable label for the option.
     * (Required)
     * 
     */
    @JsonProperty("label")
    @JsonPropertyDescription("Human readable label for the option.")
    private String label;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Option() {
    }

    /**
     * 
     * @param label
     * @param value
     */
    public Option(String value, String label) {
        super();
        this.value = value;
        this.label = label;
    }

    /**
     * Identifier for the option.
     * (Required)
     * 
     */
    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    /**
     * Identifier for the option.
     * (Required)
     * 
     */
    @JsonProperty("value")
    public void setValue(String value) {
        this.value = value;
    }

    public Option withValue(String value) {
        this.value = value;
        return this;
    }

    /**
     * Human readable label for the option.
     * (Required)
     * 
     */
    @JsonProperty("label")
    public String getLabel() {
        return label;
    }

    /**
     * Human readable label for the option.
     * (Required)
     * 
     */
    @JsonProperty("label")
    public void setLabel(String label) {
        this.label = label;
    }

    public Option withLabel(String label) {
        this.label = label;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(value).append(label).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Option) == false) {
            return false;
        }
        Option rhs = ((Option) other);
        return new EqualsBuilder().append(value, rhs.value).append(label, rhs.label).isEquals();
    }

}
