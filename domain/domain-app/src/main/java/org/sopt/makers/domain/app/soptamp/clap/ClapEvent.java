package org.sopt.makers.domain.app.soptamp.clap;

public record ClapEvent(Long ownerUserId, Long stampId, int oldClapTotal, int newClapTotal) {}
