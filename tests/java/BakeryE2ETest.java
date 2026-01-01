import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

public class BakeryE2ETest {
    
    private WebDriver driver;
    private WebDriverWait wait;
    
    // Update this path to match your local file location
    private static final String BASE_URL = "file:///C:/Users/user/OneDrive/Documents/CA4-software-testing/";
    
    @BeforeClass
    public void setUp() {
        // Initialize ChromeDriver (ensure chromedriver is in PATH or set system property)
        // System.setProperty("webdriver.chrome.driver", "path/to/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    
    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    @BeforeMethod
    public void clearStorage() {
        driver.get(BASE_URL + "login.html");
        ((JavascriptExecutor) driver).executeScript("localStorage.clear(); sessionStorage.clear();");
    }
    
    // ==========================================
    // HELPER METHODS
    // ==========================================
    
    private WebElement findByTestId(String testId) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector("[data-testid='" + testId + "']")
        ));
    }
    
    private WebElement findClickableByTestId(String testId) {
        return wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("[data-testid='" + testId + "']")
        ));
    }
    
    private void waitForElement(String testId) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("[data-testid='" + testId + "']")
        ));
    }
    
    private boolean isElementVisible(String testId) {
        try {
            WebElement element = driver.findElement(By.cssSelector("[data-testid='" + testId + "']"));
            return element.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    private void registerUser(String username, String email, String password) {
        driver.get(BASE_URL + "register.html");
        
        findByTestId("reg-username-input").sendKeys(username);
        findByTestId("reg-email-input").sendKeys(email);
        findByTestId("reg-password-input").sendKeys(password);
        findByTestId("reg-confirm-input").sendKeys(password);
        findByTestId("terms-checkbox").click();
        findClickableByTestId("register-button").click();
    }
    
    private void loginUser(String username, String password) {
        driver.get(BASE_URL + "login.html");
        
        findByTestId("username-input").sendKeys(username);
        findByTestId("password-input").sendKeys(password);
        findClickableByTestId("login-button").click();
    }
    
    // ==========================================
    // REGISTRATION TESTS
    // ==========================================
    
    @Test(priority = 1)
    public void testRegisterPageLoads() {
        driver.get(BASE_URL + "register.html");
        
        Assert.assertTrue(findByTestId("register-container").isDisplayed());
        Assert.assertTrue(findByTestId("register-title").getText().contains("Create Account"));
    }
    
    @Test(priority = 2)
    public void testRegisterWithEmptyFields() {
        driver.get(BASE_URL + "register.html");
        
        findClickableByTestId("register-button").click();
        
        // Should show validation errors
        WebElement usernameError = findByTestId("reg-username-error");
        Assert.assertTrue(usernameError.getText().contains("required"));
    }
    
    @Test(priority = 3)
    public void testRegisterWithShortUsername() {
        driver.get(BASE_URL + "register.html");
        
        findByTestId("reg-username-input").sendKeys("ab");
        findByTestId("reg-email-input").sendKeys("test@email.com");
        findByTestId("reg-password-input").sendKeys("password123");
        findByTestId("reg-confirm-input").sendKeys("password123");
        findByTestId("terms-checkbox").click();
        findClickableByTestId("register-button").click();
        
        WebElement usernameError = findByTestId("reg-username-error");
        Assert.assertTrue(usernameError.getText().contains("at least 3"));
    }
    
    @Test(priority = 4)
    public void testRegisterWithInvalidEmail() {
        driver.get(BASE_URL + "register.html");
        
        findByTestId("reg-username-input").sendKeys("testuser");
        findByTestId("reg-email-input").sendKeys("invalidemail");
        findByTestId("reg-password-input").sendKeys("password123");
        findByTestId("reg-confirm-input").sendKeys("password123");
        findByTestId("terms-checkbox").click();
        findClickableByTestId("register-button").click();
        
        WebElement emailError = findByTestId("reg-email-error");
        Assert.assertTrue(emailError.getText().contains("valid email"));
    }
    
    @Test(priority = 5)
    public void testRegisterWithMismatchedPasswords() {
        driver.get(BASE_URL + "register.html");
        
        findByTestId("reg-username-input").sendKeys("testuser");
        findByTestId("reg-email-input").sendKeys("test@email.com");
        findByTestId("reg-password-input").sendKeys("password123");
        findByTestId("reg-confirm-input").sendKeys("differentpassword");
        findByTestId("terms-checkbox").click();
        findClickableByTestId("register-button").click();
        
        WebElement confirmError = findByTestId("reg-confirm-error");
        Assert.assertTrue(confirmError.getText().contains("do not match"));
    }
    
    @Test(priority = 6)
    public void testRegisterWithoutTerms() {
        driver.get(BASE_URL + "register.html");
        
        findByTestId("reg-username-input").sendKeys("testuser");
        findByTestId("reg-email-input").sendKeys("test@email.com");
        findByTestId("reg-password-input").sendKeys("password123");
        findByTestId("reg-confirm-input").sendKeys("password123");
        // Don't check terms checkbox
        findClickableByTestId("register-button").click();
        
        WebElement registerError = findByTestId("register-error");
        Assert.assertTrue(registerError.getText().contains("Terms"));
    }
    
    @Test(priority = 7)
    public void testSuccessfulRegistration() {
        registerUser("testuser", "test@email.com", "password123");
        
        // Should show success message
        WebElement successMsg = findByTestId("register-success");
        wait.until(ExpectedConditions.visibilityOf(successMsg));
        Assert.assertTrue(successMsg.getText().contains("successful"));
        
        // Should redirect to login page
        wait.until(ExpectedConditions.urlContains("login.html"));
        Assert.assertTrue(driver.getCurrentUrl().contains("login.html"));
    }
    
    @Test(priority = 8)
    public void testNavigateToLoginFromRegister() {
        driver.get(BASE_URL + "register.html");
        
        findClickableByTestId("login-link").click();
        
        wait.until(ExpectedConditions.urlContains("login.html"));
        Assert.assertTrue(driver.getCurrentUrl().contains("login.html"));
    }
    
    // ==========================================
    // LOGIN TESTS
    // ==========================================
    
    @Test(priority = 10)
    public void testLoginPageLoads() {
        driver.get(BASE_URL + "login.html");
        
        Assert.assertTrue(findByTestId("login-container").isDisplayed());
        Assert.assertTrue(findByTestId("login-title").getText().contains("Login"));
    }
    
    @Test(priority = 11)
    public void testLoginWithEmptyFields() {
        driver.get(BASE_URL + "login.html");
        
        findClickableByTestId("login-button").click();
        
        WebElement usernameError = findByTestId("username-error");
        Assert.assertTrue(usernameError.getText().contains("required"));
    }
    
    @Test(priority = 12)
    public void testLoginWithInvalidCredentials() {
        driver.get(BASE_URL + "login.html");
        
        findByTestId("username-input").sendKeys("wronguser");
        findByTestId("password-input").sendKeys("wrongpassword");
        findClickableByTestId("login-button").click();
        
        WebElement errorMsg = findByTestId("login-error");
        wait.until(ExpectedConditions.visibilityOf(errorMsg));
        Assert.assertTrue(errorMsg.getText().contains("Invalid"));
    }
    
    @Test(priority = 13)
    public void testSuccessfulLogin() {
        // First register a user
        registerUser("logintest", "login@test.com", "testpass123");
        wait.until(ExpectedConditions.urlContains("login.html"));
        
        // Now login
        findByTestId("username-input").sendKeys("logintest");
        findByTestId("password-input").sendKeys("testpass123");
        findClickableByTestId("login-button").click();
        
        // Should redirect to dashboard
        wait.until(ExpectedConditions.urlContains("dashboard.html"));
        Assert.assertTrue(driver.getCurrentUrl().contains("dashboard.html"));
    }
    
    @Test(priority = 14)
    public void testForgotPasswordModal() {
        driver.get(BASE_URL + "login.html");
        
        findClickableByTestId("forgot-password-link").click();
        
        WebElement modal = findByTestId("forgot-modal");
        Assert.assertTrue(modal.isDisplayed());
        Assert.assertTrue(findByTestId("reset-email-input").isDisplayed());
    }
    
    @Test(priority = 15)
    public void testForgotPasswordWithValidEmail() {
        driver.get(BASE_URL + "login.html");
        
        findClickableByTestId("forgot-password-link").click();
        findByTestId("reset-email-input").sendKeys("test@email.com");
        findClickableByTestId("reset-button").click();
        
        WebElement resetMsg = findByTestId("reset-message");
        Assert.assertTrue(resetMsg.getText().contains("reset link"));
    }
    
    @Test(priority = 16)
    public void testNavigateToRegisterFromLogin() {
        driver.get(BASE_URL + "login.html");
        
        findClickableByTestId("register-link").click();
        
        wait.until(ExpectedConditions.urlContains("register.html"));
        Assert.assertTrue(driver.getCurrentUrl().contains("register.html"));
    }
    
    // ==========================================
    // DASHBOARD TESTS
    // ==========================================
    
    @Test(priority = 20)
    public void testDashboardRequiresLogin() {
        driver.get(BASE_URL + "dashboard.html");
        
        // Should redirect to login
        wait.until(ExpectedConditions.urlContains("login.html"));
        Assert.assertTrue(driver.getCurrentUrl().contains("login.html"));
    }
    
    @Test(priority = 21)
    public void testDashboardLoadsAfterLogin() {
        // Register and login
        registerUser("dashtest", "dash@test.com", "dashpass123");
        wait.until(ExpectedConditions.urlContains("login.html"));
        
        findByTestId("username-input").sendKeys("dashtest");
        findByTestId("password-input").sendKeys("dashpass123");
        findClickableByTestId("login-button").click();
        
        wait.until(ExpectedConditions.urlContains("dashboard.html"));
        
        Assert.assertTrue(findByTestId("dashboard-header").isDisplayed());
        Assert.assertTrue(findByTestId("menu-section").isDisplayed());
        Assert.assertTrue(findByTestId("cart-section").isDisplayed());
    }
    
    @Test(priority = 22)
    public void testDisplayNameShown() {
        registerUser("nametest", "name@test.com", "namepass123");
        wait.until(ExpectedConditions.urlContains("login.html"));
        
        findByTestId("username-input").sendKeys("nametest");
        findByTestId("password-input").sendKeys("namepass123");
        findClickableByTestId("login-button").click();
        
        wait.until(ExpectedConditions.urlContains("dashboard.html"));
        
        findClickableByTestId("profile-button").click();
        WebElement displayName = findByTestId("display-name");
        Assert.assertTrue(displayName.getText().contains("nametest"));
    }
    
    // ==========================================
    // MENU & SEARCH TESTS
    // ==========================================
    
    @Test(priority = 30)
    public void testMenuItemsDisplayed() {
        setupLoggedInSession();
        
        Assert.assertTrue(findByTestId("menu-table").isDisplayed());
        Assert.assertTrue(findByTestId("menu-item-1").isDisplayed());
        Assert.assertTrue(findByTestId("menu-item-2").isDisplayed());
    }
    
    @Test(priority = 31)
    public void testSearchFunctionality() {
        setupLoggedInSession();
        
        WebElement searchInput = findByTestId("search-input");
        searchInput.sendKeys("Chocolate");
        
        // Chocolate Donut should be visible
        Assert.assertTrue(findByTestId("menu-item-1").isDisplayed());
        
        // Other items should be hidden (check via class)
        WebElement strawberryItem = driver.findElement(By.cssSelector("[data-testid='menu-item-2']"));
        Assert.assertTrue(strawberryItem.getAttribute("class").contains("hidden"));
    }
    
    @Test(priority = 32)
    public void testSearchNoResults() {
        setupLoggedInSession();
        
        WebElement searchInput = findByTestId("search-input");
        searchInput.sendKeys("XYZ123NonExistent");
        
        WebElement noResults = findByTestId("no-results");
        Assert.assertTrue(noResults.isDisplayed());
    }
    
    @Test(priority = 33)
    public void testClearSearch() {
        setupLoggedInSession();
        
        WebElement searchInput = findByTestId("search-input");
        searchInput.sendKeys("Chocolate");
        
        findClickableByTestId("search-clear").click();
        
        // All items should be visible again
        Assert.assertFalse(findByTestId("menu-item-1").getAttribute("class").contains("hidden"));
        Assert.assertFalse(findByTestId("menu-item-2").getAttribute("class").contains("hidden"));
    }
    
    @Test(priority = 34)
    public void testFilterByCategory() {
        setupLoggedInSession();
        
        // Filter by drinks
        findClickableByTestId("filter-drinks").click();
        
        // Coffee should be visible
        Assert.assertFalse(findByTestId("menu-item-5").getAttribute("class").contains("hidden"));
        
        // Donuts should be hidden
        Assert.assertTrue(findByTestId("menu-item-1").getAttribute("class").contains("hidden"));
    }
    
    @Test(priority = 35)
    public void testFilterAll() {
        setupLoggedInSession();
        
        // First filter by drinks
        findClickableByTestId("filter-drinks").click();
        
        // Then click All
        findClickableByTestId("filter-all").click();
        
        // All items should be visible
        Assert.assertFalse(findByTestId("menu-item-1").getAttribute("class").contains("hidden"));
        Assert.assertFalse(findByTestId("menu-item-5").getAttribute("class").contains("hidden"));
    }
    
    // ==========================================
    // CART TESTS
    // ==========================================
    
    @Test(priority = 40)
    public void testEmptyCartDisplayed() {
        setupLoggedInSession();
        
        Assert.assertTrue(findByTestId("empty-cart").isDisplayed());
        Assert.assertEquals(findByTestId("cart-count").getText(), "0 items");
    }
    
    @Test(priority = 41)
    public void testQuickAddToCart() {
        setupLoggedInSession();
        
        findClickableByTestId("quick-add-1").click();
        
        // Cart should no longer be empty
        WebElement emptyCart = driver.findElement(By.cssSelector("[data-testid='empty-cart']"));
        Assert.assertTrue(emptyCart.getAttribute("class").contains("hidden"));
        
        // Cart count should update
        Assert.assertEquals(findByTestId("cart-count").getText(), "1 item");
    }
    
    @Test(priority = 42)
    public void testAddToCartWithQuantity() {
        setupLoggedInSession();
        
        // Select item and set quantity
        Select itemSelect = new Select(findByTestId("item-select"));
        itemSelect.selectByIndex(0); // Chocolate Donut
        
        WebElement qtyInput = findByTestId("quantity-input");
        qtyInput.clear();
        qtyInput.sendKeys("3");
        
        findClickableByTestId("add-to-cart-button").click();
        
        // Cart count should show 3 items
        Assert.assertEquals(findByTestId("cart-count").getText(), "3 items");
    }
    
    @Test(priority = 43)
    public void testQuantityButtons() {
        setupLoggedInSession();
        
        WebElement qtyInput = findByTestId("quantity-input");
        
        // Initial value should be 1
        Assert.assertEquals(qtyInput.getAttribute("value"), "1");
        
        // Click increase
        findClickableByTestId("qty-increase").click();
        Assert.assertEquals(qtyInput.getAttribute("value"), "2");
        
        // Click decrease
        findClickableByTestId("qty-decrease").click();
        Assert.assertEquals(qtyInput.getAttribute("value"), "1");
        
        // Should not go below 1
        findClickableByTestId("qty-decrease").click();
        Assert.assertEquals(qtyInput.getAttribute("value"), "1");
    }
    
    @Test(priority = 44)
    public void testRemoveFromCart() {
        setupLoggedInSession();
        
        // Add item
        findClickableByTestId("quick-add-1").click();
        Assert.assertEquals(findByTestId("cart-count").getText(), "1 item");
        
        // Remove item
        findClickableByTestId("remove-item-1").click();
        
        // Cart should be empty again
        Assert.assertEquals(findByTestId("cart-count").getText(), "0 items");
    }
    
    @Test(priority = 45)
    public void testClearCart() {
        setupLoggedInSession();
        
        // Add multiple items
        findClickableByTestId("quick-add-1").click();
        findClickableByTestId("quick-add-2").click();
        
        // Clear cart
        findClickableByTestId("clear-cart-button").click();
        
        // Cart should be empty
        Assert.assertEquals(findByTestId("cart-count").getText(), "0 items");
        Assert.assertTrue(findByTestId("empty-cart").isDisplayed());
    }
    
    @Test(priority = 46)
    public void testCartTotalCalculation() {
        setupLoggedInSession();
        
        // Add Chocolate Donut ($2.50)
        findClickableByTestId("quick-add-1").click();
        
        // Add Strawberry Tart ($4.00)
        findClickableByTestId("quick-add-2").click();
        
        // Subtotal should be $6.50
        // Tax (10%) should be $0.65
        // Total should be $7.15
        String total = findByTestId("total-price").getText();
        Assert.assertEquals(total, "7.15");
    }
    
    // ==========================================
    // PROMO CODE TESTS
    // ==========================================
    
    @Test(priority = 50)
    public void testValidPromoCode() {
        setupLoggedInSession();
        
        // Add item first
        findClickableByTestId("quick-add-1").click();
        
        // Apply valid promo code
        findByTestId("promo-input").sendKeys("SWEET10");
        findClickableByTestId("apply-promo-button").click();
        
        WebElement promoMsg = findByTestId("promo-message");
        Assert.assertTrue(promoMsg.getText().contains("10%"));
    }
    
    @Test(priority = 51)
    public void testInvalidPromoCode() {
        setupLoggedInSession();
        
        // Add item first
        findClickableByTestId("quick-add-1").click();
        
        // Apply invalid promo code
        findByTestId("promo-input").sendKeys("INVALIDCODE");
        findClickableByTestId("apply-promo-button").click();
        
        WebElement promoMsg = findByTestId("promo-message");
        Assert.assertTrue(promoMsg.getText().contains("Invalid"));
    }
    
    @Test(priority = 52)
    public void testPromoCodeAppliedOnce() {
        setupLoggedInSession();
        
        findClickableByTestId("quick-add-1").click();
        
        // Apply first promo
        findByTestId("promo-input").sendKeys("SWEET10");
        findClickableByTestId("apply-promo-button").click();
        
        // Try to apply another
        WebElement promoInput = findByTestId("promo-input");
        promoInput.clear();
        promoInput.sendKeys("BAKER20");
        findClickableByTestId("apply-promo-button").click();
        
        WebElement promoMsg = findByTestId("promo-message");
        Assert.assertTrue(promoMsg.getText().contains("already been applied"));
    }
    
    // ==========================================
    // CHECKOUT TESTS
    // ==========================================
    
    @Test(priority = 60)
    public void testCheckoutWithEmptyCart() {
        setupLoggedInSession();
        
        // Try to checkout with empty cart - button might not be visible
        // The cart summary section should be hidden when cart is empty
        WebElement cartSummary = driver.findElement(By.cssSelector("[data-testid='cart-summary']"));
        Assert.assertTrue(cartSummary.getAttribute("class").contains("hidden"));
    }
    
    @Test(priority = 61)
    public void testSuccessfulCheckout() {
        setupLoggedInSession();
        
        // Add items
        findClickableByTestId("quick-add-1").click();
        findClickableByTestId("quick-add-2").click();
        
        // Checkout
        findClickableByTestId("checkout-button").click();
        
        // Should show confirmation modal
        WebElement checkoutModal = findByTestId("checkout-modal");
        Assert.assertTrue(checkoutModal.isDisplayed());
        
        // Should show order number
        WebElement orderNumber = findByTestId("order-number");
        Assert.assertTrue(orderNumber.getText().contains("ORD-"));
    }
    
    @Test(priority = 62)
    public void testCartClearedAfterCheckout() {
        setupLoggedInSession();
        
        findClickableByTestId("quick-add-1").click();
        findClickableByTestId("checkout-button").click();
        
        // Close modal
        findClickableByTestId("close-checkout-modal").click();
        
        // Cart should be empty
        Assert.assertEquals(findByTestId("cart-count").getText(), "0 items");
    }
    
    // ==========================================
    // ORDER HISTORY TESTS
    // ==========================================
    
    @Test(priority = 70)
    public void testOrderHistoryEmpty() {
        setupLoggedInSession();
        
        findClickableByTestId("profile-button").click();
        findClickableByTestId("order-history-button").click();
        
        WebElement modal = findByTestId("order-history-modal");
        Assert.assertTrue(modal.isDisplayed());
        
        WebElement noOrders = findByTestId("no-orders");
        Assert.assertTrue(noOrders.isDisplayed());
    }
    
    @Test(priority = 71)
    public void testOrderHistoryAfterCheckout() {
        setupLoggedInSession();
        
        // Make an order
        findClickableByTestId("quick-add-1").click();
        findClickableByTestId("checkout-button").click();
        findClickableByTestId("close-checkout-modal").click();
        
        // Check order history
        findClickableByTestId("profile-button").click();
        findClickableByTestId("order-history-button").click();
        
        WebElement order = findByTestId("order-1");
        Assert.assertTrue(order.isDisplayed());
    }
    
    // ==========================================
    // SETTINGS TESTS
    // ==========================================
    
    @Test(priority = 80)
    public void testOpenSettingsModal() {
        setupLoggedInSession();
        
        findClickableByTestId("profile-button").click();
        findClickableByTestId("settings-button").click();
        
        WebElement modal = findByTestId("settings-modal");
        Assert.assertTrue(modal.isDisplayed());
    }
    
    @Test(priority = 81)
    public void testSaveSettings() {
        setupLoggedInSession();
        
        findClickableByTestId("profile-button").click();
        findClickableByTestId("settings-button").click();
        
        WebElement nameInput = findByTestId("display-name-input");
        nameInput.clear();
        nameInput.sendKeys("NewDisplayName");
        
        findClickableByTestId("save-settings-button").click();
        
        // Modal should close and name should update
        findClickableByTestId("profile-button").click();
        WebElement displayName = findByTestId("display-name");
        Assert.assertTrue(displayName.getText().contains("NewDisplayName"));
    }
    
    // ==========================================
    // LOGOUT TESTS
    // ==========================================
    
    @Test(priority = 90)
    public void testLogout() {
        setupLoggedInSession();
        
        findClickableByTestId("profile-button").click();
        findClickableByTestId("logout-button").click();
        
        // Should redirect to login
        wait.until(ExpectedConditions.urlContains("login.html"));
        Assert.assertTrue(driver.getCurrentUrl().contains("login.html"));
    }
    
    @Test(priority = 91)
    public void testCannotAccessDashboardAfterLogout() {
        setupLoggedInSession();
        
        findClickableByTestId("profile-button").click();
        findClickableByTestId("logout-button").click();
        
        wait.until(ExpectedConditions.urlContains("login.html"));
        
        // Try to access dashboard directly
        driver.get(BASE_URL + "dashboard.html");
        
        // Should redirect to login
        wait.until(ExpectedConditions.urlContains("login.html"));
        Assert.assertTrue(driver.getCurrentUrl().contains("login.html"));
    }
    
    // ==========================================
    // COMPLETE E2E FLOW TEST
    // ==========================================
    
    @Test(priority = 100)
    public void testCompleteUserJourney() {
        String username = "e2euser" + System.currentTimeMillis();
        String email = username + "@test.com";
        String password = "e2epass123";
        
        // 1. Register
        driver.get(BASE_URL + "register.html");
        findByTestId("reg-username-input").sendKeys(username);
        findByTestId("reg-email-input").sendKeys(email);
        findByTestId("reg-password-input").sendKeys(password);
        findByTestId("reg-confirm-input").sendKeys(password);
        findByTestId("terms-checkbox").click();
        findClickableByTestId("register-button").click();
        
        // 2. Wait for redirect to login
        wait.until(ExpectedConditions.urlContains("login.html"));
        
        // 3. Login
        findByTestId("username-input").sendKeys(username);
        findByTestId("password-input").sendKeys(password);
        findClickableByTestId("login-button").click();
        
        // 4. Wait for dashboard
        wait.until(ExpectedConditions.urlContains("dashboard.html"));
        
        // 5. Search for an item
        findByTestId("search-input").sendKeys("Coffee");
        Assert.assertFalse(findByTestId("menu-item-5").getAttribute("class").contains("hidden"));
        findClickableByTestId("search-clear").click();
        
        // 6. Add items to cart
        findClickableByTestId("quick-add-1").click(); // Chocolate Donut
        findClickableByTestId("quick-add-5").click(); // Hot Coffee
        
        // 7. Add more via custom order
        Select itemSelect = new Select(findByTestId("item-select"));
        itemSelect.selectByVisibleText("Strawberry Tart - $4.00");
        findClickableByTestId("qty-increase").click();
        findClickableByTestId("add-to-cart-button").click();
        
        // 8. Apply promo code
        findByTestId("promo-input").sendKeys("SWEET10");
        findClickableByTestId("apply-promo-button").click();
        
        // 9. Verify cart has items
        Assert.assertFalse(findByTestId("cart-count").getText().equals("0 items"));
        
        // 10. Checkout
        findClickableByTestId("checkout-button").click();
        Assert.assertTrue(findByTestId("checkout-modal").isDisplayed());
        findClickableByTestId("close-checkout-modal").click();
        
        // 11. Verify order in history
        findClickableByTestId("profile-button").click();
        findClickableByTestId("order-history-button").click();
        Assert.assertTrue(findByTestId("order-1").isDisplayed());
        
        // Close modal by clicking outside or finding close button
        driver.findElement(By.cssSelector("#order-history-modal .close-btn")).click();
        
        // 12. Logout
        findClickableByTestId("profile-button").click();
        findClickableByTestId("logout-button").click();
        
        // 13. Verify back at login
        wait.until(ExpectedConditions.urlContains("login.html"));
        Assert.assertTrue(driver.getCurrentUrl().contains("login.html"));
    }
    
    // ==========================================
    // HELPER: Setup logged in session
    // ==========================================
    
    private void setupLoggedInSession() {
        String username = "testuser" + System.currentTimeMillis();
        registerUser(username, username + "@test.com", "testpass123");
        wait.until(ExpectedConditions.urlContains("login.html"));
        
        findByTestId("username-input").sendKeys(username);
        findByTestId("password-input").sendKeys("testpass123");
        findClickableByTestId("login-button").click();
        
        wait.until(ExpectedConditions.urlContains("dashboard.html"));
    }
}
