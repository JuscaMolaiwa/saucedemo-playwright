package com.saucedemo.tests;

import com.saucedemo.config.TestConfig;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.utils.ScreenshotUtil;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

/**
 * InventoryTest — smoke tests for the products/inventory page.
 * <p>
 * Covers:
 *  TC-I01  Inventory page displays at least 6 products
 *  TC-I02  Products title is 'Products'
 *  TC-I03  Add single item updates cart badge to 1
 *  TC-I04  Add two items updates cart badge to 2
 *  TC-I05  Sort A→Z orders names alphabetically
 */
@Epic("SauceDemo Smoke Tests")
@Feature("Inventory / Product Listing")
public class InventoryTest extends BaseTest {

    /** Helper – logs in and returns InventoryPage. */
    private InventoryPage loginAndGetInventory() {
        return new LoginPage(page)
                .open()
                .loginAs(TestConfig.STANDARD_USER, TestConfig.PASSWORD);
    }

    // -----------------------------------------------------------------------
    // TC-I01
    // -----------------------------------------------------------------------
    @Test(description = "Inventory page should display 6 products after login")
    @Story("Product display")
    @Severity(SeverityLevel.BLOCKER)
    public void inventoryPage_displays6Products() {
        InventoryPage inventoryPage = loginAndGetInventory();

        ScreenshotUtil.capture(page, "TC-I01_inventory_page", getClass().getSimpleName());

        int count = inventoryPage.getInventoryItemCount();
        assertEquals(count, 6,
                "Expected 6 products on the inventory page, but found: " + count);
    }

    // -----------------------------------------------------------------------
    // TC-I02
    // -----------------------------------------------------------------------
    @Test(description = "Inventory page title should read 'Products'")
    @Story("Product display")
    @Severity(SeverityLevel.NORMAL)
    public void inventoryPage_titleIsProducts() {
        InventoryPage inventoryPage = loginAndGetInventory();

        assertEquals(inventoryPage.getPageTitle(), "Products",
                "Page title should be 'Products'");
    }

    // -----------------------------------------------------------------------
    // TC-I03
    // -----------------------------------------------------------------------
    @Test(description = "Adding one item to cart should show badge count of 1")
    @Story("Add to cart")
    @Severity(SeverityLevel.CRITICAL)
    public void addOneItem_cartBadgeShowsOne() {
        InventoryPage inventoryPage = loginAndGetInventory();

        assertFalse(inventoryPage.isCartBadgeVisible(),
                "Cart badge should be hidden before any item is added");

        inventoryPage.addFirstItemToCart();

        ScreenshotUtil.capture(page, "TC-I03_one_item_added", getClass().getSimpleName());

        assertTrue(inventoryPage.isCartBadgeVisible(),
                "Cart badge should appear after adding an item");
        assertEquals(inventoryPage.getCartBadgeCount(), "1",
                "Cart badge should display '1' after adding one item");
    }

    // -----------------------------------------------------------------------
    // TC-I04
    // -----------------------------------------------------------------------
    @Test(description = "Adding two items to cart should show badge count of 2")
    @Story("Add to cart")
    @Severity(SeverityLevel.CRITICAL)
    public void addTwoItems_cartBadgeShowsTwo() {
        InventoryPage inventoryPage = loginAndGetInventory();

        inventoryPage.addItemToCartByIndex(0)
                     .addItemToCartByIndex(1);

        ScreenshotUtil.capture(page, "TC-I04_two_items_added", getClass().getSimpleName());

        assertEquals(inventoryPage.getCartBadgeCount(), "2",
                "Cart badge should display '2' after adding two items");
    }

    // -----------------------------------------------------------------------
    // TC-I05
    // -----------------------------------------------------------------------
    @Test(description = "Sort 'Name (A to Z)' should order products alphabetically ascending")
    @Story("Product sorting")
    @Severity(SeverityLevel.NORMAL)
    public void sortAtoZ_productsInAlphabeticalOrder() {
        InventoryPage inventoryPage = loginAndGetInventory();
        inventoryPage.sortBy("Name (A to Z)");

        ScreenshotUtil.capture(page, "TC-I05_sorted_a_to_z", getClass().getSimpleName());

        List<String> names = inventoryPage.getProductNames();
        for (int i = 0; i < names.size() - 1; i++) {
            assertTrue(
                    names.get(i).compareToIgnoreCase(names.get(i + 1)) <= 0,
                    String.format("Products not sorted A→Z: '%s' appears before '%s'",
                            names.get(i), names.get(i + 1))
            );
        }
    }
}
