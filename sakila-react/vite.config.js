import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// https://vitejs.dev/config/
export default defineConfig({
	plugins: [react()],
	server: {
		port: 3000,
		proxy: {
			// Redirige al Authorization Server todas las rutas OAuth2/OIDC
			"/oauth2": {
				target: "http://localhost:8080",
				changeOrigin: true,
				secure: false,
			},
			"/login": {
				target: "http://localhost:8080",
				changeOrigin: true,
				secure: false,
			},
			"/logout": {
				target: "http://localhost:8080",
				changeOrigin: true,
				secure: false,
			},
			// JWKS y discovery del AS
			"/.well-known": {
				target: "http://localhost:8080",
				changeOrigin: true,
				secure: false,
			},
			// API REST del resource server
			"/api": {
				target: "http://localhost:8080",
				changeOrigin: true,
				secure: false,
			},
		},
	},
});
