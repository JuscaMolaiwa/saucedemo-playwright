package com.saucedemo.tests;

import com.saucedemo.config.TestConfig;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.utils.ScreenshotUtil;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * LoginTest — smoke tests for the SauceDemo authentication page.
 * <p>
 * Covers:
 *  TC-L01  Valid login redirects to inventory
 *  TC-L02  Locked-out user sees specific error
 *  TC-L03  Invalid password shows error
 *  TC-L04  Empty username shows error
 *  TC-L05  Empty password shows error
 *  TC-L06  Logout returns user to login page
 */
@Epic("SauceDemo Smoke Tests")
@Feature("Authentication")
public class LoginTest extends BaseTest {

    // -----------------------------------------------------------------------
    // TC-L01
    // -----------------------------------------------------------------------
    @Test(description = "Valid standard_user login should redirect to inventory page")
    @Story("Standard user login")
    @Severity(SeverityLevel.BLOCKER)
    public void validLogin_redirectsToInventory() {
        LoginPage loginPage = new LoginPage(page).open();

        assertTrue(loginPage.isLogoDisplayed(),
                "Swag Labs logo should be visible on login page");
        assertTrue(loginPage.isLoginButtonVisible(),
                "Login button should be visible");

        ScreenshotUtil.capture(page, "TC-L01_login_page", getClass().getSimpleName());

        InventoryPage inventoryPage = loginPage.loginAs(
                TestConfig.STANDARD_USER, TestConfig.PASSWORD);

        ScreenshotUtil.capture(page, "TC-L01_post_login", getClass().getSimpleName());

        assertTrue(inventoryPage.isOnInventoryPage(),
                "URL should contain 'inventory.html' after successful login. Actual: " + page.url());
        assertEquals(inventoryPage.getPageTitle(), "Products",
                "Inventory page title should be 'Products'");
    }

    // -----------------------------------------------------------------------
    // TC-L02
    // -----------------------------------------------------------------------
    @Test(description = "Locked-out user should see locked-out error message")
    @Story("Locked-out user login")
    @Severity(SeverityLevel.CRITICAL)
    public void lockedOutUser_seesLockedOutError() {
        LoginPage loginPage = new LoginPage(page).open();

        LoginPage result = loginPage.attemptLogin(
                TestConfig.LOCKED_OUT_USER, TestConfig.PASSWORD);

        ScreenshotUtil.capture(page, "TC-L02_locked_out_error", getClass().getSimpleName());

        assertTrue(result.isErrorDisplayed(),
                "An error message should be displayed for locked-out user");
        assertTrue(result.getErrorText().toLowerCase().contains("locked out"),
                "Error should mention 'locked out'. Actual: " + result.getErrorText());
    }

    // -----------------------------------------------------------------------
    // TC-L03
    // -----------------------------------------------------------------------
    @Test(description = "Invalid password should show an error message and stay on login page")
    @Story("Invalid credentials")
    @Severity(SeverityLevel.CRITICAL)
    public void invalidPassword_showsError() {
        LoginPage loginPage = new LoginPage(page).open();

        LoginPage result = loginPage.attemptLogin(
                TestConfig.STANDARD_USER, "wrong_password");

        ScreenshotUtil.capture(page, "TC-L03_invalid_password", getClass().getSimpleName());

        assertTrue(result.isErrorDisplayed(),
                "Error message should appear for invalid password");
        assertFalse(page.url().contains("inventory"),
                "User should NOT be redirected to inventory with bad password");
    }

    // -----------------------------------------------------------------------
    // TC-L04
    // -----------------------------------------------------------------------
    @Test(description = "Submitting login with empty username should show validation error")
    @Story("Empty field validation")
    @Severity(SeverityLevel.NORMAL)
    public void emptyUsername_showsValidationError() {
        LoginPage loginPage = new LoginPage(page).open();

        LoginPage result = loginPage.attemptEmptyLogin();

        ScreenshotUtil.capture(page, "TC-L04_empty_fields", getClass().getSimpleName());

        assertTrue(result.isErrorDisplayed(),
                "A validation error should be shown when username is empty");
        assertTrue(result.getErrorText().toLowerCase().contains("username"),
                "Error should reference the username field. Actual: " + result.getErrorText());
    }

    // -----------------------------------------------------------------------
    // TC-L05
    // -----------------------------------------------------------------------
    @Test(description = "Submitting login without a password should show validation error")
    @Story("Empty field validation")
    @Severity(SeverityLevel.NORMAL)
    public void emptyPassword_showsValidationError() {
        LoginPage loginPage = new LoginPage(page).open();

        LoginPage result = loginPage.attemptLogin(TestConfig.STANDARD_USER, "");

        ScreenshotUtil.capture(page, "TC-L05_empty_password", getClass().getSimpleName());

        assertTrue(result.isErrorDisplayed(),
                "A validation error should be shown when password is empty");
        assertTrue(result.getErrorText().toLowerCase().contains("password"),
                "Error should reference the password field. Actual: " + result.getErrorText());
    }

    // -----------------------------------------------------------------------
    // TC-L06
    // -----------------------------------------------------------------------
    @Test(description = "Logout from inventory page should return user to the login page")
    @Story("Logout")
    @Severity(SeverityLevel.CRITICAL)
    public void logout_returnsToLoginPage() {
        LoginPage loginPage = new LoginPage(page).open();
        InventoryPage inventoryPage = loginPage.loginAs(
                TestConfig.STANDARD_USER, TestConfig.PASSWORD);

        assertTrue(inventoryPage.isOnInventoryPage(), "Should be on inventory before logout");

        LoginPage afterLogout = inventoryPage.logout();

        ScreenshotUtil.capture(page, "TC-L06_post_logout", getClass().getSimpleName());

        assertTrue(afterLogout.isLoginButtonVisible(),
                "Login button should be visible after logout");
        assertFalse(page.url().contains("inventory"),
                "URL should not contain 'inventory' after logout");
    }
}
