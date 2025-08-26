package com.server.domain.folder.repository;

import com.server.domain.folder.entity.Folder;
import com.server.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FolderRepository extends JpaRepository<Folder, Long> {

	boolean existsByUserAndName(User user, String folderName);

	Optional<Folder> findByIdAndUserId(Long id, Long userId);

	@Query("SELECT f FROM Folder f WHERE f.user.id = :userId")
	List<Folder> findFoldersByUserId(@Param("userId") Long userId);
}
