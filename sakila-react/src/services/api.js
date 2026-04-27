import axios from 'axios';
import { getAccessToken } from './authService';

const api = axios.create({
	baseURL: '/api',   // Gracias al proxy de Vite, esto apunta a http://localhost:8080/api
	headers: { 'Content-Type': 'application/json' }
});

// Interceptor para agregar el token Bearer a todas las peticiones
api.interceptors.request.use(async (config) => {
	const token = await getAccessToken();
	if (token) {
		config.headers.Authorization = `Bearer ${token}`;
	}
	return config;
});

export default api;
