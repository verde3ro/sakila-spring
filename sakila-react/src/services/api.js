// src/services/api.js
import axios from "axios";
import { addAccessTokenInterceptor } from "./authService";

const api = axios.create({
	baseURL: "http://localhost:8080/api",
});

addAccessTokenInterceptor(api);

export default api;
