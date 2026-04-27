import { UserManager, WebStorageStateStore } from 'oidc-client-ts';

const userManagerConfig = {
	authority: 'http://localhost:8080',
	client_id: 'clientapp',
	redirect_uri: 'http://localhost:3000/callback',
	response_type: 'code',
	scope: 'openid profile api',   // OIDC + los que necesites
	loadUserInfo: false,           // si quieres obtener /userinfo, pon true
	automaticSilentRenew: false,
	pkce: true,
	code_challenge_method: 'S256',
	userStore: new WebStorageStateStore({ store: window.localStorage }),

};

export const userManager = new UserManager(userManagerConfig);

export const login = () => userManager.signinRedirect();

export const logout = () => userManager.signoutRedirect({
	post_logout_redirect_uri: 'http://localhost:3000'
});

export const getUser = async () => userManager.getUser();
export const getAccessToken = async () => (await getUser())?.access_token;
