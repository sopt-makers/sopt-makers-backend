package org.sopt.makers.storage.db.playground.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.playground.project.ProjectMember;

@Entity
@Table(name = "project_users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "user_id")
    private Long userId;

    private String role;

    private String description;

    @Column(name = "is_team_member")
    private Boolean isTeamMember;

    private ProjectMemberEntity(Long id, Long projectId, Long userId, String role,
            String description, Boolean isTeamMember) {
        this.id = id;
        this.projectId = projectId;
        this.userId = userId;
        this.role = role;
        this.description = description;
        this.isTeamMember = isTeamMember;
    }

    public static ProjectMemberEntity from(ProjectMember member) {
        return new ProjectMemberEntity(
                member.id(), member.projectId(), member.userId(),
                member.role(), member.description(), member.isTeamMember()
        );
    }

    public ProjectMember toDomain() {
        return new ProjectMember(id, projectId, userId, role, description, isTeamMember);
    }
}
