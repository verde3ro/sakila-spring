package mx.edu.upq.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de error estándar")
public class ErrorResponse implements Serializable {

	@Serial
	private static final long serialVersionUID = -8574614664390443325L;

	@Schema(description = "Título del error", example = "Bad Request")
	private String title;
	@Schema(description = "Código de estado HTTP", example = "400")
	private int status;
	@Schema(description = "Detalle del error", example = "El campo 'city' es obligatorio")
	private String detail;

}
