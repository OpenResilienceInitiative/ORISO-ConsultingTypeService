package de.caritas.cob.consultingtypeservice.api.repository;

import de.caritas.cob.consultingtypeservice.api.model.TopicGroupEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicGroupRepository extends JpaRepository<TopicGroupEntity, Long> {

  List<TopicGroupEntity> findAllByOrderByIdAsc();
}
