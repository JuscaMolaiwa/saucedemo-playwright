package com.saucedemo.utils;

import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * ScreenshotUtil captures full-page screenshots and attaches them to the
 * Allure report so failures are immediately visual.
 *
 * Screenshots are organised into per-test-class subdirectories:
 *   src/screenshots/LoginTest/TC-L01_login_page_1234567890.png
 *   src/screenshots/CartTest/TC-C01_cart_page_1234567890.png
 *
 * Pass {@code testClassName} (e.g. {@code getClass().getSimpleName()}) on
 * every call so the file lands in the correct folder.
 */
public final class ScreenshotUtil {

    private static final Logger log = LoggerFactory.getLogger(ScreenshotUtil.class);
    private static final String SCREENSHOT_BASE_DIR = "src/screenshots";

    private ScreenshotUtil() {}

    // -----------------------------------------------------------------------
    // Primary API
    // -----------------------------------------------------------------------

    /**
     * Takes a full-page screenshot, saves it under
     * {@code src/screenshots/<testClassName>/}, and attaches it to the
     * current Allure test step.
     *
     * @param page          the Playwright Page instance
     * @param label         descriptive label used in the filename and Allure attachment name
     * @param testClassName simple name of the test class (e.g. {@code "LoginTest"})
     */
    public static void capture(Page page, String label, String testClassName) {
        if (page == null) return;
        try {
            Path classDir = Paths.get(SCREENSHOT_BASE_DIR, testClassName);
            Files.createDirectories(classDir);

            String filename = label.replaceAll("[^a-zA-Z0-9_\\-]", "_")
                    + "_" + System.currentTimeMillis() + ".png";
            Path filePath = classDir.resolve(filename);

            byte[] bytes = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
            Files.write(filePath, bytes);

            // Attach to Allure report under the label
            Allure.addAttachment(label, "image/png", new ByteArrayInputStream(bytes), "png");
            log.debug("Screenshot saved: {}", filePath);

        } catch (Exception e) {
            log.warn("Failed to capture screenshot '{}' for class '{}': {}",
                    label, testClassName, e.getMessage());
        }
    }

    /**
     * Convenience overload for failure screenshots — prefixes the label with
     * {@code "FAIL_"} so failures stand out in the folder listing.
     *
     * @param page          the Playwright Page instance
     * @param testName      name of the test method that failed
     * @param testClassName simple name of the test class (e.g. {@code "LoginTest"})
     */
    public static void captureOnFailure(Page page, String testName, String testClassName) {
        capture(page, "FAIL_" + testName, testClassName);
    }
}
