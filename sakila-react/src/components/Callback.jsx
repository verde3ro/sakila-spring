// src/components/Callback.jsx
import { useEffect, useState } from "react";
import { handleCallback } from "../services/authService";
import { useNavigate } from "react-router-dom";

const Callback = () => {
	const navigate = useNavigate();
	const [error, setError] = useState(null);

	useEffect(() => {
		handleCallback()
			.then(() => navigate("/"))
			.catch((err) => setError(err.message));
	}, [navigate]);

	if (error) return <div>Error: {error}</div>;
	return <div>Cargando...</div>;
};

export default Callback;
