package de.caritas.cob.consultingtypeservice.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsEntity;
import de.caritas.cob.consultingtypeservice.api.repository.ApplicationSettingsRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class DefaultApplicationSettingsInitializerTest {

  @Mock ApplicationSettingsRepository applicationSettingsRepository;

  @InjectMocks DefaultApplicationSettingsInitializer initializer;

  @Test
  void init_Should_SeedWalkthroughOn_When_PropertyIsTrue() {
    ApplicationSettingsEntity seeded = seedWithWalkthroughProperty(true);

    assertThat(seeded.getEnableWalkthrough().getValue()).isTrue();
    assertThat(seeded.getEnableWalkthrough().getReadOnly()).isFalse();
  }

  @Test
  void init_Should_SeedWalkthroughOff_When_PropertyIsFalse() {
    ApplicationSettingsEntity seeded = seedWithWalkthroughProperty(false);

    assertThat(seeded.getEnableWalkthrough().getValue()).isFalse();
    assertThat(seeded.getEnableWalkthrough().getReadOnly()).isFalse();
  }

  @Test
  void init_Should_NotSeed_When_SettingsAlreadyExist() {
    when(applicationSettingsRepository.findAll())
        .thenReturn(List.of(new ApplicationSettingsEntity()));

    ReflectionTestUtils.invokeMethod(initializer, "init");

    verify(applicationSettingsRepository, never()).save(any(ApplicationSettingsEntity.class));
  }

  private ApplicationSettingsEntity seedWithWalkthroughProperty(boolean walkthroughEnabled) {
    ReflectionTestUtils.setField(initializer, "walkthroughEnabled", walkthroughEnabled);
    when(applicationSettingsRepository.findAll()).thenReturn(List.of());

    ReflectionTestUtils.invokeMethod(initializer, "init");

    ArgumentCaptor<ApplicationSettingsEntity> captor =
        ArgumentCaptor.forClass(ApplicationSettingsEntity.class);
    verify(applicationSettingsRepository).save(captor.capture());
    return captor.getValue();
  }
}
