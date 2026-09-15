package org.sopt.makers.domain.app.poke;

import java.util.List;

public record PokeToMeHistoryData(
    List<SimplePokeProfileData> history, int totalPageSize, int pageSize, int pageNum) {}
