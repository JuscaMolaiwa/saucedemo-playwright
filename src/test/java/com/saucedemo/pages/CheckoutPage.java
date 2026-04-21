package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.saucedemo.config.TestConfig;

/**
 * CheckoutPage — covers all three checkout steps:
 *   Step 1: Information entry  (/checkout-step-one.html)
 *   Step 2: Order overview     (/checkout-step-two.html)
 *   Step 3: Confirmation       (/checkout-complete.html)
 *
 * Returns the appropriate page-object type after each step transition.
 */
public class CheckoutPage extends BasePage {

    // -----------------------------------------------------------------------
    // Step 1 locators — Information
    // -----------------------------------------------------------------------
    private final Locator firstNameInput  = page.locator("#first-name");
    private final Locator lastNameInput   = page.locator("#last-name");
    private final Locator zipCodeInput    = page.locator("#postal-code");
    private final Locator continueButton  = page.locator("#continue");
    private final Locator cancelButton    = page.locator("#cancel");
    private final Locator errorMessage    = page.locator("[data-test='error']");
    private final Locator checkoutTitle   = page.locator(".title");

    // -----------------------------------------------------------------------
    // Step 2 locators — Overview
    // -----------------------------------------------------------------------
    private final Locator finishButton    = page.locator("#finish");
    private final Locator summaryItems    = page.locator(".cart_item");
    private final Locator itemTotal       = page.locator(".summary_subtotal_label");
    private final Locator totalLabel      = page.locator(".summary_total_label");

    // -----------------------------------------------------------------------
    // Step 3 locators — Complete
    // -----------------------------------------------------------------------
    private final Locator confirmationHeader = page.locator(".complete-header");
    private final Locator backHomeButton     = page.locator("#back-to-products");

    public CheckoutPage(Page page) {
        super(page);
    }

    // -----------------------------------------------------------------------
    // Step 1 — Information
    // -----------------------------------------------------------------------

    public boolean isOnCheckoutStepOne() {
        return currentUrl().contains(TestConfig.CHECKOUT_PATH);
    }

    /** Fills the customer information form and clicks Continue. */
    public CheckoutPage fillInformation(String firstName, String lastName, String zip) {
        log.info("Filling checkout info: {} {} {}", firstName, lastName, zip);
        firstNameInput.fill(firstName);
        lastNameInput.fill(lastName);
        zipCodeInput.fill(zip);
        continueButton.click();
        return this;  // URL will change to step-two; same object holds overview locators
    }

    /** Clicks Continue without filling any fields (triggers validation). */
    public CheckoutPage continueWithEmptyForm() {
        log.info("Clicking Continue with empty checkout form");
        continueButton.click();
        return this;
    }

    /** Clicks Continue without the first name field. */
    public CheckoutPage continueWithoutFirstName(String lastName, String zip) {
        lastNameInput.fill(lastName);
        zipCodeInput.fill(zip);
        continueButton.click();
        return this;
    }

    public boolean isErrorDisplayed() {
        return errorMessage.isVisible();
    }

    public String getErrorText() {
        waitForVisible(errorMessage);
        return errorMessage.innerText();
    }

    public String getCheckoutTitle() {
        return checkoutTitle.innerText();
    }

    // -----------------------------------------------------------------------
    // Step 2 — Overview
    // -----------------------------------------------------------------------

    public boolean isOnCheckoutStepTwo() {
        return currentUrl().contains(TestConfig.OVERVIEW_PATH);
    }

    /** Completes the purchase by clicking Finish. */
    public CheckoutPage finishCheckout() {
        log.info("Clicking Finish to complete order");
        finishButton.click();
        return this;
    }

    public int getOverviewItemCount() {
        return summaryItems.count();
    }

    public String getItemTotalText() {
        return itemTotal.innerText();
    }

    public String getOrderTotalText() {
        return totalLabel.innerText();
    }

    // -----------------------------------------------------------------------
    // Step 3 — Confirmation
    // -----------------------------------------------------------------------

    public boolean isOnConfirmationPage() {
        return currentUrl().contains(TestConfig.COMPLETE_PATH);
    }

    public String getConfirmationHeader() {
        waitForVisible(confirmationHeader);
        return confirmationHeader.innerText();
    }

    /** Clicks 'Back Home' on the confirmation page. */
    public InventoryPage backToHome() {
        log.info("Clicking Back Home");
        backHomeButton.click();
        return new InventoryPage(page);
    }
}
