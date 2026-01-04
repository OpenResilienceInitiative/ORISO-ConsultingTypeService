package de.caritas.cob.consultingtypeservice.api.mapper.group;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeGroupResponseDTO;
import de.caritas.cob.consultingtypeservice.schemas.model.ConsultingType;
import de.caritas.cob.consultingtypeservice.testHelper.HelperMethods;
import java.io.IOException;
import java.util.List;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.Test;

public class ConsultingTypeGroupResponseMapperTest {

  @Test
  public void mapConsultingType_Should_MapAllProperties() throws IOException {

    List<ConsultingType> consultingTypeList = HelperMethods.getConsultingTypeList();
    ConsultingTypeGroupResponseDTO result =
        ConsultingTypeGroupResponseMapper.mapConsultingType(
            ImmutablePair.of("group", consultingTypeList));

    assertThat(result, notNullValue());
    assertThat(result.getConsultingTypes(), notNullValue());
    assertThat(result.getConsultingTypes(), hasSize(2));
    for (int index = 0; index < consultingTypeList.size(); index++) {
      assertThat(
          result.getConsultingTypes().get(index).getId(),
          is(consultingTypeList.get(index).getId()));
    }
  }
}
