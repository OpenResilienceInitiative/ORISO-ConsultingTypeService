package de.caritas.cob.consultingtypeservice.api.service;

import java.util.Locale;

/** Slugs for topics the platform admin creates: derived from the German name. */
final class TopicSlugs {

  /** Internal identifier and slug share the value; the identifier allows 50 characters. */
  static final int MAX_LENGTH = 50;

  private TopicSlugs() {}

  static String slugify(String germanName) {
    var slug =
        germanName
            .toLowerCase(Locale.GERMAN)
            .replace("ä", "ae")
            .replace("ö", "oe")
            .replace("ü", "ue")
            .replace("ß", "ss")
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("^-+|-+$", "");
    slug = trim(slug, MAX_LENGTH);
    return slug.isEmpty() ? "topic" : slug;
  }

  static String withSuffix(String base, int suffix) {
    var tail = "-" + suffix;
    return trim(base, MAX_LENGTH - tail.length()) + tail;
  }

  private static String trim(String slug, int maxLength) {
    var trimmed = slug.length() > maxLength ? slug.substring(0, maxLength) : slug;
    return trimmed.replaceAll("-+$", "");
  }
}
