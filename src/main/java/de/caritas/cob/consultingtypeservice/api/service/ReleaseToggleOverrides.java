package de.caritas.cob.consultingtypeservice.api.service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Per-environment release toggle values served on top of the ones stored in MongoDB.
 *
 * <p>Release toggles live in the {@code application_settings} document and no API changes them. An
 * installation can now pin a toggle declaratively ({@code RELEASE_TOGGLE_OVERRIDES=
 * enableNewNotifications=true}) without touching the database. The override is applied when the
 * settings are served and never written back, so removing it restores the stored value. Unset or
 * blank means no override; malformed entries are ignored with a warning (Frontend#1979).
 */
@Slf4j
@Component
public final class ReleaseToggleOverrides {

  private static final Pattern KEY = Pattern.compile("[A-Za-z][A-Za-z0-9]*");

  private final Map<String, Boolean> values;

  /**
   * Parses the configured overrides.
   *
   * @param raw comma-separated {@code key=true|false} pairs; blank for none
   */
  public ReleaseToggleOverrides(@Value("${release.toggle.overrides:}") final String raw) {
    this.values = Collections.unmodifiableMap(parse(raw));
    if (!values.isEmpty()) {
      log.info("Serving release toggle overrides: {}", values);
    }
  }

  /**
   * No overrides: the stored release toggles are served unchanged.
   *
   * @return an instance without overrides
   */
  public static ReleaseToggleOverrides none() {
    return new ReleaseToggleOverrides("");
  }

  /**
   * The parsed overrides.
   *
   * @return an unmodifiable map of toggle name to value
   */
  public Map<String, Boolean> values() {
    return values;
  }

  /**
   * Puts every override into the given (served) release toggle map.
   *
   * @param releaseToggles the map that is served; the stored entity is never passed here
   */
  public void applyTo(final Map<String, Object> releaseToggles) {
    releaseToggles.putAll(values);
  }

  private static Map<String, Boolean> parse(final String raw) {
    Map<String, Boolean> parsed = new LinkedHashMap<>();
    if (raw == null || raw.isBlank()) {
      return parsed;
    }
    for (String entry : raw.split(",")) {
      if (entry.isBlank()) {
        continue;
      }
      String[] pair = entry.split("=", -1);
      String key = pair[0].trim();
      String value = pair.length == 2 ? pair[1].trim() : "";
      if (pair.length != 2
          || !KEY.matcher(key).matches()
          || !("true".equals(value) || "false".equals(value))) {
        log.warn("Ignoring malformed release toggle override entry");
        continue;
      }
      parsed.put(key, Boolean.valueOf(value));
    }
    return parsed;
  }
}
