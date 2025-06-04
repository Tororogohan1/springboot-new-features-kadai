package com.example.samuraitravel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.Favorite;
import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.repository.FavoriteRepository;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.UserRepository;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.FavoriteService;
@Controller
public class FavoriteController {
    private final FavoriteRepository favoriteRepository;
    private final HouseRepository houseRepository;
    private final UserRepository userRepository;
    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteRepository favoriteRepository, HouseRepository houseRepository, UserRepository userRepository, FavoriteService favoriteService) {
        this.favoriteRepository = favoriteRepository;
        this.houseRepository = houseRepository;
        this.userRepository = userRepository;
        this.favoriteService = favoriteService;
    }

    // お気に入り登録
    @PostMapping("/houses/{id}/create")
    public String createFavorite(@PathVariable(name = "id") Integer houseId, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, RedirectAttributes redirectAttributes) {
        Integer userId = userDetailsImpl.getUser().getId();
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        House house = houseRepository.findById(houseId).orElseThrow(() -> new RuntimeException("House not found"));

        favoriteService.create(house, user);
        Boolean favoriteExists = favoriteRepository.existsByHouseIdAndUserId(houseId, userId);
//        favoriteExists = favoriteExists != null ? favoriteExists : false; // nullチェックとデフォルト値の設定
        redirectAttributes.addFlashAttribute("favoriteExists", favoriteExists);
        redirectAttributes.addFlashAttribute("message", "お気に入りに追加しました");
        return "redirect:/houses/{id}";
    }

    // お気に入り解除
    @PostMapping("/houses/{id}/delete")
    public String deleteFavorite(@PathVariable(name = "id") Integer houseId, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, RedirectAttributes redirectAttributes) {
        Integer userId = userDetailsImpl.getUser().getId();
        favoriteService.delete(houseId, userId);
        redirectAttributes.addFlashAttribute("message", "お気に入りを解除しました");
        return "redirect:/houses/{id}";
    }
    // お気に入り一覧
    @GetMapping("/favorites")
    public String showFavorites(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails, @PageableDefault(size = 10) Pageable pageable) {
        User user = userDetails.getUser();
        Page<Favorite> favorites = favoriteRepository.findByUser(user, pageable);
        model.addAttribute("favoritesPage", favorites);
        return "favorites/index"; 
    }
}