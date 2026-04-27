// Componente principal de la aplicación. Maneja autenticación OAuth2 + OIDC,
// listado de ciudades con paginación/ordenación, CRUD de ciudades (solo ADMIN),
// exportación a Excel y validación de formularios.

import { useEffect, useState, useRef } from "react";
import { getUser, login, logout, userManager } from "./services/authService";
import {
	getCitiesPagination,
	deleteCity,
	createCity,
	updateCity,
	getCountries,
	downloadExcel,
} from "./services/cityService";

import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";
import { Button } from "primereact/button";
import { Dialog } from "primereact/dialog";
import { InputText } from "primereact/inputtext";
import { Dropdown } from "primereact/dropdown";
import { Calendar } from "primereact/calendar";
import { Toast } from "primereact/toast";
import { ConfirmDialog, confirmDialog } from "primereact/confirmdialog";
import { ProgressSpinner } from "primereact/progressspinner";

// Opciones para el paginador de la tabla (filas por página)
const rowsPerPageOptions = [5, 10, 20, 50];

/**
 * Convierte un valor (string, Date, null) a objeto Date.
 * Útil para manejar fechas provenientes de la API o del formulario.
 */
const parseDate = (value) => {
	if (!value) return null;
	if (value instanceof Date) return value;
	if (typeof value === "string") {
		const date = new Date(value);
		return Number.isNaN(date.getTime()) ? null : date;
	}
	return null;
};

