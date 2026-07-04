import { Menubar } from "primereact/menubar";
import { NavLink } from "react-router-dom";

// Template reutilizable: recibe el item del menú y arma un NavLink con su ícono y label.
const linkTemplate = (item) => (
	<NavLink
		to={item.url}
		end={item.url === "/"} // "end" evita que "/" quede activo en todas las rutas
		className={({ isActive }) =>
			`p-menuitem-link${isActive ? " p-menuitem-active-custom" : ""}`
		}
	>
		<span className={`p-menuitem-icon ${item.icon}`} />
		<span className="p-menuitem-text">{item.label}</span>
	</NavLink>
);

const NavBar = () => {
	const items = [
		{ label: "Ciudades", icon: "pi pi-home", url: "/", template: linkTemplate },
		{ label: "Acerca de", icon: "pi pi-info-circle", url: "/about", template: linkTemplate },
		{ label: "Contacto", icon: "pi pi-envelope", url: "/contact", template: linkTemplate },
	];

	const start = <span className="font-bold text-xl mr-4 ml-2">Mi Aplicación</span>;

	return (
		<>
			{/* Estilo del item activo. Puedes mover esto a un .css si prefieres. */}
			<style>{`
				.p-menuitem-active-custom,
				.p-menuitem-active-custom .p-menuitem-icon,
				.p-menuitem-active-custom .p-menuitem-text {
					color: var(--primary-color) !important;
					font-weight: 600;
				}
				.p-menuitem-active-custom {
					background-color: var(--primary-50, #EFF6FF);
					border-radius: 6px;
				}
			`}</style>
			<Menubar model={items} start={start} className="mb-3" />
		</>
	);
};

export default NavBar;
