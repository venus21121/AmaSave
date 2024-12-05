import { useContext, useDebugValue } from "react";
import AuthContext from "../features/Auth/AuthProvider";

// Custom hook to access authentication context
const useAuth = () => {
  const { auth } = useContext(AuthContext);
  useDebugValue(auth, (auth) => (auth?.user ? "Logged In" : "Logged Out"));
  return useContext(AuthContext); // Returns the context object, including auth and setAuth
};

export default useAuth;
