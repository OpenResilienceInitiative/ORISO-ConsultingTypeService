package de.caritas.cob.consultingtypeservice.api.admin.controller;

import de.caritas.cob.consultingtypeservice.api.auth.AuthorisationService;
import de.caritas.cob.consultingtypeservice.api.service.TopicFeatureAuthorisationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class TopicFeatureAssertionAspect {

  private final @NonNull TopicFeatureAuthorisationService topicFeatureAuthorisationService;
  private final @NonNull AuthorisationService authorisationService;

  @Before(
      "execution(* de.caritas.cob.consultingtypeservice.api.controller.TopicAdminController.*(..)))")
  public void beforeQueryAspect() {
    if (authorisationService.isSuperAdmin()) {
      // Every installation ships default topics; the platform admin manages them whether or not
      // the tenant has the topic feature switched on for the registration.
      return;
    }
    log.debug("Asserting topic feature is enabled");
    topicFeatureAuthorisationService.assertTopicFeatureIsEnabled();
  }
}
