package de.caritas.cob.consultingtypeservice.api.converter;

import de.caritas.cob.consultingtypeservice.api.model.TopicGroupEntity;
import de.caritas.cob.consultingtypeservice.api.model.TopicGroupsDTO;
import de.caritas.cob.consultingtypeservice.api.model.TopicGroupsDTOData;
import de.caritas.cob.consultingtypeservice.api.model.TopicGroupsDTODataItemsInner;
import de.caritas.cob.consultingtypeservice.api.service.TopicGroupOrder;
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
  private final @NonNull TopicGroupOrder topicGroupOrder;

  public TopicGroupsDTO toTopicGroupsDTO(Collection<TopicGroupEntity> topicGroups) {
    return new TopicGroupsDTO().data(new TopicGroupsDTOData().items(itemsOf(topicGroups)));
  }

  private List<TopicGroupsDTODataItemsInner> itemsOf(Collection<TopicGroupEntity> topicGroups) {
    final List<String> lang = translationService.getLanguagePreferences();

    return topicGroups.stream()
        .map(
            topicGroup ->
                new TopicGroupsDTODataItemsInner()
                    .topicIds(topicIdsOf(topicGroup))
                    .id(topicGroup.getId().intValue())
                    .icon(topicGroup.getIcon())
                    .name(
                        resolveNameForGivenLanguageOrFallbackToGerman(topicGroup.getName(), lang)))
        .collect(Collectors.toList());
  }

  private String resolveNameForGivenLanguageOrFallbackToGerman(
      String nameAsJson, List<String> lang) {
    return TranslationUtils.getTranslatedStringFromMap(nameAsJson, lang);
  }

  private List<Integer> topicIdsOf(TopicGroupEntity topicGroup) {
    return topicGroupOrder.topicIdsInOrder(topicGroup).stream()
        .map(Long::intValue)
        .collect(Collectors.toList());
  }
}
