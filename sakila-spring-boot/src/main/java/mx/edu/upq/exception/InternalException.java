package mx.edu.upq.exception;

import java.io.Serial;

public class InternalException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = -3642094319047815169L;

	public InternalException(String message, Throwable cause, Object... params) {
		super(String.format(message, params), cause, false, true);
	}

	public InternalException(String message) {
		this(message, (Throwable) null);
	}

}
