
package de.caritas.cob.consultingtypeservice.schemas.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Label and link for further information of the consulting type
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "label",
    "url"
})
public class FurtherInformation {

    /**
     * The label for the link
     * (Required)
     * 
     */
    @JsonProperty("label")
    @JsonPropertyDescription("The label for the link")
    private String label;
    /**
     * The link
     * (Required)
     * 
     */
    @JsonProperty("url")
    @JsonPropertyDescription("The link")
    private String url;

    /**
     * No args constructor for use in serialization
     * 
     */
    public FurtherInformation() {
    }

    /**
     * 
     * @param label
     * @param url
     */
    public FurtherInformation(String label, String url) {
        super();
        this.label = label;
        this.url = url;
    }

    /**
     * The label for the link
     * (Required)
     * 
     */
    @JsonProperty("label")
    public String getLabel() {
        return label;
    }

    /**
     * The label for the link
     * (Required)
     * 
     */
    @JsonProperty("label")
    public void setLabel(String label) {
        this.label = label;
    }

    public FurtherInformation withLabel(String label) {
        this.label = label;
        return this;
    }

    /**
     * The link
     * (Required)
     * 
     */
    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    /**
     * The link
     * (Required)
     * 
     */
    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    public FurtherInformation withUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(label).append(url).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof FurtherInformation) == false) {
            return false;
        }
        FurtherInformation rhs = ((FurtherInformation) other);
        return new EqualsBuilder().append(label, rhs.label).append(url, rhs.url).isEquals();
    }

}
