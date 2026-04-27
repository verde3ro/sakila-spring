// login.js - Manejo de mensajes de error y logout

document.addEventListener('DOMContentLoaded', function() {
	const urlParams = new URLSearchParams(window.location.search);

	if (urlParams.has('error')) {
		const errDiv = document.getElementById('errorMsg');
		errDiv.textContent = 'Usuario o contraseña incorrectos.';
		errDiv.style.display = 'block';
	}

	if (urlParams.has('logout')) {
		const logoutDiv = document.getElementById('logoutMsg');
		logoutDiv.textContent = 'Has cerrado sesión correctamente.';
		logoutDiv.style.display = 'block';
	}
});
