package com.saucedemo.tests;

import com.microsoft.playwright.Page;
import com.saucedemo.utils.BrowserManager;
import com.saucedemo.utils.ScreenshotUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.*;

/**
 * BaseTest — abstract parent that every smoke test class extends.
 * <p>
 * Responsibilities:
 *  - Initializes and shuts down the BrowserManager once per class.
 *  - Opens a fresh Page before each test method and closes it after.
 *  - Captures a failure screenshot and saves a Playwright trace on any failure.
 *  - Exposes the live {@code page} field and a convenience {@code log} to subclasses.
 */
public abstract class BaseTest {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final BrowserManager browserManager = new BrowserManager();

    /** Exposed to every subclass for page interactions. */
    protected Page page;

    // -----------------------------------------------------------------------
    // Class-level lifecycle (one browser per test class)
    // -----------------------------------------------------------------------

    @BeforeClass(alwaysRun = true)
    public void setUpClass() {
        browserManager.initialise();
        log.info("=== {} test class started ===", getClass().getSimpleName());
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        browserManager.shutdown();
        log.info("=== {} test class finished ===", getClass().getSimpleName());
    }

    // -----------------------------------------------------------------------
    // Method-level lifecycle (fresh context + page per test)
    // -----------------------------------------------------------------------

    @BeforeMethod(alwaysRun = true)
    public void setUpMethod(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        log.info("--- Starting: {} ---", testName);
        page = browserManager.newPage(testName);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownMethod(ITestResult result) {
        boolean passed = result.isSuccess();
        String  testName = result.getMethod().getMethodName();

        if (!passed) {
            log.warn("Test FAILED: {} — capturing screenshot", testName);
            ScreenshotUtil.captureOnFailure(page, testName, getClass().getSimpleName());
        }

        browserManager.closePage(testName, passed);
        log.info("--- Finished: {} — {} ---", testName, passed ? "PASSED" : "FAILED");
    }
}
