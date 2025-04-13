package com.example.samuraitravel.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewEditForm {

    @NotNull
    private Integer id;

    @NotNull
    private Integer houseId;

    @NotNull(message = "評価を選択してください。")
    @Min(value = 1, message = "1〜5の間で選択してください。")
    @Max(value = 5, message = "1〜5の間で選択してください。")
    private String reviewStar;

    @NotBlank(message = "コメントを入力してください。")
    private String reviewComment;
}
