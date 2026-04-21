export const oidcConfig = {
	authority: "http://localhost:8080",
	client_id: "react-client",
	redirect_uri: "http://localhost:3000/callback",
	response_type: "code",
	scope: "openid profile read write",
	post_logout_redirect_uri: "http://localhost:3000/",
	automaticSilentRenew: true,
	loadUserInfo: false,
};
