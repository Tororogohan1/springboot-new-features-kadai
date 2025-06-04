package com.example.samuraitravel.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.entity.Favorite;
import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.repository.FavoriteRepository;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.UserRepository;

@Service
public class FavoriteService {
    private FavoriteRepository favoriteRepository;
    private UserRepository userRepository;
    private HouseRepository houseRepository;

    public FavoriteService(FavoriteRepository favoriteRepository, UserRepository userRepository, HouseRepository houseRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.houseRepository = houseRepository;
    }

    // データの保存
    @Transactional
    public void create(House house, User user) {
        Favorite favorite = new Favorite();

        favorite.setHouse(house);
        favorite.setUser(user);

        favoriteRepository.save(favorite);
    }

    // データの削除
    @Transactional
    public void delete(Integer houseId, Integer userId) {
        favoriteRepository.deleteByHouseIdAndUserId(houseId, userId);
    }
}