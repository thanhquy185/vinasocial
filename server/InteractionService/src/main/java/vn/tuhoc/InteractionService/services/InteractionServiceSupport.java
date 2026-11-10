package vn.tuhoc.InteractionService.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;

import vn.tuhoc.InteractionService.exceptions.InteractionException;
import vn.tuhoc.core.utils.ValidationUtil;
import vn.tuhoc.security.utils.SecurityUtil;

final class InteractionServiceSupport {
    private InteractionServiceSupport() {}

    static Integer currentUserId() {
        return SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("Bạn cần đăng nhập để thực hiện thao tác này."));
    }

    static void requirePositiveId(Integer id, String field) {
        if (ValidationUtil.isNull(id) || id < 1) {
            throw new InteractionException(HttpStatus.BAD_REQUEST, "INVALID_" + field.toUpperCase(),
                    field + " phải là số nguyên dương.");
        }
    }

    static Pageable pageable(int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new InteractionException(HttpStatus.BAD_REQUEST, "INVALID_PAGE",
                    "page phải >= 0, size từ 1 đến 100.");
        }
        return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt", "id"));
    }

    static InteractionException notFound(String code, String message) {
        return new InteractionException(HttpStatus.NOT_FOUND, code, message);
    }

    static InteractionException badRequest(String code, String message) {
        return new InteractionException(HttpStatus.BAD_REQUEST, code, message);
    }
}
