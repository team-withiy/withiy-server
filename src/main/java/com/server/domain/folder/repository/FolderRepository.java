package com.server.domain.folder.repository;

import com.server.domain.folder.entity.Folder;
import com.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    Folder findByNameAndUserId(String name, Long userId);
    Folder findByIdAndUserId(Long id, Long userId);
}
