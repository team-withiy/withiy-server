package com.server.domain.user.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.server.domain.album.entity.Album;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "couple")
public class Couple {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user1;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user2;

    @Column(name = "first_met_date")
    private LocalDate firstMetDate;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(name = "deletedAt", nullable = true)
    private LocalDateTime deletedAt;


    public Couple(User user1, User user2, LocalDate firstMetDate){
        this.user1 = user1;
        this.user2 = user2;
        this.firstMetDate = firstMetDate;
    }
}
