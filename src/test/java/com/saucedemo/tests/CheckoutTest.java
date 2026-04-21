package com.saucedemo.tests;

import com.saucedemo.config.TestConfig;
import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.CheckoutPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.utils.ScreenshotUtil;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * CheckoutTest — smoke tests for all three checkout steps.
 *
 * Covers:
 *  TC-CH01  Clicking Checkout navigates to step-one (information)
 *  TC-CH02  Valid information entry advances to step-two (overview)
 *  TC-CH03  Step-two shows correct item count and totals are present
 *  TC-CH04  Finishing checkout shows confirmation page with success header
 *  TC-CH05  'Back Home' from confirmation returns to inventory
 *  TC-CH06  Empty form on step-one shows validation error
 *  TC-CH07  Missing first name on step-one shows validation error
 */
@Epic("SauceDemo Smoke Tests")
@Feature("Checkout Flow")
public class CheckoutTest extends BaseTest {

    // -----------------------------------------------------------------------
    // Shared helper — login, add one item, open cart, click Checkout
    // -----------------------------------------------------------------------
    private CheckoutPage loginAddItemAndStartCheckout() {
        InventoryPage inventoryPage = new LoginPage(page)
                .open()
                .loginAs(TestConfig.STANDARD_USER, TestConfig.PASSWORD);
        inventoryPage.addFirstItemToCart();
        CartPage cartPage = inventoryPage.goToCart();
        return cartPage.proceedToCheckout();
    }

    // -----------------------------------------------------------------------
    // TC-CH01
    // -----------------------------------------------------------------------
    @Test(description = "Clicking Checkout from cart should navigate to checkout step one")
    @Story("Checkout navigation")
    @Severity(SeverityLevel.BLOCKER)
    public void checkout_navigatesToStepOne() {
        CheckoutPage checkoutPage = loginAddItemAndStartCheckout();

        ScreenshotUtil.capture(page, "TC-CH01_checkout_step_one", getClass().getSimpleName());

        assertTrue(checkoutPage.isOnCheckoutStepOne(),
                "URL should contain 'checkout-step-one.html'. Actual: " + page.url());
        assertEquals(checkoutPage.getCheckoutTitle(), "Checkout: Your Information",
                "Step-one title should be 'Checkout: Your Information'");
    }

    // -----------------------------------------------------------------------
    // TC-CH02
    // -----------------------------------------------------------------------
    @Test(description = "Filling valid information should advance to checkout step two (overview)")
    @Story("Checkout information")
    @Severity(SeverityLevel.BLOCKER)
    public void validInformation_advancesToStepTwo() {
        CheckoutPage checkoutPage = loginAddItemAndStartCheckout();

        checkoutPage.fillInformation(
                TestConfig.CHECKOUT_FIRST_NAME,
                TestConfig.CHECKOUT_LAST_NAME,
                TestConfig.CHECKOUT_ZIP_CODE);

        ScreenshotUtil.capture(page, "TC-CH02_checkout_step_two", getClass().getSimpleName());

        assertTrue(checkoutPage.isOnCheckoutStepTwo(),
                "URL should contain 'checkout-step-two.html'. Actual: " + page.url());
        assertEquals(checkoutPage.getCheckoutTitle(), "Checkout: Overview",
                "Step-two title should be 'Checkout: Overview'");
    }

    // -----------------------------------------------------------------------
    // TC-CH03
    // -----------------------------------------------------------------------
    @Test(description = "Checkout overview should display 1 item and show item total and order total")
    @Story("Checkout overview")
    @Severity(SeverityLevel.CRITICAL)
    public void checkoutOverview_showsItemAndTotals() {
        CheckoutPage checkoutPage = loginAddItemAndStartCheckout();

        checkoutPage.fillInformation(
                TestConfig.CHECKOUT_FIRST_NAME,
                TestConfig.CHECKOUT_LAST_NAME,
                TestConfig.CHECKOUT_ZIP_CODE);

        ScreenshotUtil.capture(page, "TC-CH03_overview_totals", getClass().getSimpleName());

        assertEquals(checkoutPage.getOverviewItemCount(), 1,
                "Overview should list exactly 1 item");

        String itemTotal  = checkoutPage.getItemTotalText();
        String orderTotal = checkoutPage.getOrderTotalText();

        assertFalse(itemTotal.isBlank(),
                "Item total label should not be blank");
        assertFalse(orderTotal.isBlank(),
                "Order total label should not be blank");

        // Both labels should start with a currency indicator
        assertTrue(itemTotal.contains("$"),
                "Item total should contain a '$' symbol. Actual: " + itemTotal);
        assertTrue(orderTotal.contains("$"),
                "Order total should contain a '$' symbol. Actual: " + orderTotal);
    }

