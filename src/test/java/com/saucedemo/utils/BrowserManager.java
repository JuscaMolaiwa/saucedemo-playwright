package com.saucedemo.utils;

import com.microsoft.playwright.*;
import com.saucedemo.config.TestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

/**
 * BrowserManager manages the Playwright lifecycle for each test class.
 *
 * Lifecycle:
 *   Playwright  →  Browser  →  BrowserContext  →  Page
 *
 * One Playwright + Browser instance is shared across all tests in a class
 * (setup/teardown in @BeforeClass / @AfterClass of BaseTest).
 * A fresh BrowserContext + Page is created per test method to ensure isolation.
 */
public class BrowserManager {

    private static final Logger log = LoggerFactory.getLogger(BrowserManager.class);

    private Playwright playwright;
    private Browser     browser;
    private BrowserContext context;
    private Page        page;

    // -----------------------------------------------------------------------
    // Initialise
    // -----------------------------------------------------------------------

    /** Creates Playwright + Browser. Call once per test class. */
    public void initialise() {
        log.info("Launching Playwright ({}, headless={})", TestConfig.BROWSER, TestConfig.HEADLESS);

        playwright = Playwright.create();

        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(TestConfig.HEADLESS)
                .setSlowMo(50);  // small delay so Playwright's tracing captures clearly

        browser = switch (TestConfig.BROWSER.toLowerCase()) {
            case "firefox" -> playwright.firefox().launch(launchOptions);
            case "webkit"  -> playwright.webkit().launch(launchOptions);
            default        -> playwright.chromium().launch(launchOptions);
        };

        log.info("Browser launched: {}", browser.browserType().name());
    }

    // -----------------------------------------------------------------------
    // Per-test setup / teardown
    // -----------------------------------------------------------------------

    /**
     * Creates a fresh BrowserContext and Page for each test method.
     * Enables Playwright tracing (screenshots + snapshots on every action).
     *
     * @param testName used to label the trace file
     */
    public Page newPage(String testName) {
        context = browser.newContext(new Browser.NewContextOptions()
                .setBaseURL(TestConfig.BASE_URL)
                .setViewportSize(1280, 900)
                .setRecordVideoDir(null));  // set a path here to enable video recording

        // Enable tracing: screenshots and DOM snapshots on every action
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true));

        page = context.newPage();
        page.setDefaultTimeout(TestConfig.TIMEOUT_MS);

        log.info("New page created for test: {}", testName);
        return page;
    }

    /**
     * Saves the Playwright trace and closes the current context.
     * Call at the end of each test method (pass or fail).
     *
     * @param testName  used in the trace filename
     * @param passed    whether the test passed (trace is most useful on failure)
     */
    public void closePage(String testName, boolean passed) {
        if (context != null) {
            String tracePath = String.format("target/traces/%s.zip", testName.replaceAll("\\s+", "_"));
            try {
                context.tracing().stop(new Tracing.StopOptions()
                        .setPath(Paths.get(tracePath)));
                if (!passed) {
                    log.warn("Test FAILED – trace saved: {}", tracePath);
                }
            } catch (Exception e) {
                log.warn("Could not save trace for {}: {}", testName, e.getMessage());
            }
            context.close();
            context = null;
            page    = null;
        }
    }

    // -----------------------------------------------------------------------
    // Shutdown
    // -----------------------------------------------------------------------

    /** Closes the Browser and Playwright. Call once per test class. */
    public void shutdown() {
        if (browser != null) {
            browser.close();
            log.info("Browser closed.");
        }
        if (playwright != null) {
            playwright.close();
            log.info("Playwright closed.");
        }
    }

    // -----------------------------------------------------------------------
    // Accessors
    // -----------------------------------------------------------------------

    public Page getPage() { return page; }
}
