package org.sopt.makers.domain.admin.attendance.port;

import java.util.Optional;
import org.sopt.makers.domain.admin.attendance.SubAttendance;

public interface SubAttendanceRepositoryPort {

  Optional<SubAttendance> findById(Long id);

  SubAttendance save(SubAttendance subAttendance);
}
