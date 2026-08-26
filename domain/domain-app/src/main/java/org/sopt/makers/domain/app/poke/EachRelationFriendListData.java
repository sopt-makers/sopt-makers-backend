package org.sopt.makers.domain.app.poke;

import java.util.List;

public record EachRelationFriendListData(
    List<SimplePokeProfileData> friendList,
    int totalSize,
    int totalPageSize,
    int pageSize,
    int pageNum) {}
