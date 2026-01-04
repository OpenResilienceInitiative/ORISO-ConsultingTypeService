package de.caritas.cob.consultingtypeservice.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import de.caritas.cob.consultingtypeservice.api.model.RegistrationDTOMandatoryFields;
import de.caritas.cob.consultingtypeservice.api.model.RegistrationDTONotes;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * BasicConsultingTypeResponseDTORegistration
 */

@JsonTypeName("BasicConsultingTypeResponseDTO_registration")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-25T23:22:19.939702671Z[Etc/UTC]")
public class BasicConsultingTypeResponseDTORegistration {

  @JsonProperty("minPostcodeSize")
  private Integer minPostcodeSize;

  @JsonProperty("autoSelectAgency")
  private Boolean autoSelectAgency;

  @JsonProperty("autoSelectPostcode")
  private Boolean autoSelectPostcode;

  @JsonProperty("notes")
  private RegistrationDTONotes notes;

  @JsonProperty("mandatoryFields")
  private RegistrationDTOMandatoryFields mandatoryFields;

  public BasicConsultingTypeResponseDTORegistration minPostcodeSize(Integer minPostcodeSize) {
    this.minPostcodeSize = minPostcodeSize;
    return this;
  }

  /**
   * Get minPostcodeSize
   * @return minPostcodeSize
  */
  
  @Schema(name = "minPostcodeSize", example = "5", required = false)
  public Integer getMinPostcodeSize() {
    return minPostcodeSize;
  }

  public void setMinPostcodeSize(Integer minPostcodeSize) {
    this.minPostcodeSize = minPostcodeSize;
  }

  public BasicConsultingTypeResponseDTORegistration autoSelectAgency(Boolean autoSelectAgency) {
    this.autoSelectAgency = autoSelectAgency;
    return this;
  }

  /**
   * Get autoSelectAgency
   * @return autoSelectAgency
  */
  
  @Schema(name = "autoSelectAgency", example = "false", required = false)
  public Boolean getAutoSelectAgency() {
    return autoSelectAgency;
  }

  public void setAutoSelectAgency(Boolean autoSelectAgency) {
    this.autoSelectAgency = autoSelectAgency;
  }

  public BasicConsultingTypeResponseDTORegistration autoSelectPostcode(Boolean autoSelectPostcode) {
    this.autoSelectPostcode = autoSelectPostcode;
    return this;
  }

  /**
   * Get autoSelectPostcode
   * @return autoSelectPostcode
  */
  
  @Schema(name = "autoSelectPostcode", example = "true", required = false)
  public Boolean getAutoSelectPostcode() {
    return autoSelectPostcode;
  }

  public void setAutoSelectPostcode(Boolean autoSelectPostcode) {
    this.autoSelectPostcode = autoSelectPostcode;
  }

  public BasicConsultingTypeResponseDTORegistration notes(RegistrationDTONotes notes) {
    this.notes = notes;
    return this;
  }

  /**
   * Get notes
   * @return notes
  */
  @Valid 
  @Schema(name = "notes", required = false)
  public RegistrationDTONotes getNotes() {
    return notes;
  }

  public void setNotes(RegistrationDTONotes notes) {
    this.notes = notes;
  }

  public BasicConsultingTypeResponseDTORegistration mandatoryFields(RegistrationDTOMandatoryFields mandatoryFields) {
    this.mandatoryFields = mandatoryFields;
    return this;
  }

  /**
   * Get mandatoryFields
   * @return mandatoryFields
  */
  @Valid 
  @Schema(name = "mandatoryFields", required = false)
  public RegistrationDTOMandatoryFields getMandatoryFields() {
    return mandatoryFields;
  }

  public void setMandatoryFields(RegistrationDTOMandatoryFields mandatoryFields) {
    this.mandatoryFields = mandatoryFields;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BasicConsultingTypeResponseDTORegistration basicConsultingTypeResponseDTORegistration = (BasicConsultingTypeResponseDTORegistration) o;
    return Objects.equals(this.minPostcodeSize, basicConsultingTypeResponseDTORegistration.minPostcodeSize) &&
        Objects.equals(this.autoSelectAgency, basicConsultingTypeResponseDTORegistration.autoSelectAgency) &&
        Objects.equals(this.autoSelectPostcode, basicConsultingTypeResponseDTORegistration.autoSelectPostcode) &&
        Objects.equals(this.notes, basicConsultingTypeResponseDTORegistration.notes) &&
        Objects.equals(this.mandatoryFields, basicConsultingTypeResponseDTORegistration.mandatoryFields);
  }

  @Override
  public int hashCode() {
    return Objects.hash(minPostcodeSize, autoSelectAgency, autoSelectPostcode, notes, mandatoryFields);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BasicConsultingTypeResponseDTORegistration {\n");
    sb.append("    minPostcodeSize: ").append(toIndentedString(minPostcodeSize)).append("\n");
    sb.append("    autoSelectAgency: ").append(toIndentedString(autoSelectAgency)).append("\n");
    sb.append("    autoSelectPostcode: ").append(toIndentedString(autoSelectPostcode)).append("\n");
    sb.append("    notes: ").append(toIndentedString(notes)).append("\n");
    sb.append("    mandatoryFields: ").append(toIndentedString(mandatoryFields)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

