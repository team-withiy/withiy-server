package com.server.domain.section.entity;

import java.io.Serializable;

public class SectionCourseId implements Serializable {
    private Long section;
    private Long course;


    public SectionCourseId(){}

    public SectionCourseId(Long section, Long course){
        super();
        this.section = section;
        this.course = course;
    }
}
