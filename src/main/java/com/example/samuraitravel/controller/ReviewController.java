package com.example.samuraitravel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.ReviewEditForm;
import com.example.samuraitravel.form.ReviewRegisterForm;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.ReviewRepository;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.ReviewService;


@Controller
@RequestMapping("/houses/{houseId}/review")
public class ReviewController {
    private final ReviewRepository reviewRepository;
    private final ReviewService reviewService;
    private final HouseRepository houseRepository;

    public ReviewController(ReviewRepository reviewRepository, HouseRepository houseRepository, ReviewService reviewService) {
        this.reviewRepository = reviewRepository;
        this.reviewService = reviewService;
        this.houseRepository = houseRepository;
    }
    
    //レビュー一覧
    @GetMapping("/table")
    public String table(@PathVariable Integer houseId,
                        Model model,
                        @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable) {
        House house = houseRepository.getReferenceById(houseId);
        Page<Review> reviewPage = reviewRepository.findByHouse(house, pageable);
        model.addAttribute("house", house);
        model.addAttribute("reviewPage", reviewPage);
        return "reviews/table";
    }
    
    //レビュー投稿
    @GetMapping("/post")
    public String post(@PathVariable Integer houseId, Model model) {
        House house = houseRepository.getReferenceById(houseId);
        model.addAttribute("house", house);
        model.addAttribute("reviewRegisterForm", new ReviewRegisterForm());
        return "reviews/register";
    }
    
    //レビュー作成
    @PostMapping("/create")
    public String create(@PathVariable Integer houseId,
                         @ModelAttribute @Validated ReviewRegisterForm reviewRegisterForm,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {

        if (bindingResult.hasErrors()) {
            return "reviews/register";
        }

        User user = userDetailsImpl.getUser();
        reviewRegisterForm.setUserId(user.getId());
        reviewRegisterForm.setHouseId(houseId);
        reviewService.create(reviewRegisterForm);

        redirectAttributes.addFlashAttribute("successMessage", "レビューを登録しました。");
        return "redirect:/houses/" + houseId;
    }

    //レビュー編集
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Integer houseId,
                       @PathVariable Integer id,
                       @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                       Model model) {

        House house = houseRepository.getReferenceById(houseId);
        Review review = reviewRepository.getReferenceById(id);

        if (!userDetailsImpl.getUser().getId().equals(review.getUser().getId())) {
            return "redirect:/houses/" + houseId;
        }

        ReviewEditForm reviewEditForm = new ReviewEditForm(
                review.getId(),
                house.getId(),
                String.valueOf(review.getReviewStar()),
                review.getReviewComment()
        );

        model.addAttribute("house", house);
        model.addAttribute("reviewEditForm", reviewEditForm);
        return "reviews/edit";
    }

    //レビュー更新
    @PostMapping("/{id}/update")
    public String update(@PathVariable Integer houseId,
    		             @PathVariable("id") Integer id,
    	                 @ModelAttribute @Validated ReviewEditForm reviewEditForm,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            House house = houseRepository.getReferenceById(houseId);
            model.addAttribute("house", house);
            return "reviews/edit";
        }

        reviewService.update(reviewEditForm);
        redirectAttributes.addFlashAttribute("successMessage", "レビューを編集しました。");
        return "redirect:/houses/" + houseId + "/review/table";
    }
    
    //レビュー削除
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer houseId,
                         @PathVariable Integer id,
                         RedirectAttributes redirectAttributes,
                         @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {

        Review review = reviewRepository.getReferenceById(id);
        if (!userDetailsImpl.getUser().getId().equals(review.getUser().getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "自分のレビューのみ削除できます。");
            return "redirect:/houses/" + houseId;
        }

        reviewRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "レビューを削除しました。");
        return "redirect:/houses/" + houseId;
    }
}
