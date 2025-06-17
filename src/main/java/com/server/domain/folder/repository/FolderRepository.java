package com.server.domain.folder.repository;

import com.server.domain.folder.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    Folder findByName(String name);
}
