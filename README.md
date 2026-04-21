# SauceDemo Playwright Smoke Test Framework

> **System Under Test:** https://www.saucedemo.com/v1/  
> **Stack:** Java 17+ · Playwright for Java · TestNG · Allure Reports · Maven

---

## Table of Contents
1. [Framework Architecture](#framework-architecture)
2. [Project Structure](#project-structure)
3. [Prerequisites](#prerequisites)
4. [Setup & Installation](#setup--installation)
5. [Running the Tests](#running-the-tests)
6. [Viewing the Allure Report](#viewing-the-allure-report)
7. [Configuration](#configuration)
8. [Test Coverage](#test-coverage)
9. [Design Decisions](#design-decisions)

---

## Framework Architecture

```
┌─────────────────────────────────────────────────────┐
│              TestNG Suite XML                       │
│              (testng-smoke.xml)                     │
└────────────────────┬────────────────────────────────┘
                     │ runs
        ┌────────────▼────────────┐
        │        BaseTest         │  ← lifecycle hooks, failure screenshots
        └────────────┬────────────┘
                     │ extends
   ┌─────────────────┼─────────────────┐
   │                 │                 │
LoginTest    InventoryTest    CartTest    CheckoutTest
   │                 │                 │
   └─────────────────┼─────────────────┘
                     │ uses
        ┌────────────▼────────────┐
        │    Page Object Layer    │
        │  LoginPage              │
        │  InventoryPage          │  ← encapsulate selectors & actions
        │  CartPage               │
        │  CheckoutPage           │
        └────────────┬────────────┘
                     │ extends
        ┌────────────▼────────────┐
        │        BasePage         │  ← shared navigation + wait helpers
        └────────────┬────────────┘
                     │ wraps
        ┌────────────▼────────────┐
        │   Playwright Page API   │
        └─────────────────────────┘
```

**Supporting utilities:**

| Class            | Responsibility                                   |
|------------------|--------------------------------------------------|
| `BrowserManager` | Playwright / Browser / Context / Page lifecycle  |
| `ScreenshotUtil` | Full-page screenshots → disk + Allure attachment |
| `TestConfig`     | Central constants and system-property overrides  |

---

## Project Structure

```
saucedemo-playwright/
├── pom.xml
└── src/
    └── test/
        ├── java/com/saucedemo/
        │   ├── config/
        │   │   └── TestConfig.java          # URLs, credentials, timeouts
        │   ├── pages/
        │   │   ├── BasePage.java            # Navigation + wait helpers
        │   │   ├── LoginPage.java           # Login page POM
        │   │   ├── InventoryPage.java       # Products page POM
        │   │   ├── CartPage.java            # Cart page POM
        │   │   └── CheckoutPage.java        # All 3 checkout steps POM
        │   ├── tests/
        │   │   ├── BaseTest.java            # TestNG hooks, screenshot on failure
        │   │   ├── LoginTest.java           # TC-L01 → TC-L06
        │   │   ├── InventoryTest.java       # TC-I01 → TC-I05
        │   │   ├── CartTest.java            # TC-C01 → TC-C06
        │   │   └── CheckoutTest.java        # TC-CH01 → TC-CH07
        │   └── utils/
        │       ├── BrowserManager.java      # Playwright lifecycle manager
        │       └── ScreenshotUtil.java      # Screenshot capture + Allure attach
        └── resources/
            ├── testng-smoke.xml             # TestNG suite definition
            └── logback-test.xml             # Logging configuration
```

---

## Prerequisites

| Tool            | Version | Notes                           |
|-----------------|---------|---------------------------------|
| Java JDK        | 17+     | `java -version` to check        |
| Maven           | 3.8+    | `mvn -version` to check         |
| Internet access | —       | Tests run against the live site |

> **No browser installation needed.** Playwright downloads its own managed browser binaries automatically on first run.

---

## Setup & Installation

```bash
# 1. Clone the repository
git clone <repo-url>
cd saucedemo-playwright

# 2. Install dependencies and download Playwright browsers
mvn clean install -DskipTests
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install chromium"

# Optional: install all browsers
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

---

## Running the Tests

### Run the full smoke suite (default: Chromium, headless)
```bash
mvn clean test
```

### Run with a specific browser
```bash
mvn clean test -Dbrowser=firefox
mvn clean test -Dbrowser=webkit
mvn clean test -Dbrowser=chromium
```

### Run in headed mode (see the browser)
```bash
mvn clean test -Dheadless=false
```

### Run a single test class
```bash
mvn clean test -Dtest=LoginTest
mvn clean test -Dtest=CheckoutTest
```

### Override the base URL (e.g. point at a different environment)
```bash
mvn clean test -DbaseUrl=https://www.saucedemo.com/v1/
```

### Combine options
```bash
mvn clean test -Dbrowser=firefox -Dheadless=false -Dtest=CartTest
```

---

## Viewing the Allure Report

```bash
# Generate the HTML report from test results
mvn allure:report

# Open the report in your browser
allure serve
```

The report is also saved statically at:
```
target/site/allure-maven-plugin/index.html
```

**Report includes:**
- Pass/fail status per test
- Severity and feature groupings
- Full-page screenshots (on every test, automatically)
- Playwright trace file paths for failures (open in https://trace.playwright.dev)

---

## Configuration

All configurable values live in `TestConfig.java` and can be overridden via Maven system properties (`-D`):

| Property    | Default                         | Description                        |
|-------------|---------------------------------|------------------------------------|
| `baseUrl`   | `https://www.saucedemo.com/v1/` | Target application URL             |
| `browser`   | `chromium`                      | `chromium`, `firefox`, or `webkit` |
| `headless`  | `true`                          | `false` to see the browser         |
| `timeoutMs` | `10000`                         | Default Playwright timeout (ms)    |

### Test users (hardcoded in `TestConfig`)

| User              | Password       | Use                   |
|-------------------|----------------|-----------------------|
| `standard_user`   | `secret_sauce` | Happy-path tests      |
| `locked_out_user` | `secret_sauce` | Locked-out error test |

---

## Test Coverage

### Authentication (LoginTest) — 6 tests

| ID     | Scenario                              | Severity |
|--------|---------------------------------------|----------|
| TC-L01 | Valid login redirects to inventory    | BLOCKER  |
| TC-L02 | Locked-out user sees locked-out error | CRITICAL |
| TC-L03 | Invalid password shows error          | CRITICAL |
| TC-L04 | Empty username shows validation error | NORMAL   |
| TC-L05 | Empty password shows validation error | NORMAL   |
| TC-L06 | Logout returns to login page          | CRITICAL |

### Inventory / Product Listing (InventoryTest) — 5 tests

| ID     | Scenario                                | Severity |
|--------|-----------------------------------------|----------|
| TC-I01 | Inventory displays 6 products           | BLOCKER  |
| TC-I02 | Inventory page title reads 'Products'   | NORMAL   |
| TC-I03 | Adding one item shows cart badge of 1   | CRITICAL |
| TC-I04 | Adding two items shows cart badge of 2  | CRITICAL |
| TC-I05 | Sort A→Z orders products alphabetically | NORMAL   |

### Shopping Cart (CartTest) — 6 tests

| ID     | Scenario                                 | Severity |
|--------|------------------------------------------|----------|
| TC-C01 | Cart page accessible with correct title  | CRITICAL |
| TC-C02 | Added item appears in cart               | BLOCKER  |
| TC-C03 | Empty cart has no items                  | NORMAL   |
| TC-C04 | Checkout button is visible               | CRITICAL |
| TC-C05 | Removing only item empties cart          | CRITICAL |
| TC-C06 | 'Continue Shopping' returns to inventory | NORMAL   |

### Checkout Flow (CheckoutTest) — 7 tests

| ID      | Scenario                                   | Severity |
|---------|--------------------------------------------|----------|
| TC-CH01 | Checkout navigates to step one             | BLOCKER  |
| TC-CH02 | Valid info advances to step two (overview) | BLOCKER  |
| TC-CH03 | Overview shows item count and totals       | CRITICAL |
| TC-CH04 | Finishing checkout shows confirmation      | BLOCKER  |
| TC-CH05 | 'Back Home' from confirmation -> inventory | NORMAL   |
| TC-CH06 | Empty form shows validation error          | CRITICAL |
| TC-CH07 | Missing first name shows first-name error  | NORMAL   |

**Total: 24 smoke tests**

---

## Design Decisions

### Page Object Model (POM)
Each page of the SUT is represented by its own class. Locators and actions are fully encapsulated — test classes never reference CSS selectors directly. This means a selector change requires an update in one place only.

### Fresh context per test
A new `BrowserContext` is created before each test method and closed after it. This gives every test a clean cookie/session state, preventing inter-test pollution without the overhead of restarting the browser.

### Playwright tracing
Tracing (screenshots + DOM snapshots on every action) is started for every test and saved to `target/traces/<testName>.zip` on failure. Traces can be inspected interactively at https://trace.playwright.dev without any local tooling.

### Allure annotations
`@Epic`, `@Feature`, `@Story`, and `@Severity` on every test class and method give the Allure report a clean three-level hierarchy: Suite → Feature → Test, with severity-based filtering.

### No Thread.sleep
All waits use Playwright's built-in auto-waiting and explicit `waitFor` calls. This makes the suite faster and more stable than fixed delays.
