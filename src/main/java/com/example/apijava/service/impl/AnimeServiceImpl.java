package com.example.apijava.service.impl;

import com.example.apijava.models.Anime;
import com.example.apijava.repositorys.AnimeRepository;
import com.example.apijava.service.inteface.AnimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnimeServiceImpl implements AnimeService {

    private final AnimeRepository animeRepository;

    @Autowired
    public AnimeServiceImpl(AnimeRepository animeRepository) {
        this.animeRepository = animeRepository;
    }

    @Override
    public List<Anime> findAll() {
        return animeRepository.findAll();
    }

    @Override
    public Anime findById(Integer id) {
        return animeRepository.findById(id).orElse(null);
    }

    @Override
    public boolean addAnime(Anime anime) {
        return Optional.of(anime).filter(a -> !animeRepository.existsByAnimeName(anime.getAnimeName()))
                .map(animeRepository::save)
                .orElseThrow()
                .getAnimeId() != null;
    }

    @Override
    public boolean updateAnime(Anime anime, Integer id) {
        return Optional.ofNullable(findById(id)).map(oldAnime -> {
            oldAnime.setAnimeName(anime.getAnimeName());
            oldAnime.setDescription(anime.getDescription());
            oldAnime.setHot(anime.isHot());
            oldAnime.setView(anime.getView());
            oldAnime.setVideo(anime.getVideo());
            oldAnime.setImage(anime.getImage());
            oldAnime.setStatus(anime.isStatus());
            oldAnime.setCategories(anime.getCategories());
            animeRepository.save(oldAnime);
            return true;
        }).orElse(false);
    }

    @Override
    public boolean deleteAnime(Integer id) {
        animeRepository.findById(id)
                .ifPresentOrElse(animeRepository :: delete, ()-> {
                    throw new RuntimeException("Anime not found");
                });
        return true;
    }

    @Override
    public Page<Anime> findAll(String keyword, Integer pageNO) {
        Pageable pageable = PageRequest.of(pageNO - 1, 10);
        return animeRepository.searchAnime(keyword, pageable);
    }

    @Override
    public Page<Anime> findAllByCategory(Integer categoryId, Integer pageNO) {
        Pageable pageable = PageRequest.of(pageNO - 1, 10);
        return animeRepository.findAnimeByCategory(categoryId, pageable);
    }
}
