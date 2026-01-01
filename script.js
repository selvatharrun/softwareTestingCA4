// ==========================================
// REGISTRATION FUNCTIONALITY
// ==========================================
function performRegister() {
    const user = document.getElementById('reg-username').value.trim();
    const email = document.getElementById('reg-email').value.trim();
    const pass = document.getElementById('reg-password').value;
    const confirm = document.getElementById('reg-confirm').value;
    const termsChecked = document.getElementById('terms-checkbox').checked;
    
    // Clear previous errors
    clearFieldErrors();
    hideMessages();
    
    let hasError = false;
    
    // Validate username
    if (user === "") {
        showFieldError('reg-username-error', 'Username is required');
        hasError = true;
    } else if (user.length < 3) {
        showFieldError('reg-username-error', 'Username must be at least 3 characters');
        hasError = true;
    }
    
    // Validate email
    if (email === "") {
        showFieldError('reg-email-error', 'Email is required');
        hasError = true;
    } else if (!isValidEmail(email)) {
        showFieldError('reg-email-error', 'Please enter a valid email address');
        hasError = true;
    }
    
    // Validate password
    if (pass === "") {
        showFieldError('reg-password-error', 'Password is required');
        hasError = true;
    } else if (pass.length < 6) {
        showFieldError('reg-password-error', 'Password must be at least 6 characters');
        hasError = true;
    }
    
    // Validate confirm password
    if (confirm === "") {
        showFieldError('reg-confirm-error', 'Please confirm your password');
        hasError = true;
    } else if (pass !== confirm) {
        showFieldError('reg-confirm-error', 'Passwords do not match');
        hasError = true;
    }
    
    // Validate terms
    if (!termsChecked) {
        showError('reg-error', 'You must agree to the Terms & Conditions');
        hasError = true;
    }
    
    if (hasError) return;

    // Store user data
    localStorage.setItem("storedUser", user);
    localStorage.setItem("storedPass", pass);
    localStorage.setItem("storedEmail", email);
    
    showSuccess('reg-success', 'Registration successful! Redirecting to login...');
    
    setTimeout(function() {
        window.location.href = "login.html";
    }, 1500);
}

function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

// Password strength indicator
if (document.getElementById('reg-password')) {
    document.getElementById('reg-password').addEventListener('input', function() {
        updatePasswordStrength(this.value);
    });
}

function updatePasswordStrength(password) {
    const strengthBar = document.getElementById('strength-bar');
    if (!strengthBar) return;
    
    let strength = 0;
    if (password.length >= 6) strength++;
    if (password.length >= 10) strength++;
    if (/[A-Z]/.test(password)) strength++;
    if (/[0-9]/.test(password)) strength++;
    if (/[^A-Za-z0-9]/.test(password)) strength++;
    
    const widths = ['0%', '20%', '40%', '60%', '80%', '100%'];
    const colors = ['#e74c3c', '#e74c3c', '#f39c12', '#f1c40f', '#2ecc71', '#27ae60'];
    
    strengthBar.style.width = widths[strength];
    strengthBar.style.backgroundColor = colors[strength];
}

// ==========================================
// LOGIN FUNCTIONALITY
// ==========================================
function performLogin() {
    const userInput = document.getElementById('username').value.trim();
    const passInput = document.getElementById('password').value;
    const rememberMe = document.getElementById('remember-me') ? document.getElementById('remember-me').checked : false;
    
    // Clear previous errors
    clearFieldErrors();
    hideMessages();
    
    let hasError = false;
    
    // Validate inputs
    if (userInput === "") {
        showFieldError('username-error', 'Username is required');
        hasError = true;
    }
    
    if (passInput === "") {
        showFieldError('password-error', 'Password is required');
        hasError = true;
    }
    
    if (hasError) return;
    
    const storedUser = localStorage.getItem("storedUser");
    const storedPass = localStorage.getItem("storedPass");

    if (userInput === storedUser && passInput === storedPass) {
        sessionStorage.setItem("currentUser", userInput);
        
        if (rememberMe) {
            localStorage.setItem("rememberedUser", userInput);
        } else {
            localStorage.removeItem("rememberedUser");
        }
        
        showSuccess('success-msg', 'Login successful! Redirecting...');
        
        setTimeout(function() {
            window.location.href = "dashboard.html";
        }, 1000);
    } else {
        showError('error-msg', 'Invalid username or password');
    }
}

