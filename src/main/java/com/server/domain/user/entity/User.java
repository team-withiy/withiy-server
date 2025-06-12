package com.server.domain.user.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.server.domain.album.entity.Album;
import com.server.domain.course.entity.CourseBookmark;
import com.server.domain.course.entity.CoursePlace;
import com.server.domain.place.entity.PlaceBookmark;
import com.server.domain.review.entity.Review;
import com.server.domain.schedule.entity.Schedule;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.server.domain.oauth.entity.OAuth;
import com.server.domain.term.entity.Term;
import com.server.domain.term.entity.TermAgreement;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Slf4j
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin;

    @Column(name = "refresh_token", length = 512)
    private String refreshToken;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "code", nullable = true)
    private String code;

    @Column(name = "deletedAt", nullable = true)
    private LocalDateTime deletedAt;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true,
            fetch = FetchType.LAZY)
    @JsonIgnore
    private List<OAuth> oAuth;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true,
            fetch = FetchType.EAGER)
    @JsonIgnore
    private List<TermAgreement> termAgreements = new ArrayList<>();

    @OneToOne(mappedBy = "user1", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Couple coupleAsUser1;

    @OneToOne(mappedBy = "user2", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Couple coupleAsUser2;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlaceBookmark> placeBookmarks = new ArrayList<>();


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseBookmark> courseBookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Album> albums = new ArrayList<>();


    @Builder
    public User(String nickname, String thumbnail, List<Term> terms, String code) {
        this.nickname = nickname;
        this.thumbnail = thumbnail;
        this.isAdmin = false;
        this.code = code;
        for (Term term : terms) {
            this.termAgreements.add(TermAgreement.builder().user(this).term(term).build());
        }
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Couple getCouple() {
        return coupleAsUser1 != null ? coupleAsUser1 : coupleAsUser2;
    }

    public boolean isConnectedCouple() {
        return coupleAsUser1 != null || coupleAsUser2 != null;
    }
}
