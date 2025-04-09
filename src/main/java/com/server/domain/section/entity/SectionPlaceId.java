package com.server.domain.section.entity;

import java.io.Serializable;

public class SectionPlaceId implements Serializable {
    private Long section;
    private Long place;


    public SectionPlaceId(){}

    public SectionPlaceId(Long section, Long place){
        super();
        this.section = section;
        this.place = place;
    }
}
