package org.sopt.makers.domain.crew.property.exception;

import org.sopt.makers.core.exception.BaseException;

public class CrewPropertyException extends BaseException {

  public CrewPropertyException(CrewPropertyFailure failure) {
    super(failure);
  }
}
