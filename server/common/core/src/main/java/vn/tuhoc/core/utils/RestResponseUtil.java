package vn.tuhoc.core.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import vn.tuhoc.core.dtos.responses.RestResponseDTO;

public class RestResponseUtil {
    public static <T> ResponseEntity<RestResponseDTO<T>> ok(String message, T data) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(RestResponseDTO.ok(message, data));
    }

    public static <T> ResponseEntity<RestResponseDTO<T>> okWithCookie(String cookie, String message, T data) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie)
                .body(RestResponseDTO.ok(message, data));
    }

    public static <T> ResponseEntity<RestResponseDTO<T>> created(String message, T data) {
        return ResponseEntity
                .status(HttpStatus.CREATED

                ).body(RestResponseDTO.created(message, data));
    }

    public static <T> ResponseEntity<RestResponseDTO<T>> badRequest(String error, String message) {
        return error(HttpStatus.BAD_REQUEST, error, message);
    }

    public static <T> ResponseEntity<RestResponseDTO<T>> notFound(String error, String message) {
        return error(HttpStatus.NOT_FOUND, error, message);
    }

    public static <T> ResponseEntity<RestResponseDTO<T>> conflict(String error, String message) {
        return error(HttpStatus.CONFLICT, error, message);
    }

    public static <T> ResponseEntity<RestResponseDTO<T>> serviceUnavailable(String error, String message) {
        return error(HttpStatus.SERVICE_UNAVAILABLE, error, message);
    }

    public static <T> ResponseEntity<RestResponseDTO<T>> error(
            HttpStatus status, String error, String message) {
        return ResponseEntity.status(status).body(RestResponseDTO.<T>builder()
                .at(TimeUtil.getCurrentDatetime())
                .status(status.value())
                .error(error)
                .message(message)
                .data(null)
                .build());
    }
}
