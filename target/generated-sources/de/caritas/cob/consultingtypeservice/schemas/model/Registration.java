
package de.caritas.cob.consultingtypeservice.schemas.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Settings for the registration process
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "minPostcodeSize",
    "autoSelectAgency",
    "autoSelectPostcode",
    "notes",
    "mandatoryFields"
})
public class Registration {

    /**
     * The minimum number of digits for the postal code that must be entered during registration.
     * 
     */
    @JsonProperty("minPostcodeSize")
    @JsonPropertyDescription("The minimum number of digits for the postal code that must be entered during registration.")
    private Integer minPostcodeSize = 5;
    /**
     * For consulting types to which only one agency is assigned: if true the assigned agency is automatically selected
     * 
     */
    @JsonProperty("autoSelectAgency")
    @JsonPropertyDescription("For consulting types to which only one agency is assigned: if true the assigned agency is automatically selected")
    private Boolean autoSelectAgency = false;
    /**
     * If true postcode is automatically taken over from agency in the registration form
     * 
     */
    @JsonProperty("autoSelectPostcode")
    @JsonPropertyDescription("If true postcode is automatically taken over from agency in the registration form")
    private Boolean autoSelectPostcode = false;
    /**
     * Additional info to display in agency selection and password accordion (optional)
     * (Required)
     * 
     */
    @JsonProperty("notes")
    @JsonPropertyDescription("Additional info to display in agency selection and password accordion (optional)")
    private Notes notes;
    /**
     * Control of mandatory fields for registration
     * (Required)
     * 
     */
    @JsonProperty("mandatoryFields")
    @JsonPropertyDescription("Control of mandatory fields for registration")
    private MandatoryFields mandatoryFields;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Registration() {
    }

    /**
     * 
     * @param minPostcodeSize
     * @param notes
     * @param mandatoryFields
     * @param autoSelectAgency
     * @param autoSelectPostcode
     */
    public Registration(Integer minPostcodeSize, Boolean autoSelectAgency, Boolean autoSelectPostcode, Notes notes, MandatoryFields mandatoryFields) {
        super();
        this.minPostcodeSize = minPostcodeSize;
        this.autoSelectAgency = autoSelectAgency;
        this.autoSelectPostcode = autoSelectPostcode;
        this.notes = notes;
        this.mandatoryFields = mandatoryFields;
    }

    /**
     * The minimum number of digits for the postal code that must be entered during registration.
     * 
     */
    @JsonProperty("minPostcodeSize")
    public Integer getMinPostcodeSize() {
        return minPostcodeSize;
    }

    /**
     * The minimum number of digits for the postal code that must be entered during registration.
     * 
     */
    @JsonProperty("minPostcodeSize")
    public void setMinPostcodeSize(Integer minPostcodeSize) {
        this.minPostcodeSize = minPostcodeSize;
    }

    public Registration withMinPostcodeSize(Integer minPostcodeSize) {
        this.minPostcodeSize = minPostcodeSize;
        return this;
    }

    /**
     * For consulting types to which only one agency is assigned: if true the assigned agency is automatically selected
     * 
     */
    @JsonProperty("autoSelectAgency")
    public Boolean getAutoSelectAgency() {
        return autoSelectAgency;
    }

    /**
     * For consulting types to which only one agency is assigned: if true the assigned agency is automatically selected
     * 
     */
    @JsonProperty("autoSelectAgency")
    public void setAutoSelectAgency(Boolean autoSelectAgency) {
        this.autoSelectAgency = autoSelectAgency;
    }

    public Registration withAutoSelectAgency(Boolean autoSelectAgency) {
        this.autoSelectAgency = autoSelectAgency;
        return this;
    }

    /**
     * If true postcode is automatically taken over from agency in the registration form
     * 
     */
    @JsonProperty("autoSelectPostcode")
    public Boolean getAutoSelectPostcode() {
        return autoSelectPostcode;
    }

    /**
     * If true postcode is automatically taken over from agency in the registration form
     * 
     */
    @JsonProperty("autoSelectPostcode")
    public void setAutoSelectPostcode(Boolean autoSelectPostcode) {
        this.autoSelectPostcode = autoSelectPostcode;
    }

    public Registration withAutoSelectPostcode(Boolean autoSelectPostcode) {
        this.autoSelectPostcode = autoSelectPostcode;
        return this;
    }

    /**
     * Additional info to display in agency selection and password accordion (optional)
     * (Required)
     * 
     */
    @JsonProperty("notes")
    public Notes getNotes() {
        return notes;
    }

    /**
     * Additional info to display in agency selection and password accordion (optional)
     * (Required)
     * 
     */
    @JsonProperty("notes")
    public void setNotes(Notes notes) {
        this.notes = notes;
    }

    public Registration withNotes(Notes notes) {
        this.notes = notes;
        return this;
    }

    /**
     * Control of mandatory fields for registration
     * (Required)
     * 
     */
    @JsonProperty("mandatoryFields")
    public MandatoryFields getMandatoryFields() {
        return mandatoryFields;
    }

    /**
     * Control of mandatory fields for registration
     * (Required)
     * 
     */
    @JsonProperty("mandatoryFields")
    public void setMandatoryFields(MandatoryFields mandatoryFields) {
        this.mandatoryFields = mandatoryFields;
    }

    public Registration withMandatoryFields(MandatoryFields mandatoryFields) {
        this.mandatoryFields = mandatoryFields;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(minPostcodeSize).append(autoSelectAgency).append(autoSelectPostcode).append(notes).append(mandatoryFields).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Registration) == false) {
            return false;
        }
        Registration rhs = ((Registration) other);
        return new EqualsBuilder().append(minPostcodeSize, rhs.minPostcodeSize).append(autoSelectAgency, rhs.autoSelectAgency).append(autoSelectPostcode, rhs.autoSelectPostcode).append(notes, rhs.notes).append(mandatoryFields, rhs.mandatoryFields).isEquals();
    }

}
