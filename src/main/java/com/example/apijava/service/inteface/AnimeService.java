package com.example.apijava.service.inteface;

import com.example.apijava.models.Anime;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AnimeService {
    List<Anime> findAll();
    Anime findById(Integer id);
    boolean addAnime(Anime anime);
    boolean updateAnime(Anime anime, Integer id);
    boolean deleteAnime(Integer id);
    Page<Anime> findAll(String keyword, Integer pageNO);
    Page<Anime> findAllByCategory(Integer categoryId, Integer pageNO);
}
