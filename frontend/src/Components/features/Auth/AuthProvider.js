import { createContext, useState, useEffect } from "react";
import { jwtDecode } from "jwt-decode";
import { useNavigate } from "react-router-dom";

const AuthContext = createContext({});

export const AuthProvider = ({ children }) => {
  const [auth, setAuth] = useState({});
  const navigate = useNavigate();

  const isTokenExpired = (token) => {
    try {
      const { exp } = jwtDecode(token); // Decode and get expiry
      return exp * 1000 < Date.now(); // Compare with the current time
    } catch {
      return true; // If decoding fails, treat it as expired
    }
  };

  useEffect(() => {
    const accessToken = localStorage.getItem("token");
    const user = JSON.parse(localStorage.getItem("user"));

    if (accessToken) {
      if (isTokenExpired(accessToken)) {
        // Clear expired token
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        setAuth({});
        navigate("/login"); // Redirect to login
        console.warn("Token expired. User redirected to login.");
      } else {
        setAuth({ user, accessToken });
      }
    }
  }, [navigate]);

  return (
    <AuthContext.Provider value={{ auth, setAuth }}>
      {children}
    </AuthContext.Provider>
  );
};

export default AuthContext;
