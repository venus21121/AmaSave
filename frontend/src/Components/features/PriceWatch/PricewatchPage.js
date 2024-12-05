// User price Watch dashboard page
import React, { useEffect, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import useAxiosPrivate from "../../hooks/useAxiosPrivate";
import Product from "../Product/Product";

const AMAZON_URL = "https://www.amazon.com/dp/";
const PRICEWATCH_URLS = {
  GET: "/api/pricewatch",
  UPDATE: "/api/pricewatch/update",
  DELETE: "/api/pricewatch/delete",
};

function PricewatchPage() {
  const [userPriceWatches, setUserPriceWatches] = useState([]);
  const [editPriceWatchId, setEditPriceWatchId] = useState(null);
  const [desiredPrice, setDesiredPrice] = useState(""); // It can be a number if needed

  const axiosPrivate = useAxiosPrivate();
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    const getPriceWatches = async () => {
      try {
        const response = await axiosPrivate.get(PRICEWATCH_URLS.GET);
        setUserPriceWatches(response.data);
      } catch (error) {
        console.error("Error fetching user price watches: ", error);
        navigate("/login", { state: { from: location }, replace: true });
      }
    };
    getPriceWatches();
  }, [axiosPrivate, navigate, location]);

  const handleEdit = (priceWatch) => {
    setEditPriceWatchId(priceWatch.priceWatchId);
    setDesiredPrice(priceWatch.desiredPrice.toFixed(2)); // Pre-fill the input
  };

  const handleUpdate = async (priceWatch) => {
    if (parseFloat(desiredPrice) >= priceWatch.product.currentPrice) {
      alert("Price too high.");
      return;
    }

    try {
      await axiosPrivate.patch(
        `${PRICEWATCH_URLS.UPDATE}?priceWatchId=${priceWatch.priceWatchId}&newDesiredPrice=${desiredPrice}`
      );
      setUserPriceWatches((prev) =>
        prev.map((pw) =>
          pw.priceWatchId === priceWatch.priceWatchId
            ? { ...pw, desiredPrice: parseFloat(desiredPrice) }
            : pw
        )
      );
      setEditPriceWatchId(null); // Close the edit mode
    } catch (error) {
      console.error("Error updating price watch:", error);
    }
  };

  const handleDelete = async (priceWatch) => {
    const userConfirmed = window.confirm(
      "Are you sure you want to delete this price watch?"
    );
    if (userConfirmed) {
      try {
        await axiosPrivate.delete(
          `${PRICEWATCH_URLS.DELETE}?priceWatchId=${priceWatch.priceWatchId}`
        );
        setUserPriceWatches((prev) =>
          prev.filter((pw) => pw.priceWatchId !== priceWatch.priceWatchId)
        );
      } catch (error) {
        console.error("Error deleting price watch:", error);
      }
    }
  };

  const renderPriceWatch = (priceWatch, isActive) => (
    <li
      key={priceWatch.priceWatchId}
      className={`bg-${
        isActive ? "white" : "gray-100"
      } shadow-lg rounded-lg p-4`}
    >
      <div className="product-container">
        <Product
          id={priceWatch.product.productId}
          image={priceWatch.product.imgUrl}
          title={priceWatch.product.productName}
          b_price={priceWatch.product.currentPrice}
          url={`${AMAZON_URL}${priceWatch.product.productSku}`}
          sku={priceWatch.product.productSku}
        />
        <div className="mt-3">
          <p className="text-gray-700">
            <span className="font-semibold">Desired Price:</span> $
            {editPriceWatchId === priceWatch.priceWatchId ? (
              <input
                type="text"
                value={desiredPrice}
                onChange={(e) => setDesiredPrice(e.target.value)}
                className="border border-gray-300 rounded p-1 w-24"
                onKeyDown={(e) =>
                  ["e", "E", "+", "-"].includes(e.key) && e.preventDefault()
                }
              />
            ) : (
              priceWatch.desiredPrice.toFixed(2)
            )}
          </p>
          <p className="text-gray-700">
            <span className="font-semibold">Current Price:</span> $
            {priceWatch.product.currentPrice.toFixed(2)}
          </p>
        </div>
        <div className="flex justify-between mt-4">
          {isActive && editPriceWatchId === priceWatch.priceWatchId ? (
            <button
              onClick={() => handleUpdate(priceWatch)}
              className="text-blue-500 hover:underline"
            >
              Confirm
            </button>
          ) : isActive ? (
            <button
              onClick={() => handleEdit(priceWatch)}
              className="text-blue-500 hover:underline"
            >
              Edit
            </button>
          ) : null}{" "}
          {/* Hide edit button for inactive price watches */}
          <button
            onClick={() => handleDelete(priceWatch)}
            className="text-red-500 hover:underline"
          >
            Delete
          </button>
        </div>
      </div>
    </li>
  );

  const renderPriceWatchSection = (title, filterFn, noDataMessage) => (
    <section
      className={
        title === "Active Price Watches"
          ? "active-pricewatches mb-8"
          : "price-drop-alerts"
      }
    >
      <h3
        className={`text-xl font-semibold ${
          title === "Active Price Watches"
            ? "text-green-600"
            : "text-orange-500"
        } mb-3`}
      >
        {title}
      </h3>
      {userPriceWatches.filter(filterFn).length ? (
        <ul className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {userPriceWatches
            .filter((pw) => filterFn(pw)) // Apply filter function correctly
            .map(
              (priceWatch) =>
                renderPriceWatch(priceWatch, priceWatch.alertActive) // Pass alertActive as isActive
            )}
        </ul>
      ) : (
        <p className="text-gray-500">{noDataMessage}</p>
      )}
    </section>
  );

  return (
    <div className="pricewatch p-6">
      <h2 className="text-2xl font-bold mb-4 text-center">Price Watch List</h2>

      {renderPriceWatchSection(
        "Active Price Watches",
        (pw) => pw.alertActive,
        "No active price watches"
      )}
      {renderPriceWatchSection(
        "Price Drop Alerts Sent",
        (pw) => !pw.alertActive,
        "No price drop alerts sent"
      )}
    </div>
  );
}

export default PricewatchPage;
