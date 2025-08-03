package com.server.domain.place.entity;

import com.server.domain.album.entity.Album;
import com.server.domain.album.entity.PlaceAlbum;
import com.server.domain.category.entity.Category;
import com.server.domain.course.entity.CoursePlace;
import com.server.domain.folder.entity.FolderPlace;
import com.server.domain.photo.entity.Photo;
import com.server.domain.place.dto.PlaceDto;
import com.server.domain.place.dto.PlaceStatus;
import com.server.domain.review.entity.Review;
import com.server.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "place")
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "region_1depth")
    private String region1depth;

    @Column(name = "region_2depth")
    private String region2depth;

    @Column(name = "region_3depth")
    private String region3depth;

    @Column(name = "address")
    private String address;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "like_count")
    private Long likeCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PlaceStatus status;

    public Place(String name, String region1depth, String region2depth, String region3depth, String address,
                 String latitude, String longitude, Category category){
        this.name = name;
        this.region1depth = region1depth;
        this.region2depth = region2depth;
        this.region3depth = region3depth;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
    }

    public Place(String name, String address,
                 String latitude, String longitude, Category category, Long likeCount){
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
        this.likeCount = likeCount;
    }

    public boolean isCreatedByAdmin() {
        return createdBy != null && createdBy.isAdmin();
    }
}
