package mx.edu.upq.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import mx.edu.upq.response.ErrorResponse;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
		// Obtener el primer mensaje de error de validación
		String mensaje = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.findFirst()
				.map(DefaultMessageSourceResolvable::getDefaultMessage)
				.orElse("Campos no validos");

		ErrorResponse error = new ErrorResponse("Error de validación", 400, mensaje);
		return ResponseEntity.badRequest().body(error);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleValidation(ConstraintViolationException ex) {
		String detail = ex.getConstraintViolations().stream()
				.map(ConstraintViolation::getMessage)
				.collect(Collectors.joining("<br/>"));
		ErrorResponse error = new ErrorResponse("Error de validación", 400, detail);
		return ResponseEntity.badRequest().body(error);
	}

	@ExceptionHandler(InternalException.class)
	public ResponseEntity<ErrorResponse> handleInternal(InternalException ex) {
		ErrorResponse error = new ErrorResponse("Error interno", 500, ex.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}


	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(NoHandlerFoundException ex) {
		if (ex.getRequestURL().contains("favicon.ico")) {
			return ResponseEntity.notFound().build();
		}
		ErrorResponse error = new ErrorResponse(
				"Recurso no encontrado",
				404,
				"La ruta '" + ex.getRequestURL() + "' no existe"
		);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
}