package com.server.domain.album.entity;

import com.server.domain.photo.entity.Photo;
import com.server.domain.user.entity.Couple;
import com.server.global.common.BaseTime;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "album")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Where(clause = "deleted = false") // 쿼리 시 deleted=false인 레코드만 조회
public class Album extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "title")
	private String title;

	@Column(name = "schedule_at")
	private LocalDate scheduleAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "couple_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Couple couple;

	@Column(name = "deleted")
	@Builder.Default
	private boolean deleted = false;

	@Builder.Default
	@OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<AlbumPhoto> albumPhotos = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<AlbumComment> albumComments = new ArrayList<>();

	public void addPhoto(Photo photo) {
		if (this.albumPhotos == null) {
			this.albumPhotos = new ArrayList<>();
		}
		AlbumPhoto albumPhoto = new AlbumPhoto(this, photo);
		this.albumPhotos.add(albumPhoto);
		photo.getAlbumPhotos().add(albumPhoto);
	}

	public void delete() {
		this.deleted = true;
	}

}
