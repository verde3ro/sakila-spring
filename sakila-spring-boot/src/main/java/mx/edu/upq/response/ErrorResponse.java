package mx.edu.upq.response;

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
public class ErrorResponse implements Serializable {

	@Serial
	private static final long serialVersionUID = -8574614664390443325L;
	private String title;
	private int status;
	private String detail;

}
