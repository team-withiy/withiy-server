package com.server.domain.album.entity;

import com.server.domain.course.entity.Course;
import com.server.domain.photo.entity.Photo;
import com.server.domain.place.entity.Place;
import com.server.domain.dateSchedule.entity.DateSchedule;
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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "album")
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Place place;

    // 장소가 삭제되었을 경우 앨범에 장소 이름 저장해두기 위한 스냅샷
    @Column(name = "place_name_snapshot")
    private String placeNameSnapshot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "date_schedule_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DateSchedule dateSchedule;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Photo> photos = new ArrayList<>();


    public Album(String title, Place place, String placeName, User user) {
        this.title = title;
        this.place = place;
        this.placeNameSnapshot = placeName;
        this.user = user;
    }
}
