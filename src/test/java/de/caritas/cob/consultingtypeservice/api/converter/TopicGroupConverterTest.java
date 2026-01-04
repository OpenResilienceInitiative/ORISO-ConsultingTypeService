package de.caritas.cob.consultingtypeservice.api.converter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import de.caritas.cob.consultingtypeservice.api.model.TopicGroupEntity;
import de.caritas.cob.consultingtypeservice.api.service.TranslationService;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TopicGroupConverterTest {

  @InjectMocks TopicGroupConverter topicGroupConverter;

  @Mock TranslationService translationService;

  @Test
  void should_ResolveNameForGivenLang_When_TopicGroupConverterIsCalled() {
    // given
    var topicGroups =
        Lists.newArrayList(
            TopicGroupEntity.builder()
                .id(1L)
                .name("{\"de\":\"name_de\",\"en\":\"name_en\"}")
                .topicEntities(Sets.newHashSet())
                .build(),
            TopicGroupEntity.builder()
                .id(2L)
                .name("{\"de\":\"second_name_de\",\"en\":\"second_name_en\"}")
                .topicEntities(Sets.newHashSet())
                .build());
    when(translationService.getCurrentLanguageContext()).thenReturn("en");

    // when
    var result = topicGroupConverter.toTopicGroupsDTO(topicGroups);
    // then
    assertEquals("name_en", result.getData().getItems().get(0).getName());
    assertEquals("second_name_en", result.getData().getItems().get(1).getName());
  }
}
