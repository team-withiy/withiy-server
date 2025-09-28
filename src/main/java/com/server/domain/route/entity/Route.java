package com.server.domain.route.entity;

import com.server.domain.dateSchedule.entity.DateSchedule;
import com.server.domain.route.dto.RouteStatus;
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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
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
    @Column(name ="route_type")
    private RouteType routeType;

	@Column(name = "deleted_at", nullable = true)
	private LocalDateTime deletedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dateSchedule_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DateSchedule dateSchedule;

    @OneToMany(mappedBy = "route", fetch = FetchType.LAZY)
    private List<DateSchedule> dateSchedules = new ArrayList<>();

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RoutePlace> routePlaces = new ArrayList<>();

    public void updateStatus(RouteStatus routeStatus) {
        this.status = routeStatus;
    }
}
