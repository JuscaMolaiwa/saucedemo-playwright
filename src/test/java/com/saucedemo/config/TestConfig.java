package com.saucedemo.config;

/**
 * Central configuration and test-data constants for the SauceDemo smoke suite.
 * <p>
 * All values that may change per environment can be overridden by system properties:
 *   -DbaseUrl=<a href="https://www.saucedemo.com/v1/">...</a>
 *   -Dbrowser=firefox
 *   -Dheadless=false
 */
public final class TestConfig {

    // -----------------------------------------------------------------------
    // Application
    // -----------------------------------------------------------------------
    public static final String BASE_URL =
            System.getProperty("baseUrl", "https://www.saucedemo.com/v1/");

    // -----------------------------------------------------------------------
    // Browser
    // -----------------------------------------------------------------------
    public static final String BROWSER =
            System.getProperty("browser", "chromium");          // chromium | firefox | webkit
    public static final boolean HEADLESS =
            Boolean.parseBoolean(System.getProperty("headless", "true")); // run in headless mode
    public static final int TIMEOUT_MS =
            Integer.parseInt(System.getProperty("timeoutMs", "10000"));  // default 10 s

    // -----------------------------------------------------------------------
    // Users
    // -----------------------------------------------------------------------
    public static final String STANDARD_USER   = "standard_user";
    public static final String LOCKED_OUT_USER = "locked_out_user";
    public static final String PASSWORD        = "secret_sauce";

    // -----------------------------------------------------------------------
    // Checkout data
    // -----------------------------------------------------------------------
    public static final String CHECKOUT_FIRST_NAME = "Test";
    public static final String CHECKOUT_LAST_NAME  = "User";
    public static final String CHECKOUT_ZIP_CODE   = "12345";

    // -----------------------------------------------------------------------
    // Page paths  (relative to BASE_URL)
    // -----------------------------------------------------------------------
    public static final String LOGIN_PATH     = "";
    public static final String INVENTORY_PATH = "inventory.html";
    public static final String CART_PATH      = "cart.html";
    public static final String CHECKOUT_PATH  = "checkout-step-one.html";
    public static final String OVERVIEW_PATH  = "checkout-step-two.html";
    public static final String COMPLETE_PATH  = "checkout-complete.html";

    private TestConfig() { /* utility class - no instantiation */ }
}
