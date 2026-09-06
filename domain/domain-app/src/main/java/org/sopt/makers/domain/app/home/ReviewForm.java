package org.sopt.makers.domain.app.home;

public record ReviewForm(
    String title, String subTitle, String actionButtonName, String linkUrl, boolean isActive) {}
