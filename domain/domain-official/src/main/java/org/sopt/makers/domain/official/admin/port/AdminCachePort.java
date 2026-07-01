package org.sopt.makers.domain.official.admin.port;

import java.util.List;

public interface AdminCachePort {

  void putCommon(Integer generationId, CommonCacheData data);

  CommonCacheData getCommon(Integer generationId);

  void evictCommon(Integer generationId);

  void putHome(Integer generationId, HomeCacheData data);

  HomeCacheData getHome(Integer generationId);

  void evictHome(Integer generationId);

  void putAbout(Integer generationId, AboutCacheData data);

  AboutCacheData getAbout(Integer generationId);

  void evictAbout(Integer generationId);

  void putRecruit(Integer generationId, RecruitCacheData data);

  RecruitCacheData getRecruit(Integer generationId);

  void evictRecruit(Integer generationId);

  record CommonCacheData(
      Integer generationId,
      String name,
      String darkModeKeyColor,
      String darkModeTextColor,
      String lightModeKeyColor,
      String lightModeTextColor,
      List<RecruitScheduleData> recruitSchedules) {}

  record HomeCacheData(
      Integer generationId,
      String homeHeaderImageUrl,
      List<NewsData> news,
      List<ReviewData> reviews) {}

  record AboutCacheData(
      Integer generationId,
      String headerImageUrl,
      List<CoreValueData> coreValues,
      List<MemberData> members,
      List<ActivityScheduleData> activitySchedules) {}

  record RecruitCacheData(
      Integer generationId,
      String recruitHeaderImageUrl,
      List<PartIntroductionData> partIntroductions,
      List<PartCurriculumData> partCurriculums,
      List<RecruitPartCurriculumData> recruitPartCurriculums,
      List<RecruitQuestionData> recruitQuestions) {}

  record RecruitScheduleData(String type, ScheduleData schedule) {}

  record ScheduleData(
      String applicationStartTime,
      String applicationEndTime,
      String applicationResultTime,
      String interviewStartTime,
      String interviewEndTime,
      String finalResultTime) {}

  record NewsData(String title, String link, String imageUrl) {}

  record ReviewData(String title, String content, String authorInfo) {}

  record CoreValueData(
      String value, String description, String detailDescription, String imageUrl) {}

  record MemberData(
      String role,
      String name,
      String affiliation,
      String introduction,
      String profileImageUrl,
      String snsEmail,
      String snsLinkedin,
      String snsGithub,
      String snsBehance) {}

  record ActivityScheduleData(String name, String startDate, String endDate) {}

  record PartIntroductionData(String part, String description) {}

  record PartCurriculumData(String part, List<String> curriculums) {}

  record RecruitPartCurriculumData(String part, IntroductionData introduction) {}

  record IntroductionData(String content, String preference) {}

  record RecruitQuestionData(String part, List<QuestionData> questions) {}

  record QuestionData(String question, String answer) {}
}
