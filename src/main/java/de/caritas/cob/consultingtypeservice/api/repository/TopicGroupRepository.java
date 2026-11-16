package de.caritas.cob.consultingtypeservice.api.repository;

import de.caritas.cob.consultingtypeservice.api.model.TopicGroupEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TopicGroupRepository extends JpaRepository<TopicGroupEntity, Long> {

  /** The categories in the order the registration shows them. */
  List<TopicGroupEntity> findAllByOrderBySortOrderAscIdAsc();

  /**
   * The topics of a category in display order. Rows written without an order (older seeds) share
   * position 0 and fall back to the topic id, so nothing is ever lost.
   */
  @Query(
      value =
          "SELECT topic_id FROM topic_group_x_topic WHERE group_id = :groupId"
              + " ORDER BY sort_order, topic_id",
      nativeQuery = true)
  List<Long> findTopicIdsInOrder(@Param("groupId") Long groupId);

  @Modifying
  @Query(
      value =
          "UPDATE topic_group_x_topic SET sort_order = :position"
              + " WHERE group_id = :groupId AND topic_id = :topicId",
      nativeQuery = true)
  void setTopicPosition(
      @Param("groupId") Long groupId,
      @Param("topicId") Long topicId,
      @Param("position") int position);
}