const App = () => {
	// Referencia para mostrar notificaciones tipo toast
	const toast = useRef(null);

	// Estados de autenticación
	const [isAuthenticated, setIsAuthenticated] = useState(false);
	const [username, setUsername] = useState("");
	const [userRoles, setUserRoles] = useState([]);
	const [checkingAuth, setCheckingAuth] = useState(true); // Verificación inicial

	// Estados de la tabla y carga de datos
	const [cities, setCities] = useState([]);
	const [countries, setCountries] = useState([]);
	const [totalRecords, setTotalRecords] = useState(0);
	const [loading, setLoading] = useState(false);

	// Objeto que representa la ciudad del formulario (creación/edición)
	const [city, setCity] = useState({
		cityId: null,
		city: "",
		countryId: null,
		lastUpdate: null,
	});

	// Estados para validación del formulario
	const [errors, setErrors] = useState({
		city: "",
		countryId: "",
	});

	// Control del diálogo modal
	const [visible, setVisible] = useState(false);

	// Estados para paginación y ordenamiento (lazy)
	const [first, setFirst] = useState(0);
	const [rows, setRows] = useState(10);
	const [sortField, setSortField] = useState("cityId");
	const [sortOrder, setSortOrder] = useState(1);

	// ------------------------------------------------------------
	// 1. EFECTO PRINCIPAL: VERIFICAR AUTENTICACIÓN Y REDIRIGIR
	// ------------------------------------------------------------
	useEffect(() => {
		/**
		 * Función asíncrona que verifica si ya hay un usuario logeado.
		 * Si existe y no ha expirado, actualiza los estados.
		 * Si no, llama a login() para redirigir al servidor OAuth.
		 */
		const initAuth = async () => {
			try {
				const user = await getUser();
				if (user && !user.expired) {
					setIsAuthenticated(true);
					setUsername(user.profile?.sub || user.profile?.preferred_username || "Usuario");
					// Extrae los roles del claim "roles" enviado por el backend
					const roles = user.profile?.roles;
					if (Array.isArray(roles)) {
						setUserRoles(roles);
					} else if (typeof roles === "string") {
						setUserRoles([roles]);
					} else {
						setUserRoles([]);
					}
				} else {
					await login(); // Redirige automáticamente al login
				}
			} catch (error) {
				console.error("Error verificando autenticación:", error);
				await login();
			} finally {
				setCheckingAuth(false);
			}
		};

		initAuth();

		// Manejadores de eventos del UserManager (cuando se carga o cierra sesión)
		const handleUserLoaded = (user) => {
			setIsAuthenticated(true);
			setUsername(user.profile?.sub || user.profile?.preferred_username || "Usuario");
			const roles = user.profile?.roles;
			if (Array.isArray(roles)) {
				setUserRoles(roles);
			} else if (typeof roles === "string") {
				setUserRoles([roles]);
			} else {
				setUserRoles([]);
			}
		};
		const handleUserUnloaded = () => {
			setIsAuthenticated(false);
			setUsername("");
			setUserRoles([]);
		};

		userManager.events.addUserLoaded(handleUserLoaded);
		userManager.events.addUserUnloaded(handleUserUnloaded);

		// Limpieza al desmontar el componente
		return () => {
			userManager.events.removeUserLoaded(handleUserLoaded);
			userManager.events.removeUserUnloaded(handleUserUnloaded);
		};
	}, []);

	// ------------------------------------------------------------
	// 2. EFECTO: CARGAR DATOS UNA VEZ QUE EL USUARIO ESTÉ AUTENTICADO
	// ------------------------------------------------------------
	useEffect(() => {
		if (isAuthenticated) {
			loadCities({ page: 0, size: rows, sf: sortField, so: sortOrder });
			loadCountries();
		}
	}, [isAuthenticated]);

	// ------------------------------------------------------------
	// 3. FUNCIONES DE CARGA DE DATOS (API)
	// ------------------------------------------------------------
	/**
	 * Carga las ciudades desde el backend usando paginación y ordenamiento.
	 * @param {object} params - { page, size, sf (sortField), so (sortOrder: 1=asc, -1=desc) }
	 */
	const loadCities = async ({ page, size, sf, so }) => {
		setLoading(true);
		try {
			const res = await getCitiesPagination({
				page,
				size,
				sortField: sf,
				sortOrder: so === 1 ? "asc" : "desc",
			});
			setCities(res.data.content);
			setTotalRecords(res.data.totalElements);
		} catch (error) {
			toast.current?.show({
				severity: "error",
				summary: "Error",
				detail: `Error cargando datos: ${error}`,
			});
		} finally {
			setLoading(false);
		}
	};

	/**
	 * Carga la lista de países para el dropdown del formulario.
	 */
	const loadCountries = async () => {
		try {
			const res = await getCountries();
			setCountries(res.data);
		} catch (error) {
			console.error(error);
		}
	};

	// ------------------------------------------------------------
	// 4. MANEJADORES DE EVENTOS DE LA TABLA (ordenamiento y página)
	// ------------------------------------------------------------
	/**
	 * Se ejecuta cuando el usuario hace clic en una columna ordenable.
	 * Actualiza los criterios de orden y recarga desde la primera página.
	 */
	const onSort = (event) => {
		setSortField(event.sortField);
		setSortOrder(event.sortOrder);
		setFirst(0);
		loadCities({ page: 0, size: rows, sf: event.sortField, so: event.sortOrder });
	};

	/**
	 * Se ejecuta al cambiar de página. Recarga los datos con la nueva página.
	 */
	const onPage = (event) => {
		const newFirst = event.first;
		const newRows = event.rows;
		setFirst(newFirst);
		setRows(newRows);
		loadCities({
			page: Math.floor(newFirst / newRows),
			size: newRows,
			sf: sortField,
			so: sortOrder,
		});
	};

	// ------------------------------------------------------------
	// 5. FUNCIONES DEL FORMULARIO (creación/edición de ciudades)
	// ------------------------------------------------------------
	/**
	 * Abre el diálogo para crear una nueva ciudad. Resetea el formulario y los errores.
	 */
	const openNew = () => {
		setCity({ cityId: null, city: "", countryId: null, lastUpdate: new Date() });
		setErrors({ city: "", countryId: "" });
		setVisible(true);
	};

	/**
	 * Abre el diálogo para editar una ciudad existente.
	 * @param {object} row - Datos de la ciudad a editar.
	 */
	const editCity = (row) => {
		setCity({ ...row, lastUpdate: parseDate(row.lastUpdate) });
		setErrors({ city: "", countryId: "" });
		setVisible(true);
	};

	/**
	 * Valida los campos del formulario antes de enviar.
	 * @returns {boolean} - true si es válido, false en caso contrario.
	 */
	const validateForm = () => {
		let isValid = true;
		const newErrors = { city: "", countryId: "" };

		if (!city.city || city.city.trim() === "") {
			newErrors.city = "La ciudad es obligatoria.";
			isValid = false;
		}

		if (!city.countryId) {
			newErrors.countryId = "Debes seleccionar un país.";
			isValid = false;
		}

		setErrors(newErrors);
		return isValid;
	};

	/**
	 * Guarda la ciudad (crea o actualiza). Primero valida, luego envía al backend.
	 */
	const saveCity = async () => {
		if (!validateForm()) {
			toast.current.show({
				severity: "warn",
				summary: "Validación",
				detail: "Por favor completa los campos obligatorios.",
			});
			return;
		}

		try {
			const payload = {
				...city,
				lastUpdate: city.lastUpdate ? city.lastUpdate.toISOString() : null,
			};
			if (city.cityId) {
				await updateCity(payload);
				toast.current.show({ severity: "success", summary: "Actualizado" });
			} else {
				await createCity(payload);
				toast.current.show({ severity: "success", summary: "Creado" });
			}
			setVisible(false);
			// Recarga la tabla manteniendo la página actual
			loadCities({ page: Math.floor(first / rows), size: rows, sf: sortField, so: sortOrder });
		} catch (error) {
			let errorMessage = "Error guardando";
			if (error.response?.data?.detail) errorMessage = error.response.data.detail;
			toast.current.show({ severity: "error", summary: "Error", detail: errorMessage });
		}
	};

	// ------------------------------------------------------------
	// 6. FUNCIONES DE ELIMINACIÓN Y EXCEL
	// ------------------------------------------------------------
	/**
	 * Muestra un diálogo de confirmación antes de eliminar.
	 * @param {object} row - Ciudad a eliminar.
	 */
	const confirmDelete = (row) => {
		confirmDialog({
			message: "¿Eliminar registro?",
			header: "Confirmar",
			icon: "pi pi-exclamation-triangle",
			accept: () => removeCity(row),
		});
	};

	/**
	 * Elimina una ciudad llamando al API. Refresca la tabla después.
	 * @param {object} row - Ciudad a eliminar.
	 */
	const removeCity = async (row) => {
		await deleteCity(row.cityId);
		toast.current.show({ severity: "warn", summary: "Eliminado" });
		loadCities({ page: Math.floor(first / rows), size: rows, sf: sortField, so: sortOrder });
	};

	/**
	 * Descarga un archivo Excel con la lista de ciudades (en base64).
	 */
	const handleDownloadExcel = async () => {
		const res = await downloadExcel();
		const link = document.createElement("a");
		link.href = `data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64,${res.data.base64}`;
		link.download = "cities.xlsx";
		link.click();
	};

	// Funciones de autenticación
	const handleLogin = () => login();
	const handleLogout = () => logout();

	/**
	 * Template para renderizar la fecha en la tabla.
	 * @param {object} rowData - Fila de la tabla.
	 * @returns {string} Fecha formateada o cadena vacía.
	 */
	const dateBodyTemplate = (rowData) => {
		const date = parseDate(rowData.lastUpdate);
		return date ? date.toLocaleString() : "";
	};

	// Determina si el usuario actual tiene rol de administrador
	const isAdmin = userRoles.includes("ADMIN");

	// ------------------------------------------------------------
	// RENDERIZADO CONDICIONAL (pantallas de carga/autenticación)
	// ------------------------------------------------------------
	if (checkingAuth) {
		return (
			<div className="p-5 text-center flex justify-content-center align-items-center" style={{ minHeight: "100vh" }}>
				<div>
					<ProgressSpinner style={{ width: "50px", height: "50px" }} strokeWidth="4" />
					<h3 className="mt-3">Verificando autenticación...</h3>
				</div>
			</div>
		);
	}

	if (!isAuthenticated) {
		return (
			<div className="p-5 text-center flex justify-content-center align-items-center" style={{ minHeight: "100vh" }}>
				<div>
					<ProgressSpinner style={{ width: "50px", height: "50px" }} strokeWidth="4" />
					<h3 className="mt-3">Redirigiendo al login...</h3>
				</div>
			</div>
		);
	}

	// ------------------------------------------------------------
	// RENDERIZADO PRINCIPAL (USUARIO AUTENTICADO)
	// ------------------------------------------------------------
	return (
		<div className="p-3">
			{/* Toast para notificaciones */}
			<Toast ref={toast} />
			<ConfirmDialog />

			{/* Barra superior con título, usuario y botones de acción */}
			<div className="flex justify-content-between align-items-center mb-3">
				<h2>Administración de Ciudades</h2>
				<div className="flex gap-2 align-items-center">
          <span className="mr-3">
            Bienvenido, <strong>{username}</strong> ({userRoles.join(", ")})
          </span>
					{isAdmin && (
						<Button label="Nueva" icon="pi pi-plus" onClick={openNew} />
					)}
					<Button
						label="Excel"
						icon="pi pi-file-excel"
						severity="success"
						onClick={handleDownloadExcel}
					/>
					<Button
						label="Cerrar Sesión"
						icon="pi pi-sign-out"
						severity="warning"
						onClick={handleLogout}
					/>
				</div>
			</div>

			{/* Tabla de ciudades (lazy loading, paginación, ordenamiento) */}
			<DataTable
				value={cities}
				lazy
				loading={loading}
				onSort={onSort}
				sortField={sortField}
				sortOrder={sortOrder}
				paginator
				rows={rows}
				first={first}
				totalRecords={totalRecords}
				onPage={onPage}
				rowsPerPageOptions={rowsPerPageOptions}
			>
				<Column field="cityId" header="ID" sortable />
				<Column field="city" header="Ciudad" sortable />
				<Column field="countryName" header="País" sortable />
				<Column field="lastUpdate" header="Fecha" sortable body={dateBodyTemplate} />
				{isAdmin && (
					<Column
						header="Acciones"
						body={(row) => (
							<div className="flex gap-2">
								<Button icon="pi pi-pencil" onClick={() => editCity(row)} />
								<Button icon="pi pi-trash" severity="danger" onClick={() => confirmDelete(row)} />
							</div>
						)}
					/>
				)}
			</DataTable>

			{/* Diálogo modal para crear/editar ciudad con validación de campos */}
			<Dialog
				header="Ciudad"
				visible={visible}
				onHide={() => setVisible(false)}
				style={{ width: "400px" }}
			>
				<div className="p-fluid">
					<div className="field">
						<label htmlFor="city">Ciudad *</label>
						<InputText
							id="city"
							value={city.city}
							onChange={(e) => {
								setCity({ ...city, city: e.target.value });
								if (errors.city) setErrors({ ...errors, city: "" });
							}}
							className={errors.city ? "p-invalid" : ""}
						/>
						{errors.city && <small className="p-error">{errors.city}</small>}
					</div>
					<div className="field">
						<label htmlFor="country">País *</label>
						<Dropdown
							id="country"
							value={city.countryId}
							options={countries}
							optionLabel="country"
							optionValue="countryId"
							onChange={(e) => {
								setCity({ ...city, countryId: e.value });
								if (errors.countryId) setErrors({ ...errors, countryId: "" });
							}}
							placeholder="Seleccione un país"
							className={errors.countryId ? "p-invalid" : ""}
						/>
						{errors.countryId && <small className="p-error">{errors.countryId}</small>}
					</div>
					<div className="field">
						<label htmlFor="lastUpdate">Fecha</label>
						<Calendar
							id="lastUpdate"
							value={city.lastUpdate}
							onChange={(e) => setCity({ ...city, lastUpdate: e.value })}
							showIcon
							showTime
							hourFormat="24"
						/>
					</div>
					<Button label="Guardar" onClick={saveCity} />
				</div>
			</Dialog>
		</div>
	);
};

export default App;