// Auto-fill remembered user
if (document.getElementById('username')) {
    const remembered = localStorage.getItem("rememberedUser");
    if (remembered) {
        document.getElementById('username').value = remembered;
        if (document.getElementById('remember-me')) {
            document.getElementById('remember-me').checked = true;
        }
    }
}

// Forgot Password Modal
function showForgotPassword() {
    const modal = document.getElementById('forgot-modal');
    if (modal) modal.classList.remove('hidden');
}

function closeForgotModal() {
    const modal = document.getElementById('forgot-modal');
    if (modal) modal.classList.add('hidden');
}

function sendResetLink() {
    const email = document.getElementById('reset-email').value.trim();
    const msgEl = document.getElementById('reset-msg');
    
    if (email === "") {
        msgEl.innerText = "Please enter your email address";
        msgEl.className = "error-text";
    } else if (!isValidEmail(email)) {
        msgEl.innerText = "Please enter a valid email address";
        msgEl.className = "error-text";
    } else {
        msgEl.innerText = "If an account exists with this email, a reset link has been sent.";
        msgEl.className = "success-text";
    }
}

// ==========================================
// SESSION & AUTHENTICATION
// ==========================================
function checkSession() {
    if (!sessionStorage.getItem("currentUser")) {
        window.location.href = "login.html";
    } else {
        const username = sessionStorage.getItem("currentUser");
        document.getElementById('display-name').innerText = "Hi, " + username;
        if (document.getElementById('display-name-input')) {
            document.getElementById('display-name-input').value = username;
        }
    }
}

function performLogout() {
    sessionStorage.removeItem("currentUser");
    showNotification("You have been logged out", "info");
    setTimeout(function() {
        window.location.href = "login.html";
    }, 500);
}

function toggleMenu() {
    const menu = document.getElementById('profile-menu');
    menu.classList.toggle('hidden');
}

// Close dropdown when clicking outside
document.addEventListener('click', function(e) {
    const profileBtn = document.getElementById('profile-btn');
    const profileMenu = document.getElementById('profile-menu');
    
    if (profileBtn && profileMenu && !profileBtn.contains(e.target) && !profileMenu.contains(e.target)) {
        profileMenu.classList.add('hidden');
    }
});

// ==========================================
// CART FUNCTIONALITY
// ==========================================
let cart = [];
let promoApplied = false;
let promoDiscount = 0;

function quickAdd(itemName, itemPrice) {
    addItemToCart(itemName, itemPrice, 1);
}

function addToCart() {
    const select = document.getElementById('combo-item');
    const selectedOption = select.options[select.selectedIndex];
    const itemName = selectedOption.getAttribute('data-name') || selectedOption.text.split(' - ')[0];
    const itemPrice = parseFloat(select.value);
    const qty = parseInt(document.getElementById('quantity').value);

    if (qty < 1 || qty > 99 || isNaN(qty)) {
        showNotification("Please enter a valid quantity (1-99)", "error");
        return;
    }

    addItemToCart(itemName, itemPrice, qty);
}

function addItemToCart(itemName, itemPrice, qty) {
    // Check if item already exists in cart
    const existingItem = cart.find(item => item.name === itemName);
    
    if (existingItem) {
        existingItem.qty += qty;
        existingItem.cost = existingItem.qty * existingItem.price;
    } else {
        cart.push({
            id: Date.now(),
            name: itemName,
            price: itemPrice,
            qty: qty,
            cost: itemPrice * qty
        });
    }
    
    updateCartDisplay();
    showNotification(`Added ${qty}x ${itemName} to cart`, "success");
}

function removeFromCart(itemId) {
    cart = cart.filter(item => item.id !== itemId);
    updateCartDisplay();
    showNotification("Item removed from cart", "info");
}

