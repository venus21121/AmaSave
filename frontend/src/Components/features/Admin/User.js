import { useNavigate, useLocation } from "react-router-dom";
import { useState, useEffect } from "react";
import useAxiosPrivate from "../../hooks/useAxiosPrivate";
import useAuth from "../../hooks/useAuth";
const Users = () => {
  const [users, setUsers] = useState([]);
  const { auth } = useAuth();
  const axiosPrivate = useAxiosPrivate();
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    let isMounted = true;
    const controller = new AbortController();

    const getUsers = async () => {
      try {
        console.log("Current access token:", auth.accessToken); // Check token for debugging

        const response = await axiosPrivate.get("/users/", {
          signal: controller.signal, // Cancel request if the component unmounts
        });

        setUsers(response.data); // Set users data from response
      } catch (err) {
        console.error("Failed to fetch users:", err); // Log error
        navigate("/login", { state: { from: location }, replace: true }); // Redirect to login if error
      }
    };

    if (auth.accessToken) {
      getUsers();
    }

    return () => {
      isMounted = false;
      controller.abort();
    };
  }, [auth.accessToken, axiosPrivate, navigate, location]);

  return (
    <article>
      <h2>Users List</h2>
      {users.length ? (
        <ul>
          {users.map((user) => (
            <li key={user.id}>{user.username}</li>
          ))}
        </ul>
      ) : (
        <p>No users to display</p>
      )}
    </article>
  );
};

export default Users;
