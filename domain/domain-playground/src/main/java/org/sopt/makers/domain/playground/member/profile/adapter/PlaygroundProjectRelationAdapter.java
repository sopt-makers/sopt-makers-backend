package org.sopt.makers.domain.playground.member.profile.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.member.profile.port.PlaygroundProjectRelationPort;
import org.sopt.makers.domain.playground.project.Project;
import org.sopt.makers.domain.playground.project.port.ProjectQueryPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaygroundProjectRelationAdapter implements PlaygroundProjectRelationPort {

  private final ProjectQueryPort projectQueryPort;

  @Override
  public List<Project> findProjectsByUserId(Long userId) {
    return projectQueryPort.findProjectsByUserId(userId);
  }
}
