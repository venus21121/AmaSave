import React from "react";
import { useLocation } from "react-router-dom";

function ProductNotFoundPage() {
  const location = useLocation();
  const search = new URLSearchParams(location.search).get("sq");

  return (
    <div className="product_notfound_page flex flex-col items-center justify-center h-screen bg-gray-100">
      <h1 className="text-3xl font-bold text-600 mb-4">
        Result "{search}" Not Found
      </h1>
      <p className="text-lg text-gray-600 mb-6">
        We're sorry, but the product you are looking for doesn't exist.
      </p>
    </div>
  );
}

export default ProductNotFoundPage;
