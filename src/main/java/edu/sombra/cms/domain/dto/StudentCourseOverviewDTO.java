package edu.sombra.cms.domain.dto;

import edu.sombra.cms.domain.enumeration.CourseStatus;
import edu.sombra.cms.domain.enumeration.StudentCourseStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentCourseOverviewDTO {

    private long id;
    private String name;
    private Integer mark;
    private CourseStatus courseStatus;
    private StudentCourseStatus passStatus;

}
