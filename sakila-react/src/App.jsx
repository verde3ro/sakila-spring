// src/App.jsx
import { useEffect, useState, useRef } from "react";
import { getUser, login, logout } from "./services/authService";
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

const rowsPerPageOptions = [5, 10, 20, 50];

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
	const toast = useRef(null);
	const [isAuthenticated, setIsAuthenticated] = useState(false);
	const [username, setUsername] = useState("");
	const [cities, setCities] = useState([]);
	const [countries, setCountries] = useState([]);
	const [totalRecords, setTotalRecords] = useState(0);
	const [loading, setLoading] = useState(false);
	const [city, setCity] = useState({
		cityId: null,
		city: "",
		countryId: null,
		lastUpdate: null,
	});

	const [visible, setVisible] = useState(false);
	const [first, setFirst] = useState(0);
	const [rows, setRows] = useState(10);
	const [sortField, setSortField] = useState("cityId");
	const [sortOrder, setSortOrder] = useState(1);

	useEffect(() => {
		getUser().then((u) => {
			if (u && !u.expired) {
				setIsAuthenticated(true);
				setUsername(u.profile?.sub || u.profile?.preferred_username || "Usuario");
			} else {
				setIsAuthenticated(false);
			}
		});
	}, []);

	useEffect(() => {
		if (isAuthenticated) {
			loadCities({ page: 0, size: rows, sf: sortField, so: sortOrder });
			loadCountries();
		}
	}, [isAuthenticated]);

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

	const loadCountries = async () => {
		try {
			const res = await getCountries();
			setCountries(res.data);
		} catch (error) {
			console.error(error);
		}
	};

	const onSort = (event) => {
		setSortField(event.sortField);
		setSortOrder(event.sortOrder);
		setFirst(0);
		loadCities({ page: 0, size: rows, sf: event.sortField, so: event.sortOrder });
	};

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

	const openNew = () => {
		setCity({ cityId: null, city: "", countryId: null, lastUpdate: new Date() });
		setVisible(true);
	};

	const editCity = (row) => {
		setCity({ ...row, lastUpdate: parseDate(row.lastUpdate) });
		setVisible(true);
	};

	const saveCity = async () => {
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
			loadCities({ page: Math.floor(first / rows), size: rows, sf: sortField, so: sortOrder });
		} catch (error) {
			let errorMessage = "Error guardando";
			if (error.response?.data?.detail) errorMessage = error.response.data.detail;
			toast.current.show({ severity: "error", summary: "Error", detail: errorMessage });
		}
	};

	const confirmDelete = (row) => {
		confirmDialog({
			message: "¿Eliminar registro?",
			header: "Confirmar",
			icon: "pi pi-exclamation-triangle",
			accept: () => removeCity(row),
		});
	};

	const removeCity = async (row) => {
		await deleteCity(row.cityId);
		toast.current.show({ severity: "warn", summary: "Eliminado" });
		loadCities({ page: Math.floor(first / rows), size: rows, sf: sortField, so: sortOrder });
	};

	const handleDownloadExcel = async () => {
		const res = await downloadExcel();
		const link = document.createElement("a");
		link.href = `data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64,${res.data.base64}`;
		link.download = "cities.xlsx";
		link.click();
	};

	const handleLogin = () => login();
	const handleLogout = () => logout();

	const dateBodyTemplate = (rowData) => {
		const date = parseDate(rowData.lastUpdate);
		return date ? date.toLocaleString() : "";
	};

	if (!isAuthenticated) {
		return (
			<div className="p-5 text-center">
				<h2>No has iniciado sesión</h2>
				<Button label="Iniciar Sesión" icon="pi pi-sign-in" onClick={handleLogin} />
			</div>
		);
	}

	return (
		<div className="p-3">
			<Toast ref={toast} />
			<ConfirmDialog />

			<div className="flex justify-content-between align-items-center mb-3">
				<h2>Administración de Ciudades</h2>
				<div className="flex gap-2 align-items-center">
					<span className="mr-3">Bienvenido, <strong>{username}</strong></span>
					<Button label="Nueva" icon="pi pi-plus" onClick={openNew} />
					<Button label="Excel" icon="pi pi-file-excel" severity="success" onClick={handleDownloadExcel} />
					<Button label="Cerrar Sesión" icon="pi pi-sign-out" severity="warning" onClick={handleLogout} />
				</div>
			</div>

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
				<Column
					header="Acciones"
					body={(row) => (
						<div className="flex gap-2">
							<Button icon="pi pi-pencil" onClick={() => editCity(row)} />
							<Button icon="pi pi-trash" severity="danger" onClick={() => confirmDelete(row)} />
						</div>
					)}
				/>
			</DataTable>

			<Dialog header="Ciudad" visible={visible} onHide={() => setVisible(false)} style={{ width: "400px" }}>
				<div className="p-fluid">
					<div className="field">
						<label htmlFor="city">Ciudad</label>
						<InputText id="city" value={city.city} onChange={(e) => setCity({ ...city, city: e.target.value })} />
					</div>
					<div className="field">
						<label htmlFor="country">País</label>
						<Dropdown
							id="country"
							value={city.countryId}
							options={countries}
							optionLabel="country"
							optionValue="countryId"
							onChange={(e) => setCity({ ...city, countryId: e.value })}
						/>
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
