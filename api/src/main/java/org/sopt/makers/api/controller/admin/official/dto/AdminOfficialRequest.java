package org.sopt.makers.api.controller.admin.official.dto;

import java.time.LocalDate;
import java.util.List;
import org.sopt.makers.domain.official.admin.AdminOfficialFacade;
import org.sopt.makers.domain.official.admin.port.AdminCachePort;

public class AdminOfficialRequest {

  public record AddCommonRequest(
      Integer generationId,
      String name,
      String darkModeKeyColor,
      String darkModeTextColor,
      String lightModeKeyColor,
      String lightModeTextColor,
      List<RecruitScheduleRequest> recruitSchedules) {

    public record RecruitScheduleRequest(String type, ScheduleRequest schedule) {}

    public record ScheduleRequest(
        String applicationStartTime,
        String applicationEndTime,
        String applicationResultTime,
        String interviewStartTime,
        String interviewEndTime,
        String finalResultTime) {}

    public AdminOfficialFacade.AddCommonRequest toCommand() {
      List<AdminCachePort.RecruitScheduleData> schedules =
          recruitSchedules == null
              ? null
              : recruitSchedules.stream()
                  .map(
                      rs ->
                          new AdminCachePort.RecruitScheduleData(
                              rs.type(),
                              rs.schedule() == null
                                  ? null
                                  : new AdminCachePort.ScheduleData(
                                      rs.schedule().applicationStartTime(),
                                      rs.schedule().applicationEndTime(),
                                      rs.schedule().applicationResultTime(),
                                      rs.schedule().interviewStartTime(),
                                      rs.schedule().interviewEndTime(),
                                      rs.schedule().finalResultTime())))
                  .toList();
      return new AdminOfficialFacade.AddCommonRequest(
          generationId,
          name,
          darkModeKeyColor,
          darkModeTextColor,
          lightModeKeyColor,
          lightModeTextColor,
          schedules);
    }
  }

  public record AddHomeRequest(
      Integer generationId,
      String homeHeaderImageFileName,
      List<NewsRequest> news,
      List<ReviewRequest> reviews) {

    public record NewsRequest(String title, String link, String imageFileName) {}

    public record ReviewRequest(String title, String content, String authorInfo) {}

    public AdminOfficialFacade.AddHomeRequest toCommand() {
      List<AdminOfficialFacade.AddHomeRequest.NewsFileRequest> newsRequests =
          news == null
              ? null
              : news.stream()
                  .map(n -> new AdminOfficialFacade.AddHomeRequest.NewsFileRequest(n.title(), n.link(), n.imageFileName()))
                  .toList();
      List<AdminOfficialFacade.AddHomeRequest.ReviewRequest> reviewRequests =
          reviews == null
              ? null
              : reviews.stream()
                  .map(r -> new AdminOfficialFacade.AddHomeRequest.ReviewRequest(r.title(), r.content(), r.authorInfo()))
                  .toList();
      return new AdminOfficialFacade.AddHomeRequest(
          generationId, homeHeaderImageFileName, newsRequests, reviewRequests);
    }
  }

  public record AddAboutRequest(
      Integer generationId,
      String headerImageFileName,
      List<CoreValueRequest> coreValues,
      List<MemberRequest> members,
      List<ActivityScheduleRequest> activitySchedules) {

    public record CoreValueRequest(
        String value, String description, String detailDescription, String imageFileName) {}

    public record MemberRequest(
        String role,
        String name,
        String affiliation,
        String introduction,
        String profileImageFileName,
        String snsEmail,
        String snsLinkedin,
        String snsGithub,
        String snsBehance) {}

    public record ActivityScheduleRequest(String name, LocalDate startDate, LocalDate endDate) {}

    public AdminOfficialFacade.AddAboutRequest toCommand() {
      List<AdminOfficialFacade.AddAboutRequest.CoreValueRequest> cvRequests =
          coreValues == null
              ? null
              : coreValues.stream()
                  .map(
                      cv ->
                          new AdminOfficialFacade.AddAboutRequest.CoreValueRequest(
                              cv.value(), cv.description(), cv.detailDescription(), cv.imageFileName()))
                  .toList();
      List<AdminOfficialFacade.AddAboutRequest.MemberRequest> memberRequests =
          members == null
              ? null
              : members.stream()
                  .map(
                      m ->
                          new AdminOfficialFacade.AddAboutRequest.MemberRequest(
                              m.role(),
                              m.name(),
                              m.affiliation(),
                              m.introduction(),
                              m.profileImageFileName(),
                              m.snsEmail(),
                              m.snsLinkedin(),
                              m.snsGithub(),
                              m.snsBehance()))
                  .toList();
      List<AdminOfficialFacade.AddAboutRequest.ActivityScheduleRequest> scheduleRequests =
          activitySchedules == null
              ? null
              : activitySchedules.stream()
                  .map(
                      s ->
                          new AdminOfficialFacade.AddAboutRequest.ActivityScheduleRequest(
                              s.name(), s.startDate(), s.endDate()))
                  .toList();
      return new AdminOfficialFacade.AddAboutRequest(
          generationId, headerImageFileName, cvRequests, memberRequests, scheduleRequests);
    }
  }

  public record AddRecruitRequest(
      Integer generationId,
      String recruitHeaderImageFileName,
      List<PartIntroductionRequest> partIntroductions,
      List<PartCurriculumRequest> partCurriculums,
      List<RecruitPartCurriculumRequest> recruitPartCurriculums,
      List<RecruitQuestionRequest> recruitQuestions) {

    public record PartIntroductionRequest(String part, String description) {}

    public record PartCurriculumRequest(String part, List<String> curriculums) {}

    public record RecruitPartCurriculumRequest(String part, IntroductionRequest introduction) {}

    public record IntroductionRequest(String content, String preference) {}

    public record RecruitQuestionRequest(String part, List<QuestionRequest> questions) {}

    public record QuestionRequest(String question, String answer) {}

    public AdminOfficialFacade.AddRecruitRequest toCommand() {
      List<AdminCachePort.PartIntroductionData> piData =
          partIntroductions == null
              ? null
              : partIntroductions.stream()
                  .map(pi -> new AdminCachePort.PartIntroductionData(pi.part(), pi.description()))
                  .toList();
      List<AdminCachePort.PartCurriculumData> pcData =
          partCurriculums == null
              ? null
              : partCurriculums.stream()
                  .map(pc -> new AdminCachePort.PartCurriculumData(pc.part(), pc.curriculums()))
                  .toList();
      List<AdminCachePort.RecruitPartCurriculumData> rpcData =
          recruitPartCurriculums == null
              ? null
              : recruitPartCurriculums.stream()
                  .map(
                      rpc ->
                          new AdminCachePort.RecruitPartCurriculumData(
                              rpc.part(),
                              rpc.introduction() == null
                                  ? null
                                  : new AdminCachePort.IntroductionData(
                                      rpc.introduction().content(),
                                      rpc.introduction().preference())))
                  .toList();
      List<AdminCachePort.RecruitQuestionData> rqData =
          recruitQuestions == null
              ? null
              : recruitQuestions.stream()
                  .map(
                      rq ->
                          new AdminCachePort.RecruitQuestionData(
                              rq.part(),
                              rq.questions() == null
                                  ? null
                                  : rq.questions().stream()
                                      .map(q -> new AdminCachePort.QuestionData(q.question(), q.answer()))
                                      .toList()))
                  .toList();
      return new AdminOfficialFacade.AddRecruitRequest(
          generationId, recruitHeaderImageFileName, piData, pcData, rpcData, rqData);
    }
  }

  private AdminOfficialRequest() {}
}
