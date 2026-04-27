import {UserManager, WebStorageStateStore} from 'oidc-client-ts';

const userManagerConfig = {
	authority: 'http://localhost:8080',               // Authorization Server
	client_id: 'clientapp',
	redirect_uri: 'http://localhost:3000/callback',   // Debe coincidir con el backend
	response_type: 'code',
	scope: 'openid profile read_profile_info',        // 'openid' es necesario para obtener el sub
	loadUserInfo: false,                              // No pedimos /userinfo
	automaticSilentRenew: false,                      // Simplificamos
	pkce: true,                                       // Habilita PKCE (obligatorio)
	code_challenge_method: 'S256',
	userStore: new WebStorageStateStore({ store: window.localStorage }),
	metadata: {
		authorization_endpoint: 'http://localhost:8080/oauth2/authorize',
		token_endpoint: 'http://localhost:8080/oauth2/token'
	}
};

export const userManager = new UserManager(userManagerConfig);

export const login = () => userManager.signinRedirect();

export const logout = () => userManager.signoutRedirect({
	post_logout_redirect_uri: 'http://localhost:3000'
});

export const getUser = async () => {
	return await userManager.getUser();
};

export const getAccessToken = async () => {
	const user = await getUser();
	return user?.access_token;
};
