package org.sopt.makers.storage.db.playground.project.entity;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.sopt.makers.domain.playground.project.Project;

@Entity
@Table(name = "projects")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creator_id")
    private Long writerId;

    @Column(name = "name")
    private String name;

    @Column(name = "generation")
    private Integer generation;

    @Column(name = "category")
    private String category;

    @Column(name = "start_at")
    private LocalDate startAt;

    @Column(name = "end_at")
    private LocalDate endAt;

    @Type(ListArrayType.class)
    @Column(name = "service_type", columnDefinition = "text[]")
    private List<String> serviceType;

    @Column(name = "is_available")
    private Boolean isAvailable;

    @Column(name = "is_founding")
    private Boolean isFounding;

    private String summary;

    private String detail;

    @Column(name = "logo_image")
    private String logoImage;

    @Column(name = "thumbnail_image")
    private String thumbnailImage;

    @Type(ListArrayType.class)
    @Column(name = "images", columnDefinition = "text[]")
    private List<String> images;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private ProjectEntity(Long id, Long writerId, String name, Integer generation, String category,
            LocalDate startAt, LocalDate endAt, List<String> serviceType, Boolean isAvailable,
            Boolean isFounding, String summary, String detail, String logoImage,
            String thumbnailImage, List<String> images, LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.writerId = writerId;
        this.name = name;
        this.generation = generation;
        this.category = category;
        this.startAt = startAt;
        this.endAt = endAt;
        this.serviceType = serviceType;
        this.isAvailable = isAvailable;
        this.isFounding = isFounding;
        this.summary = summary;
        this.detail = detail;
        this.logoImage = logoImage;
        this.thumbnailImage = thumbnailImage;
        this.images = images;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ProjectEntity from(Project project) {
        return new ProjectEntity(
                project.id(), project.writerId(), project.name(), project.generation(),
                project.category(), project.startAt(), project.endAt(), project.serviceType(),
                project.isAvailable(), project.isFounding(), project.summary(), project.detail(),
                project.logoImage(), project.thumbnailImage(), project.images(),
                project.createdAt(), project.updatedAt()
        );
    }

    public Project toDomain() {
        return new Project(id, writerId, name, generation, category, startAt, endAt,
                serviceType, isAvailable, isFounding, summary, detail,
                logoImage, thumbnailImage, images, createdAt, updatedAt);
    }
}
