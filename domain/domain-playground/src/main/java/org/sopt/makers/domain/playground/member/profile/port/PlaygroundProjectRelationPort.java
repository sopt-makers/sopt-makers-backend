package org.sopt.makers.domain.playground.member.profile.port;

import java.util.List;
import org.sopt.makers.domain.playground.project.Project;

public interface PlaygroundProjectRelationPort {

  List<Project> findProjectsByUserId(Long userId);
}
