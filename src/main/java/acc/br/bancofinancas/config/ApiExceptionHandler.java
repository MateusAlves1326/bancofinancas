package acc.br.bancofinancas.config;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of("message", messageOf(exception, "Dados inválidos para a solicitação.")));
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(error -> error.getDefaultMessage() == null
                ? "Campo inválido: " + error.getField()
                : error.getDefaultMessage())
            .orElse("Dados inválidos para a solicitação.");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of("message", message));
        }

        private String messageOf(Exception exception, String fallback) {
        return exception.getMessage() == null || exception.getMessage().isBlank()
            ? fallback
            : exception.getMessage();
    }
}