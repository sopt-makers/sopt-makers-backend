package org.sopt.makers.domain.playground.project.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.project.port.ProjectUserPort;
import org.sopt.makers.domain.user.port.PlaygroundProjectUserPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectUserAdapter implements ProjectUserPort {

    private final PlaygroundProjectUserPort playgroundProjectUserPort;

    @Override
    public List<ProjectUserInfo> getProjectUserInfosByIds(List<Long> userIds) {
        return playgroundProjectUserPort.getProjectUserInfosByIds(userIds).stream()
                .map(info -> new ProjectUserInfo(
                        info.id(),
                        info.name(),
                        info.profileImage(),
                        info.generations(),
                        info.hasProfile()
                ))
                .toList();
    }
}
