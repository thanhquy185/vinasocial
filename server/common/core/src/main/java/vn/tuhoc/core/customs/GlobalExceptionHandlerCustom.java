package vn.tuhoc.core.customs;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import vn.tuhoc.core.dtos.responses.RestResponseDTO;
import vn.tuhoc.core.utils.TimeUtil;

@RestControllerAdvice
public class GlobalExceptionHandlerCustom {
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<?> validationError(MethodArgumentNotValidException exception) {
                // log.error("Exception: ", exception);

                BindingResult result = exception.getBindingResult();
                List<FieldError> fieldErrors = result.getFieldErrors();

                List<Map<String, String>> errors = fieldErrors.stream()
                                .map(fieldError -> Map.of(
                                                "field", fieldError.getField(),
                                                "message", fieldError.getDefaultMessage()))
                                .collect(Collectors.toList());

                RestResponseDTO<Object> response = RestResponseDTO.builder()
                                .at(TimeUtil.getCurrentDatetime())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("VALIDATION_ERROR")
                                .message("Dữ liệu không hợp lệ!")
                                .data(errors)
                                .build();

                return ResponseEntity.badRequest().body(response);
        }

        // Tạm thời tắt xử lý riêng cho xung đột dữ liệu.
        // @ExceptionHandler(DataIntegrityViolationException.class)
        // public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException exception) {
        //         return RestResponseUtil.conflict(
        //                         "DATA_INTEGRITY_CONFLICT",
        //                         "Dữ liệu không hợp lệ hoặc đã xung đột với thay đổi khác; vui lòng kiểm tra và thử lại.");
        // }
}
