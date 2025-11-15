package de.caritas.cob.consultingtypeservice.api.converter;

import static de.caritas.cob.consultingtypeservice.api.util.JsonConverter.convertMapFromJson;
import static de.caritas.cob.consultingtypeservice.api.util.JsonConverter.convertToJson;
import static de.caritas.cob.consultingtypeservice.api.util.TranslationUtils.getTranslatedStringFromMap;

import de.caritas.cob.consultingtypeservice.api.model.*;
import de.caritas.cob.consultingtypeservice.api.service.TranslationService;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TopicConverter {

  private final @NonNull TranslationService translationService;

  public TopicDTO toDTO(final TopicEntity topic, final String lang) {
    final var topicDTO =
        new TopicDTO()
            .id(topic.getId())
            .name(getTranslatedStringFromMap(topic.getName(), lang))
            .slug(topic.getSlug())
            .description(getTranslatedStringFromMap(topic.getDescription(), lang))
            .status(topic.getStatus().name())
            .fallbackAgencyId(topic.getFallbackAgencyId())
            .fallbackUrl(topic.getFallbackUrl())
            .welcomeMessage(topic.getWelcomeMessage())
            .titles(toTitlesDTO(topic, lang))
            .internalIdentifier(topic.getInternalIdentifier());
    if (topic.getCreateDate() != null) {
      topicDTO.setCreateDate(topic.getCreateDate().toString());
    }
    if (topic.getUpdateDate() != null) {
      topicDTO.setUpdateDate(topic.getUpdateDate().toString());
    }
    return topicDTO;
  }

  private static TitlesDTO toTitlesDTO(TopicEntity topic, String lang) {
    return new TitlesDTO()
        ._short(getTranslatedStringFromMap(topic.getTitlesShort(), lang))
        ._long(getTranslatedStringFromMap(topic.getTitlesLong(), lang))
        .welcome(topic.getTitlesWelcome())
        .registrationDropdown(topic.getTitlesDropdown());
  }

  public TopicDTO toDTO(final TopicEntity topic) {
    final String lang = translationService.getCurrentLanguageContext();
    return toDTO(topic, lang);
  }

  public TopicMultilingualDTO toMultilingualDTO(final TopicEntity topic) {
    final var topicMultilingualDTO =
        new TopicMultilingualDTO()
            .id(topic.getId())
            .name(convertMapFromJson(topic.getName()))
            .slug(topic.getSlug())
            .description(convertMapFromJson(topic.getDescription()))
            .status(topic.getStatus().name())
            .fallbackAgencyId(topic.getFallbackAgencyId())
            .fallbackUrl(topic.getFallbackUrl())
            .welcomeMessage(topic.getWelcomeMessage())
            .titles(toMultilingualTitlesDTO(topic))
            .internalIdentifier(topic.getInternalIdentifier());
    if (topic.getCreateDate() != null) {
      topicMultilingualDTO.setCreateDate(topic.getCreateDate().toString());
    }
    if (topic.getUpdateDate() != null) {
      topicMultilingualDTO.setUpdateDate(topic.getUpdateDate().toString());
    }
    return topicMultilingualDTO;
  }

  private TitlesMultilingualDTO toMultilingualTitlesDTO(TopicEntity topic) {
    return new TitlesMultilingualDTO()
        ._short(convertMapFromJson(topic.getTitlesShort()))
        ._long(convertMapFromJson(topic.getTitlesLong()))
        .welcome(topic.getTitlesWelcome())
        .registrationDropdown(topic.getTitlesDropdown());
  }

  public List<TopicMultilingualDTO> toMultilingualDTOList(
      final Collection<TopicEntity> topicEntities) {
    return topicEntities.stream().map(this::toMultilingualDTO).collect(Collectors.toList());
  }

  public List<TopicDTO> toDTOList(final Collection<TopicEntity> topicEntities) {
    final String lang = translationService.getCurrentLanguageContext();
    return topicEntities.stream().map(topic -> toDTO(topic, lang)).collect(Collectors.toList());
  }

  public TopicEntity toEntity(final TopicMultilingualDTO topicDTO) {
    final TopicEntity topicEntity = new TopicEntity();
    topicEntity.setName(convertToJson(topicDTO.getName()));
    topicEntity.setSlug(topicDTO.getSlug());
    topicEntity.setStatus(TopicStatus.valueOf(topicDTO.getStatus().toUpperCase()));
    topicEntity.setDescription(convertToJson(topicDTO.getDescription()));
    topicEntity.setUpdateDate(LocalDateTime.now(ZoneOffset.UTC));
    topicEntity.setInternalIdentifier(topicDTO.getInternalIdentifier());
    topicEntity.setFallbackAgencyId(topicDTO.getFallbackAgencyId());
    topicEntity.setFallbackUrl(topicDTO.getFallbackUrl());
    topicEntity.setWelcomeMessage(topicDTO.getWelcomeMessage());
    titlesToEntity(topicDTO, topicEntity);
    return topicEntity;
  }

  private void titlesToEntity(TopicMultilingualDTO topicDTO, TopicEntity topicEntity) {
    TitlesMultilingualDTO titles = topicDTO.getTitles();
    if (titles != null) {
      topicEntity.setTitlesShort(convertToJson(titles.getShort()));
      topicEntity.setTitlesLong(convertToJson(titles.getLong()));
      topicEntity.setTitlesWelcome(convertToJson(titles.getWelcome()));
      topicEntity.setTitlesDropdown(convertToJson(titles.getRegistrationDropdown()));
    } else {
      nullifyTitleAttributes(topicEntity);
    }
  }

  private void nullifyTitleAttributes(TopicEntity topicEntity) {
    topicEntity.setTitlesLong(null);
    topicEntity.setTitlesShort(null);
    topicEntity.setTitlesWelcome(null);
    topicEntity.setTitlesDropdown(null);
  }

  public TopicEntity toEntity(final TopicEntity targetEntity, final TopicMultilingualDTO topicDTO) {
    targetEntity.setName(convertToJson(topicDTO.getName()));
    targetEntity.setSlug(topicDTO.getSlug());
    targetEntity.setStatus(TopicStatus.valueOf(topicDTO.getStatus()));
    targetEntity.setDescription(convertToJson(topicDTO.getDescription()));
    targetEntity.setInternalIdentifier(topicDTO.getInternalIdentifier());
    targetEntity.setUpdateDate(LocalDateTime.now(ZoneOffset.UTC));
    targetEntity.setFallbackAgencyId(topicDTO.getFallbackAgencyId());
    targetEntity.setFallbackUrl(topicDTO.getFallbackUrl());
    targetEntity.setWelcomeMessage(topicDTO.getWelcomeMessage());
    titlesToEntity(topicDTO, targetEntity);
    return targetEntity;
  }
}
