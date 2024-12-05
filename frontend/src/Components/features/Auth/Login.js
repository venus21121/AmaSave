import { useRef, useState, useEffect } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import useAuth from "../../hooks/useAuth";
import axios from "../../api/axios";

const LOGIN_URL = "/auth/login"; // Your backend login URL

const Login = () => {
  const navigate = useNavigate();
  const { auth, setAuth } = useAuth(); // Access setAuth from the context
  const location = useLocation();
  const from = location.state?.from?.pathname || "/"; // Redirect after login

  const userRef = useRef(); // Auto-focus on username input
  const errRef = useRef(null); // Focus on error message

  const [user, setUser] = useState(""); // User email
  const [pwd, setPwd] = useState("");
  const [errMsg, setErrMsg] = useState("");

  // Auto-focus on username input when the component loads
  useEffect(() => {
    userRef.current.focus();
  }, []);

  // Redirect to home if already logged in
  useEffect(() => {
    let isMounted = true;
    if (auth?.user && isMounted) {
      navigate("/");
    }
    return () => {
      isMounted = false;
    };
  }, [auth, navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrMsg(""); // Reset error message
    const controller = new AbortController(); // For request cancellation

    try {
      const response = await axios.post(
        LOGIN_URL,
        JSON.stringify({ email: user, password: pwd }),
        {
          headers: { "Content-Type": "application/json" },
          signal: controller.signal, // Attach AbortController signal
        }
      );

      const accessToken = response?.data?.token;
      localStorage.setItem("token", accessToken);
      localStorage.setItem("user", JSON.stringify(user));
      setAuth({ user, accessToken }); // Set authentication context

      // Clear inputs
      setUser("");
      setPwd("");

      // Redirect to previous page
      navigate(from, { replace: true });
    } catch (err) {
      // Handle errors
      handleError(err);
    } finally {
      controller.abort(); // Cancel Axios request on cleanup
    }
  };

  const handleError = (err) => {
    if (!err?.response) {
      setErrMsg("No Server Response");
    } else if (err.response?.status === 400) {
      setErrMsg("Missing Username or Password");
    } else if (err.response?.status === 401) {
      setErrMsg("Incorrect username or password.");
    } else if (err.response?.data?.message) {
      setErrMsg(`Error: ${err.response.data.message}`);
    } else {
      setErrMsg("Login Failed");
    }

    // Focus on error message if it exists
    if (errRef.current) errRef.current.focus();
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-100">
      <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-sm">
        <h1 className="text-xl font-semibold mb-4">Sign In</h1>
        {errMsg && <p className="text-red-500 mb-4">{errMsg}</p>}{" "}
        {/* Show error message if it exists */}
        <form onSubmit={handleSubmit}>
          <label className="block mb-2" htmlFor="username">
            Username or Email
            <input
              type="text"
              id="username"
              ref={userRef}
              autoComplete="off"
              onChange={(e) => setUser(e.target.value)}
              value={user}
              required
              className="border border-gray-300 rounded-md p-2 w-full mb-4"
              placeholder="youremail@gmail.com" // Optional placeholder for clarity
            />
          </label>

          <label className="block mb-2" htmlFor="password">
            Password
            <input
              type="password"
              id="password"
              onChange={(e) => setPwd(e.target.value)}
              value={pwd}
              required
              className="border border-gray-300 rounded-md p-2 w-full mb-4"
              placeholder="**********" // Optional placeholder for clarity
            />
          </label>

          <button
            type="submit"
            className={`bg-blue-500 text-white p-2 rounded-md w-full ${
              !user || !pwd ? "opacity-50 cursor-not-allowed" : ""
            }`}
            disabled={!user || !pwd} // Disable button if fields are empty
          >
            Sign In
          </button>
        </form>
        <Link to="/register" className="block mt-4 text-center text-blue-500">
          Need an Account? Sign Up
        </Link>
      </div>
    </div>
  );
};

export default Login;
