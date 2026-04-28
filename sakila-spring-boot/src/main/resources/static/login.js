// Función auxiliar para obtener elementos del DOM
const $ = (selector) => document.querySelector(selector);

// Funciones de utilidad (flecha)
const clearErrors = () => {
	const errDiv = $('#errorMsg');
	if (errDiv) {
		errDiv.textContent = '';
		errDiv.style.display = 'none';
	}
	document.querySelectorAll('.p-invalid').forEach(el => el.classList.remove('p-invalid'));
	document.querySelectorAll('.field-error').forEach(el => el.remove());
};

const showFieldError = (input, message) => {
	input.classList.add('p-invalid');
	const errorEl = document.createElement('small');
	errorEl.className = 'p-error field-error';
	errorEl.textContent = message;
	input.parentNode.appendChild(errorEl);
};

const showFormError = (message) => {
	const errDiv = $('#errorMsg');
	if (errDiv) {
		errDiv.textContent = message;
		errDiv.style.display = 'block';
	}
};


document.addEventListener('DOMContentLoaded', () => {
	const form = $('form');
	const username = $('#username');
	const password = $('#password');
	const errorDiv = $('#errorMsg');
	const logoutDiv = $('#logoutMsg');

	// Validación en submit
	form?.addEventListener('submit', (e) => {
		clearErrors();
		let valid = true;

		if (!username.value.trim()) {
			showFieldError(username, 'El usuario es obligatorio.');
			valid = false;
		}
		if (!password.value.trim()) {
			showFieldError(password, 'La contraseña es obligatoria.');
			valid = false;
		}
		if (!valid) {
			e.preventDefault();
			showFormError('Por favor, completa todos los campos.');
		}
	});

	// Mensajes según parámetros de la URL
	const params = new URLSearchParams(location.search);
	if (params.has('error') && errorDiv) {
		errorDiv.textContent = 'Usuario o contraseña incorrectos.';
		errorDiv.style.display = 'block';
	}
	if (params.has('logout') && logoutDiv) {
		logoutDiv.textContent = 'Has cerrado sesión correctamente.';
		logoutDiv.style.display = 'block';
	}
});
