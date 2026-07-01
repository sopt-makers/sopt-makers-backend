package org.sopt.makers.domain.official.recruitment.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.official.recruitment.RecruitType;
import org.sopt.makers.domain.official.recruitment.Recruitment;
import org.sopt.makers.domain.official.recruitment.Schedule;
import org.sopt.makers.domain.official.recruitment.port.RecruitmentRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitmentService {

  private final RecruitmentRepositoryPort recruitmentRepositoryPort;

  @Transactional
  public void bulkCreate(BulkCreateRecruitmentsCommand command) {
    recruitmentRepositoryPort.deleteByGenerationId(command.generationId());

    List<Recruitment> recruitments =
        command.recruitments().stream()
            .map(
                data ->
                    new Recruitment(
                        null,
                        command.generationId(),
                        RecruitType.fromString(data.type()),
                        data.schedule() == null
                            ? null
                            : new Schedule(
                                data.schedule().applicationStartTime(),
                                data.schedule().applicationEndTime(),
                                data.schedule().applicationResultTime(),
                                data.schedule().interviewStartTime(),
                                data.schedule().interviewEndTime(),
                                data.schedule().finalResultTime())))
            .toList();

    recruitmentRepositoryPort.saveAll(recruitments);
  }

  public List<Recruitment> findByGeneration(Integer generationId) {
    return recruitmentRepositoryPort.findByGenerationId(generationId);
  }

  public record BulkCreateRecruitmentsCommand(
      Integer generationId, List<RecruitmentData> recruitments) {

    public record RecruitmentData(String type, ScheduleData schedule) {}

    public record ScheduleData(
        String applicationStartTime,
        String applicationEndTime,
        String applicationResultTime,
        String interviewStartTime,
        String interviewEndTime,
        String finalResultTime) {}
  }
}
