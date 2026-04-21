package com.saucedemo.tests;

import com.saucedemo.config.TestConfig;
import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.utils.ScreenshotUtil;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * CartTest — smoke tests for the shopping cart page.
 *
 * Covers:
 *  TC-C01  Cart page is accessible and title reads 'Your Cart'
 *  TC-C02  Item added to cart appears in cart
 *  TC-C03  Empty cart has no items
 *  TC-C04  Checkout button is present on cart page
 *  TC-C05  Removing an item from the cart empties it
 *  TC-C06  'Continue Shopping' returns user to inventory
 */
@Epic("SauceDemo Smoke Tests")
@Feature("Shopping Cart")
public class CartTest extends BaseTest {

    /** Helper – logs in, optionally adds a product, and opens the cart. */
    private CartPage loginAddItemAndOpenCart() {
        InventoryPage inventoryPage = new LoginPage(page)
                .open()
                .loginAs(TestConfig.STANDARD_USER, TestConfig.PASSWORD);
        inventoryPage.addFirstItemToCart();
        return inventoryPage.goToCart();
    }

    // -----------------------------------------------------------------------
    // TC-C01
    // -----------------------------------------------------------------------
    @Test(description = "Cart page should be accessible and display 'Your Cart' title")
    @Story("Cart page access")
    @Severity(SeverityLevel.CRITICAL)
    public void cartPage_isAccessibleWithCorrectTitle() {
        InventoryPage inventoryPage = new LoginPage(page)
                .open()
                .loginAs(TestConfig.STANDARD_USER, TestConfig.PASSWORD);
        CartPage cartPage = inventoryPage.goToCart();

        ScreenshotUtil.capture(page, "TC-C01_cart_page", getClass().getSimpleName());

        assertTrue(cartPage.isOnCartPage(),
                "URL should contain 'cart.html'. Actual: " + page.url());
        assertEquals(cartPage.getPageTitle(), "Your Cart",
                "Cart page title should be 'Your Cart'");
    }

    // -----------------------------------------------------------------------
    // TC-C02
    // -----------------------------------------------------------------------
    @Test(description = "Item added from inventory should appear in the cart")
    @Story("Cart contents")
    @Severity(SeverityLevel.BLOCKER)
    public void addedItem_appearsInCart() {
        CartPage cartPage = loginAddItemAndOpenCart();

        ScreenshotUtil.capture(page, "TC-C02_item_in_cart", getClass().getSimpleName());

        assertEquals(cartPage.getCartItemCount(), 1,
                "Cart should contain exactly 1 item after adding one product");
    }

    // -----------------------------------------------------------------------
    // TC-C03
    // -----------------------------------------------------------------------
    @Test(description = "Cart should be empty when no items have been added")
    @Story("Empty cart")
    @Severity(SeverityLevel.NORMAL)
    public void emptyCart_hasNoItems() {
        InventoryPage inventoryPage = new LoginPage(page)
                .open()
                .loginAs(TestConfig.STANDARD_USER, TestConfig.PASSWORD);
        CartPage cartPage = inventoryPage.goToCart();

        ScreenshotUtil.capture(page, "TC-C03_empty_cart", getClass().getSimpleName());

        assertTrue(cartPage.isCartEmpty(),
                "Cart should be empty when no items have been added");
    }

    // -----------------------------------------------------------------------
    // TC-C04
    // -----------------------------------------------------------------------
    @Test(description = "Checkout button should be present on the cart page")
    @Story("Cart navigation")
    @Severity(SeverityLevel.CRITICAL)
    public void cartPage_checkoutButtonIsVisible() {
        CartPage cartPage = loginAddItemAndOpenCart();

        assertTrue(cartPage.isCheckoutButtonVisible(),
                "Checkout button should be visible on the cart page");
    }

    // -----------------------------------------------------------------------
    // TC-C05
    // -----------------------------------------------------------------------
    @Test(description = "Removing the only cart item should leave the cart empty")
    @Story("Remove from cart")
    @Severity(SeverityLevel.CRITICAL)
    public void removeItem_cartBecomesEmpty() {
        CartPage cartPage = loginAddItemAndOpenCart();
        assertEquals(cartPage.getCartItemCount(), 1, "Precondition: cart should have 1 item");

        cartPage.removeItemByIndex(0);

        ScreenshotUtil.capture(page, "TC-C05_after_remove",  getClass().getSimpleName());

        assertTrue(cartPage.isCartEmpty(),
                "Cart should be empty after removing the only item");
    }

    // -----------------------------------------------------------------------
    // TC-C06
    // -----------------------------------------------------------------------
    @Test(description = "'Continue Shopping' should return the user to the inventory page")
    @Story("Cart navigation")
    @Severity(SeverityLevel.NORMAL)
    public void continueShopping_returnsToInventory() {
        CartPage cartPage = loginAddItemAndOpenCart();
        InventoryPage inventoryPage = cartPage.continueShopping();

        ScreenshotUtil.capture(page, "TC-C06_back_to_inventory", getClass().getSimpleName());

        assertTrue(inventoryPage.isOnInventoryPage(),
                "Should be on inventory after clicking 'Continue Shopping'. Actual: " + page.url());
    }
}
