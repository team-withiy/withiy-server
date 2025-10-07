package com.server.domain.folder.repository;

import com.server.domain.folder.entity.FolderPlace;
import com.server.domain.folder.repository.projection.PlaceBookmarkProjection;
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

	@Query("SELECT fp.place.id FROM FolderPlace fp " +
		"WHERE fp.folder.id = :folderId " +
		"AND (:cursor IS NULL OR fp.place.id < :cursor) " +
		"ORDER BY fp.place.id DESC")
	List<Long> findNextPlaceIdsByFolder(@Param("folderId") Long folderId,
		@Param("cursor") Long cursor, Pageable pageable);

	@Query("SELECT fp.place.id FROM FolderPlace fp " +
		"WHERE fp.folder.id = :folderId " +
		"AND (:cursor IS NULL OR fp.place.id > :cursor) " +
		"ORDER BY fp.place.id ASC")
	List<Long> findPrevPlaceIdsByFolder(@Param("folderId") Long folderId,
		@Param("cursor") Long cursor, Pageable pageable);

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

	@Query("SELECT DISTINCT fp.place.id FROM FolderPlace fp " +
		"JOIN fp.folder f " +
		"WHERE f.user.id = :userId " +
		"AND (:cursor IS NULL OR fp.place.id < :cursor) " +
		"ORDER BY fp.place.id DESC")
	List<Long> findNextPlaceIdsByUser(@Param("userId") Long userId,
		@Param("cursor") Long cursor, Pageable pageable);

	@Query("SELECT DISTINCT fp.place.id FROM FolderPlace fp " +
		"JOIN fp.folder f " +
		"WHERE f.user.id = :userId " +
		"AND (:cursor IS NULL OR fp.place.id > :cursor) " +
		"ORDER BY fp.place.id ASC")
	List<Long> findPrevPlaceIdsByUser(@Param("userId") Long userId,
		@Param("cursor") Long cursor, Pageable pageable);

	@Query("SELECT COUNT(fp) FROM FolderPlace fp WHERE fp.folder.id = :folderId")
	long countPlacesInFolder(Long folderId);

	@Query("SELECT COUNT(DISTINCT fp.place.id) FROM FolderPlace fp " +
		"JOIN fp.folder f " +
		"WHERE f.user.id = :userId")
	long countDistinctPlacesByUser(Long userId);

	@Query("SELECT CASE WHEN COUNT(fp) > 0 THEN true ELSE false END " +
		"FROM FolderPlace fp " +
		"WHERE fp.folder.id = :folderId AND fp.place.id < :cursor")
	boolean existsNextPlaceByFolder(Long folderId, Long cursor);

	@Query("SELECT CASE WHEN COUNT(fp) > 0 THEN true ELSE false END " +
		"FROM FolderPlace fp " +
		"WHERE fp.folder.id = :folderId AND fp.place.id > :cursor")
	boolean existsPrevPlaceByFolder(Long folderId, Long cursor);

	@Query("SELECT CASE WHEN COUNT(DISTINCT fp.place.id) > 0 THEN true ELSE false END " +
		"FROM FolderPlace fp " +
		"JOIN fp.folder f " +
		"WHERE f.user.id = :userId AND fp.place.id < :cursor")
	boolean existsNextPlaceByUser(Long userId, Long cursor);

	@Query("SELECT CASE WHEN COUNT(DISTINCT fp.place.id) > 0 THEN true ELSE false END " +
		"FROM FolderPlace fp " +
		"JOIN fp.folder f " +
		"WHERE f.user.id = :userId AND fp.place.id > :cursor")
	boolean existsPrevPlaceByUser(Long userId, Long cursor);

	@Query("SELECT p.id AS placeId, " +
		"CASE WHEN EXISTS (" +
		"   SELECT 1 FROM FolderPlace fp " +
		"   WHERE fp.place.id = p.id AND fp.folder.user.id = :userId" +
		") THEN true ELSE false END AS bookmarked " +
		"FROM Place p " +
		"WHERE p.id IN :placeIds")
	List<PlaceBookmarkProjection> findPlaceBookmarks(
		@Param("placeIds") List<Long> placeIds,
		@Param("userId") Long userId
	);

	@Query("SELECT DISTINCT fp.place FROM FolderPlace fp " +
		"JOIN fp.folder f " +
		"WHERE f.user.id = :userId")
	List<Place> findBookmarkedPlacesByUserId(@Param("userId") Long userId);

	@Query("SELECT COUNT(DISTINCT f.user.id) " +
		"FROM FolderPlace fp " +
		"JOIN fp.folder f " +
		"WHERE fp.place.id = :placeId")
	long countDistinctUsersByPlaceId(@Param("placeId") Long placeId);
}
