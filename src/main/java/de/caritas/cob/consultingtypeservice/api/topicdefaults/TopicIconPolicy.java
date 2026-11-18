package de.caritas.cob.consultingtypeservice.api.topicdefaults;

import de.caritas.cob.consultingtypeservice.api.exception.httpresponses.BadRequestException;
import java.util.regex.Pattern;

/** Which pictures a topic or category may carry. */
public final class TopicIconPolicy {

  /** Base64 data URLs only: they render through an img tag, where SVG cannot run scripts. */
  private static final Pattern ALLOWED_ICON =
      Pattern.compile(
          "^(/service/topic/public/icons/[a-z0-9-]+\\.png"
              + "|data:image/(png|jpeg|webp|svg\\+xml);base64,[A-Za-z0-9+/=]+)$");

  private static final int MAX_ICON_LENGTH = 700_000;

  private TopicIconPolicy() {}

  /** Throws 400 unless the icon is a shipped icon or an image data URL of at most 512 KB. */
  public static void assertAllowed(String icon) {
    if (icon.length() > MAX_ICON_LENGTH || !ALLOWED_ICON.matcher(icon).matches()) {
      throw new BadRequestException(
          "Icon must be a shipped icon or a PNG, JPEG, WebP or SVG data URL up to 512 KB");
    }
  }
}
