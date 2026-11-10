package vn.tuhoc.ContentService.dtos.responses;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoryHighlightResponseDTO {
    private Integer id;
    private String title;
    private List<StorySummaryResponseDTO> stories;
}
