// src/components/Contact.jsx
// Página de contacto con un formulario simple (nombre, correo, mensaje).
// Usa componentes de PrimeReact ya utilizados en el resto de la app
// (InputText, Button, Toast) más InputTextarea para el mensaje.
// De momento el envío solo simula una llamada; puedes conectarlo a un
// endpoint real (por ejemplo /api/contact) cuando lo tengas listo.

import { useRef, useState } from "react";
import { Card } from "primereact/card";
import { InputText } from "primereact/inputtext";
import { InputTextarea } from "primereact/inputtextarea";
import { Button } from "primereact/button";
import { Toast } from "primereact/toast";
import NavBar from "./NavBar";

const Contact = () => {
	const toast = useRef(null);

	// Estado del formulario
	const [form, setForm] = useState({ name: "", email: "", message: "" });

	// Estado de errores de validación (mismo patrón que el formulario de ciudades en App.jsx)
	const [errors, setErrors] = useState({ name: "", email: "", message: "" });

	const [sending, setSending] = useState(false);

	/**
	 * Valida los campos del formulario antes de enviar.
	 * @returns {boolean} true si el formulario es válido.
	 */
	const validateForm = () => {
		const newErrors = { name: "", email: "", message: "" };
		let isValid = true;

		if (!form.name.trim()) {
			newErrors.name = "El nombre es obligatorio.";
			isValid = false;
		}

		if (!form.email.trim()) {
			newErrors.email = "El correo es obligatorio.";
			isValid = false;
		} else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
			newErrors.email = "El correo no tiene un formato válido.";
			isValid = false;
		}

		if (!form.message.trim()) {
			newErrors.message = "El mensaje es obligatorio.";
			isValid = false;
		}

		setErrors(newErrors);
		return isValid;
	};

	/**
	 * Maneja el envío del formulario. Reemplaza el setTimeout por tu
	 * llamada real al backend (por ejemplo con axios, igual que en cityService.js).
	 */
	const handleSubmit = async () => {
		if (!validateForm()) {
			toast.current.show({
				severity: "warn",
				summary: "Validación",
				detail: "Por favor completa los campos obligatorios.",
			});
			return;
		}

		setSending(true);
		try {
			// TODO: reemplazar por la llamada real, ej:
			// await axios.post("/api/contact", form);
			await new Promise((resolve) => setTimeout(resolve, 800));

			toast.current.show({
				severity: "success",
				summary: "Enviado",
				detail: "Gracias por tu mensaje, te responderemos pronto.",
			});
			setForm({ name: "", email: "", message: "" });
		} catch (error) {
			toast.current.show({
				severity: "error",
				summary: "Error",
				detail: "No se pudo enviar el mensaje. Intenta de nuevo.",
			});
		} finally {
			setSending(false);
		}
	};

	return (
		<div className="p-3">
			<Toast ref={toast} />
			<NavBar />

			<div className="flex justify-content-center mt-4">
				<Card title="Contacto" style={{ maxWidth: "500px", width: "100%" }}>
					<div className="p-fluid">
						<div className="field">
							<label htmlFor="name">Nombre *</label>
							<InputText
								id="name"
								value={form.name}
								onChange={(e) => {
									setForm({ ...form, name: e.target.value });
									if (errors.name) setErrors({ ...errors, name: "" });
								}}
								className={errors.name ? "p-invalid" : ""}
							/>
							{errors.name && <small className="p-error">{errors.name}</small>}
						</div>

						<div className="field">
							<label htmlFor="email">Correo *</label>
							<InputText
								id="email"
								value={form.email}
								onChange={(e) => {
									setForm({ ...form, email: e.target.value });
									if (errors.email) setErrors({ ...errors, email: "" });
								}}
								className={errors.email ? "p-invalid" : ""}
							/>
							{errors.email && <small className="p-error">{errors.email}</small>}
						</div>

						<div className="field">
							<label htmlFor="message">Mensaje *</label>
							<InputTextarea
								id="message"
								value={form.message}
								onChange={(e) => {
									setForm({ ...form, message: e.target.value });
									if (errors.message) setErrors({ ...errors, message: "" });
								}}
								rows={5}
								autoResize
								className={errors.message ? "p-invalid" : ""}
							/>
							{errors.message && <small className="p-error">{errors.message}</small>}
						</div>

						<Button
							label="Enviar"
							icon="pi pi-send"
							onClick={handleSubmit}
							loading={sending}
						/>
					</div>
				</Card>
			</div>
		</div>
	);
};

export default Contact;
