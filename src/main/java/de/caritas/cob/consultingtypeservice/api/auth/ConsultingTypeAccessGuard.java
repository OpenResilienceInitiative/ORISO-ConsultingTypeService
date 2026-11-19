package de.caritas.cob.consultingtypeservice.api.auth;

import de.caritas.cob.consultingtypeservice.api.exception.httpresponses.NotFoundException;
import de.caritas.cob.consultingtypeservice.api.exception.httpresponses.ServiceUnavailableException;
import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypePatchDTO;
import de.caritas.cob.consultingtypeservice.api.service.AppearanceLock;
import de.caritas.cob.consultingtypeservice.schemas.model.ConsultingType;
import java.util.Objects;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

/**
 * Who may write which consulting type (ORISO-ConsultingTypeService#190). Holding the authority to
 * patch or create is not enough: every Träger admin holds {@code tenant-admin}, like the platform
 * admin, and only the tenant in the token tells them apart (0 = platform). So:
 *
 * <ul>
 *   <li>the platform admin may write every consulting type, and is the only one to create them;
 *   <li>any other caller may write only the consulting types of the organisation in its token, and
 *       a consulting type of another organisation is answered exactly like one that does not exist;
 *   <li>and while the platform admin has locked the organisation's editing rights, not change the
 *       form of address (see {@link AppearanceLock}).
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class ConsultingTypeAccessGuard {

  private final @NonNull AuthorisationService authorisationService;
  private final @NonNull AppearanceLock appearanceLock;

  /**
   * Creating a consulting type is the platform admin's act: TenantService does it at tenant
   * creation with the platform admin's token. An organisation admin has no use for it, and a second
   * consulting type for a tenant breaks the lookup by tenant, with no route to repair it.
   *
   * @throws AccessDeniedException when the caller is not the platform admin
   */
  public void assertMayCreate() {
    if (!authorisationService.isSuperAdmin()) {
      throw new AccessDeniedException("Only the platform admin may create a consulting type");
    }
  }

  /**
   * @param consultingTypeId the id the caller asked for
   * @param existing the stored consulting type with that id
   * @throws NotFoundException when it belongs to another organisation, or the token names none
   */
  public void assertMayPatch(Integer consultingTypeId, ConsultingType existing) {
    if (authorisationService.isSuperAdmin()) {
      return;
    }
    Long ownTenant = authorisationService.findTenantIdInAccessToken().orElse(null);
    if (ownTenant == null
        || existing.getTenantId() == null
        || existing.getTenantId().longValue() != ownTenant) {
      // The same answer as for an id that does not exist, so the id reveals nothing.
      throw new NotFoundException(
          String.format("Consulting type with id %s not found.", consultingTypeId));
    }
  }

  /**
   * Called after {@link #assertMayPatch}, so the consulting type is the caller's own. Asks
   * TenantService only when the patch changes the form of address, as the tenant route does.
   *
   * @throws AccessDeniedException when the organisation is locked and the patch changes the form of
   *     address
   * @throws ServiceUnavailableException when the lock could not be read
   */
  public void assertEditingNotLocked(ConsultingType existing, ConsultingTypePatchDTO patch) {
    if (authorisationService.isSuperAdmin() || !formOfAddressChanged(patch, existing)) {
      return;
    }
    if (appearanceLock.isLocked(existing.getTenantId().longValue())) {
      throw new AccessDeniedException("The organisation's editing rights are locked");
    }
  }

  /**
   * A patch replaces the stored form of address with what it carries, so a missing value clears it
   * and counts as a change; the same value as stored does not.
   */
  private static boolean formOfAddressChanged(ConsultingTypePatchDTO patch, ConsultingType stored) {
    return !Objects.equals(patch.getLanguageFormal(), stored.getLanguageFormal());
  }
}
