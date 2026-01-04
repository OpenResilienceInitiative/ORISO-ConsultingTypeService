
package de.caritas.cob.consultingtypeservice.schemas.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Application settings
 * <p>
 * Settings for application instance
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "multitenancyWithSingleDomainEnabled",
    "multitenancyEnabled",
    "useTenantService",
    "useConsultingTypesForAgencies",
    "enableWalkthrough",
    "disableVideoAppointments",
    "mainTenantSubdomainForSingleDomainMultitenancy",
    "useOverviewPage",
    "calcomUrl",
    "budibaseAuthClientId",
    "budibaseUrl",
    "calendarAppUrl",
    "legalContentChangesBySingleTenantAdminsAllowed",
    "documentationEnabled",
    "required"
})
public class ApplicationSettings {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("multitenancyWithSingleDomainEnabled")
    private MultitenancyWithSingleDomainEnabled multitenancyWithSingleDomainEnabled;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("multitenancyEnabled")
    private MultitenancyEnabled multitenancyEnabled;
    @JsonProperty("useTenantService")
    private UseTenantService useTenantService;
    @JsonProperty("useConsultingTypesForAgencies")
    private UseConsultingTypesForAgencies useConsultingTypesForAgencies;
    @JsonProperty("enableWalkthrough")
    private EnableWalkthrough enableWalkthrough;
    @JsonProperty("disableVideoAppointments")
    private DisableVideoAppointments disableVideoAppointments;
    @JsonProperty("mainTenantSubdomainForSingleDomainMultitenancy")
    private MainTenantSubdomainForSingleDomainMultitenancy mainTenantSubdomainForSingleDomainMultitenancy;
    @JsonProperty("useOverviewPage")
    private UseOverviewPage useOverviewPage;
    @JsonProperty("calcomUrl")
    private CalcomUrl calcomUrl;
    @JsonProperty("budibaseAuthClientId")
    private BudibaseAuthClientId budibaseAuthClientId;
    @JsonProperty("budibaseUrl")
    private BudibaseUrl budibaseUrl;
    @JsonProperty("calendarAppUrl")
    private CalendarAppUrl calendarAppUrl;
    @JsonProperty("legalContentChangesBySingleTenantAdminsAllowed")
    private LegalContentChangesBySingleTenantAdminsAllowed legalContentChangesBySingleTenantAdminsAllowed;
    @JsonProperty("documentationEnabled")
    private DocumentationEnabled documentationEnabled;
    @JsonProperty("required")
    private Object required;

    /**
     * No args constructor for use in serialization
     * 
     */
    public ApplicationSettings() {
    }

