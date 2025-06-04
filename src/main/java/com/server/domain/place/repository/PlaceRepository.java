package com.server.domain.place.repository;

import com.server.domain.place.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    @Query(value = "SELECT * FROM place p WHERE " +
            "CAST(p.latitude AS DECIMAL(10,8)) BETWEEN CAST(:minLat AS DECIMAL(10,8)) AND CAST(:maxLat AS DECIMAL(10,8)) AND " +
            "CAST(p.longitude AS DECIMAL(11,8)) BETWEEN CAST(:minLng AS DECIMAL(11,8)) AND CAST(:maxLng AS DECIMAL(11,8))",
            nativeQuery = true)
    List<Place> findByLatitudeBetweenAndLongitudeBetween(
            @Param("minLat") String minLat, @Param("maxLat") String maxLat,
            @Param("minLng") String minLng, @Param("maxLng") String maxLng);



}
