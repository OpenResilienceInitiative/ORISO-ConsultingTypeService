
package de.caritas.cob.consultingtypeservice.schemas.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Settings to control which optional fields for consultation should be initialized for this consultation type.
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "addictiveDrugs",
    "age",
    "gender",
    "relation",
    "state"
})
public class SessionDataInitializing {

    /**
     * True, if the field addictiveDrugs should be initialized for this consulting type
     * 
     */
    @JsonProperty("addictiveDrugs")
    @JsonPropertyDescription("True, if the field addictiveDrugs should be initialized for this consulting type")
    private Boolean addictiveDrugs = false;
    /**
     * True, if the field age should be initialized for this consulting type
     * 
     */
    @JsonProperty("age")
    @JsonPropertyDescription("True, if the field age should be initialized for this consulting type")
    private Boolean age = false;
    /**
     * True, if the field gender should be initialized for this consulting type
     * 
     */
    @JsonProperty("gender")
    @JsonPropertyDescription("True, if the field gender should be initialized for this consulting type")
    private Boolean gender = false;
    /**
     * True, if the field relation should be initialized for this consulting type
     * 
     */
    @JsonProperty("relation")
    @JsonPropertyDescription("True, if the field relation should be initialized for this consulting type")
    private Boolean relation = false;
    /**
     * True, if the field state should be initialized for this consulting type
     * 
     */
    @JsonProperty("state")
    @JsonPropertyDescription("True, if the field state should be initialized for this consulting type")
    private Boolean state = false;

    /**
     * No args constructor for use in serialization
     * 
     */
    public SessionDataInitializing() {
    }

    /**
     * 
     * @param gender
     * @param state
     * @param addictiveDrugs
     * @param age
     * @param relation
     */
    public SessionDataInitializing(Boolean addictiveDrugs, Boolean age, Boolean gender, Boolean relation, Boolean state) {
        super();
        this.addictiveDrugs = addictiveDrugs;
        this.age = age;
        this.gender = gender;
        this.relation = relation;
        this.state = state;
    }

    /**
     * True, if the field addictiveDrugs should be initialized for this consulting type
     * 
     */
    @JsonProperty("addictiveDrugs")
    public Boolean getAddictiveDrugs() {
        return addictiveDrugs;
    }

    /**
     * True, if the field addictiveDrugs should be initialized for this consulting type
     * 
     */
    @JsonProperty("addictiveDrugs")
    public void setAddictiveDrugs(Boolean addictiveDrugs) {
        this.addictiveDrugs = addictiveDrugs;
    }

    public SessionDataInitializing withAddictiveDrugs(Boolean addictiveDrugs) {
        this.addictiveDrugs = addictiveDrugs;
        return this;
    }

    /**
     * True, if the field age should be initialized for this consulting type
     * 
     */
    @JsonProperty("age")
    public Boolean getAge() {
        return age;
    }

    /**
     * True, if the field age should be initialized for this consulting type
     * 
     */
    @JsonProperty("age")
    public void setAge(Boolean age) {
        this.age = age;
    }

    public SessionDataInitializing withAge(Boolean age) {
        this.age = age;
        return this;
    }

    /**
     * True, if the field gender should be initialized for this consulting type
     * 
     */
    @JsonProperty("gender")
    public Boolean getGender() {
        return gender;
    }

    /**
     * True, if the field gender should be initialized for this consulting type
     * 
     */
    @JsonProperty("gender")
    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public SessionDataInitializing withGender(Boolean gender) {
        this.gender = gender;
        return this;
    }

    /**
     * True, if the field relation should be initialized for this consulting type
     * 
     */
    @JsonProperty("relation")
    public Boolean getRelation() {
        return relation;
    }

    /**
     * True, if the field relation should be initialized for this consulting type
     * 
     */
    @JsonProperty("relation")
    public void setRelation(Boolean relation) {
        this.relation = relation;
    }

    public SessionDataInitializing withRelation(Boolean relation) {
        this.relation = relation;
        return this;
    }

    /**
     * True, if the field state should be initialized for this consulting type
     * 
     */
    @JsonProperty("state")
    public Boolean getState() {
        return state;
    }

    /**
     * True, if the field state should be initialized for this consulting type
     * 
     */
    @JsonProperty("state")
    public void setState(Boolean state) {
        this.state = state;
    }

    public SessionDataInitializing withState(Boolean state) {
        this.state = state;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(addictiveDrugs).append(age).append(gender).append(relation).append(state).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof SessionDataInitializing) == false) {
            return false;
        }
        SessionDataInitializing rhs = ((SessionDataInitializing) other);
        return new EqualsBuilder().append(addictiveDrugs, rhs.addictiveDrugs).append(age, rhs.age).append(gender, rhs.gender).append(relation, rhs.relation).append(state, rhs.state).isEquals();
    }

}
