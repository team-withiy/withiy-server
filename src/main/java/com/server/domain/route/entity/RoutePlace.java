package com.server.domain.route.entity;

import com.server.domain.place.entity.Place;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "route_place")
@EntityListeners(AuditingEntityListener.class)
public class RoutePlace {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "route_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Route route;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "place_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Place place;

    public RoutePlace(Route route, Place place) {
        this.route = route;
        this.place = place;
    }
}
