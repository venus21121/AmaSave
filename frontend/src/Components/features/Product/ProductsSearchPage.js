import React, { useEffect, useState } from "react";
import { useLocation, Link } from "react-router-dom";
import axios from "../../api/axios";

const AMAZON_URL = "https://www.amazon.com/dp/";
const PRODUCT_URL = "/product/search";

function ProductsSearchPage() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const location = useLocation();

  const productKeywords = new URLSearchParams(location.search).get("sq");

  const getProducts = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await axios.get(
        `${PRODUCT_URL}?input=${productKeywords}`
      );
      setProducts(response.data);
    } catch (error) {
      setError("Error fetching product details. Please try again.");
      console.error("Error fetching product details:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    getProducts();
  }, [productKeywords]);

  return (
    <div className="products_search_page p-4 ">
      <h1 className="text-2xl font-bold mb-6 ">
        Results for "{productKeywords}"
      </h1>
      {loading && <p>Loading products...</p>}
      {error && <p className="text-red-500">{error}</p>}
      {products.length === 0 && !loading && !error && (
        <p>No products found for "{productKeywords}"</p>
      )}
      <ul>
        {products.map((product) => (
          <li
            key={product.productId}
            className="flex items-center border border-gray-200 rounded-lg p-4 mb-4 shadow-md hover:shadow-lg transition-shadow duration-300"
          >
            <div className="flex flex-1">
              <Link
                to={`/productpage?sku=${product.productSku}`}
                className="flex items-center flex-1"
              >
                <img
                  className="w-24 h-24 object-contain rounded-md mr-4"
                  src={product.imgUrl}
                  alt={product.productName}
                />
                <div className="flex-1">
                  <h2 className="text-lg font-semibold mb-2 hover:underline">
                    {product.productName}
                  </h2>
                  <h2 className="text-md font-bold text-green-600 mb-4">
                    {product.currentPrice === -1
                      ? "Currently Unavailable"
                      : `$${product.currentPrice.toFixed(2)}`}
                  </h2>
                </div>
              </Link>

              <a
                href={AMAZON_URL + product.productSku}
                target="_blank"
                rel="noopener noreferrer"
                className="ml-4"
                aria-label={`View ${product.productName} on Amazon`}
              >
                <button className="bg-blue-500 text-white py-2 px-4 rounded hover:bg-blue-600 transition duration-200">
                  View at Amazon
                </button>
              </a>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default ProductsSearchPage;
