
package de.caritas.cob.consultingtypeservice.schemas.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Item 'anonymous' on registration welcome screen
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "title",
    "text"
})
public class Anonymous {

    /**
     * Title for item 'anonymous'
     * 
     */
    @JsonProperty("title")
    @JsonPropertyDescription("Title for item 'anonymous'")
    private String title = null;
    /**
     * Text for item 'anonymous'
     * 
     */
    @JsonProperty("text")
    @JsonPropertyDescription("Text for item 'anonymous'")
    private String text = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Anonymous() {
    }

    /**
     * 
     * @param text
     * @param title
     */
    public Anonymous(String title, String text) {
        super();
        this.title = title;
        this.text = text;
    }

    /**
     * Title for item 'anonymous'
     * 
     */
    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    /**
     * Title for item 'anonymous'
     * 
     */
    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    public Anonymous withTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Text for item 'anonymous'
     * 
     */
    @JsonProperty("text")
    public String getText() {
        return text;
    }

    /**
     * Text for item 'anonymous'
     * 
     */
    @JsonProperty("text")
    public void setText(String text) {
        this.text = text;
    }

    public Anonymous withText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(title).append(text).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Anonymous) == false) {
            return false;
        }
        Anonymous rhs = ((Anonymous) other);
        return new EqualsBuilder().append(title, rhs.title).append(text, rhs.text).isEquals();
    }

}
