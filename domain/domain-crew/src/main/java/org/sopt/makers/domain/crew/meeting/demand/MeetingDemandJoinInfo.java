package org.sopt.makers.domain.crew.meeting.demand;

import org.sopt.makers.domain.crew.meeting.MeetingFrequency;
import org.sopt.makers.domain.crew.meeting.MeetingType;

public record MeetingDemandJoinInfo(MeetingType meetingType, MeetingFrequency meetingFrequency) {}
