import React, { useState } from "react";
import Product from "./Product/Product.js";

const AMAZON_URL = "https://www.amazon.com/dp/";

function HomePage({ h_products }) {
  const user = JSON.parse(localStorage.getItem("user"));
  const [currentIndex, setCurrentIndex] = useState(0); // State to manage the index of the first product displayed
  const itemsToShow = 5; // Show 5 products at a time
  const totalSlides = Math.ceil(h_products.length / itemsToShow); // Calculate total number of slides

  // Generic function for navigating between slides
  const handleNavigation = (direction) => {
    const newIndex = currentIndex + direction * itemsToShow;
    if (newIndex < 0) {
      setCurrentIndex(h_products.length - (h_products.length % itemsToShow));
    } else if (newIndex >= h_products.length) {
      setCurrentIndex(0); // Loop back to the start
    } else {
      setCurrentIndex(newIndex);
    }
  };

  // Calculate the translation percentage for carousel movement
  const transformStyle = {
    transform: `translateX(-${(currentIndex / itemsToShow) * 100}%)`, // Adjust the transformation for full slides
  };

  return (
    <div className="home">
      <div className="home_container flex flex-col items-center p-4 bg-gray-100">
        {user && <h1>Welcome {user}</h1>} {/* Simplified greeting */}
        <h1 className="mb-4 text-2xl font-bold">Popular Products</h1>
        <div className="relative w-full max-w-screen-xl overflow-hidden p-4">
          <div
            className="flex transition-transform duration-300"
            style={transformStyle}
          >
            {h_products.map((product) => (
              <div
                key={product.productId}
                className="w-1/5 flex-shrink-0 p-2 mx-auto"
              >
                <Product
                  id={product.productId}
                  image={product.imgUrl}
                  title={product.productName}
                  b_price={product.currentPrice}
                  url={AMAZON_URL + product.productSku}
                  sku={product.productSku}
                />
              </div>
            ))}
          </div>
          {/* Left Arrow */}
          <button
            className="absolute left-0 top-1/2 transform -translate-y-1/2 bg-gray-800 text-white p-2 rounded-full hover:bg-gray-700"
            onClick={() => handleNavigation(-1)} // Handle previous click
          >
            &#10094;
          </button>
          {/* Right Arrow */}
          <button
            className="absolute right-0 top-1/2 transform -translate-y-1/2 bg-gray-800 text-white p-2 rounded-full hover:bg-gray-700"
            onClick={() => handleNavigation(1)} // Handle next click
          >
            &#10095;
          </button>
        </div>
        {/* Pagination Indicator */}
        <div className="mt-4">
          <p className="text-sm">
            {Math.floor(currentIndex / itemsToShow) + 1} of {totalSlides}
          </p>
        </div>
      </div>
    </div>
  );
}

export default HomePage;
