package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.saucedemo.config.TestConfig;

/**
 * LoginPage — page object for https://www.saucedemo.com/v1/
 *
 * Encapsulates all locators and interactions on the login screen.
 */
public class LoginPage extends BasePage {

    // -----------------------------------------------------------------------
    // Locators
    // -----------------------------------------------------------------------
    private final Locator usernameInput  = page.locator("#user-name");
    private final Locator passwordInput  = page.locator("#password");
    private final Locator loginButton    = page.locator("#login-button");
    private final Locator errorMessage   = page.locator("[data-test='error']");
    private final Locator swagLabsLogo   = page.locator(".login_logo");

    public LoginPage(Page page) {
        super(page);
    }

    // -----------------------------------------------------------------------
    // Actions
    // -----------------------------------------------------------------------

    /** Opens the login page. */
    public LoginPage open() {
        navigateTo(TestConfig.LOGIN_PATH);
        log.info("Opened login page: {}", currentUrl());
        return this;
    }

    /** Fills credentials and clicks the login button. */
    public InventoryPage loginAs(String username, String password) {
        log.info("Logging in as: {}", username);
        usernameInput.fill(username);
        passwordInput.fill(password);
        loginButton.click();
        return new InventoryPage(page);
    }

    /** Attempts login and stays on the login page (for invalid-credential tests). */
    public LoginPage attemptLogin(String username, String password) {
        log.info("Attempting login (expecting failure) as: {}", username);
        usernameInput.fill(username);
        passwordInput.fill(password);
        loginButton.click();
        return this;
    }

    /** Submits the login form with empty fields. */
    public LoginPage attemptEmptyLogin() {
        log.info("Submitting login with empty fields");
        loginButton.click();
        return this;
    }

    // -----------------------------------------------------------------------
    // Assertions helpers
    // -----------------------------------------------------------------------

    public boolean isLogoDisplayed() {
        return swagLabsLogo.isVisible();
    }

    public boolean isErrorDisplayed() {
        return errorMessage.isVisible();
    }

    public String getErrorText() {
        waitForVisible(errorMessage);
        return errorMessage.innerText();
    }

    public boolean isLoginButtonVisible() {
        return loginButton.isVisible();
    }
}
