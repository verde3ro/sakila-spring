import axios from "axios";

const API = "http://localhost:8080/api";

export const getCitiesPagination = (params) =>
	axios.get(`${API}/cities/pagination`, { params });

export const createCity = (data) => axios.post(`${API}/cities`, data);

export const updateCity = (data) => axios.put(`${API}/cities`, data);

export const deleteCity = (id) => axios.delete(`${API}/cities/${id}`);

export const getCountries = () => axios.get(`${API}/coutries/`);

export const downloadExcel = () => axios.get(`${API}/cities/excel`);
