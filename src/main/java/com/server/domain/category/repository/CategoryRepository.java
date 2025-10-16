package com.server.domain.category.repository;

import com.server.domain.category.entity.Category;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	@Query("SELECT c FROM Category c WHERE LOWER(c.name) = LOWER(:name)")
	Optional<Category> findByNameIgnoreCase(@Param("name") String name);
}
