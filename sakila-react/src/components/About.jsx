// src/components/About.jsx
// Página "Acerca de" construida con el componente Card de PrimeReact.
// No requiere autenticación (es información pública de la aplicación).

import { Card } from "primereact/card";
import { Divider } from "primereact/divider";
import { Avatar } from "primereact/avatar";
import NavBar from "./NavBar";

const About = () => {
	// Encabezado del Card: puede ser cualquier elemento, aquí un ícono grande
	const header = (
		<div className="flex justify-content-center align-items-center bg-blue-50" style={{ height: "160px" }}>
			<i className="pi pi-map-marker" style={{ fontSize: "4rem", color: "#3B82F6" }} />
		</div>
	);

	return (
		<div className="p-3">
			<NavBar />

			<div className="flex justify-content-center mt-4">
				<Card title="Acerca de esta aplicación" header={header} style={{ maxWidth: "600px", width: "100%" }}>
					<p className="m-0">
						Esta aplicación permite administrar un catálogo de ciudades y países,
						con soporte de autenticación mediante OAuth2 / OIDC. Los usuarios con
						rol <strong>ADMIN</strong> pueden crear, editar y eliminar registros,
						mientras que el resto de usuarios autenticados puede consultarlos.
					</p>

					<Divider />

					<div className="flex align-items-center gap-3">
						<Avatar icon="pi pi-user" size="large" shape="circle" />
						<div>
							<p className="m-0 font-semibold">Tecnologías utilizadas</p>
							<p className="m-0 text-sm text-color-secondary">
								React + Vite, PrimeReact, Spring Authorization Server, JWT
							</p>
						</div>
					</div>
				</Card>
			</div>
		</div>
	);
};

export default About;
