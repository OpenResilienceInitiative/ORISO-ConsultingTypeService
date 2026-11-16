package de.caritas.cob.consultingtypeservice.api.topicdefaults;

import java.io.IOException;
import java.time.Duration;
import java.util.regex.Pattern;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Serves the shipped default topic and category icons, so the registration and the Admin show the
 * same picture through one address. Public, like the topic list itself.
 */
@RestController
public class DefaultTopicIconController {

  private static final Pattern FILE_NAME = Pattern.compile("[a-z0-9][a-z0-9-]*\\.png");

  @GetMapping("/topic/public/icons/{fileName:.+}")
  public ResponseEntity<byte[]> getDefaultIcon(@PathVariable String fileName) throws IOException {
    if (!FILE_NAME.matcher(fileName).matches()) {
      return ResponseEntity.notFound().build();
    }
    var icon = new ClassPathResource(DefaultTopicCatalogue.ICON_FOLDER + fileName);
    if (!icon.exists()) {
      return ResponseEntity.notFound().build();
    }
    try (var in = icon.getInputStream()) {
      return ResponseEntity.ok()
          .contentType(MediaType.IMAGE_PNG)
          .cacheControl(CacheControl.maxAge(Duration.ofDays(1)).cachePublic())
          .body(in.readAllBytes());
    }
  }
}
