
package de.caritas.cob.consultingtypeservice.schemas.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Properties for registration welcome screen items
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "anonymous"
})
public class WelcomeScreen {

    /**
     * Item 'anonymous' on registration welcome screen
     * 
     */
    @JsonProperty("anonymous")
    @JsonPropertyDescription("Item 'anonymous' on registration welcome screen")
    private Anonymous anonymous = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public WelcomeScreen() {
    }

    /**
     * 
     * @param anonymous
     */
    public WelcomeScreen(Anonymous anonymous) {
        super();
        this.anonymous = anonymous;
    }

    /**
     * Item 'anonymous' on registration welcome screen
     * 
     */
    @JsonProperty("anonymous")
    public Anonymous getAnonymous() {
        return anonymous;
    }

    /**
     * Item 'anonymous' on registration welcome screen
     * 
     */
    @JsonProperty("anonymous")
    public void setAnonymous(Anonymous anonymous) {
        this.anonymous = anonymous;
    }

    public WelcomeScreen withAnonymous(Anonymous anonymous) {
        this.anonymous = anonymous;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(anonymous).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof WelcomeScreen) == false) {
            return false;
        }
        WelcomeScreen rhs = ((WelcomeScreen) other);
        return new EqualsBuilder().append(anonymous, rhs.anonymous).isEquals();
    }

}
