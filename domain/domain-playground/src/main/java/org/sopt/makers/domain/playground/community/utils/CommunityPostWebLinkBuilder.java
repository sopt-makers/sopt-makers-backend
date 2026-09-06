package org.sopt.makers.domain.playground.community.utils;

import java.util.Objects;
import org.sopt.makers.domain.playground.community.CommunityCategoryCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CommunityPostWebLinkBuilder {

    private static final String PROD_PROFILE = "prod";
    private static final String PROD_BASE_URL = "https://playground.sopt.org";
    private static final String DEV_BASE_URL = "https://sopt-internal-dev.pages.dev";

    private final String activeProfile;

    public CommunityPostWebLinkBuilder(@Value("${spring.profiles.active:}") String activeProfile) {
        this.activeProfile = activeProfile;
    }

    /**
     * categoryCode/parentCategoryCode는 storage 조회로 미리 해석된 canonical 코드다.
     * (domain Category는 parentId만 가지므로, 부모 코드 해석은 이 유틸의 책임 밖이다.)
     */
    public String build(Long postId, CommunityCategoryCode categoryCode, CommunityCategoryCode parentCategoryCode) {
        String baseUrl = resolveBaseUrl();

        if (categoryCode == null) {
            return baseUrl + "/feed?feed=" + postId;
        }

        CommunityCategoryCode rootCategoryCode = parentCategoryCode != null ? parentCategoryCode : categoryCode;

        if (rootCategoryCode == CommunityCategoryCode.MEETING) {
            return baseUrl + "/group/post?id=" + postId;
        }

        StringBuilder webLink = new StringBuilder(baseUrl)
            .append("/feed?category=")
            .append(rootCategoryCode.name())
            .append("&feed=")
            .append(postId);

        String subcategory = resolveSubcategory(categoryCode, parentCategoryCode);
        if (subcategory != null) {
            webLink.append("&subcategory=").append(subcategory);
        }

        return webLink.toString();
    }

    private String resolveBaseUrl() {
        return Objects.equals(activeProfile, PROD_PROFILE)
            ? PROD_BASE_URL
            : DEV_BASE_URL;
    }

    private String resolveSubcategory(CommunityCategoryCode categoryCode, CommunityCategoryCode parentCategoryCode) {
        if (parentCategoryCode == null) {
            return null;
        }

        String rootPrefix = parentCategoryCode.name() + "_";
        String code = categoryCode.name();

        return code.startsWith(rootPrefix)
            ? code.substring(rootPrefix.length())
            : code;
    }
}
