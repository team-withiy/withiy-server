package com.server.domain.folder.repository;

import com.server.domain.folder.entity.FolderPlace;
import com.server.domain.place.entity.Place;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FolderPlaceRepository extends JpaRepository<FolderPlace, Long> {

	@Modifying
	@Query("DELETE FROM FolderPlace fp WHERE fp.folder.id = :folderId")
	void deleteByFolderId(Long folderId);

	@Query("SELECT fp.place FROM FolderPlace fp WHERE fp.folder.id = :folderId")
	List<Place> findPlacesByFolderId(@Param("folderId") Long folderId);

	boolean existsByFolderIdAndPlaceId(Long folderId, Long placeId);
	
	@Query("SELECT fp FROM FolderPlace fp WHERE fp.folder.id = :folderId AND fp.place.id = :placeId")
	Optional<FolderPlace> findByFolderIdAndPlaceId(Long folderId, Long placeId);
}
