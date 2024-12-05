import React, { useState, useEffect } from "react";
import axios from "../../api/axios";
import useAuth from "../../hooks/useAuth";
import { useNavigate } from "react-router-dom";

const REGISTER_URL = "/auth/signup"; // Your backend login URL

function Register() {
  const navigate = useNavigate(); // For redirection
  const { auth, setAuth } = useAuth(); // Access setAuth from the context

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [registerError, setRegisterError] = useState("");

  // Redirect to home if already logged in
  useEffect(() => {
    if (auth?.user) {
      navigate("/");
    }
  }, [auth, navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post(REGISTER_URL, {
        email: email,
        password: password,
      });
      const accessToken = response?.data?.token;

      // Update auth context and localStorage
      setAuth({ email, accessToken });
      localStorage.setItem("token", accessToken);
      localStorage.setItem("user", JSON.stringify(email));

      setEmail("");
      setPassword("");
      navigate("/");
    } catch (error) {
      if (error.response && error.response.status === 400) {
        setRegisterError(
          error.response.data.description || "This email is already registered."
        );
      } else {
        setRegisterError("An error occurred. Please try again later.");
      }
      console.error(
        "Register Error:",
        error.response ? error.response.data : error
      );
    }
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-100">
      <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-sm">
        <h1 className="text-xl font-semibold mb-4">
          Get started with a Free account
        </h1>
        {registerError && <p className="text-red-500 mb-4">{registerError}</p>}
        <form onSubmit={handleSubmit}>
          <label className="block mb-2">
            Email Address:
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="border border-gray-300 rounded-md p-2 w-full mb-4"
              required
            />
          </label>
          <label className="block mb-2">
            Password:
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="border border-gray-300 rounded-md p-2 w-full mb-4"
              required
            />
          </label>
          <button
            type="submit"
            className={`bg-blue-500 text-white p-2 rounded-md w-full ${
              !email || !password ? "opacity-50 cursor-not-allowed" : ""
            }`}
            disabled={!email || !password}
          >
            Create Account
          </button>
        </form>
      </div>
    </div>
  );
}

export default Register;
