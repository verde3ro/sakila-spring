import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// https://vitejs.dev/config/
export default defineConfig({
	plugins: [react()],
	server: {
		port: 3000,
		proxy: {
			// NOTA IMPORTANTE:
			// oidc-client-ts (authService.js) usa `authority: 'http://localhost:8080'`,
			// por lo que TODAS las llamadas OIDC (discovery, /oauth2/authorize,
			// /oauth2/token, /.well-known/jwks.json, signout, etc.) se hacen con
			// URLs absolutas directo al puerto 8080, NUNCA a través de este proxy.
			// Por eso las entradas /oauth2, /login, /logout, /.well-known, etc.
			// se removieron: eran código muerto que nunca se ejecutaba.
			//
			// Para que el flujo OIDC funcione en dev, el Spring Authorization
			// Server (puerto 8080) debe tener CORS habilitado para
			// http://localhost:3000 en esos endpoints.

			// API REST del resource server.
			// Esta SÍ se usa, siempre que en el frontend llames con ruta
			// relativa, ej: fetch('/api/algo'), y NO con
			// fetch('http://localhost:8080/api/algo').
			"/api": {
				target: "http://localhost:8080",
				changeOrigin: true,
				secure: false,
			},
		},
	},
});