function updateCartDisplay() {
    const tbody = document.getElementById('cart-body');
    const emptyCart = document.getElementById('empty-cart');
    const cartTable = document.getElementById('cart-table');
    const cartSummary = document.getElementById('cart-summary');
    const cartCount = document.getElementById('cart-count');
    
    if (cart.length === 0) {
        emptyCart.classList.remove('hidden');
        cartTable.classList.add('hidden');
        cartSummary.classList.add('hidden');
        cartCount.innerText = "0 items";
        return;
    }
    
    emptyCart.classList.add('hidden');
    cartTable.classList.remove('hidden');
    cartSummary.classList.remove('hidden');
    
    // Update cart count
    const totalItems = cart.reduce((sum, item) => sum + item.qty, 0);
    cartCount.innerText = totalItems + (totalItems === 1 ? " item" : " items");
    
    // Build cart table
    tbody.innerHTML = "";
    cart.forEach(function(item, index) {
        const row = document.createElement('tr');
        row.setAttribute('data-testid', 'cart-item-' + (index + 1));
        row.innerHTML = `
            <td>${item.name}</td>
            <td>${item.qty}</td>
            <td>$${item.cost.toFixed(2)}</td>
            <td><button class="remove-btn" data-testid="remove-item-${index + 1}" onclick="removeFromCart(${item.id})">✕</button></td>
        `;
        tbody.appendChild(row);
    });
    
    // Calculate totals
    const subtotal = cart.reduce((sum, item) => sum + item.cost, 0);
    const tax = subtotal * 0.10;
    let total = subtotal + tax;
    
    if (promoApplied) {
        total = total * (1 - promoDiscount);
    }
    
    document.getElementById('subtotal').innerText = subtotal.toFixed(2);
    document.getElementById('tax-amount').innerText = tax.toFixed(2);
    document.getElementById('total-price').innerText = total.toFixed(2);
}

function changeQty(delta) {
    const qtyInput = document.getElementById('quantity');
    let newVal = parseInt(qtyInput.value) + delta;
    if (newVal < 1) newVal = 1;
    if (newVal > 99) newVal = 99;
    qtyInput.value = newVal;
}

function clearCart() {
    if (cart.length === 0) {
        showNotification("Cart is already empty", "info");
        return;
    }
    
    cart = [];
    promoApplied = false;
    promoDiscount = 0;
    updateCartDisplay();
    hidePromoMessage();
    showNotification("Cart cleared", "info");
}

function applyPromo() {
    const promoCode = document.getElementById('promo-code').value.trim().toUpperCase();
    const promoMsg = document.getElementById('promo-message');
    
    const validCodes = {
        'SWEET10': 0.10,
        'BAKER20': 0.20,
        'TREAT15': 0.15
    };
    
    if (promoCode === "") {
        promoMsg.innerText = "Please enter a promo code";
        promoMsg.className = "promo-message error-text";
        promoMsg.classList.remove('hidden');
        return;
    }
    
    if (promoApplied) {
        promoMsg.innerText = "A promo code has already been applied";
        promoMsg.className = "promo-message error-text";
        promoMsg.classList.remove('hidden');
        return;
    }
    
    if (validCodes[promoCode]) {
        promoDiscount = validCodes[promoCode];
        promoApplied = true;
        promoMsg.innerText = `Promo code applied! ${promoDiscount * 100}% discount`;
        promoMsg.className = "promo-message success-text";
        promoMsg.classList.remove('hidden');
        updateCartDisplay();
    } else {
        promoMsg.innerText = "Invalid promo code";
        promoMsg.className = "promo-message error-text";
        promoMsg.classList.remove('hidden');
    }
}

function hidePromoMessage() {
    const promoMsg = document.getElementById('promo-message');
    if (promoMsg) {
        promoMsg.classList.add('hidden');
        document.getElementById('promo-code').value = '';
    }
}

function checkout() {
    if (cart.length === 0) {
        showNotification("Your cart is empty!", "error");
        return;
    }
    
    const total = document.getElementById('total-price').innerText;
    const orderNumber = 'ORD-' + Date.now().toString().slice(-8);
    
    // Save to order history
    const orderHistory = JSON.parse(localStorage.getItem('orderHistory') || '[]');
    orderHistory.push({
        orderNumber: orderNumber,
        date: new Date().toLocaleDateString(),
        items: [...cart],
        total: total
    });
    localStorage.setItem('orderHistory', JSON.stringify(orderHistory));
    
    // Show confirmation modal
    document.getElementById('checkout-message').innerText = `Thank you for your order! Total: $${total}`;
    document.getElementById('order-number').innerText = `Order Number: ${orderNumber}`;
    document.getElementById('checkout-modal').classList.remove('hidden');
    
    // Reset cart
    cart = [];
    promoApplied = false;
    promoDiscount = 0;
    updateCartDisplay();
    hidePromoMessage();
}

function closeCheckoutModal() {
    document.getElementById('checkout-modal').classList.add('hidden');
}

