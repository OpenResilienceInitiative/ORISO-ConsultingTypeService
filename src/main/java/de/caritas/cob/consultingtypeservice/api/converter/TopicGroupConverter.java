package de.caritas.cob.consultingtypeservice.api.converter;

import de.caritas.cob.consultingtypeservice.api.model.TopicGroupEntity;
import de.caritas.cob.consultingtypeservice.api.model.TopicGroupsDTO;
import de.caritas.cob.consultingtypeservice.api.model.TopicGroupsDTOData;
import de.caritas.cob.consultingtypeservice.api.model.TopicGroupsDTODataItemsInner;
import de.caritas.cob.consultingtypeservice.api.service.TranslationService;
import de.caritas.cob.consultingtypeservice.api.util.TranslationUtils;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TopicGroupConverter {

  private final @NonNull TranslationService translationService;

  public TopicGroupsDTO toTopicGroupsDTO(Collection<TopicGroupEntity> topicGroups) {
    return new TopicGroupsDTO().data(new TopicGroupsDTOData().items(itemsOf(topicGroups)));
  }

  private List<TopicGroupsDTODataItemsInner> itemsOf(Collection<TopicGroupEntity> topicGroups) {
    final String lang = translationService.getCurrentLanguageContext();

    return topicGroups.stream()
        .map(
            topicGroup ->
                new TopicGroupsDTODataItemsInner()
                    .topicIds(topicIdsOf(topicGroup))
                    .id(topicGroup.getId().intValue())
                    .name(
                        resolveNameForGivenLanguageOrFallbackToGerman(topicGroup.getName(), lang)))
        .collect(Collectors.toList());
  }

  private String resolveNameForGivenLanguageOrFallbackToGerman(String nameAsJson, String lang) {
    return TranslationUtils.getTranslatedStringFromMap(nameAsJson, lang);
  }

  private static List<Integer> topicIdsOf(TopicGroupEntity topicGroup) {
    return topicGroup.getTopicEntities().stream()
        .map(topicEntity -> topicEntity.getId().intValue())
        .collect(Collectors.toList());
  }
}
