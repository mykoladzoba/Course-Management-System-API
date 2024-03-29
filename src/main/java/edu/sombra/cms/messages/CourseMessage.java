package edu.sombra.cms.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static edu.sombra.cms.util.constants.SystemSettings.MINIMUM_NUMBER_OF_COURSE_INSTRUCTORS;
import static edu.sombra.cms.util.constants.SystemSettings.MINIMUM_NUMBER_OF_COURSE_LESSONS;

@Getter
@AllArgsConstructor
public enum CourseMessage implements ThrowMessage {

    NOT_FOUND("Course not found", HttpStatus.NOT_FOUND),
    ACTIVE_NOT_FOUND("Active course not found", HttpStatus.NOT_FOUND),
    INSTRUCTOR_IS_ALREADY_ASSIGNED("Instructor is already assigned", HttpStatus.BAD_REQUEST),
    STUDENT_IS_ALREADY_ASSIGNED("Student is already assigned", HttpStatus.BAD_REQUEST),
    STUDENT_COURSES_LIMIT_IS_ALREADY_ASSIGNED("Student is already assigned", HttpStatus.BAD_REQUEST),
    MINIMUM_NUMBER_OF_INSTRUCTORS_AND_LESSONS("Course should have at least " + MINIMUM_NUMBER_OF_COURSE_INSTRUCTORS + " instructor(s) and " + MINIMUM_NUMBER_OF_COURSE_LESSONS + " lessons", HttpStatus.BAD_REQUEST),
    LESSONS_NOT_FINISHED("Course lessons are not finished yet", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus status;

}
