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

export const logout = async () => {
	const user = await userManager.getUser();
	let idTokenHint = user?.id_token;

	// Si el token expiró, no lo enviamos para evitar error 400
	if (idTokenHint && isTokenExpired(idTokenHint)) {
		idTokenHint = undefined;
	}

	try {
		await userManager.signoutRedirect({
			id_token_hint: idTokenHint,
			post_logout_redirect_uri: 'http://localhost:3000',
		});
	} catch (err) {
		// Fallback por si ocurre otro error (por ejemplo, red inestable)
		console.error('Error durante logout redirect:', err);
		// Podrías intentar un signoutRedirect sin id_token_hint directamente
		await userManager.signoutRedirect({
			post_logout_redirect_uri: 'http://localhost:3000',
		});
	} finally {
		// Limpia los tokens locales después de iniciar la redirección
		await userManager.removeUser();
	}
};

export const getUser = async () => userManager.getUser();
export const getAccessToken = async () => (await getUser())?.access_token;


const isTokenExpired = (token) => {
	if (!token) return true;
	try {
		const payload = JSON.parse(atob(token.split('.')[1]));
		const now = Math.floor(Date.now() / 1000);
		return payload.exp <= now;
	} catch {
		return true; // si no se puede decodificar, lo tratamos como expirado
	}
}
