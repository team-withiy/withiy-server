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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
@Table(name = "place")
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

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
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PlaceStatus status;

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public Place(String name, String region1depth, String region2depth, String region3depth,
                 String address, String latitude, String longitude, Long likeCount,
                 User user, Category category, PlaceStatus status) {
        this.name = name;
        this.region1depth = region1depth;
        this.region2depth = region2depth;
        this.region3depth = region3depth;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.likeCount = likeCount;
        this.user = user;
        this.category = category;
        this.status = status;
    }


    public boolean isCreatedByAdmin() {
        return user != null && user.isAdmin();
    }
}
