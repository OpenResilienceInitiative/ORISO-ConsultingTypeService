package de.caritas.cob.consultingtypeservice.api.controller;

import de.caritas.cob.consultingtypeservice.api.model.DpaSigningEmailPreviewResponse;
import de.caritas.cob.consultingtypeservice.api.model.DpaSigningEmailRequest;
import de.caritas.cob.consultingtypeservice.api.model.DpaSigningEmailResponse;
import de.caritas.cob.consultingtypeservice.api.model.DpaSigningEmailResponse.StatusEnum;
import de.caritas.cob.consultingtypeservice.api.service.DpaMailSendReceipt;
import de.caritas.cob.consultingtypeservice.api.service.DpaSigningEmailService;
import de.caritas.cob.consultingtypeservice.api.service.DpaSigningEmailService.DpaSigningEmailCommand;
import de.caritas.cob.consultingtypeservice.api.service.DpaSigningEmailService.DpaSigningEmailPreview;
import de.caritas.cob.consultingtypeservice.generated.api.controller.DpaSigningEmailControllerApi;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

/** Sends and previews DPA signing emails (TEN-INV-U5). */
@RestController
@RequiredArgsConstructor
public class DpaSigningEmailController implements DpaSigningEmailControllerApi {

  private final @NonNull DpaSigningEmailService dpaSigningEmailService;

  @Override
  @PreAuthorize("hasAuthority('AUTHORIZATION_PATCH_APPLICATION_SETTINGS')")
  public ResponseEntity<DpaSigningEmailResponse> sendDpaSigningEmail(
      @Valid DpaSigningEmailRequest request) {
    DpaMailSendReceipt receipt = dpaSigningEmailService.send(toCommand(request));
    return ResponseEntity.ok(
        new DpaSigningEmailResponse(
            StatusEnum.SENT,
            receipt.getRecipientEmail(),
            receipt.getSentAt().atOffset(ZoneOffset.UTC)));
  }

  /** Renders the exact DPA signing mail without accessing SMTP settings or sending it. */
  @Override
  @PreAuthorize("hasAuthority('AUTHORIZATION_PATCH_APPLICATION_SETTINGS')")
  public ResponseEntity<DpaSigningEmailPreviewResponse> previewDpaSigningEmail(
      @Valid DpaSigningEmailRequest request) {
    DpaSigningEmailPreview preview = dpaSigningEmailService.preview(toCommand(request));
    return ResponseEntity.ok(
        new DpaSigningEmailPreviewResponse(preview.getSubject(), preview.getHtml()));
  }

  private static DpaSigningEmailCommand toCommand(DpaSigningEmailRequest request) {
    return new DpaSigningEmailCommand(
        request.getRecipientEmail(),
        request.getTenantName(),
        request.getSignLink(),
        LocalDateTime.parse(request.getExpiresAt()));
  }
}