    /**
     * 
     * @param useOverviewPage
     * @param documentationEnabled
     * @param mainTenantSubdomainForSingleDomainMultitenancy
     * @param budibaseUrl
     * @param legalContentChangesBySingleTenantAdminsAllowed
     * @param budibaseAuthClientId
     * @param disableVideoAppointments
     * @param calendarAppUrl
     * @param required
     * @param multitenancyWithSingleDomainEnabled
     * @param useTenantService
     * @param enableWalkthrough
     * @param calcomUrl
     * @param useConsultingTypesForAgencies
     * @param multitenancyEnabled
     */
    public ApplicationSettings(MultitenancyWithSingleDomainEnabled multitenancyWithSingleDomainEnabled, MultitenancyEnabled multitenancyEnabled, UseTenantService useTenantService, UseConsultingTypesForAgencies useConsultingTypesForAgencies, EnableWalkthrough enableWalkthrough, DisableVideoAppointments disableVideoAppointments, MainTenantSubdomainForSingleDomainMultitenancy mainTenantSubdomainForSingleDomainMultitenancy, UseOverviewPage useOverviewPage, CalcomUrl calcomUrl, BudibaseAuthClientId budibaseAuthClientId, BudibaseUrl budibaseUrl, CalendarAppUrl calendarAppUrl, LegalContentChangesBySingleTenantAdminsAllowed legalContentChangesBySingleTenantAdminsAllowed, DocumentationEnabled documentationEnabled, Object required) {
        super();
        this.multitenancyWithSingleDomainEnabled = multitenancyWithSingleDomainEnabled;
        this.multitenancyEnabled = multitenancyEnabled;
        this.useTenantService = useTenantService;
        this.useConsultingTypesForAgencies = useConsultingTypesForAgencies;
        this.enableWalkthrough = enableWalkthrough;
        this.disableVideoAppointments = disableVideoAppointments;
        this.mainTenantSubdomainForSingleDomainMultitenancy = mainTenantSubdomainForSingleDomainMultitenancy;
        this.useOverviewPage = useOverviewPage;
        this.calcomUrl = calcomUrl;
        this.budibaseAuthClientId = budibaseAuthClientId;
        this.budibaseUrl = budibaseUrl;
        this.calendarAppUrl = calendarAppUrl;
        this.legalContentChangesBySingleTenantAdminsAllowed = legalContentChangesBySingleTenantAdminsAllowed;
        this.documentationEnabled = documentationEnabled;
        this.required = required;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("multitenancyWithSingleDomainEnabled")
    public MultitenancyWithSingleDomainEnabled getMultitenancyWithSingleDomainEnabled() {
        return multitenancyWithSingleDomainEnabled;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("multitenancyWithSingleDomainEnabled")
    public void setMultitenancyWithSingleDomainEnabled(MultitenancyWithSingleDomainEnabled multitenancyWithSingleDomainEnabled) {
        this.multitenancyWithSingleDomainEnabled = multitenancyWithSingleDomainEnabled;
    }

    public ApplicationSettings withMultitenancyWithSingleDomainEnabled(MultitenancyWithSingleDomainEnabled multitenancyWithSingleDomainEnabled) {
        this.multitenancyWithSingleDomainEnabled = multitenancyWithSingleDomainEnabled;
        return this;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("multitenancyEnabled")
    public MultitenancyEnabled getMultitenancyEnabled() {
        return multitenancyEnabled;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("multitenancyEnabled")
    public void setMultitenancyEnabled(MultitenancyEnabled multitenancyEnabled) {
        this.multitenancyEnabled = multitenancyEnabled;
    }

    public ApplicationSettings withMultitenancyEnabled(MultitenancyEnabled multitenancyEnabled) {
        this.multitenancyEnabled = multitenancyEnabled;
        return this;
    }

    @JsonProperty("useTenantService")
    public UseTenantService getUseTenantService() {
        return useTenantService;
    }

    @JsonProperty("useTenantService")
    public void setUseTenantService(UseTenantService useTenantService) {
        this.useTenantService = useTenantService;
    }

    public ApplicationSettings withUseTenantService(UseTenantService useTenantService) {
        this.useTenantService = useTenantService;
        return this;
    }

    @JsonProperty("useConsultingTypesForAgencies")
    public UseConsultingTypesForAgencies getUseConsultingTypesForAgencies() {
        return useConsultingTypesForAgencies;
    }

    @JsonProperty("useConsultingTypesForAgencies")
    public void setUseConsultingTypesForAgencies(UseConsultingTypesForAgencies useConsultingTypesForAgencies) {
        this.useConsultingTypesForAgencies = useConsultingTypesForAgencies;
    }

    public ApplicationSettings withUseConsultingTypesForAgencies(UseConsultingTypesForAgencies useConsultingTypesForAgencies) {
        this.useConsultingTypesForAgencies = useConsultingTypesForAgencies;
        return this;
    }

    @JsonProperty("enableWalkthrough")
    public EnableWalkthrough getEnableWalkthrough() {
        return enableWalkthrough;
    }

    @JsonProperty("enableWalkthrough")
    public void setEnableWalkthrough(EnableWalkthrough enableWalkthrough) {
        this.enableWalkthrough = enableWalkthrough;
    }

    public ApplicationSettings withEnableWalkthrough(EnableWalkthrough enableWalkthrough) {
        this.enableWalkthrough = enableWalkthrough;
        return this;
    }

    @JsonProperty("disableVideoAppointments")
    public DisableVideoAppointments getDisableVideoAppointments() {
        return disableVideoAppointments;
    }

    @JsonProperty("disableVideoAppointments")
    public void setDisableVideoAppointments(DisableVideoAppointments disableVideoAppointments) {
        this.disableVideoAppointments = disableVideoAppointments;
    }

    public ApplicationSettings withDisableVideoAppointments(DisableVideoAppointments disableVideoAppointments) {
        this.disableVideoAppointments = disableVideoAppointments;
        return this;
    }

    @JsonProperty("mainTenantSubdomainForSingleDomainMultitenancy")
    public MainTenantSubdomainForSingleDomainMultitenancy getMainTenantSubdomainForSingleDomainMultitenancy() {
        return mainTenantSubdomainForSingleDomainMultitenancy;
    }

    @JsonProperty("mainTenantSubdomainForSingleDomainMultitenancy")
    public void setMainTenantSubdomainForSingleDomainMultitenancy(MainTenantSubdomainForSingleDomainMultitenancy mainTenantSubdomainForSingleDomainMultitenancy) {
        this.mainTenantSubdomainForSingleDomainMultitenancy = mainTenantSubdomainForSingleDomainMultitenancy;
    }

    public ApplicationSettings withMainTenantSubdomainForSingleDomainMultitenancy(MainTenantSubdomainForSingleDomainMultitenancy mainTenantSubdomainForSingleDomainMultitenancy) {
        this.mainTenantSubdomainForSingleDomainMultitenancy = mainTenantSubdomainForSingleDomainMultitenancy;
        return this;
    }

    @JsonProperty("useOverviewPage")
    public UseOverviewPage getUseOverviewPage() {
        return useOverviewPage;
    }

    @JsonProperty("useOverviewPage")
    public void setUseOverviewPage(UseOverviewPage useOverviewPage) {
        this.useOverviewPage = useOverviewPage;
    }

    public ApplicationSettings withUseOverviewPage(UseOverviewPage useOverviewPage) {
        this.useOverviewPage = useOverviewPage;
        return this;
    }

    @JsonProperty("calcomUrl")
    public CalcomUrl getCalcomUrl() {
        return calcomUrl;
    }

    @JsonProperty("calcomUrl")
    public void setCalcomUrl(CalcomUrl calcomUrl) {
        this.calcomUrl = calcomUrl;
    }

    public ApplicationSettings withCalcomUrl(CalcomUrl calcomUrl) {
        this.calcomUrl = calcomUrl;
        return this;
    }

    @JsonProperty("budibaseAuthClientId")
    public BudibaseAuthClientId getBudibaseAuthClientId() {
        return budibaseAuthClientId;
    }

    @JsonProperty("budibaseAuthClientId")
    public void setBudibaseAuthClientId(BudibaseAuthClientId budibaseAuthClientId) {
        this.budibaseAuthClientId = budibaseAuthClientId;
    }

    public ApplicationSettings withBudibaseAuthClientId(BudibaseAuthClientId budibaseAuthClientId) {
        this.budibaseAuthClientId = budibaseAuthClientId;
        return this;
    }

    @JsonProperty("budibaseUrl")
    public BudibaseUrl getBudibaseUrl() {
        return budibaseUrl;
    }

    @JsonProperty("budibaseUrl")
    public void setBudibaseUrl(BudibaseUrl budibaseUrl) {
        this.budibaseUrl = budibaseUrl;
    }

    public ApplicationSettings withBudibaseUrl(BudibaseUrl budibaseUrl) {
        this.budibaseUrl = budibaseUrl;
        return this;
    }

    @JsonProperty("calendarAppUrl")
    public CalendarAppUrl getCalendarAppUrl() {
        return calendarAppUrl;
    }

    @JsonProperty("calendarAppUrl")
    public void setCalendarAppUrl(CalendarAppUrl calendarAppUrl) {
        this.calendarAppUrl = calendarAppUrl;
    }

    public ApplicationSettings withCalendarAppUrl(CalendarAppUrl calendarAppUrl) {
        this.calendarAppUrl = calendarAppUrl;
        return this;
    }

    @JsonProperty("legalContentChangesBySingleTenantAdminsAllowed")
    public LegalContentChangesBySingleTenantAdminsAllowed getLegalContentChangesBySingleTenantAdminsAllowed() {
        return legalContentChangesBySingleTenantAdminsAllowed;
    }

    @JsonProperty("legalContentChangesBySingleTenantAdminsAllowed")
    public void setLegalContentChangesBySingleTenantAdminsAllowed(LegalContentChangesBySingleTenantAdminsAllowed legalContentChangesBySingleTenantAdminsAllowed) {
        this.legalContentChangesBySingleTenantAdminsAllowed = legalContentChangesBySingleTenantAdminsAllowed;
    }

    public ApplicationSettings withLegalContentChangesBySingleTenantAdminsAllowed(LegalContentChangesBySingleTenantAdminsAllowed legalContentChangesBySingleTenantAdminsAllowed) {
        this.legalContentChangesBySingleTenantAdminsAllowed = legalContentChangesBySingleTenantAdminsAllowed;
        return this;
    }

    @JsonProperty("documentationEnabled")
    public DocumentationEnabled getDocumentationEnabled() {
        return documentationEnabled;
    }

    @JsonProperty("documentationEnabled")
    public void setDocumentationEnabled(DocumentationEnabled documentationEnabled) {
        this.documentationEnabled = documentationEnabled;
    }

    public ApplicationSettings withDocumentationEnabled(DocumentationEnabled documentationEnabled) {
        this.documentationEnabled = documentationEnabled;
        return this;
    }

    @JsonProperty("required")
    public Object getRequired() {
        return required;
    }

    @JsonProperty("required")
    public void setRequired(Object required) {
        this.required = required;
    }

    public ApplicationSettings withRequired(Object required) {
        this.required = required;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(multitenancyWithSingleDomainEnabled).append(multitenancyEnabled).append(useTenantService).append(useConsultingTypesForAgencies).append(enableWalkthrough).append(disableVideoAppointments).append(mainTenantSubdomainForSingleDomainMultitenancy).append(useOverviewPage).append(calcomUrl).append(budibaseAuthClientId).append(budibaseUrl).append(calendarAppUrl).append(legalContentChangesBySingleTenantAdminsAllowed).append(documentationEnabled).append(required).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ApplicationSettings) == false) {
            return false;
        }
        ApplicationSettings rhs = ((ApplicationSettings) other);
        return new EqualsBuilder().append(multitenancyWithSingleDomainEnabled, rhs.multitenancyWithSingleDomainEnabled).append(multitenancyEnabled, rhs.multitenancyEnabled).append(useTenantService, rhs.useTenantService).append(useConsultingTypesForAgencies, rhs.useConsultingTypesForAgencies).append(enableWalkthrough, rhs.enableWalkthrough).append(disableVideoAppointments, rhs.disableVideoAppointments).append(mainTenantSubdomainForSingleDomainMultitenancy, rhs.mainTenantSubdomainForSingleDomainMultitenancy).append(useOverviewPage, rhs.useOverviewPage).append(calcomUrl, rhs.calcomUrl).append(budibaseAuthClientId, rhs.budibaseAuthClientId).append(budibaseUrl, rhs.budibaseUrl).append(calendarAppUrl, rhs.calendarAppUrl).append(legalContentChangesBySingleTenantAdminsAllowed, rhs.legalContentChangesBySingleTenantAdminsAllowed).append(documentationEnabled, rhs.documentationEnabled).append(required, rhs.required).isEquals();
    }

}
