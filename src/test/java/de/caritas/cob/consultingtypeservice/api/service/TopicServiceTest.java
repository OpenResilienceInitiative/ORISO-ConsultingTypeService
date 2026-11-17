package de.caritas.cob.consultingtypeservice.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.caritas.cob.consultingtypeservice.api.auth.AuthorisationService;
import de.caritas.cob.consultingtypeservice.api.model.TopicEntity;
import de.caritas.cob.consultingtypeservice.api.model.TopicStatus;
import de.caritas.cob.consultingtypeservice.api.repository.TopicRepository;
import de.caritas.cob.consultingtypeservice.api.tenant.TenantContext;
import de.caritas.cob.consultingtypeservice.api.topicdefaults.DefaultTopicSeeder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class TopicServiceTest {

  @InjectMocks TopicService topicService;

  @Mock TopicRepository topicRepository;

  @Mock AuthorisationService authorisationService;

  @Mock DefaultTopicSeeder defaultTopicSeeder;

  @Test
  void topicList_ofThePlatformAdminInTenantZero_isTheDefaultTenantOnly_neverAllTenants() {
    ReflectionTestUtils.setField(topicService, "multitenancy", true);
    TenantContext.setCurrentTenant(0L);
    when(authorisationService.isSuperAdmin()).thenReturn(true);
    when(defaultTopicSeeder.defaultTopicTenantId()).thenReturn(1L);

    topicService.getAllTopics();

    verify(topicRepository).findAllForTenant(1L);
    verify(topicRepository, never()).findAll();
  }

  @Test
  void topicList_ofThePlatformAdminInATenant_isThatTenant() {
    ReflectionTestUtils.setField(topicService, "multitenancy", true);
    TenantContext.setCurrentTenant(7L);

    topicService.getAllTopics();

    verify(topicRepository).findAllForTenant(7L);
  }

  @Test
  void topicById_ofThePlatformAdminInTenantZero_isLookedUpInTheDefaultTenant() {
    ReflectionTestUtils.setField(topicService, "multitenancy", true);
    TenantContext.setCurrentTenant(0L);
    when(authorisationService.isSuperAdmin()).thenReturn(true);
    when(defaultTopicSeeder.defaultTopicTenantId()).thenReturn(1L);

    topicService.findTopicById(5L);

    verify(topicRepository).findByIdForTenant(5L, 1L);
    verify(topicRepository, never()).findById(5L);
  }

  @Test
  void topicList_ofAnyoneElseInTenantZero_staysInTenantZero() {
    ReflectionTestUtils.setField(topicService, "multitenancy", true);
    TenantContext.setCurrentTenant(0L);
    when(authorisationService.isSuperAdmin()).thenReturn(false);

    topicService.getAllTopics();

    verify(topicRepository).findAllForTenant(0L);
  }

  @Test
  void catalogueTopics_spanAllTenants_onlyForThePlatformAdmin() {
    ReflectionTestUtils.setField(topicService, "multitenancy", true);
    TenantContext.setCurrentTenant(0L);
    when(authorisationService.isSuperAdmin()).thenReturn(true);

    topicService.getCatalogueTopics();

    verify(topicRepository).findAll();
  }

  @Test
  void catalogueTopics_stayInTheTenantContext_forAnyoneElse_evenUnderTenantZero() {
    ReflectionTestUtils.setField(topicService, "multitenancy", true);
    TenantContext.setCurrentTenant(0L);
    when(authorisationService.isSuperAdmin()).thenReturn(false);

    topicService.getCatalogueTopics();

    verify(topicRepository).findAllForTenant(0L);
  }

  @AfterEach
  void tearDown() {
    TenantContext.clear();
  }

  @Test
  void getAllTopics_Should_CallRepository() {
    // given, when
    TenantContext.clear();
    topicService.getAllTopics();
    // then
    verify(topicRepository).findAll();
  }

  @Test
  void getAllActiveTopics_Should_callRepository() {
    // given, when
    TenantContext.clear();
    topicService.getAllActiveTopics();
    // Then
    verify(topicRepository).findAllActive();
  }

  @Test
  void createTopics_Should_TakeTopicStatusFromRequest() {
    // given, when
    TopicEntity topicEntity = new TopicEntity();
    topicEntity.setStatus(TopicStatus.INACTIVE);
    topicService.createTopic(topicEntity);
    // then
    verify(topicRepository).save(topicEntity);
    assertThat(topicEntity.getCreateDate()).isNotNull();
    assertThat(topicEntity.getStatus()).isEqualTo(TopicStatus.INACTIVE);
  }

  @Test
  void updateTopics_Should_UpdateUpdateDateAndCallRepository() {
    // given, when
    TopicEntity topicEntity = new TopicEntity();
    topicService.updateTopic(topicEntity);
    // then
    verify(topicRepository).save(topicEntity);
    assertThat(topicEntity.getUpdateDate()).isNotNull();
  }
}
