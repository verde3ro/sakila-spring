// src/services/cityService.js
import api from "./api";

export const getCitiesPagination = (params) =>
	api.get("/cities/pagination", { params });

export const createCity = (data) => api.post("/cities", data);
export const updateCity = (data) => api.put("/cities", data);
export const deleteCity = (id) => api.delete(`/cities/${id}`);
export const getCountries = () => api.get("/countries/");
export const downloadExcel = () => api.get("/cities/excel");
