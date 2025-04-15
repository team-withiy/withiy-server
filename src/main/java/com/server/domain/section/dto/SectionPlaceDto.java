package com.server.domain.section.dto;

import com.server.domain.place.dto.PlaceDto;
import com.server.domain.section.entity.SectionPlace;
import lombok.*;

import static org.antlr.v4.runtime.misc.Utils.sequence;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionPlaceDto {
    private SectionDto sectionDto;
    private PlaceDto placeDto;
    private int sequence;

    public static SectionPlaceDto from(SectionPlace sectionPlace, SectionDto sectionDto){
        return SectionPlaceDto.builder()
                .sectionDto(sectionDto)
                .placeDto(PlaceDto.from(sectionPlace.getPlace()))
                .sequence(sectionPlace.getSequence())
                .build();
    }
}
