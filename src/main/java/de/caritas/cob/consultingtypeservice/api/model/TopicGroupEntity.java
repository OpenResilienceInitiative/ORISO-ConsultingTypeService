package de.caritas.cob.consultingtypeservice.api.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Filter;

@Entity
@Table(name = "topic_group")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Access(AccessType.FIELD)
public class TopicGroupEntity {

  @Id
  @SequenceGenerator(name = "id_seq", allocationSize = 1, sequenceName = "SEQUENCE_TOPIC_GROUP")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_seq")
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "create_date", nullable = false)
  private LocalDateTime createDate;

  @Column(name = "update_date")
  private LocalDateTime updateDate;

  /** Position of the category in the registration; lower comes first. */
  @Builder.Default
  @Column(name = "sort_order", nullable = false)
  private Integer sortOrder = 0;

  /** Address or data URL of the category picture. */
  @Column(name = "icon", columnDefinition = "MEDIUMTEXT")
  private String icon;

  /** True once a platform admin saved the catalogue; the default seed then leaves it alone. */
  @Builder.Default
  @Column(name = "customized", nullable = false)
  private Boolean customized = false;

  // Groups are platform-wide, their topics are not; an entity filter never reaches this join.
  @ManyToMany(targetEntity = TopicEntity.class)
  @Filter(name = TenantFilter.NAME, condition = TenantFilter.CONDITION)
  @JoinTable(
      name = "topic_group_x_topic",
      joinColumns = @JoinColumn(name = "group_id"),
      inverseJoinColumns = @JoinColumn(name = "topic_id"))
  @Builder.Default
  private Set<TopicEntity> topicEntities = new HashSet<>();
}
