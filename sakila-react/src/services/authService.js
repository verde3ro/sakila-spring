// src/services/authService.js
import { UserManager, WebStorageStateStore } from "oidc-client-ts";
import { oidcConfig } from "../authConfig";

const userManager = new UserManager({
	...oidcConfig,
	userStore: new WebStorageStateStore({ store: window.localStorage }),
});

export const login = () => userManager.signinRedirect();
export const logout = () => userManager.signoutRedirect();
export const handleCallback = () => userManager.signinRedirectCallback();
export const getUser = () => userManager.getUser();
export const getAccessToken = async () => {
	const user = await getUser();
	return user?.access_token;
};

export const addAccessTokenInterceptor = (axiosInstance) => {
	axiosInstance.interceptors.request.use(async (config) => {
		const token = await getAccessToken();
		if (token) {
			config.headers.Authorization = `Bearer ${token}`;
		}
		return config;
	});
};
