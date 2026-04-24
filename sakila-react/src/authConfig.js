// authority = mismo origen que el frontend (localhost:3000)
// Spring Authorization Server anuncia issuer=http://localhost:3000
// El discovery en /.well-known/openid-configuration devuelve
// authorization_endpoint=http://localhost:3000/oauth2/authorize
// → Vite proxy lo intercepta y lo reenvía a :8080 correctamente

export const oidcConfig = {
	authority: "http://localhost:3000",
	client_id: "react-client",
	redirect_uri: "http://localhost:3000/callback",
	response_type: "code",
	scope: "openid profile read write offline_access",
	post_logout_redirect_uri: "http://localhost:3000/",
	silent_redirect_uri: "http://localhost:3000/silent-renew.html",
	automaticSilentRenew: true,
	monitorSession: false,
	loadUserInfo: false,
};
