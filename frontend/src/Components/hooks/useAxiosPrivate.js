import { axiosPrivate } from "../api/axios";
import { useEffect } from "react";
import useAuth from "./useAuth";

// Custom hook for handling axios requests with the access token
const useAxiosPrivate = () => {
  const { auth } = useAuth();

  useEffect(() => {
    // Request interceptor to add the Authorization header
    const requestIntercept = axiosPrivate.interceptors.request.use(
      (config) => {
        if (auth?.accessToken && !config.headers["Authorization"]) {
          config.headers["Authorization"] = `Bearer ${auth.accessToken}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    // Response interceptor to handle token expiration
    const responseIntercept = axiosPrivate.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error?.response?.status === 403) {
          console.log("Token expired or invalid");
        }
        return Promise.reject(error);
      }
    );

    // Cleanup interceptors on component unmount or when auth changes
    return () => {
      axiosPrivate.interceptors.request.eject(requestIntercept);
      axiosPrivate.interceptors.response.eject(responseIntercept);
    };
  }, [auth]); // Depend on auth to update the interceptors when the token changes

  return axiosPrivate;
};

export default useAxiosPrivate;
