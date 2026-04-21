import { useState, useRef } from "react";
import { Dialog } from "primereact/dialog";
import { InputText } from "primereact/inputtext";
import { Password } from "primereact/password";
import { Button } from "primereact/button";
import { Toast } from "primereact/toast";
import { login } from "../services/authService";

const Login = ({ visible, onHide, onLoginSuccess }) => {
	const [username, setUsername] = useState("");
	const [password, setPassword] = useState("");
	const [loading, setLoading] = useState(false);
	const toast = useRef(null);

	const handleSubmit = async (e) => {
		e.preventDefault();
		setLoading(true);
		try {
			await login(username, password);
			toast.current.show({ severity: "success", summary: "Bienvenido", life: 2000 });
			onLoginSuccess();
			onHide();
		} catch (error) {
			toast.current.show({
				severity: "error",
				summary: "Error",
				detail: error.response?.data?.detail || "Credenciales inválidas",
			});
		} finally {
			setLoading(false);
		}
	};

	return (
		<>
			<Toast ref={toast} />
			<Dialog
				header="Iniciar Sesión"
				visible={visible}
				onHide={onHide}
				style={{ width: "400px" }}
				closable={false}
			>
				<form onSubmit={handleSubmit} className="p-fluid">
					<div className="field">
						<label htmlFor="username">Usuario</label>
						<InputText
							id="username"
							value={username}
							onChange={(e) => setUsername(e.target.value)}
							required
							autoFocus
						/>
					</div>
					<div className="field">
						<label htmlFor="password">Contraseña</label>
						<Password
							id="password"
							value={password}
							onChange={(e) => setPassword(e.target.value)}
							feedback={false}
							toggleMask
							required
						/>
					</div>
					<Button label="Ingresar" icon="pi pi-sign-in" loading={loading} />
				</form>
			</Dialog>
		</>
	);
};

export default Login;
