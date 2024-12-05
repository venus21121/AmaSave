import React, { useEffect, useState } from "react";
import { useLocation, Link } from "react-router-dom";
import useAxiosPrivate from "../../hooks/useAxiosPrivate";
import PriceWatchForm from "../PriceWatch/PriceWatchForm";
import useAuth from "../../hooks/useAuth";
import axios from "../../api/axios";

const AMAZON_URL = "https://www.amazon.com/dp/";
const PRODUCT_URL = "/product/";
const PRICEWATCH_URLS = {
  FETCH: "/api/pricewatch/list",
  CREATE: "/api/pricewatch/create",
};

function ProductPage() {
  const [product, setProduct] = useState(null);
  // Price watch forms
  const [oldPriceWatches, setOldPriceWatches] = useState([]);
  const [newPriceWatches, setNewPriceWatches] = useState([]);
  const [isFormVisible, setIsFormVisible] = useState(false);
  const [targetPrice, setTargetPrice] = useState("");
  const [errMsg, setErrMsg] = useState("");

  const { auth } = useAuth();
  const location = useLocation();
  const axiosPrivate = useAxiosPrivate();
  const productSku = new URLSearchParams(location.search).get("sku");

  // Fetch product details
  useEffect(() => {
    let isMounted = true;
    if (!productSku) return;

    const fetchProductDetails = async () => {
      try {
        const response = await axios.get(PRODUCT_URL + productSku);
        if (isMounted) setProduct(response.data);
      } catch (error) {
        console.error("Error fetching product details:", error);
      }
    };

    fetchProductDetails();

    return () => {
      isMounted = false;
    };
  }, [productSku]);

  // Fetch price watches
  useEffect(() => {
    if (!product?.productId) return;

    setNewPriceWatches([]); // Reset the input field

    const fetchPriceWatches = async () => {
      try {
        const response = await axiosPrivate.get(
          `${PRICEWATCH_URLS.FETCH}?productId=${product.productId}`
        );
        setOldPriceWatches(response.data);
      } catch (error) {
        console.error("Error fetching price watches:", error);
      }
    };

    fetchPriceWatches();
  }, [product?.productId, axiosPrivate]);

  // Toggle form visibility
  const handleCreatePriceWatchClick = () => setIsFormVisible((prev) => !prev);

  // Handles price input formating
  const handlePriceChange = (e) => {
    const numericValue = e.target.value.replace(/[^0-9]/g, ""); // Remove non-numeric characters
    const formattedValue = (parseFloat(numericValue) / 100).toFixed(2);
    setTargetPrice(formattedValue);
  };

  // Create PriceWatch
  const handlePriceWatchSubmit = async (e) => {
    e.preventDefault();
    setErrMsg("");

    const desiredPrice = parseFloat(targetPrice);
    if (desiredPrice >= product.currentPrice) {
      return setErrMsg("Price too high.");
    }
    if (desiredPrice <= 0) {
      return setErrMsg("Price must be higher.");
    }
    try {
      const response = await axiosPrivate.post(
        PRICEWATCH_URLS.CREATE,
        {},
        {
          params: {
            productId: product.productId,
            desiredPrice,
          },
        }
      );

      setNewPriceWatches((prev) => [...prev, response.data]);
      setTargetPrice(""); // Reset the input field
    } catch (error) {
      console.error("Error creating price watch:", error);
    }
  };

  if (!product) return <div>Loading...</div>;

  return (
    <div className="product_page max-w-8xl p-6 mx-auto">
      {/* Product Info and Price Watch Row */}
      <div className="flex flex-col md:flex-row gap-8">
        {/* Product Info Section */}
        <div className="md:w-3/4 flex flex-col items-start bg-white shadow rounded-lg p-8">
          <div className="flex items-start mb-6">
            <img
              className="w-48 h-48 object-contain rounded-lg mr-6"
              src={product.imgUrl}
              alt={product.productName}
            />
            <div className="flex-1">
              <h2 className="text-2xl font-bold mb-2">{product.productName}</h2>
              <div className="flex items-center justify-between mb-4">
                {product.currentPrice === -1 ? (
                  <h3 className="text-1xl font-bold text-green-600">
                    Currently Unavailable
                  </h3>
                ) : (
                  <h3 className="text-2xl font-bold text-green-600">
                    ${product.currentPrice.toFixed(2)}
                  </h3>
                )}
                <a
                  href={AMAZON_URL + product.productSku}
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  <button className="amazon_link bg-blue-500 text-white py-2 px-4 rounded-lg hover:bg-blue-600 transition duration-300 w-full">
                    View at Amazon
                  </button>
                </a>
              </div>
            </div>
          </div>
          {/* Product Details */}
          <div className="product_detail mt-6">
            <h3 className="text-xl font-semibold mb-2">Product Details</h3>
            <p className="text-gray-700">
              Manufacturer, Model, Locale, List Price, EAN, UPC, SKU, Last
              Update, Scan
            </p>
          </div>
          {/* Amazon Price History Section */}
          <div className="amazon_price_history mt-6">
            <h3 className="text-xl font-semibold mb-2">Amazon Price History</h3>
            {/* You can add a price history chart or data table here */}
          </div>
        </div>

        {/* Price Watch Section */}
        <div className="md:w-1/4 bg-gray-100 shadow rounded-lg p-6">
          <h3 className="text-xl font-semibold mb-4 text-gray-700">
            Your Price Watches
          </h3>
          {auth?.user ? (
            <>
              {product.currentPrice !== -1 && (
                <>
                  {/* Create Price Watch Button */}
                  <div className="flex justify-start mb-4">
                    <button
                      onClick={handleCreatePriceWatchClick}
                      className="bg-blue-500 text-white py-2 px-4 rounded-lg hover:bg-blue-600 transition duration-300"
                    >
                      {isFormVisible ? "Cancel" : "Create Price Watch"}
                    </button>
                  </div>
                  {/* New Price Watch Form */}
                  {isFormVisible && (
                    <div className="pricewatch_form bg-white shadow rounded-lg p-4 mb-6">
                      <h4 className="text-lg font-semibold mb-4">
                        Set a Price Watch
                      </h4>
                      <form
                        onSubmit={handlePriceWatchSubmit}
                        className="space-y-4"
                      >
                        <div>
                          {errMsg && (
                            <p className="text-red-600 mb-4">{errMsg}</p>
                          )}

                          <label
                            className="block text-sm font-medium text-gray-700 mb-1"
                            htmlFor="targetPrice"
                          >
                            Desired Price:
                          </label>
                          <input
                            id="targetPrice"
                            type="text"
                            value={targetPrice}
                            onChange={handlePriceChange}
                            onKeyDown={(e) =>
                              ["e", "E", "+", "-"].includes(e.key) &&
                              e.preventDefault()
                            }
                            className="w-full border border-gray-300 rounded-lg p-2"
                            placeholder="Enter your target price e.g., 50.00"
                          />
                        </div>
                        <button
                          type="submit"
                          className="bg-green-500 text-white py-2 px-4 rounded-lg hover:bg-green-600 transition duration-300"
                        >
                          Save Price Watch
                        </button>
                      </form>
                    </div>
                  )}

                  {/* Existing Price Watches */}
                  <div className="space-y-4">
                    {newPriceWatches.map((priceWatch, index) => (
                      <PriceWatchForm
                        key={`user-${priceWatch.id || `fallback-${index}`}`}
                        priceWatch={priceWatch}
                      />
                    ))}
                    {oldPriceWatches.map((priceWatch, index) => (
                      <PriceWatchForm
                        key={`global-${priceWatch.id || `fallback-${index}`}`}
                        priceWatch={priceWatch}
                      />
                    ))}
                  </div>
                </>
              )}
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
          )}{" "}
        </div>
      </div>
    </div>
  );
}

export default ProductPage;
