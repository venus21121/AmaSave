import React, { useState, useRef, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import useAuth from "../hooks/useAuth"; // Import useAuth hook
import axios from "../api/axios";
import ArrowDropDownIcon from "@mui/icons-material/ArrowDropDown";
import SearchIcon from "@mui/icons-material/Search";
import PersonIcon from "@mui/icons-material/Person";
import logo from "../../Assets/amaSave.png"; // Adjust the path as necessary

const PRODUCT_URL = "http://localhost:8080/product/search";

function Header() {
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [search, setSearch] = useState("");
  const { auth, setAuth } = useAuth();
  const dropdownRef = useRef(null);
  const nav = useNavigate();

  // Close dropdown when clicking outside of it
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setDropdownOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside); // Cleanup event listener
    };
  }, [dropdownRef]);

  const handleLogout = async () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    setAuth({});
    nav("/login"); // Redirect to login after logout
  };

  const handleSearch = async (e) => {
    e.preventDefault();

    // Exit if input is empty
    if (search.trim() === "") {
      return;
    }

    try {
      const response = await axios.get(`${PRODUCT_URL}?input=${search}`);
      const searchResult = response.data;
      if (Array.isArray(searchResult)) {
        if (searchResult.length === 1) {
          const productSku = searchResult[0].productSku;
          nav(`/productpage?sku=${productSku}`);
        } else if (searchResult.length > 1) {
          nav(`/search?sq=${search}`);
        } else {
          console.log("No products found");
        }
      } else {
        console.log("Unexpected response format");
      }
    } catch (err) {
      console.error("Error during search:", err);
      nav(`/error?sq=${search || "An error occurred"}`);
    }
  };

  return (
    <header className="flex items-center justify-between p-4 bg-[#232F3E] text-white shadow-md">
      <Link to="/">
        <img className="h-14" src={logo} alt="Logo" />
      </Link>

      <form className="flex flex-1 mx-4" onSubmit={handleSearch}>
        <input
          className="border border-gray-300 rounded-l-md p-2 w-full text-black"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          type="search"
          placeholder="Find Amazon Products"
        />
        <button
          type="submit"
          className="bg-[#FF9900] text-white p-2 rounded-r-md"
        >
          <SearchIcon style={{ color: "white" }} />
        </button>
      </form>

      <div className="relative" ref={dropdownRef}>
        <button
          className="flex items-center p-2 border border-gray-300 rounded-md focus:outline-none"
          onClick={() => setDropdownOpen(!dropdownOpen)} // Toggle dropdown
        >
          <PersonIcon />
          <ArrowDropDownIcon />
        </button>

        {dropdownOpen && (
          <div className="absolute right-0 mt-2 w-48 bg-white border border-gray-300 rounded-md shadow-lg z-10">
            <div className="py-2">
              {auth?.user ? (
                <>
                  <Link
                    to="/account"
                    className="block px-4 py-2 text-gray-800 hover:bg-gray-100"
                  >
                    My Account
                  </Link>
                  <Link
                    to="/pricewatch"
                    className="block px-4 py-2 text-gray-800 hover:bg-gray-100"
                  >
                    My Pricewatch
                  </Link>
                  {/* <Link
                    to="/admin"
                    className="block px-4 py-2 text-gray-800 hover:bg-gray-100"
                  >
                    Admin Page
                  </Link> */}
                  <button
                    onClick={handleLogout}
                    className="block w-full text-left px-4 py-2 text-gray-800 hover:bg-gray-100"
                  >
                    Logout
                  </button>
                </>
              ) : (
                <>
                  <Link
                    to="/register"
                    className="block px-4 py-2 text-gray-800 hover:bg-gray-100"
                  >
                    Create Free Account
                  </Link>
                  <Link
                    to="/login"
                    className="block px-4 py-2 text-gray-800 hover:bg-gray-100"
                  >
                    Login
                  </Link>
                </>
              )}
            </div>
          </div>
        )}
      </div>
    </header>
  );
}

export default Header;
