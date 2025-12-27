package com.server.domain.route.entity;

import com.server.domain.dateSchedule.entity.DateSchedule;
import com.server.domain.place.entity.Place;
import com.server.domain.user.entity.User;
import com.server.global.common.BaseTime;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "route")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Route extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private RouteStatus status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by")
	private User createdBy;

	@Column(name = "score")
	private Long score;

	@Enumerated(EnumType.STRING)
	@Column(name = "route_type")
	private RouteType routeType;

	@Column(name = "deleted_at", nullable = true)
	private LocalDateTime deletedAt;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "date_schedule_id", nullable = false)
	private DateSchedule dateSchedule;

	@OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@Builder.Default
	private List<RoutePlace> routePlaces = new ArrayList<>();

	public void updateStatus(RouteStatus routeStatus) {
		this.status = routeStatus;
	}

	/**
	 * 새로운 Route 생성
	 */
	public static Route createForDateSchedule(
		DateSchedule dateSchedule,
		String routeName,
		User creator,
		List<Place> places
	) {
		Route route = Route.builder()
			.name(routeName)
			.status(RouteStatus.WRITE)
			.createdBy(creator)
			.routeType(RouteType.COURSE)
			.dateSchedule(dateSchedule)
			.score(0L)
			.build();

		// RoutePlace 생성 및 연결
		for (Place place : places) {
			RoutePlace routePlace = new RoutePlace(route, place);
			route.routePlaces.add(routePlace);
		}

		return route;
	}

	/**
	 * Route 복제
	 */
	public Route copy(DateSchedule newDateSchedule, User copyingUser) {
		Route copiedRoute = Route.builder()
			.name(this.name)
			.status(RouteStatus.WRITE)
			.createdBy(copyingUser)
			.routeType(this.routeType)
			.dateSchedule(newDateSchedule)
			.score(0L)
			.build();

		// RoutePlace 복제 (새로운 RoutePlace 인스턴스 생성)
		for (RoutePlace originalRoutePlace : this.routePlaces) {
			RoutePlace copiedRoutePlace = new RoutePlace(
				copiedRoute,
				originalRoutePlace.getPlace()
			);
			copiedRoute.routePlaces.add(copiedRoutePlace);
		}

		return copiedRoute;
	}

	/**
	 * Place 추가
	 */
	public void addPlace(Place place) {
		RoutePlace routePlace = new RoutePlace(this, place);
		this.routePlaces.add(routePlace);
	}

	/**
	 * Place 제거
	 */
	public void removePlace(Place place) {
		this.routePlaces.removeIf(rp -> rp.getPlace().getId().equals(place.getId()));
	}
}
