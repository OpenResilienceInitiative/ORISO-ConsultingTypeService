# DPA signing email: platform sender branding

Frank requested consistent first-party branding and real received-mail evidence.
The agent authored and locally tested this change. PreDev delivery is a separate
coordinated test; no production or shared branch was changed.

The signing invitation is legal correspondence from the platform to the named
recipient organisation. Its global SMTP settings and settings-admin permission
already establish that owner. `tenantName` remains the contract addressee and
`email.brand.org-name` remains the legal sender; an ambient target tenant header
must not choose the sender logo or product name.

`mainTenantSubdomainForSingleDomainMultitenancy` from application settings selects
the platform's existing public tenant representation. The existing restricted
subdomain endpoint is called with explicit override `0`, which TenantService
interprets as platform scope without a recipient override. Zero is **not** assumed
to have a database row. The returned DTO supplies the actual logo asset tenant ID.
Missing settings/lookup use configured platform fallbacks; no ID is invented.

Stored inline/legacy base64 logos resolve to
`<configured App origin>/service/tenant/public/branding/<returned id>/logo`.
That endpoint belongs to TenantService and must be deployed and verified separately.
Existing absolute logos are included only on the same HTTPS origin with no userinfo,
query or fragment. Other URLs are omitted. No image is fetched or proxied here.
The whole logo cell is omitted when no eligible image exists, avoiding `src=""`.

The HTML template was copied from the Frontend emails-v2 generated plain/de-sie
`avv-unterschrift.html`, bringing its optional `logoCell` and border-collapse fixes.
No copy was redesigned locally. The effective platform primary color is used when
valid; white-label button contrast below 4.5 uses the existing dark fallback.
SMTP transport colors do not select product branding.

Validation: Java 21 targeted DpaSigningEmailServiceTest,
JakartaDpaMailTransportTest, TenantServicePlatformBrandTest; package, Spotless and
Checkstyle's configured `checkstyle:check@validate` (google_checks_light.xml).
The generic `checkstyle:check` command instead selects default Sun rules and
reports broad generated/source violations; it is not the configured project gate. Tests cover no logo, foreign/same-origin URLs, inline and legacy
images, missing IDs, target-tenant isolation, lookup fallback and button contrast.
No PreDev screenshot or received DPA email is claimed by this local commit.
