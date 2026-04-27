import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { userManager } from '../services/authService';

const Callback = () => {
	const navigate = useNavigate();

	useEffect(() => {
		userManager.signinRedirectCallback()
			.then(() => {
				navigate('/');               // Redirige al home después de login exitoso
			})
			.catch(err => {
				console.error('Error en callback:', err);
				navigate('/');               // En caso de error, también va al home
			});
	}, [navigate]);

	return <div>Completando inicio de sesión...</div>;
};

export default Callback;
