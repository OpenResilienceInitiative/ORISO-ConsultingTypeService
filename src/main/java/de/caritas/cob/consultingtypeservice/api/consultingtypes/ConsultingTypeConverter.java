package de.caritas.cob.consultingtypeservice.api.consultingtypes;

import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeDTO;
import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeEntity;
import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypePatchDTO;
import de.caritas.cob.consultingtypeservice.api.model.FurtherInformationDTO;
import de.caritas.cob.consultingtypeservice.api.model.GroupChatDTO;
import de.caritas.cob.consultingtypeservice.api.model.NotificationsDTO;
import de.caritas.cob.consultingtypeservice.api.model.RegistrationDTO;
import de.caritas.cob.consultingtypeservice.api.model.RequiredComponentsDTO;
import de.caritas.cob.consultingtypeservice.api.model.RolesDTO;
import de.caritas.cob.consultingtypeservice.api.model.SessionDataInitializingDTO;
import de.caritas.cob.consultingtypeservice.api.model.UrlsDTO;
import de.caritas.cob.consultingtypeservice.api.model.WelcomeMessageDTO;
import de.caritas.cob.consultingtypeservice.api.model.WelcomeScreenDTO;
import de.caritas.cob.consultingtypeservice.api.model.WhiteSpotDTO;
import de.caritas.cob.consultingtypeservice.schemas.model.Age;
import de.caritas.cob.consultingtypeservice.schemas.model.Anonymous;
import de.caritas.cob.consultingtypeservice.schemas.model.ConsultingType;
import de.caritas.cob.consultingtypeservice.schemas.model.FurtherInformation;
import de.caritas.cob.consultingtypeservice.schemas.model.GroupChat;
import de.caritas.cob.consultingtypeservice.schemas.model.MandatoryFields;
import de.caritas.cob.consultingtypeservice.schemas.model.NewMessage;
import de.caritas.cob.consultingtypeservice.schemas.model.Notes;
import de.caritas.cob.consultingtypeservice.schemas.model.Notifications;
import de.caritas.cob.consultingtypeservice.schemas.model.Option;
import de.caritas.cob.consultingtypeservice.schemas.model.Registration;
import de.caritas.cob.consultingtypeservice.schemas.model.RequiredComponents;
import de.caritas.cob.consultingtypeservice.schemas.model.Roles;
import de.caritas.cob.consultingtypeservice.schemas.model.SessionDataInitializing;
import de.caritas.cob.consultingtypeservice.schemas.model.State;
import de.caritas.cob.consultingtypeservice.schemas.model.TeamSessions;
import de.caritas.cob.consultingtypeservice.schemas.model.Urls;
import de.caritas.cob.consultingtypeservice.schemas.model.WelcomeMessage;
import de.caritas.cob.consultingtypeservice.schemas.model.WelcomeScreen;
import de.caritas.cob.consultingtypeservice.schemas.model.WhiteSpot;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ConsultingTypeConverter {

  public List<ConsultingType> convertList(List<ConsultingTypeEntity> consultingTypeEntities) {
    return consultingTypeEntities.stream()
        .map(this::copyToConsultingType)
        .collect(Collectors.toList());
  }

  private ConsultingType copyToConsultingType(ConsultingTypeEntity entity) {
    ConsultingType target = new ConsultingType();
    BeanUtils.copyProperties(entity, target);
    return target;
  }

  public ConsultingType convert(final ConsultingTypeDTO consultingTypeDTO) {

    return this.convert(new ConsultingType(), consultingTypeDTO);
  }

  public ConsultingType convert(
      ConsultingType consultingTypeEntity, final ConsultingTypeDTO consultingTypeDTO) {
    return consultingTypeEntity
        .withTenantId(consultingTypeDTO.getTenantId())
        .withDescription(consultingTypeDTO.getDescription())
        .withGroups(consultingTypeDTO.getGroups())
        .withFurtherInformation(convert(consultingTypeDTO.getFurtherInformation()))
        .withSlug(consultingTypeDTO.getSlug())
        .withExcludeNonMainConsultantsFromTeamSessions(
            consultingTypeDTO.getExcludeNonMainConsultantsFromTeamSessions())
        .withWhiteSpot(convert(consultingTypeDTO.getWhiteSpot()))
        .withGroupChat(convert(consultingTypeDTO.getGroupChat()))
        .withConsultantBoundedToConsultingType(
            consultingTypeDTO.getConsultantBoundedToConsultingType())
        .withWelcomeMessage(convert(consultingTypeDTO.getWelcomeMessage()))
        .withSendFurtherStepsMessage(consultingTypeDTO.getSendFurtherStepsMessage())
        .withSessionDataInitializing(convert(consultingTypeDTO.getSessionDataInitializing()))
        .withLanguageFormal(consultingTypeDTO.getLanguageFormal())
        .withRoles(convert(consultingTypeDTO.getRoles()))
        .withNotifications(convert(consultingTypeDTO.getNotifications()))
        .withRegistration(convert(consultingTypeDTO.getRegistration()))
        .withUrls(convert(consultingTypeDTO.getUrls()))
        .withShowAskerProfile(consultingTypeDTO.getShowAskerProfile())
        .withIsVideoCallAllowed(consultingTypeDTO.getIsVideoCallAllowed())
        .withIsSubsequentRegistrationAllowed(consultingTypeDTO.getIsSubsequentRegistrationAllowed())
        .withIsAnonymousConversationAllowed(consultingTypeDTO.getIsAnonymousConversationAllowed())
        .withRequiredComponents(convert(consultingTypeDTO.getRequiredComponents()))
        .withWelcomeScreen(convert(consultingTypeDTO.getWelcomeScreen()));
  }

  public ConsultingType convert(
      ConsultingType consultingTypeEntity, final ConsultingTypePatchDTO consultingTypeDTO) {
    return consultingTypeEntity
        .withWelcomeMessage(convert(consultingTypeDTO.getWelcomeMessage()))
        .withSendFurtherStepsMessage(consultingTypeDTO.getSendFurtherStepsMessage())
        .withLanguageFormal(consultingTypeDTO.getLanguageFormal())
        .withNotifications(convert(consultingTypeDTO.getNotifications()))
        .withIsVideoCallAllowed(consultingTypeDTO.getIsVideoCallAllowed());
  }

  private WelcomeScreen convert(WelcomeScreenDTO welcomeScreen) {
    if (welcomeScreen == null) {
      return null;
    }
    return new WelcomeScreen()
        .withAnonymous(
            new Anonymous(
                welcomeScreen.getAnonymous().getTitle(), welcomeScreen.getAnonymous().getText()));
  }

  private Urls convert(UrlsDTO urls) {
    if (urls == null) {
      return null;
    }
    return new Urls(
        urls.getRequiredAidMissingRedirectUrl(), urls.getRegistrationPostcodeFallbackUrl());
  }

  private Registration convert(RegistrationDTO registration) {
    if (registration == null) {
      return null;
    }
    return new Registration()
        .withAutoSelectAgency(registration.getAutoSelectAgency())
        .withAutoSelectPostcode(registration.getAutoSelectPostcode())
        .withNotes(
            new Notes(
                registration.getNotes().getAgencySelection(),
                registration.getNotes().getPassword()))
        .withMandatoryFields(
            new MandatoryFields(
                registration.getMandatoryFields().getAge(),
                registration.getMandatoryFields().getState()));
  }

  private Notifications convert(NotificationsDTO notifications) {
    if (notifications == null) {
      return null;
    }
    return new Notifications()
        .withTeamSessions(
            new TeamSessions()
                .withNewMessage(
                    new NewMessage(
                        notifications.getTeamSessions().getNewMessage().getAllTeamConsultants())));
  }

  private Roles convert(RolesDTO roles) {
    if (roles == null) {
      return null;
    }
    return new Roles(roles.getConsultant().getRoleNames());
  }

  private SessionDataInitializing convert(SessionDataInitializingDTO sessionDataInitializing) {
    if (sessionDataInitializing == null) {
      return null;
    }
    return new SessionDataInitializing()
        .withAddictiveDrugs(sessionDataInitializing.getAddictiveDrugs())
        .withAge(sessionDataInitializing.getAge())
        .withGender(sessionDataInitializing.getGender())
        .withState(sessionDataInitializing.getState())
        .withRelation(sessionDataInitializing.getRelation());
  }

  private WelcomeMessage convert(WelcomeMessageDTO welcomeMessage) {
    if (welcomeMessage == null) {
      return null;
    }
    return new WelcomeMessage(
        welcomeMessage.getSendWelcomeMessage(), welcomeMessage.getWelcomeMessageText());
  }

  private GroupChat convert(GroupChatDTO groupChat) {
    if (groupChat == null) {
      return null;
    }
    return new GroupChat(groupChat.getIsGroupChat(), groupChat.getGroupChatRules());
  }

  private WhiteSpot convert(WhiteSpotDTO whiteSpot) {
    if (whiteSpot == null) {
      return null;
    }
    return new WhiteSpot(whiteSpot.getWhiteSpotAgencyAssigned(), whiteSpot.getWhiteSpotAgencyId());
  }

  private RequiredComponents convert(RequiredComponentsDTO requiredComponents) {
    if (requiredComponents == null) {
      return null;
    }
    List<Option> options = new ArrayList<>();
    requiredComponents
        .getAge()
        .getOptions()
        .forEach(optionDTO -> options.add(new Option(optionDTO.getValue(), optionDTO.getLabel())));

    return new RequiredComponents()
        .withAge(
            new Age()
                .withIsEnabled(requiredComponents.getAge().getIsEnabled())
                .withOptions(options))
        .withState(new State(requiredComponents.getState().getIsEnabled()));
  }

  private FurtherInformation convert(FurtherInformationDTO furtherInformation) {
    if (furtherInformation == null) {
      return null;
    }
    return new FurtherInformation(furtherInformation.getLabel(), furtherInformation.getUrl());
  }
}
