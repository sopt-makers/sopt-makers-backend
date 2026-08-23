package org.sopt.makers.domain.playground.project.exception;

import org.sopt.makers.core.exception.BaseException;

public class ProjectException extends BaseException {

    public ProjectException(ProjectFailure failure) {
        super(failure);
    }
}
