package vn.tuhoc.core.utils;

import org.springframework.data.domain.Page;

import vn.tuhoc.core.dtos.responses.PageResponseDTO;

public class PageResponseUtil {
    public static <T> PageResponseDTO<T> convert(Page<T> page) {
        return new PageResponseDTO<T>(
                page.getContent(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isFirst(),
                page.isLast(),
                page.getNumberOfElements(),
                page.getSize(),
                page.getNumber());
    }
}
