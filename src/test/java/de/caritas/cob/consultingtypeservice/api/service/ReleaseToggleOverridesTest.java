package de.caritas.cob.consultingtypeservice.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/** Frontend#1979: dev switches enableNewNotifications on declaratively, prod stays untouched. */
class ReleaseToggleOverridesTest {

  @Test
  void parsesKeyValuePairs() {
    assertThat(
            new ReleaseToggleOverrides("enableNewNotifications=true, enableMagicLinksLogin=false")
                .values())
        .containsExactlyInAnyOrderEntriesOf(
            Map.of("enableNewNotifications", true, "enableMagicLinksLogin", false));
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"  ", ","})
  void hasNoEffectWhenUnset(String raw) {
    assertThat(new ReleaseToggleOverrides(raw).values()).isEmpty();
  }

  @Test
  void ignoresMalformedEntriesAndKeepsValidOnes() {
    assertThat(
            new ReleaseToggleOverrides(
                    "enableNewNotifications=true,noValue,=true,bad key=true,x=yes,a.b=true")
                .values())
        .containsExactlyEntriesOf(Map.of("enableNewNotifications", true));
  }

  @Test
  void overridesWinOverStoredTogglesAndLeaveOthersAlone() {
    Map<String, Object> stored = new LinkedHashMap<>();
    stored.put("enableNewNotifications", false);
    stored.put("featureToggleTenantCreationEnabled", true);

    new ReleaseToggleOverrides("enableNewNotifications=true").applyTo(stored);

    assertThat(stored)
        .containsEntry("enableNewNotifications", true)
        .containsEntry("featureToggleTenantCreationEnabled", true);
  }
}
