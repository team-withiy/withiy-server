package com.server.domain.folder.entity;

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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
@Table(name = "folder_place")
public class FolderPlace {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "folder_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Folder folder;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "place_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Place place;

	@Builder
	public FolderPlace(Folder folder, Place place) {
		this.folder = folder;
		this.place = place;
	}
}
