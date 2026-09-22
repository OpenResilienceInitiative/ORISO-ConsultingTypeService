package de.caritas.cob.consultingtypeservice.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/**
 * Team rule: a deployed service never invents a URL. A missing origin must fail startup, and the
 * production host must not appear at all. Only the local and testing profiles may default.
 */
class NoHardcodedUrlFallbacksTest {

  private static final Path RESOURCES = Path.of("src/main/resources");
  private static final Pattern URL_DEFAULT = Pattern.compile("\\$\\{[^}:]+:\\s*https?://");
  private static final Pattern ORISO_HOST = Pattern.compile("oriso\\.org");

  @Test
  void deployedProfiles_declareNoUrlDefaults_andNeverNameTheProductionHost() throws IOException {
    List<String> violations;
    try (Stream<Path> files = Files.list(RESOURCES)) {
      violations =
          files
              .filter(p -> p.getFileName().toString().matches("application(-[a-z]+)?\\.properties"))
              .filter(p -> !p.getFileName().toString().matches("application-(local|testing)\\..*"))
              .flatMap(NoHardcodedUrlFallbacksTest::violationsIn)
              .toList();
    }
    assertThat(violations).isEmpty();
  }

  private static Stream<String> violationsIn(Path file) {
    try {
      List<String> lines = Files.readAllLines(file);
      return java.util.stream.IntStream.range(0, lines.size())
          .filter(
              i ->
                  URL_DEFAULT.matcher(lines.get(i)).find()
                      || ORISO_HOST.matcher(lines.get(i)).find())
          .mapToObj(i -> file.getFileName() + ":" + (i + 1) + " " + lines.get(i));
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }
}
