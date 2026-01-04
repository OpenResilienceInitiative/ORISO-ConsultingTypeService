
package de.caritas.cob.consultingtypeservice.schemas.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Forwarding urls
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "requiredAidMissingRedirectUrl",
    "registrationPostcodeFallbackUrl"
})
public class Urls {

    /**
     * If no `aid` query parameter (agency ID) is passed to the respective registration page as get parameter, the UI will redirect to this URL.
     * 
     */
    @JsonProperty("requiredAidMissingRedirectUrl")
    @JsonPropertyDescription("If no `aid` query parameter (agency ID) is passed to the respective registration page as get parameter, the UI will redirect to this URL.")
    private String requiredAidMissingRedirectUrl = null;
    /**
     * If no white spot agency provided for this consulting type a note is display with a link to this url, in case no matching agency for the given postcode was found
     * 
     */
    @JsonProperty("registrationPostcodeFallbackUrl")
    @JsonPropertyDescription("If no white spot agency provided for this consulting type a note is display with a link to this url, in case no matching agency for the given postcode was found")
    private String registrationPostcodeFallbackUrl = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Urls() {
    }

    /**
     * 
     * @param requiredAidMissingRedirectUrl
     * @param registrationPostcodeFallbackUrl
     */
    public Urls(String requiredAidMissingRedirectUrl, String registrationPostcodeFallbackUrl) {
        super();
        this.requiredAidMissingRedirectUrl = requiredAidMissingRedirectUrl;
        this.registrationPostcodeFallbackUrl = registrationPostcodeFallbackUrl;
    }

    /**
     * If no `aid` query parameter (agency ID) is passed to the respective registration page as get parameter, the UI will redirect to this URL.
     * 
     */
    @JsonProperty("requiredAidMissingRedirectUrl")
    public String getRequiredAidMissingRedirectUrl() {
        return requiredAidMissingRedirectUrl;
    }

    /**
     * If no `aid` query parameter (agency ID) is passed to the respective registration page as get parameter, the UI will redirect to this URL.
     * 
     */
    @JsonProperty("requiredAidMissingRedirectUrl")
    public void setRequiredAidMissingRedirectUrl(String requiredAidMissingRedirectUrl) {
        this.requiredAidMissingRedirectUrl = requiredAidMissingRedirectUrl;
    }

    public Urls withRequiredAidMissingRedirectUrl(String requiredAidMissingRedirectUrl) {
        this.requiredAidMissingRedirectUrl = requiredAidMissingRedirectUrl;
        return this;
    }

    /**
     * If no white spot agency provided for this consulting type a note is display with a link to this url, in case no matching agency for the given postcode was found
     * 
     */
    @JsonProperty("registrationPostcodeFallbackUrl")
    public String getRegistrationPostcodeFallbackUrl() {
        return registrationPostcodeFallbackUrl;
    }

    /**
     * If no white spot agency provided for this consulting type a note is display with a link to this url, in case no matching agency for the given postcode was found
     * 
     */
    @JsonProperty("registrationPostcodeFallbackUrl")
    public void setRegistrationPostcodeFallbackUrl(String registrationPostcodeFallbackUrl) {
        this.registrationPostcodeFallbackUrl = registrationPostcodeFallbackUrl;
    }

    public Urls withRegistrationPostcodeFallbackUrl(String registrationPostcodeFallbackUrl) {
        this.registrationPostcodeFallbackUrl = registrationPostcodeFallbackUrl;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(requiredAidMissingRedirectUrl).append(registrationPostcodeFallbackUrl).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Urls) == false) {
            return false;
        }
        Urls rhs = ((Urls) other);
        return new EqualsBuilder().append(requiredAidMissingRedirectUrl, rhs.requiredAidMissingRedirectUrl).append(registrationPostcodeFallbackUrl, rhs.registrationPostcodeFallbackUrl).isEquals();
    }

}