    // -----------------------------------------------------------------------
    // TC-CH04
    // -----------------------------------------------------------------------
    @Test(description = "Finishing checkout should show the order confirmation page")
    @Story("Order confirmation")
    @Severity(SeverityLevel.BLOCKER)
    public void finishCheckout_showsConfirmation() {
        CheckoutPage checkoutPage = loginAddItemAndStartCheckout();

        checkoutPage.fillInformation(
                TestConfig.CHECKOUT_FIRST_NAME,
                TestConfig.CHECKOUT_LAST_NAME,
                TestConfig.CHECKOUT_ZIP_CODE);

        checkoutPage.finishCheckout();

        ScreenshotUtil.capture(page, "TC-CH04_order_confirmed", getClass().getSimpleName());

        assertTrue(checkoutPage.isOnConfirmationPage(),
                "URL should contain 'checkout-complete.html'. Actual: " + page.url());

        String header = checkoutPage.getConfirmationHeader();
        assertTrue(header.toLowerCase().contains("thank you"),
                "Confirmation header should say 'THANK YOU'. Actual: " + header);
    }

    // -----------------------------------------------------------------------
    // TC-CH05
    // -----------------------------------------------------------------------
    @Test(description = "'Back Home' from confirmation page should return user to inventory")
    @Story("Post-order navigation")
    @Severity(SeverityLevel.NORMAL)
    public void backHome_returnsToInventory() {
        CheckoutPage checkoutPage = loginAddItemAndStartCheckout();

        checkoutPage.fillInformation(
                TestConfig.CHECKOUT_FIRST_NAME,
                TestConfig.CHECKOUT_LAST_NAME,
                TestConfig.CHECKOUT_ZIP_CODE);
        checkoutPage.finishCheckout();

        assertTrue(checkoutPage.isOnConfirmationPage(), "Precondition: should be on confirmation");

        InventoryPage inventoryPage = checkoutPage.backToHome();

        ScreenshotUtil.capture(page, "TC-CH05_back_home", getClass().getSimpleName());

        assertTrue(inventoryPage.isOnInventoryPage(),
                "Should be back on inventory after clicking 'Back Home'. Actual: " + page.url());
    }

    // -----------------------------------------------------------------------
    // TC-CH06
    // -----------------------------------------------------------------------
    @Test(description = "Submitting empty checkout form should show a validation error")
    @Story("Checkout validation")
    @Severity(SeverityLevel.CRITICAL)
    public void emptyCheckoutForm_showsValidationError() {
        CheckoutPage checkoutPage = loginAddItemAndStartCheckout();

        checkoutPage.continueWithEmptyForm();

        ScreenshotUtil.capture(page, "TC-CH06_empty_form_error", getClass().getSimpleName());

        assertTrue(checkoutPage.isErrorDisplayed(),
                "Validation error should be shown for an empty checkout form");
        assertFalse(checkoutPage.getErrorText().isBlank(),
                "Error message text should not be blank");
    }

    // -----------------------------------------------------------------------
    // TC-CH07
    // -----------------------------------------------------------------------
    @Test(description = "Missing first name at checkout should show a first-name validation error")
    @Story("Checkout validation")
    @Severity(SeverityLevel.NORMAL)
    public void missingFirstName_showsFirstNameError() {
        CheckoutPage checkoutPage = loginAddItemAndStartCheckout();

        checkoutPage.continueWithoutFirstName(
                TestConfig.CHECKOUT_LAST_NAME,
                TestConfig.CHECKOUT_ZIP_CODE);

        ScreenshotUtil.capture(page, "TC-CH07_missing_first_name", getClass().getSimpleName());

        assertTrue(checkoutPage.isErrorDisplayed(),
                "Validation error should appear when first name is missing");
        assertTrue(checkoutPage.getErrorText().toLowerCase().contains("first name"),
                "Error should reference 'First Name'. Actual: " + checkoutPage.getErrorText());
    }
}
