
package de.caritas.cob.consultingtypeservice.schemas.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Behavior regarding the white spots agencies
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "whiteSpotAgencyAssigned",
    "whiteSpotAgencyId"
})
public class WhiteSpot {

    /**
     * If true, agency with id in property 'whiteSpotAgencyId' will be proposed if no agency was found via given postcode in registration form
     * (Required)
     * 
     */
    @JsonProperty("whiteSpotAgencyAssigned")
    @JsonPropertyDescription("If true, agency with id in property 'whiteSpotAgencyId' will be proposed if no agency was found via given postcode in registration form")
    private Boolean whiteSpotAgencyAssigned;
    /**
     * The id the of the white spot agency
     * 
     */
    @JsonProperty("whiteSpotAgencyId")
    @JsonPropertyDescription("The id the of the white spot agency")
    private Integer whiteSpotAgencyId;

    /**
     * No args constructor for use in serialization
     * 
     */
    public WhiteSpot() {
    }

    /**
     * 
     * @param whiteSpotAgencyId
     * @param whiteSpotAgencyAssigned
     */
    public WhiteSpot(Boolean whiteSpotAgencyAssigned, Integer whiteSpotAgencyId) {
        super();
        this.whiteSpotAgencyAssigned = whiteSpotAgencyAssigned;
        this.whiteSpotAgencyId = whiteSpotAgencyId;
    }

    /**
     * If true, agency with id in property 'whiteSpotAgencyId' will be proposed if no agency was found via given postcode in registration form
     * (Required)
     * 
     */
    @JsonProperty("whiteSpotAgencyAssigned")
    public Boolean getWhiteSpotAgencyAssigned() {
        return whiteSpotAgencyAssigned;
    }

    /**
     * If true, agency with id in property 'whiteSpotAgencyId' will be proposed if no agency was found via given postcode in registration form
     * (Required)
     * 
     */
    @JsonProperty("whiteSpotAgencyAssigned")
    public void setWhiteSpotAgencyAssigned(Boolean whiteSpotAgencyAssigned) {
        this.whiteSpotAgencyAssigned = whiteSpotAgencyAssigned;
    }

    public WhiteSpot withWhiteSpotAgencyAssigned(Boolean whiteSpotAgencyAssigned) {
        this.whiteSpotAgencyAssigned = whiteSpotAgencyAssigned;
        return this;
    }

    /**
     * The id the of the white spot agency
     * 
     */
    @JsonProperty("whiteSpotAgencyId")
    public Integer getWhiteSpotAgencyId() {
        return whiteSpotAgencyId;
    }

    /**
     * The id the of the white spot agency
     * 
     */
    @JsonProperty("whiteSpotAgencyId")
    public void setWhiteSpotAgencyId(Integer whiteSpotAgencyId) {
        this.whiteSpotAgencyId = whiteSpotAgencyId;
    }

    public WhiteSpot withWhiteSpotAgencyId(Integer whiteSpotAgencyId) {
        this.whiteSpotAgencyId = whiteSpotAgencyId;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(whiteSpotAgencyAssigned).append(whiteSpotAgencyId).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof WhiteSpot) == false) {
            return false;
        }
        WhiteSpot rhs = ((WhiteSpot) other);
        return new EqualsBuilder().append(whiteSpotAgencyAssigned, rhs.whiteSpotAgencyAssigned).append(whiteSpotAgencyId, rhs.whiteSpotAgencyId).isEquals();
    }

}
