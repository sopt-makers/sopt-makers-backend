package org.sopt.makers.domain.playground.resolution;

public class UserResolutionLuckyPick {

    private final Long id;
    private final Long userId;
    private boolean result;
    private boolean hasDrawn;

    public UserResolutionLuckyPick(Long id, Long userId, boolean result, boolean hasDrawn) {
        this.id = id;
        this.userId = userId;
        this.result = result;
        this.hasDrawn = hasDrawn;
    }

    public Long id() {
        return id;
    }

    public Long userId() {
        return userId;
    }

    public boolean isResult() {
        return result;
    }

    public boolean isHasDrawn() {
        return hasDrawn;
    }

    public void win() {
        this.result = true;
    }

    public void draw() {
        this.hasDrawn = true;
    }
}
