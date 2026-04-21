package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.saucedemo.config.TestConfig;

import java.util.List;

/**
 * InventoryPage — page object for the products listing page (/inventory.html).
 *
 * Covers: product display, sorting, add-to-cart, cart badge, navigation.
 */
public class InventoryPage extends BasePage {

    // -----------------------------------------------------------------------
    // Locators
    // -----------------------------------------------------------------------
    private final Locator pageTitle      = page.locator(".title");
    private final Locator inventoryItems = page.locator(".inventory_item");
    private final Locator sortDropdown   = page.locator(".product_sort_container");
    private final Locator cartIcon       = page.locator(".shopping_cart_link");
    private final Locator cartBadge      = page.locator(".shopping_cart_badge");
    private final Locator menuButton     = page.locator("#react-burger-menu-btn");
    private final Locator logoutLink     = page.locator("#logout_sidebar_link");
    private final Locator productNames   = page.locator(".inventory_item_name");
    private final Locator productPrices  = page.locator(".inventory_item_price");

    public InventoryPage(Page page) {
        super(page);
    }

    // -----------------------------------------------------------------------
    // Navigation
    // -----------------------------------------------------------------------

    /** Directly navigates to the inventory page (bypasses login – use after login). */
    public InventoryPage open() {
        navigateTo(TestConfig.INVENTORY_PATH);
        return this;
    }

    // -----------------------------------------------------------------------
    // Actions
    // -----------------------------------------------------------------------

    /**
     * Adds the product at position {@code index} (0-based) to the cart.
     * Returns {@code this} for chaining.
     */
    public InventoryPage addItemToCartByIndex(int index) {
        Locator addButton = inventoryItems
                .nth(index)
                .locator("button");
        String name = productNames.nth(index).innerText();
        log.info("Adding item to cart: {} (index {})", name, index);
        addButton.click();
        return this;
    }

    /** Adds the first product in the listing to the cart. */
    public InventoryPage addFirstItemToCart() {
        return addItemToCartByIndex(0);
    }

    /** Clicks the cart icon to navigate to the cart page. */
    public CartPage goToCart() {
        log.info("Navigating to cart");
        cartIcon.click();
        return new CartPage(page);
    }

    /** Opens the hamburger menu and clicks Logout. */
    public LoginPage logout() {
        log.info("Logging out via menu");
        menuButton.click();
        waitForVisible(logoutLink);
        logoutLink.click();
        return new LoginPage(page);
    }

    /** Selects a sort option by its visible label. */
    public InventoryPage sortBy(String optionLabel) {
        log.info("Sorting inventory by: {}", optionLabel);
        sortDropdown.selectOption(new com.microsoft.playwright.options.SelectOption().setLabel(optionLabel));
        return this;
    }

    // -----------------------------------------------------------------------
    // Assertion helpers
    // -----------------------------------------------------------------------

    public boolean isOnInventoryPage() {
        return currentUrl().contains(TestConfig.INVENTORY_PATH);
    }

    public String getPageTitle() {
        return pageTitle.innerText();
    }

    public int getInventoryItemCount() {
        return inventoryItems.count();
    }

    public boolean isCartBadgeVisible() {
        return cartBadge.isVisible();
    }

    public String getCartBadgeCount() {
        return cartBadge.innerText();
    }

    public List<String> getProductNames() {
        return productNames.allInnerTexts();
    }

    public List<String> getProductPrices() {
        return productPrices.allInnerTexts();
    }
}
