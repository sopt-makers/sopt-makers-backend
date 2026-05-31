package org.sopt.makers.domain.admin.attendance.exception;

import org.sopt.makers.core.exception.BaseException;

public class AttendanceException extends BaseException {

  public AttendanceException(AttendanceFailure failure) {
    super(failure);
  }
}
