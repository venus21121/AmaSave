import { useLocation, Navigate, Outlet } from "react-router-dom";

// Protects routes and ensures user is authenticated
const RequireAuth = () => {
  const location = useLocation();
  const user = JSON.parse(localStorage.getItem("user"));

  // If user is authenticated, render child routes, else redirect to login
  return user ? (
    <Outlet />
  ) : (
    <Navigate to="/login" state={{ from: location }} replace />
  );
};

export default RequireAuth;
