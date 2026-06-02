
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
    "age",
    "state"
})
public class RequiredComponents {

    /**
     * Age of the asker.
     * (Required)
     * 
     */
    @JsonProperty("age")
    @JsonPropertyDescription("Age of the asker.")
    private Age age;
    /**
     * Selection for the federal state of the asker.
     * (Required)
     * 
     */
    @JsonProperty("state")
    @JsonPropertyDescription("Selection for the federal state of the asker.")
    private State state;

    /**
     * No args constructor for use in serialization
     * 
     */
    public RequiredComponents() {
    }

    /**
     * 
     * @param state
     * @param age
     */
    public RequiredComponents(Age age, State state) {
        super();
        this.age = age;
        this.state = state;
    }

    /**
     * Age of the asker.
     * (Required)
     * 
     */
    @JsonProperty("age")
    public Age getAge() {
        return age;
    }

    /**
     * Age of the asker.
     * (Required)
     * 
     */
    @JsonProperty("age")
    public void setAge(Age age) {
        this.age = age;
    }

    public RequiredComponents withAge(Age age) {
        this.age = age;
        return this;
    }

    /**
     * Selection for the federal state of the asker.
     * (Required)
     * 
     */
    @JsonProperty("state")
    public State getState() {
        return state;
    }

    /**
     * Selection for the federal state of the asker.
     * (Required)
     * 
     */
    @JsonProperty("state")
    public void setState(State state) {
        this.state = state;
    }

    public RequiredComponents withState(State state) {
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
        if ((other instanceof RequiredComponents) == false) {
            return false;
        }
        RequiredComponents rhs = ((RequiredComponents) other);
        return new EqualsBuilder().append(age, rhs.age).append(state, rhs.state).isEquals();
    }

}
