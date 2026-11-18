package de.caritas.cob.consultingtypeservice;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mariadb.MariaDBContainer;

/**
 * Changeset 0015 decodes the HTML entities that older Admin saves stored in topic and category
 * texts (ORISO-ConsultingTypeService#207), against a real MariaDB.
 *
 * <p>The schema is migrated first, then rows as the old sanitizer wrote them are inserted and the
 * changeset is run once more. Local run: {@code ./mvnw test
 * -Dtest=TopicTextHtmlEntitiesMigrationIT}
 */
@Testcontainers
class TopicTextHtmlEntitiesMigrationIT {

  private static final String MASTER_CHANGELOG = "db/changelog/consultingtypeservice-master.xml";
  private static final String CHANGESET = "0015_topic_text_html_entities";

  @Container
  private static final MariaDBContainer MARIADB =
      new MariaDBContainer("mariadb:10.11.18")
          .withDatabaseName("consultingtypeservice")
          .withUsername("test")
          .withPassword("test");

  private static final ObjectMapper JSON = new ObjectMapper();

  @BeforeAll
  static void migrateEscapedRows() throws Exception {
    migrate();
    try (var connection = connect();
        var statement = connection.createStatement()) {
      statement.executeUpdate(
          "INSERT INTO topic_group (id, name, sort_order, customized) VALUES "
              + "(1, '{\"de\": \"Gesundheit &amp; Sucht\", \"en\": \"Health &amp; Addiction\"}', 0, 1),"
              + "(2, '{\"de\": \"Alter\", \"en\": \"Ageing\"}', 1, 0),"
              + "(3, '{\"de\": \"Sie sagte &#34;Hallo&#34; &#43; &#61; &#64; &#96;\"}', 2, 1),"
              + "(4, '{\"de\": \"&lt;img src&#61;x&gt; &amp;lt;b&amp;gt;\"}', 3, 1)");
      statement.executeUpdate(
          "INSERT INTO topic (id, tenant_id, name, description, status, create_date,"
              + " internal_identifier, send_next_step_message, titles_short, titles_long,"
              + " titles_welcome, titles_dropdown, slug, customized) VALUES "
              + "(1, 1, '{\"de\": \"Jungen &amp; Männer\", \"en\": \"Boys and Men&#39;s Support\"}',"
              + " '{\"de\": \"Beratung f&#252;r &#34;Jungen&#34;\"}', 'ACTIVE', UTC_TIMESTAMP,"
              + " 'men-boys', 0, '{\"de\": \"Jungen &amp; Männer\"}', 'Jungen &amp; Männer',"
              + " 'Willkommen &amp; hallo', 'Sie sagte &#34;Hallo&#34;', 'men-boys', 1)");
      statement.executeUpdate("DELETE FROM DATABASECHANGELOG WHERE ID = '" + CHANGESET + "'");
    }
    migrate();
  }

  @Test
  void categoryNames_areDecodedInEveryLanguage() throws Exception {
    assertThat(groupName(1))
        .containsEntry("de", "Gesundheit & Sucht")
        .containsEntry("en", "Health & Addiction");
  }

  @Test
  void quotesInsideJsonText_stayValidJson() throws Exception {
    assertThat(groupName(3)).containsEntry("de", "Sie sagte \"Hallo\" + = @ `");
  }

  @Test
  void encodedAngleBrackets_areDropped_andTypedEntitiesStayText() throws Exception {
    assertThat(groupName(4)).containsEntry("de", "img src=x &lt;b&gt;");
  }

  @Test
  void untouchedRows_stayAsTheyAre() throws Exception {
    assertThat(groupName(2))
        .containsExactlyInAnyOrderEntriesOf(Map.of("de", "Alter", "en", "Ageing"));
  }

  @Test
  void topicTexts_areDecodedInJsonAndPlainColumns() throws Exception {
    try (var connection = connect();
        var statement = connection.createStatement();
        var row =
            statement.executeQuery(
                "SELECT name, description, titles_short, titles_long, titles_welcome,"
                    + " titles_dropdown FROM topic WHERE id = 1")) {
      row.next();
      assertThat(map(row.getString("name")))
          .containsEntry("de", "Jungen & Männer")
          .containsEntry("en", "Boys and Men's Support");
      // Only the entities the sanitizer wrote are decoded; others it never produced stay.
      assertThat(map(row.getString("description")))
          .containsEntry("de", "Beratung f&#252;r \"Jungen\"");
      assertThat(map(row.getString("titles_short"))).containsEntry("de", "Jungen & Männer");
      assertThat(row.getString("titles_long")).isEqualTo("Jungen & Männer");
      assertThat(row.getString("titles_welcome")).isEqualTo("Willkommen & hallo");
      assertThat(row.getString("titles_dropdown")).isEqualTo("Sie sagte \"Hallo\"");
    }
  }

  private static Map<String, String> groupName(long id) throws Exception {
    try (var connection = connect();
        var statement = connection.prepareStatement("SELECT name FROM topic_group WHERE id = ?")) {
      statement.setLong(1, id);
      try (var row = statement.executeQuery()) {
        row.next();
        return map(row.getString("name"));
      }
    }
  }

  private static Map<String, String> map(String json) throws Exception {
    return JSON.readValue(json, new TypeReference<>() {});
  }

  private static Connection connect() throws SQLException {
    return DriverManager.getConnection(
        MARIADB.getJdbcUrl(), MARIADB.getUsername(), MARIADB.getPassword());
  }

  private static void migrate() throws Exception {
    try (var connection = connect()) {
      var database =
          DatabaseFactory.getInstance()
              .findCorrectDatabaseImplementation(new JdbcConnection(connection));
      try (var liquibase =
          new Liquibase(MASTER_CHANGELOG, new ClassLoaderResourceAccessor(), database)) {
        liquibase.update(new Contexts("seed"), new LabelExpression());
      }
    }
  }
}
