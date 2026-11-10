package vn.tuhoc.core.dtos.responses;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.core.utils.TimeUtil;

@Data
@Builder
@NoArgsConstructor 
@AllArgsConstructor 
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RestResponseDTO<T> {
        String at;
        int status;
        String error;
        Object message;
        T data;

        public static <T> RestResponseDTO<T> ok(
                        String message,
                        T data) {
                return RestResponseDTO.<T>builder()
                                .at(TimeUtil.getCurrentDatetime())
                                .status(HttpStatus.OK.value())
                                .error(null)
                                .message(message)
                                .data(data)
                                .build();
        }

        public static <T> RestResponseDTO<T> created(
                        String message,
                        T data) {
                return RestResponseDTO.<T>builder()
                                .at(TimeUtil.getCurrentDatetime())
                                .status(HttpStatus.CREATED.value())
                                .error(null)
                                .message(message)
                                .data(data)
                                .build();
        }

        public static <T> RestResponseDTO<T> badRequest(
                        String error,
                        String message) {
                return RestResponseDTO.<T>builder()
                                .at(TimeUtil.getCurrentDatetime())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error(error)
                                .message(message)
                                .data(null)
                                .build();
        }

        public static <T> RestResponseDTO<T> notFound(
                        String error,
                        String message) {
                return RestResponseDTO.<T>builder()
                                .at(TimeUtil.getCurrentDatetime())
                                .status(HttpStatus.NOT_FOUND.value())
                                .error(error)
                                .message(message)
                                .data(null)
                                .build();
        }
}
