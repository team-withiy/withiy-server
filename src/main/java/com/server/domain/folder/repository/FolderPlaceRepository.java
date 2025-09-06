package com.server.domain.folder.repository;

import com.server.domain.folder.entity.FolderPlace;
import com.server.domain.place.entity.Place;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FolderPlaceRepository extends JpaRepository<FolderPlace, Long> {

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("DELETE FROM FolderPlace fp WHERE fp.folder.id = :folderId")
	int deleteByFolderId(Long folderId);

	@Query("SELECT p FROM FolderPlace fp " +
		"JOIN fp.place p " +
		"LEFT JOIN p.category " +
		"WHERE fp.folder.id = :folderId " +
		"AND p.id < :cursor " +
		"ORDER BY p.id DESC")
	List<Place> findNextPlacesByFolder(@Param("folderId") Long folderId,
		@Param("cursor") Long cursor,
		Pageable pageable);

	@Query("SELECT p FROM FolderPlace fp " +
		"JOIN fp.place p " +
		"LEFT JOIN p.category " +
		"WHERE fp.folder.id = :folderId " +
		"AND p.id > :cursor " +
		"ORDER BY p.id ASC")
	List<Place> findPrevPlacesByFolder(@Param("folderId") Long folderId,
		@Param("cursor") Long cursor,
		Pageable pageable);

	@Query("SELECT f.id \n"
		+ "    FROM FolderPlace fp\n"
		+ "    JOIN fp.folder f\n"
		+ "    WHERE fp.place.id = :placeId\n"
		+ "      AND f.user.id = :userId")
	List<Long> findFolderIdsByPlaceIdAndUserId(Long placeId, Long userId);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("""
		  DELETE FROM FolderPlace fp
		  WHERE fp.place.id = :placeId
		    AND fp.folder.user.id = :ownerId
		    AND fp.folder.id IN :folderIds
		""")
	int deleteByFolderIdsAndPlaceIdAndOwner(Set<Long> folderIds, Long placeId, Long ownerId);

	@Query("SELECT fp FROM FolderPlace fp " +
		"JOIN FETCH fp.place p " +
		"WHERE fp.folder.id IN :folderIds")
	List<FolderPlace> findFolderPlacesByFolderIds(@Param("folderIds") List<Long> folderIds);

	@Query("SELECT p FROM Place p WHERE p.id IN (" +
		"SELECT fp.place.id FROM FolderPlace fp " +
		"JOIN fp.folder f " +
		"WHERE f.user.id = :userId " +
		"AND (:cursor IS NULL OR fp.place.id < :cursor) " +
		"ORDER BY fp.place.id DESC" +
		") ORDER BY p.id DESC")
	List<Place> findNextPlacesByUser(@Param("userId") Long userId,
		@Param("cursor") Long cursor,
		Pageable pageable);

	@Query("SELECT fp.place.id FROM FolderPlace fp " +
		"JOIN fp.folder f " +
		"WHERE f.user.id = :userId " +
//		"AND (:cursor IS NULL OR fp.place.id < :cursor) " +
		"ORDER BY fp.place.id DESC")
	List<Long> findNextPlaceIdsByUser(
		@Param("userId") Long userId
//		,
//		@Param("cursor") Long cursor,
//		Pageable pageable
	);

	@Query("SELECT p FROM Place p WHERE p.id IN (" +
		"  SELECT fp.place.id FROM FolderPlace fp " +
		"  JOIN fp.folder f " +
		"  WHERE f.user.id = :userId " +
		"  AND (:cursor IS NULL OR fp.place.id > :cursor)" +
		"  ORDER BY fp.place.id ASC" +
		") ORDER BY p.id ASC")
	List<Place> findPrevPlacesByUser(@Param("userId") Long userId,
		@Param("cursor") Long cursor,
		Pageable pageable);


}
