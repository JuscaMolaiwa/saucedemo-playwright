package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.saucedemo.config.TestConfig;

/**
 * CartPage — page object for the shopping cart (/cart.html).
 */
public class CartPage extends BasePage {

    // -----------------------------------------------------------------------
    // Locators
    // -----------------------------------------------------------------------
    private final Locator pageTitle        = page.locator(".title");
    private final Locator cartItems        = page.locator(".cart_item");
    private final Locator cartItemNames    = page.locator(".inventory_item_name");
    private final Locator checkoutButton   = page.locator("#checkout");
    private final Locator continueButton   = page.locator("#continue-shopping");
    private final Locator removeButtons    = page.locator("[id^='remove-']");

    public CartPage(Page page) {
        super(page);
    }

    // -----------------------------------------------------------------------
    // Navigation
    // -----------------------------------------------------------------------

    public CartPage open() {
        navigateTo(TestConfig.CART_PATH);
        return this;
    }

    // -----------------------------------------------------------------------
    // Actions
    // -----------------------------------------------------------------------

    /** Proceeds to the checkout information form. */
    public CheckoutPage proceedToCheckout() {
        log.info("Clicking Checkout button");
        checkoutButton.click();
        return new CheckoutPage(page);
    }

    /** Clicks 'Continue Shopping' and returns to inventory. */
    public InventoryPage continueShopping() {
        log.info("Clicking Continue Shopping");
        continueButton.click();
        return new InventoryPage(page);
    }

    /** Removes the item at position {@code index} (0-based) from the cart. */
    public CartPage removeItemByIndex(int index) {
        log.info("Removing item at index {}", index);
        removeButtons.nth(index).click();
        return this;
    }

    // -----------------------------------------------------------------------
    // Assertion helpers
    // -----------------------------------------------------------------------

    public boolean isOnCartPage() {
        return currentUrl().contains(TestConfig.CART_PATH);
    }

    public String getPageTitle() {
        return pageTitle.innerText();
    }

    public int getCartItemCount() {
        return cartItems.count();
    }

    public boolean isCartEmpty() {
        return getCartItemCount() == 0;
    }

    public boolean containsItem(String itemName) {
        return cartItemNames.allInnerTexts().stream()
                .anyMatch(name -> name.equalsIgnoreCase(itemName));
    }

    public boolean isCheckoutButtonVisible() {
        return checkoutButton.isVisible();
    }
}
