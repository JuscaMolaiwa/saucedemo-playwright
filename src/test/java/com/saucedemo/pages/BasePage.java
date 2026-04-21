package com.saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.saucedemo.config.TestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BasePage — parent for all page-object classes.
 *
 * Provides:
 *  - the Playwright {@code Page} instance
 *  - a shared {@code log} logger
 *  - common navigation and wait helpers
 */
public abstract class BasePage {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final Page   page;

    protected BasePage(Page page) {
        this.page = page;
        page.setDefaultTimeout(TestConfig.TIMEOUT_MS);
    }

    // -----------------------------------------------------------------------
    // Navigation helpers
    // -----------------------------------------------------------------------

    /** Navigate to a path relative to the configured base URL. */
    protected void navigateTo(String path) {
        String url = TestConfig.BASE_URL + path;
        log.debug("Navigating to: {}", url);
        page.navigate(url);
    }

    /** Returns the current page URL. */
    public String currentUrl() {
        return page.url();
    }

    /** Returns the page title. */
    public String pageTitle() {
        return page.title();
    }

    // -----------------------------------------------------------------------
    // Wait helpers
    // -----------------------------------------------------------------------

    /** Waits until the given locator is visible. */
    protected void waitForVisible(Locator locator) {
        locator.waitFor(new Locator.WaitForOptions()
                .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
    }

    /** Returns true when the given text appears anywhere on the page. */
    protected boolean isTextPresent(String text) {
        return page.locator("body").innerText().contains(text);
    }
}
