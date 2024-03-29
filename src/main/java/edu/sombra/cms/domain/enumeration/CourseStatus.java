package edu.sombra.cms.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CourseStatus {

    INACTIVE("INACTIVE"),
    ACTIVE("ACTIVE"),
    FINISHED("FINISHED");

    private final String name;

}
