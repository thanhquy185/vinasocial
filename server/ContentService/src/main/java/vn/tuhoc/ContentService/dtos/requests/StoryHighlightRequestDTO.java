package vn.tuhoc.ContentService.dtos.requests;

import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StoryHighlightRequestDTO {
    @NotBlank(message = "Tên mục nổi bật không được để trống.")
    @Size(max = 30, message = "Tên mục nổi bật không được vượt quá 30 ký tự.")
    private String title;

    @NotEmpty(message = "Hãy chọn ít nhất một tin.")
    @Size(max = 100, message = "Mỗi mục nổi bật chứa tối đa 100 tin.")
    private List<Integer> storyIds;
}
