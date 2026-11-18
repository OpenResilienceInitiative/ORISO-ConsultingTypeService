package de.caritas.cob.consultingtypeservice.api.service;

import de.caritas.cob.consultingtypeservice.api.exception.httpresponses.BadRequestException;
import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsPatchDTO;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Bounds and stored form of the platform-wide chat message settings. The stored values are strings
 * because GET /settings serves every setting as a {value, readOnly} string pair. The defaults
 * describe the behaviour the Frontend had before these settings existed.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ChatMessageSettings {

  static final String DEFAULT_MAX_WIDTH_DESKTOP = "770";
  static final String DEFAULT_COLLAPSE_THRESHOLD = "300";
  private static final int MIN_MAX_WIDTH = 320;
  private static final int MAX_MAX_WIDTH = 1600;
  private static final int COLLAPSE_OFF = 0;
  private static final int MIN_COLLAPSE_THRESHOLD = 50;
  private static final int MAX_COLLAPSE_THRESHOLD = 100_000;

  /** Enum order, so that the same selection always stores the same string. */
  static String toStoredDisabledFormats(
      Collection<ApplicationSettingsPatchDTO.ChatComposerDisabledFormatsEnum> formats) {
    return formats.stream()
        .filter(Objects::nonNull)
        .distinct()
        .sorted()
        .map(ApplicationSettingsPatchDTO.ChatComposerDisabledFormatsEnum::getValue)
        .collect(Collectors.joining(","));
  }

  static String toStoredMaxWidth(int maxWidth) {
    if (maxWidth < MIN_MAX_WIDTH || maxWidth > MAX_MAX_WIDTH) {
      throw new BadRequestException(
          "chatMessageMaxWidthDesktop must be between " + MIN_MAX_WIDTH + " and " + MAX_MAX_WIDTH);
    }
    return Integer.toString(maxWidth);
  }

  static String toStoredCollapseThreshold(int threshold) {
    boolean inRange = threshold >= MIN_COLLAPSE_THRESHOLD && threshold <= MAX_COLLAPSE_THRESHOLD;
    if (threshold != COLLAPSE_OFF && !inRange) {
      throw new BadRequestException(
          "chatMessageCollapseThreshold must be 0 or between "
              + MIN_COLLAPSE_THRESHOLD
              + " and "
              + MAX_COLLAPSE_THRESHOLD);
    }
    return Integer.toString(threshold);
  }
}
