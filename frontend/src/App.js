import { BrowserRouter, Routes, Route } from "react-router-dom";
import { useState, useEffect } from "react";
import { AuthProvider } from "./Components/features/Auth/AuthProvider.js";
import Register from "./Components/features/Auth/Register.js";
import Login from "./Components/features/Auth/Login.js";
import axios from "./Components/api/axios.js";
import Header from "./Components/navigation/Header.js";
import RequireAuth from "./Components/navigation/RequireAuth.js";
import HomePage from "./Components/features/HomePage.js";
import ProductPage from "./Components/features/Product/ProductPage.js";
import ProductSearchPage from "./Components/features/Product/ProductsSearchPage.js";
import ProductNotFoundPage from "./Components/features/Product/ProductNotFoundPage.js";
import PricewatchPage from "./Components/features/PriceWatch/PricewatchPage.js";
import Admin from "./Components/features/Admin/AdminPage.js";

const PRODUCT_URL = "/product";

const App = () => {
  const [products, setProducts] = useState([]);
  const [connection, setConnections] = useState(false);

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const response = await axios.get(PRODUCT_URL);
        setProducts(response.data);
        setConnections(true);
      } catch (err) {
        console.log(err);
        setConnections(false);
      }
    };
    fetchProducts();
  }, []);

  if (!connection)
    return (
      <div className="error-container">
        <p>Unable to load products. Please try again later.</p>
      </div>
    );
  return (
    <div className="app">
      <BrowserRouter>
        <AuthProvider>
          <Header />
          <Routes>
            <Route path="/" element={<HomePage h_products={products} />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/productpage" element={<ProductPage />} />
            <Route path="/search" element={<ProductSearchPage />} />
            <Route path="/error" element={<ProductNotFoundPage />} />

            {/* Protected Routes */}
            <Route element={<RequireAuth />}>
              <Route path="/pricewatch" element={<PricewatchPage />} />
              <Route path="/admin" element={<Admin />} />
            </Route>
          </Routes>
        </AuthProvider>
      </BrowserRouter>
    </div>
  );
};

export default App;
