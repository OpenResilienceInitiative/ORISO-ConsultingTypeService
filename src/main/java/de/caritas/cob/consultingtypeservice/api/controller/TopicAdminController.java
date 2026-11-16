package de.caritas.cob.consultingtypeservice.api.controller;

import de.caritas.cob.consultingtypeservice.api.auth.AuthenticatedUser;
import de.caritas.cob.consultingtypeservice.api.model.TopicCatalogueDTO;
import de.caritas.cob.consultingtypeservice.api.model.TopicDTO;
import de.caritas.cob.consultingtypeservice.api.model.TopicMultilingualDTO;
import de.caritas.cob.consultingtypeservice.api.service.TopicCatalogueService;
import de.caritas.cob.consultingtypeservice.api.service.TopicFeatureAuthorisationService;
import de.caritas.cob.consultingtypeservice.api.service.TopicServiceFacade;
import de.caritas.cob.consultingtypeservice.generated.api.controller.TopicadminApi;
import io.swagger.annotations.Api;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Api(tags = "topic-controller")
@Slf4j
public class TopicAdminController implements TopicadminApi {

  private final @NonNull TopicServiceFacade topicServiceFacade;

  @Autowired private AuthenticatedUser authenticatedUser;

  @Autowired private TopicFeatureAuthorisationService topicFeatureAuthorisationService;

  @Autowired private TopicCatalogueService topicCatalogueService;

  @Override
  @PreAuthorize(
      "hasAuthority('AUTHORIZATION_GET_ALL_TOPICS') or @authorisationService.isSuperAdmin()")
  public ResponseEntity<TopicCatalogueDTO> getTopicCatalogue() {
    return ResponseEntity.ok(topicCatalogueService.getCatalogue());
  }

  @Override
  @PreAuthorize(
      "hasAuthority('AUTHORIZATION_UPDATE_TOPIC') or @authorisationService.isSuperAdmin()")
  public ResponseEntity<TopicCatalogueDTO> saveTopicCatalogue(
      final TopicCatalogueDTO topicCatalogueDTO) {
    log.info("Saving the topic catalogue by user {}", authenticatedUser.getUsername());
    return ResponseEntity.ok(topicCatalogueService.saveCatalogue(topicCatalogueDTO));
  }

  @Override
  @PreAuthorize(
      "hasAuthority('AUTHORIZATION_CREATE_TOPIC') or @authorisationService.isSuperAdmin()")
  public ResponseEntity<TopicMultilingualDTO> createTopic(
      final TopicMultilingualDTO topicMultilingualDTO) {
    log.info("Creating topic by user {} ", authenticatedUser.getUsername());
    final TopicMultilingualDTO savedTopic = topicServiceFacade.createTopic(topicMultilingualDTO);
    return new ResponseEntity<>(savedTopic, HttpStatus.OK);
  }

  @Override
  @PreAuthorize(
      "hasAuthority('AUTHORIZATION_UPDATE_TOPIC') or @authorisationService.isSuperAdmin()")
  public ResponseEntity<TopicMultilingualDTO> updateTopic(
      final Long id, final TopicMultilingualDTO topicMultilingualDTO) {
    log.info("Updating topic with id {} by user {} ", id, authenticatedUser.getUsername());
    final TopicMultilingualDTO savedTopic =
        topicServiceFacade.updateTopic(id, topicMultilingualDTO);
    return new ResponseEntity<>(savedTopic, HttpStatus.OK);
  }

  @Override
  @PreAuthorize(
      "hasAuthority('AUTHORIZATION_GET_ALL_TOPICS') or @authorisationService.isSuperAdmin()")
  public ResponseEntity<List<TopicDTO>> getAllTopicsAsAdmin() {
    final var topics = topicServiceFacade.getAllTopics();
    return !CollectionUtils.isEmpty(topics)
        ? new ResponseEntity<>(topics, HttpStatus.OK)
        : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Override
  @PreAuthorize(
      "hasAuthority('AUTHORIZATION_GET_TOPICS_TRANSLATION_BY_ID') or @authorisationService.isSuperAdmin()")
  public ResponseEntity<TopicMultilingualDTO> getTopicWithTranslationById(final Long id) {
    return new ResponseEntity<>(topicServiceFacade.getTopicMultilingualById(id), HttpStatus.OK);
  }
}
