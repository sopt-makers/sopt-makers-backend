package org.sopt.makers.domain.playground.resolution.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.resolution.UserResolution;
import org.sopt.makers.domain.playground.resolution.UserResolutionLuckyPick;
import org.sopt.makers.domain.playground.resolution.port.UserResolutionLuckyPickRepositoryPort;
import org.sopt.makers.domain.playground.resolution.port.UserResolutionRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LuckyPickService {

    // TODO: 새 기수 시작 전 값 변경 필수
    private static final int CURRENT_GENERATION = 38;
    private static final int WINNER_COUNT = 3;

    private final UserResolutionRepositoryPort userResolutionRepositoryPort;
    private final UserResolutionLuckyPickRepositoryPort luckyPickRepositoryPort;

    @Transactional
    public boolean checkLuckyPickResult(Long userId) {
        Optional<UserResolutionLuckyPick> luckyPickOptional = luckyPickRepositoryPort.findByUserId(userId);

        if (luckyPickOptional.isEmpty()) {
            return false;
        }

        UserResolutionLuckyPick luckyPick = luckyPickOptional.get();
        boolean isWinner = luckyPick.isResult();

        luckyPick.draw();
        luckyPickRepositoryPort.save(luckyPick);

        return isWinner;
    }

    @Transactional
    public void prepareLuckyPickEvent() {
        if (luckyPickRepositoryPort.count() > 0) {
            return;
        }

        List<UserResolution> resolutions = userResolutionRepositoryPort.findAllByGeneration(CURRENT_GENERATION);
        List<Long> participantIds = resolutions.stream()
                .map(UserResolution::userId)
                .distinct()
                .toList();

        List<UserResolutionLuckyPick> participants = participantIds.stream()
                .map(id -> new UserResolutionLuckyPick(null, id, false, false))
                .collect(Collectors.toList());

        participants = luckyPickRepositoryPort.saveAll(participants);

        Collections.shuffle(participants);
        int countToPick = Math.min(WINNER_COUNT, participants.size());

        List<UserResolutionLuckyPick> winners = new ArrayList<>();
        for (int i = 0; i < countToPick; i++) {
            UserResolutionLuckyPick winner = participants.get(i);
            winner.win();
            winners.add(winner);
        }
        luckyPickRepositoryPort.saveAll(winners);
    }
}
