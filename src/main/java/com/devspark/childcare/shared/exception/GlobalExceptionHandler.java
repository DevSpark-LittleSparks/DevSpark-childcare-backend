package com.devspark.childcare.shared.exception;

import com.devspark.childcare.payment.PaymentAlreadyPaidException;
import com.devspark.childcare.shared.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. When requested data is not found in the database (e.g., requesting a non-existent child's details) - 404 Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleNotFound(ResourceNotFoundException ex) {
        return ApiResponse.error(ex.getMessage());
    }

    // 2. When the user does not have permission to view or modify the specific data (Object-level authorization failure) - 403 Forbidden
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Void> handleUnauthorized(UnauthorizedException ex) {
        return ApiResponse.error(ex.getMessage());
    }

    // 3. When there are validation errors in the submitted data (e.g., leaving a mandatory name field blank) - 400 Bad Request
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        
        // Extract all field errors from the exception and put them into a Map
        ex.getBindingResult().getFieldErrors()
            .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        
        // Include the validation errors inside the 'data' field and send them to the Frontend
        ApiResponse<Map<String, String>> response = ApiResponse.error("Validation failed");
        response.setData(errors);
        return response;
    }

    // 4. When an unexpected system error occurs - 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleGeneral(Exception ex) {
        log.error("Unexpected error occurred", ex);
        try {
            java.io.FileWriter fw = new java.io.FileWriter("error_log.txt", true);
            fw.write(java.time.LocalDateTime.now().toString() + " - " + ex.getMessage() + "\n");
            for (StackTraceElement elem : ex.getStackTrace()) {
                fw.write("\t" + elem.toString() + "\n");
            }
            fw.write("\n");
            fw.close();
        } catch (Exception ignored) {}
        return ApiResponse.error("An unexpected error occurred. " + ex.getMessage());
    }

    @ExceptionHandler(PaymentAlreadyPaidException.class)
    public ResponseEntity<com.devspark.childcare.payment.dto.response.ApiResponse<Void>> handleAlreadyPaid(PaymentAlreadyPaidException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(com.devspark.childcare.payment.dto.response.ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<com.devspark.childcare.payment.dto.response.ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(com.devspark.childcare.payment.dto.response.ApiResponse.error(ex.getMessage()));
    }

}