// ==========================================
// SEARCH & FILTER FUNCTIONALITY
// ==========================================
function searchMenu() {
    const searchTerm = document.getElementById('search-input').value.toLowerCase();
    const rows = document.querySelectorAll('#menu-body tr');
    let visibleCount = 0;
    
    rows.forEach(function(row) {
        const itemName = row.querySelector('td').innerText.toLowerCase();
        if (itemName.includes(searchTerm)) {
            row.classList.remove('hidden');
            visibleCount++;
        } else {
            row.classList.add('hidden');
        }
    });
    
    // Show/hide no results message
    const noResults = document.getElementById('no-results');
    if (visibleCount === 0) {
        noResults.classList.remove('hidden');
    } else {
        noResults.classList.add('hidden');
    }
}

function clearSearch() {
    document.getElementById('search-input').value = '';
    searchMenu();
}

function filterMenu(category) {
    const buttons = document.querySelectorAll('.filter-btn');
    buttons.forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');
    
    const rows = document.querySelectorAll('#menu-body tr');
    let visibleCount = 0;
    
    rows.forEach(function(row) {
        if (category === 'all' || row.getAttribute('data-category') === category) {
            row.classList.remove('hidden');
            visibleCount++;
        } else {
            row.classList.add('hidden');
        }
    });
    
    const noResults = document.getElementById('no-results');
    if (visibleCount === 0) {
        noResults.classList.remove('hidden');
    } else {
        noResults.classList.add('hidden');
    }
}

// ==========================================
// ORDER HISTORY
// ==========================================
function showOrderHistory() {
    const modal = document.getElementById('order-history-modal');
    const list = document.getElementById('order-history-list');
    const history = JSON.parse(localStorage.getItem('orderHistory') || '[]');
    
    if (history.length === 0) {
        list.innerHTML = '<p class="no-orders" data-testid="no-orders">No previous orders found.</p>';
    } else {
        list.innerHTML = history.map((order, index) => `
            <div class="order-card" data-testid="order-${index + 1}">
                <div class="order-header">
                    <strong>${order.orderNumber}</strong>
                    <span>${order.date}</span>
                </div>
                <div class="order-items">
                    ${order.items.map(item => `<span>${item.qty}x ${item.name}</span>`).join(', ')}
                </div>
                <div class="order-total">Total: $${order.total}</div>
            </div>
        `).join('');
    }
    
    modal.classList.remove('hidden');
    document.getElementById('profile-menu').classList.add('hidden');
}

function closeOrderHistory() {
    document.getElementById('order-history-modal').classList.add('hidden');
}

// ==========================================
// SETTINGS
// ==========================================
function openSettings() {
    document.getElementById('settings-modal').classList.remove('hidden');
    document.getElementById('profile-menu').classList.add('hidden');
}

function closeSettings() {
    document.getElementById('settings-modal').classList.add('hidden');
}

function saveSettings() {
    const newName = document.getElementById('display-name-input').value.trim();
    
    if (newName.length < 2) {
        showNotification("Display name must be at least 2 characters", "error");
        return;
    }
    
    sessionStorage.setItem('currentUser', newName);
    document.getElementById('display-name').innerText = "Hi, " + newName;
    closeSettings();
    showNotification("Settings saved successfully", "success");
}

// ==========================================
// NOTIFICATIONS
// ==========================================
function showNotification(message, type) {
    const notification = document.getElementById('notification');
    const text = document.getElementById('notification-text');
    
    if (!notification || !text) return;
    
    text.innerText = message;
    notification.className = 'notification ' + type;
    notification.classList.remove('hidden');
    
    setTimeout(function() {
        hideNotification();
    }, 3000);
}

function hideNotification() {
    const notification = document.getElementById('notification');
    if (notification) notification.classList.add('hidden');
}

// ==========================================
// HELPER FUNCTIONS
// ==========================================
function showFieldError(elementId, message) {
    const el = document.getElementById(elementId);
    if (el) {
        el.innerText = message;
        el.classList.add('visible');
    }
}

function clearFieldErrors() {
    const errors = document.querySelectorAll('.field-error');
    errors.forEach(function(el) {
        el.innerText = '';
        el.classList.remove('visible');
    });
}

function showError(elementId, message) {
    const el = document.getElementById(elementId);
    if (el) {
        el.innerText = message;
        el.classList.remove('hidden');
        el.classList.add('visible');
    }
}

function showSuccess(elementId, message) {
    const el = document.getElementById(elementId);
    if (el) {
        el.innerText = message;
        el.classList.remove('hidden');
        el.classList.add('visible');
    }
}

function hideMessages() {
    const errors = document.querySelectorAll('.error-banner, .success-banner');
    errors.forEach(function(el) {
        el.classList.add('hidden');
        el.classList.remove('visible');
    });
}