package com.example.apijava.repositorys;

import com.example.apijava.models.Anime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimeRepository extends JpaRepository<Anime, Integer> {
    @Query("select a from Anime a JOIN a.categories c where c.categoryId = :categoryId")
    Page<Anime> findAnimeByCategory(@Param("categoryId") Integer categoryId, Pageable pageable);

    @Query("select a from Anime a where a.animeName like %:keyword%")
    Page<Anime> searchAnime(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByAnimeName(String animeName);
}
