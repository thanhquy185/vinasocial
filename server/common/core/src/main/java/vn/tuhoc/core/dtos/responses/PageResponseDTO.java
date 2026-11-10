package vn.tuhoc.core.dtos.responses;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PageResponseDTO<T> {
    private List<T> content;

    private int totalPages;

    private long totalElements;

    private boolean first;

    private boolean last;

    private int numberOfElements;

    private int size;

    private int number;
}
