package de.caritas.cob.consultingtypeservice.api.topicdefaults;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * The Caritas default topics and categories shipped with every ORISO installation: texts in all
 * platform languages and the suggested icon of each topic. Owned by Deutscher Caritasverband e.V.
 * (ORISO-Frontend#973); a platform admin overrides them in the Admin's Settings.
 */
@Component
public class DefaultTopicCatalogue {

  static final String RESOURCE = "default-topics/caritas-default-topics.json";
  static final String ICON_FOLDER = "default-topics/icons/";

  /** Titles of a default topic; {@code short} and {@code long} per language. */
  @Data
  @NoArgsConstructor
  public static class Titles {
    private Map<String, String> shortTitles;
    private Map<String, String> longTitles;
    private String welcome;
    private String registrationDropdown;

    @com.fasterxml.jackson.annotation.JsonProperty("short")
    public void setShort(Map<String, String> value) {
      this.shortTitles = value;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("long")
    public void setLong(Map<String, String> value) {
      this.longTitles = value;
    }
  }

  /** One default topic. */
  @Data
  @NoArgsConstructor
  public static class DefaultTopic {
    private String slug;
    private String internalIdentifier;
    private String icon;
    private Map<String, String> name;
    private Map<String, String> description;
    private Titles titles;
  }

  /** A topic inside a category, with the icon the category shows for it. */
  @Data
  @NoArgsConstructor
  public static class Placement {
    private String slug;
    private String icon;
  }

  /** One default category with its topics in display order. */
  @Data
  @NoArgsConstructor
  public static class DefaultGroup {
    private String key;
    private Map<String, String> name;
    private String icon;
    private List<Placement> topics;
  }

  /** The whole shipped catalogue. */
  @Data
  @NoArgsConstructor
  public static class Catalogue {
    private int version;
    private String source;
    private List<DefaultTopic> topics;
    private List<DefaultGroup> groups;
  }

  private final Catalogue catalogue;

  public DefaultTopicCatalogue() {
    var mapper =
        new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    try (InputStream in = new ClassPathResource(RESOURCE).getInputStream()) {
      this.catalogue = mapper.readValue(in, Catalogue.class);
    } catch (IOException e) {
      throw new UncheckedIOException("Default topic catalogue missing: " + RESOURCE, e);
    }
  }

  public List<DefaultTopic> topics() {
    return catalogue.getTopics();
  }

  public List<DefaultGroup> groups() {
    return catalogue.getGroups();
  }

  public Optional<DefaultTopic> findBySlug(String slug) {
    return catalogue.getTopics().stream().filter(topic -> topic.getSlug().equals(slug)).findFirst();
  }
}
