const CART_KEY = "cart";

function getCart() {
  const cart = localStorage.getItem(CART_KEY);
  return cart ? JSON.parse(cart) : [];
}

function saveCart(cart) {
  localStorage.setItem(CART_KEY, JSON.stringify(cart));
}

function getProductImage(product) {
  if (product.imageUrls && product.imageUrls.length > 0) {
    return product.imageUrls[0];
  }

  return product.imageUrl || null;
}

function addToCart(product) {
  const cart = getCart();

  const existingItem = cart.find(item => item.id === product.id);

  if (existingItem) {
    existingItem.quantity += 1;

    if (!existingItem.imageUrl) {
      existingItem.imageUrl = getProductImage(product);
    }
  } else {
    cart.push({
      id: product.id,
      name: product.name,
      description: product.description,
      price: product.price,
      imageUrl: getProductImage(product),
      quantity: 1
    });
  }

  saveCart(cart);
}

function updateQuantity(productId, quantity) {
  const cart = getCart();

  const updatedCart = cart
    .map(item => {
      if (item.id === productId) {
        return { ...item, quantity: quantity };
      }
      return item;
    })
    .filter(item => item.quantity > 0);

  saveCart(updatedCart);
}

function removeFromCart(productId) {
  const cart = getCart();
  const updatedCart = cart.filter(item => item.id !== productId);
  saveCart(updatedCart);
}

function clearCart() {
  localStorage.removeItem(CART_KEY);
}

export default {
  getCart,
  addToCart,
  updateQuantity,
  removeFromCart,
  clearCart
};