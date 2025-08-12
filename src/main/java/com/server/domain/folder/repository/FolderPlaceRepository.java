package com.server.domain.folder.repository;

import com.server.domain.folder.entity.FolderPlace;
import com.server.domain.place.entity.Place;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FolderPlaceRepository extends JpaRepository<FolderPlace, Long> {

	@Query("DELETE FROM FolderPlace fp WHERE fp.folder.id = :folderId")
	void deleteByFolderId(Long folderId);

	@Query("SELECT fp.place FROM FolderPlace fp WHERE fp.folder.id = :folderId")
	List<Place> findPlacesByFolderId(@Param("folderId") Long folderId);
}
