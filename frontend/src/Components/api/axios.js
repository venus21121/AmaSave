import axios from "axios";
const BASE_URL = "http://localhost:8080";

// Default Axios instance
export default axios.create({
  baseURL: BASE_URL,
});

export const axiosPrivate = axios.create({
  baseURL: BASE_URL,
  headers: { "Content-Type": "application/json" },
  // withCredentials can be added if you plan to use cookies or refresh tokens in the future
});
