package de.caritas.cob.consultingtypeservice.api.controller;

import de.caritas.cob.consultingtypeservice.api.service.DpaMailSendReceipt;
import de.caritas.cob.consultingtypeservice.api.service.DpaSigningEmailService;
import de.caritas.cob.consultingtypeservice.api.service.DpaSigningEmailService.DpaSigningEmailCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/settingsadmin/dpa-signing-emails")
public class DpaSigningEmailController {

  private final @NonNull DpaSigningEmailService dpaSigningEmailService;

  @PostMapping
  @PreAuthorize("hasAuthority('AUTHORIZATION_PATCH_APPLICATION_SETTINGS')")
  public ResponseEntity<DpaSigningEmailResponse> send(
      @Valid @RequestBody DpaSigningEmailRequest request) {
    DpaMailSendReceipt receipt =
        dpaSigningEmailService.send(
            new DpaSigningEmailCommand(
                request.recipientEmail, request.tenantName, request.signLink, request.expiresAt));
    return ResponseEntity.ok(
        new DpaSigningEmailResponse(
            "SENT", receipt.getRecipientEmail(), receipt.getSentAt().toString()));
  }

  @Data
  public static class DpaSigningEmailRequest {
    @NotBlank @Email private String recipientEmail;
    @NotBlank private String tenantName;
    @NotBlank private String signLink;
    @NotNull private LocalDateTime expiresAt;
  }

  /** Returned only after the SMTP server accepted the message - callers may set SENT on it. */
  @lombok.Value
  public static class DpaSigningEmailResponse {
    String status;
    String recipientEmail;
    String sentAt;
  }
}
