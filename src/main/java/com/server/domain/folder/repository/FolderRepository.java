package com.server.domain.folder.repository;

import com.server.domain.folder.entity.Folder;
import com.server.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    boolean existsByUserAndName(User user, String folderName);

    Optional<Folder> findByIdAndUser(Long id, User user);
}
