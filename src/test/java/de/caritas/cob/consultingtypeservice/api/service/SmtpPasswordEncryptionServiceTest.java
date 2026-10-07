package de.caritas.cob.consultingtypeservice.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class SmtpPasswordEncryptionServiceTest {

  private static final String TEST_SECRET = "test-smtp-encryption-secret";

  @Test
  void encrypt_Should_RejectPassword_When_SecretNotConfigured() {
    var service = new SmtpPasswordEncryptionService("");

    assertThatThrownBy(() -> service.encrypt("plain-pass"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("settings.smtp.password.encryption.secret");
  }

  @Test
  void decrypt_Should_RejectStoredPassword_When_SecretNotConfigured() {
    var service = new SmtpPasswordEncryptionService("");

    assertThatThrownBy(() -> service.decrypt("legacy-plaintext"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("settings.smtp.password.encryption.secret");
  }

  @Test
  void encrypt_Should_ReturnEncryptedValue_When_SecretConfigured() {
    var service = new SmtpPasswordEncryptionService(TEST_SECRET);

    String encrypted = service.encrypt("plain-pass");

    assertThat(encrypted).startsWith(SmtpPasswordEncryptionService.ENCRYPTED_PREFIX);
    assertThat(encrypted).isNotEqualTo("plain-pass");
  }

  @Test
  void decrypt_Should_RoundTripEncryptedValue() {
    var service = new SmtpPasswordEncryptionService(TEST_SECRET);

    String encrypted = service.encrypt("plain-pass");

    assertThat(service.decrypt(encrypted)).isEqualTo("plain-pass");
  }

  @Test
  void decrypt_Should_ReadLegacyPlaintext_When_SecretIsConfiguredForMigration() {
    var service = new SmtpPasswordEncryptionService(TEST_SECRET);

    assertThat(service.decrypt("legacy-plaintext")).isEqualTo("legacy-plaintext");
    assertThat(service.encrypt("legacy-plaintext"))
        .startsWith(SmtpPasswordEncryptionService.ENCRYPTED_PREFIX);
  }

  @Test
  void encrypt_Should_ReturnEmptyString_When_PasswordIsEmpty() {
    var service = new SmtpPasswordEncryptionService(TEST_SECRET);

    assertThat(service.encrypt("")).isEmpty();
  }

  @Test
  void encrypt_Should_NotDoubleEncrypt_When_ValueAlreadyEncrypted() {
    var service = new SmtpPasswordEncryptionService(TEST_SECRET);

    String encrypted = service.encrypt("plain-pass");

    assertThat(service.encrypt(encrypted)).isEqualTo(encrypted);
  }
}
