
package de.caritas.cob.consultingtypeservice.schemas.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Control of mandatory fields for registration
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "age",
    "state"
})
public class MandatoryFields {

    /**
     * If true, this field is mandatory for the registration
     * (Required)
     * 
     */
    @JsonProperty("age")
    @JsonPropertyDescription("If true, this field is mandatory for the registration")
    private Boolean age;
    /**
     * If true, this field is mandatory for the registration
     * (Required)
     * 
     */
    @JsonProperty("state")
    @JsonPropertyDescription("If true, this field is mandatory for the registration")
    private Boolean state;

    /**
     * No args constructor for use in serialization
     * 
     */
    public MandatoryFields() {
    }

    /**
     * 
     * @param state
     * @param age
     */
    public MandatoryFields(Boolean age, Boolean state) {
        super();
        this.age = age;
        this.state = state;
    }

    /**
     * If true, this field is mandatory for the registration
     * (Required)
     * 
     */
    @JsonProperty("age")
    public Boolean getAge() {
        return age;
    }

    /**
     * If true, this field is mandatory for the registration
     * (Required)
     * 
     */
    @JsonProperty("age")
    public void setAge(Boolean age) {
        this.age = age;
    }

    public MandatoryFields withAge(Boolean age) {
        this.age = age;
        return this;
    }

    /**
     * If true, this field is mandatory for the registration
     * (Required)
     * 
     */
    @JsonProperty("state")
    public Boolean getState() {
        return state;
    }

    /**
     * If true, this field is mandatory for the registration
     * (Required)
     * 
     */
    @JsonProperty("state")
    public void setState(Boolean state) {
        this.state = state;
    }

    public MandatoryFields withState(Boolean state) {
        this.state = state;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(age).append(state).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof MandatoryFields) == false) {
            return false;
        }
        MandatoryFields rhs = ((MandatoryFields) other);
        return new EqualsBuilder().append(age, rhs.age).append(state, rhs.state).isEquals();
    }

}
