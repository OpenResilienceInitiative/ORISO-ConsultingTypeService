# Reviewed removal of the DPA signing mail endpoints

Since OpenResilienceInitiative/ORISO-UserService#1238 the contract (DPA) signing
mail is rendered and sent by UserService. The two ConsultingTypeService
endpoints below have no caller left in UserService, Admin, Frontend,
TenantService, Helm or E2E, so they are removed without a deprecation period.
These exact removals are accepted once; any other provider change still fails
the compatibility gate.

POST /settingsadmin/dpa-signing-emails api path removed without deprecation
POST /settingsadmin/dpa-signing-emails/preview api path removed without deprecation
