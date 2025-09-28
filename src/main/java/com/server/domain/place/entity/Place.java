package com.server.domain.place.entity;

import com.server.domain.category.entity.Category;
import com.server.domain.place.dto.PlaceStatus;
import com.server.domain.place.dto.UpdatePlaceDto;
import com.server.domain.user.entity.User;
import com.server.global.common.BaseTime;
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
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Builder
@Table(name = "place")
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place extends BaseTime {

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
	private Double latitude;

	@Column(name = "longitude")
	private Double longitude;

	@Column(name = "score")
	private Long score;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "category_id")
	@OnDelete(action = OnDeleteAction.SET_NULL)
	private Category category;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private PlaceStatus status;

	@Column(name = "deleted_at", nullable = true)
	private LocalDateTime deletedAt;

  @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Photo> photos = new ArrayList<>();

  @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PlaceReview> placeReviews = new ArrayList<>();

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

    public void addPhoto(Photo photo) {
        this.photos.add(photo);
    }

    public void addReview(Category category, Long score, String review, String hashTag) {
        PlaceReview placeReview = PlaceReview.builder()
                .category(category)
                .score(score)
                .review(review)
                .hashTag(hashTag)
                .user(this.user)
                .place(this)
                .build();
        this.placeReviews.add(placeReview);
    }

    public void updateScore(Long score) {
        int totalReviews = this.getPlaceReviews().size(); // 이미 새 리뷰를 포함

        // (이전 평균 * (개수-1))을 총점으로 가정하고 계산
        if (totalReviews > 1) {
            // 기존 총점 (단순화: 이전 평균에 개수-1을 곱함)
            // NOTE: 이 방식도 부정확할 수 있으므로, 실제로는 'totalScore' 필드를 별도 관리해야 합니다.
            long previousTotalScore = this.score * (totalReviews - 1);
            this.score = (previousTotalScore + score) / totalReviews;
        } else { // 첫 번째 리뷰일 때
            this.score = score;
        }
        this.status = PlaceStatus.ACTIVE;
    }

}
