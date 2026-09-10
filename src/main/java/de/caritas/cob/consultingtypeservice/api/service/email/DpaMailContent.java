package de.caritas.cob.consultingtypeservice.api.service.email;

import lombok.Value;

/**
 * The per-mail facts of the DPA signing mail. Everything identical for every recipient (platform
 * name, operator address, legal links, brand colours) belongs to the renderer's configuration, not
 * here.
 *
 * <p>{@code providedAt} is optional: the publication date is owned by TenantService and does not
 * travel with the send command yet, and an empty value would render a labelled row with nothing in
 * it — so a blank value drops that row instead of shipping a visible gap.
 *
 * <p>A Lombok {@code @Value} class rather than a record: this module's checkstyle grammar cannot
 * parse records, which is why nothing else here is one either.
 */
@Value
public class DpaMailContent {
  String tenantName;
  String providedAt;
  String expiresAt;
  String dpaUrl;
}
