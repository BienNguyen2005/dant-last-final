import createToast, { toastComponent } from "./toast.js";
import { setTotalCartItemsQuantity } from "./header.js";

// STATIC DATA
const currentUserIdMetaTag = document.querySelector("meta[name='currentUserId']");
const productTitleElement = document.querySelector(".title");

// MESSAGES
const REQUIRED_SIGNIN_MESSAGE = "Vui lòng đăng nhập để thực hiện thao tác!";
const SUCCESS_ADD_CART_ITEM_MESSAGE = (quantity, productTitle) =>
  `Đã thêm thành công ${quantity} sản phẩm ${productTitle} vào giỏ hàng!`;
const FAILED_ADD_CART_ITEM_MESSAGE = "Đã có lỗi truy vấn!";

// UTILS

async function _HomefetchPostAddCartItem(productId) {
  const cartItemRequest = {
    userId: currentUserIdMetaTag.content,
    productId: productId,
    quantity: 1,
  };

  const response = await fetch("/cartItem", {
    method: "POST",
    headers: {
      "Accept": "application/json",
      "Content-Type": "application/json",
    },
    body: JSON.stringify(cartItemRequest),
  });
  return [response.status, await response.json()];
}


// EVENT HANDLERS
function noneSigninEvent() {
  createToast(toastComponent(REQUIRED_SIGNIN_MESSAGE));
}

async function buyNowBtnEvent(productId) {
  const [status] = await _HomefetchPostAddCartItem(productId);
  if (status === 200) {
    window.location.href = "/cart";
  } else if (status === 404) {
    createToast(toastComponent(FAILED_ADD_CART_ITEM_MESSAGE, "danger"));
  }
}

async function addCartItemBtnEvent(productId) {
  const [status] = await _HomefetchPostAddCartItem(productId);
  if (status === 200) {
    createToast(toastComponent(
      SUCCESS_ADD_CART_ITEM_MESSAGE('1', productTitleElement.innerText), "success"));
    setTotalCartItemsQuantity('1');
  } else if (status === 404) {
    createToast(toastComponent(FAILED_ADD_CART_ITEM_MESSAGE, "danger"));
  }
}

// MAIN
const addCartItemBtns = document.querySelectorAll(".add-cart-item");
const buyNowBtns = document.querySelectorAll(".buy-now");

if (currentUserIdMetaTag) {
	addCartItemBtns.forEach((btn) => {
  		 btn.addEventListener("click", () => {
        	addCartItemBtnEvent(btn.getAttribute("data-id")); 
    	});
  	});
  	
  	buyNowBtns.forEach((btn) => {
  		 btn.addEventListener("click", () => {
        	buyNowBtnEvent(btn.getAttribute("data-id")); 
    	});
  	});
} else {
  	addCartItemBtns.forEach((btn) => {
  		btn.addEventListener("click", noneSigninEvent);
  	});
  	
  	buyNowBtns.forEach((btn) => {
  		btn.addEventListener("click", noneSigninEvent);
  	});
}
