package com.server.domain.place.entity;

import com.server.domain.category.entity.Category;
import com.server.domain.photo.entity.Photo;
import com.server.domain.place.dto.PlaceStatus;
import com.server.domain.place.dto.UpdatePlaceDto;
import com.server.domain.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

	@Column(name = "score")
	private Long score;

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

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Photo> photos = new ArrayList<>();

	@Builder
	public Place(String name, String region1depth, String region2depth, String region3depth,
		String address, String latitude, String longitude, Long score,
		User user, Category category, PlaceStatus status) {
		this.name = name;
		this.region1depth = region1depth;
		this.region2depth = region2depth;
		this.region3depth = region3depth;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		this.score = score;
		this.user = user;
		this.category = category;
		this.status = status;
	}

	public boolean isCreatedByAdmin() {
		return user != null && user.isAdmin();
	}
	
	public void update(UpdatePlaceDto dto, Category category) {
		if (dto.getName() != null) {
			this.name = dto.getName();
		}
		if (dto.getAddress() != null) {
			this.address = dto.getAddress();
		}
		if (dto.getRegion1depth() != null) {
			this.region1depth = dto.getRegion1depth();
		}
		if (dto.getRegion2depth() != null) {
			this.region2depth = dto.getRegion2depth();
		}
		if (dto.getRegion3depth() != null) {
			this.region3depth = dto.getRegion3depth();
		}
		if (dto.getLatitude() != null) {
			this.latitude = dto.getLatitude();
		}
		if (dto.getLongitude() != null) {
			this.longitude = dto.getLongitude();
		}
		if (dto.getScore() != null) {
			this.score = dto.getScore();
		}
		if (category != null) {
			this.category = category;
		}
	}
}
