package com.server.domain.folder.repository;

import com.server.domain.folder.entity.Folder;
import com.server.domain.folder.entity.FolderPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FolderPlaceRepository extends JpaRepository<FolderPlace, Long> {

	@Query("SELECT CASE WHEN COUNT(fp) > 0 THEN true ELSE false END FROM FolderPlace fp WHERE fp.folder.user.id = :userId AND fp.place.id = :placeId")
	boolean existsByUserIdAndPlaceId(@Param("userId") Long userId, @Param("placeId") Long placeId);

	void deleteByFolder(Folder folder);
}
