package org.sopt.makers.domain.playground.member.ask;

import java.util.List;

public record AskPage(
    List<AskDetail> asks,
    int currentPage,
    int pageSize,
    long totalElements,
    int totalPages,
    boolean hasNext,
    boolean hasPrevious) {}
