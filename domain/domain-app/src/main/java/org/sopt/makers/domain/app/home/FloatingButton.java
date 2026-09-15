package org.sopt.makers.domain.app.home;

public record FloatingButton(
    String imageUrl,
    String title,
    String expandedSubTitle,
    String collapsedSubtitle,
    String actionButtonName,
    String linkUrl,
    boolean isActive) {}
