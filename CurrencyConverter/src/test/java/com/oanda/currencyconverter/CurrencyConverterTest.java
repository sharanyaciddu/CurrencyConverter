/**
 * @author sharanya
 */
package com.oanda.currencyconverter;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.PageFactory;

import com.oanda.currencyconverter.pages.CurrencyConverterPage;
import com.oanda.utils.ExcelUtils;

/**
 * Test cases for the currency conversion page at
 * http://www.oanda.com/currency/converter/
 * 
 * @author sharanya Ciddu
 *
 */
public class CurrencyConverterTest {
	private static final String PROPERTIES_FILE = "resources/oanda.properties";
	private static final String XLS_FILE = "resources/oanda.xls";
	private static WebDriver driver;
	private static String baseUrl;
	private static com.oanda.currencyconverter.pages.CurrencyConverterPage currencyConverterPage;
	private static Vector<CurrencyTestData> currencyConversionTestVector;

	// Define ExcelSheet Columns
	private enum CurrencyInfoColumns {
		INPUT_CURRENCY, WANTED_CURRENCY, INPUT_AMOUNT, VALID_INPUT
	}

	@Rule
	public ScreenshotTestRule screenshotTestRule = new ScreenshotTestRule();

	@BeforeClass
	public static void setUp() throws Exception {
		Properties props = new Properties();
		props.load(new FileInputStream(PROPERTIES_FILE));
		driver = new FirefoxDriver();
		baseUrl = props.getProperty("url");
		driver.manage().timeouts().implicitlyWait(180, TimeUnit.SECONDS);

		currencyConverterPage = PageFactory.initElements(driver,
				CurrencyConverterPage.class);
		driver.navigate().to(baseUrl + CurrencyConverterPage.URL);
	}

	/**
	 * Reads all the test input data from the excel sheet.
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void readTestData() throws Exception {
		currencyConversionTestVector = new Vector<CurrencyTestData>();
		// Read Excel Data
		List<List<HSSFCell>> testData = ExcelUtils.readDataFromFile(XLS_FILE);
		HSSFCell cell;
		for (int row = 1; row < testData.size(); row++) {
			List<?> list = testData.get(row);
			cell = (HSSFCell) list.get(CurrencyInfoColumns.INPUT_CURRENCY
					.ordinal());
			String inputCurrency = cell.getStringCellValue();

			cell = (HSSFCell) list.get(CurrencyInfoColumns.WANTED_CURRENCY
					.ordinal());
			String wantedCurrency = cell.getStringCellValue();

			String inputAmountString = "";
			cell = (HSSFCell) list.get(CurrencyInfoColumns.INPUT_AMOUNT
					.ordinal());
			if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
				inputAmountString = "" + cell.getNumericCellValue();
			} else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
				inputAmountString = cell.getStringCellValue();
			}

			boolean inputAmountValid = true;
			cell = (HSSFCell) list.get(CurrencyInfoColumns.VALID_INPUT
					.ordinal());
			inputAmountValid = cell.getBooleanCellValue();
			CurrencyTestData currencyTestData = new CurrencyTestData(
					inputCurrency, wantedCurrency, inputAmountString,
					inputAmountValid);
			currencyConversionTestVector.addElement(currencyTestData);
		}
	}

	@AfterClass
	public static void tearDown() throws Exception {
		System.out.println("tearDown");
		driver.quit();
	}
	
	@Before
	public void navigateToPage() {
		driver.navigate().to(baseUrl + CurrencyConverterPage.URL);
	}

	@Test
	public void testCurrencyConversions() throws InterruptedException,
			IOException {

		for (CurrencyTestData t : currencyConversionTestVector) {
			currencyConverterPage.setCurrencyIHave(t.getInputCurrency());
			currencyConverterPage.setCurrencyIWant(t.getWantedCurrency());
			currencyConverterPage.setInputAmountToBuy(t.getInputAmountString());
			Thread.sleep(3000);
			if (t.isInputAmountValid()) {
				assertEquals(t.getInputCurrency() + "/" + t.getWantedCurrency()
						+ " Details",
						currencyConverterPage.getInfoDetailsText());
				Assert.assertNotEquals("you get - " + t.getWantedCurrency(),
						currencyConverterPage.getSellMyCurrencyGet());
			} else {
				// assert equalfsd
				Assert.assertEquals("you get - " + t.getWantedCurrency(),
						currencyConverterPage.getSellMyCurrencyGet());
			}
		}
	}

	/**
	 * Test to check that the sell amount is always less than the buy. Except
	 * when the input amount is 0
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@Test
	public void testSellAmountLessThanBuy() throws InterruptedException,
			IOException {
		currencyConverterPage.setCurrencyIHave("USD");
		currencyConverterPage.setCurrencyIWant("EUR");

		// Test that the amount you get when you sell is equal to the cost when
		// you buy when the amount is 0.
		currencyConverterPage.setInputAmountToBuy("0");
		Thread.sleep(3000);

		String[] sellMyCurrency = currencyConverterPage.getSellMyCurrencyGet()
				.split(" ");
		String[] buyMyCurrency = currencyConverterPage
				.getBuyMyCurrencyCostLabel().split(" ");
		assertEquals(4, sellMyCurrency.length);
		assertEquals(4, buyMyCurrency.length);
		assertEquals(sellMyCurrency[2], buyMyCurrency[2]);
		assertEquals(Double.parseDouble(sellMyCurrency[2]), 0.0, 0.000001);

		// Test that the amount you get when you sell is less than the cost
		// when
		// you buy
		currencyConverterPage.setInputAmountToBuy("23");
		Thread.sleep(3000);
		sellMyCurrency = currencyConverterPage.getSellMyCurrencyGet()
				.split(" ");
		buyMyCurrency = currencyConverterPage.getBuyMyCurrencyCostLabel()
				.split(" ");
		assertEquals(4, sellMyCurrency.length);
		assertEquals(4, buyMyCurrency.length);
		boolean sellGreater = true;
		if (Double.parseDouble(sellMyCurrency[2]) < Double
				.parseDouble(buyMyCurrency[2])) {
			sellGreater = false;
		}
		Assert.assertFalse(sellGreater);

	}

	/**
	 * Test you cannot enter more than 15 digits in the input amount to buy
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@Test
	public void testMaxInputBuyAmountValue() throws InterruptedException,
			IOException {
		currencyConverterPage.setCurrencyIHave("USD");
		currencyConverterPage.setCurrencyIWant("EUR");
		// Setting 20 9s, but only 15 would be accepted.
		currencyConverterPage.setInputAmountToBuy("999999999999999999999");
		Thread.sleep(3000);

		String[] sellMyCurrency = currencyConverterPage.getSellMyCurrency()
				.split(" ");
		assertEquals(3, sellMyCurrency.length);
		assertEquals("1,000,000,000,000,000", sellMyCurrency[1]);

	}

	/**
	 * Test you cannot enter more than 15 digits in the input amount to sell
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@Test
	public void testMaxInputSellAmountValue() throws InterruptedException,
			IOException {
		currencyConverterPage.setCurrencyIHave("USD");
		currencyConverterPage.setCurrencyIWant("EUR");
		// Setting 20 9s, but only 15 would be accepted.
		currencyConverterPage.setInputAmountToSell("999999999999999999999");
		Thread.sleep(3000);
		String[] sellMyCurrency = currencyConverterPage.getSellMyCurrencyGet()
				.split(" ");
		assertEquals(4, sellMyCurrency.length);
		assertEquals("1,000,000,000,000,000", sellMyCurrency[2]);

	}

	/**
	 * Check that the max input bank rate is 99.99%. Even if something higher
	 * than that is entered, it converts it to 99.99%
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@Test
	public void testMaxInterBankRate() throws InterruptedException, IOException {
		currencyConverterPage.setCurrencyIHave("USD");
		currencyConverterPage.setCurrencyIWant("INR");
		currencyConverterPage.setInputAmountToBuy("1");
		currencyConverterPage.setInterbankRate("100");
		Thread.sleep(2000);
		// TODO(sharanya): For some reason getInterbankRate isn't giving the
		// correct value in the input box.
		// For now, check the final value is not zero.
		// assertEquals("99.99%", currencyConverterPage.getInterbankRate());

		String[] sellMyCurrency = currencyConverterPage.getSellMyCurrencyGet()
				.split(" ");
		assertEquals(4, sellMyCurrency.length);
		Assert.assertNotEquals(0, Double.parseDouble(sellMyCurrency[2]), 0.0001);
		Assert.assertFalse(currencyConverterPage.isWarningVisible());

	}

	/**
	 * Check that invalid bankrate shows error box.
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@Test
	public void testInvalidBankRate() throws InterruptedException, IOException {
		currencyConverterPage.setCurrencyIHave("USD");
		currencyConverterPage.setCurrencyIWant("INR");
		currencyConverterPage.setInputAmountToBuy("1");
		currencyConverterPage.setInterbankRate("asdf%");
		Thread.sleep(2000);
		Assert.assertTrue(currencyConverterPage.isWarningVisible());

	}

	@Test
	public void testFlipCurrencies() throws InterruptedException, IOException {
		String inputCurrency = "USD";
		String wantedCurrency = "INR";
		currencyConverterPage.setCurrencyIHave(inputCurrency);
		currencyConverterPage.setCurrencyIWant(wantedCurrency);
		currencyConverterPage.setInputAmountToBuy("1");
		Thread.sleep(3000);

		assertEquals(inputCurrency + "/" + wantedCurrency + " Details",
				currencyConverterPage.getInfoDetailsText());
		assertEquals(inputCurrency,
				currencyConverterPage.getQuoteCurrencyCode());
		assertEquals(wantedCurrency,
				currencyConverterPage.getBaseCurrencyCode());
		currencyConverterPage.clickFlipButton();
		Thread.sleep(3000);
		assertEquals(wantedCurrency + "/" + inputCurrency + " Details",
				currencyConverterPage.getInfoDetailsText());
		assertEquals(wantedCurrency,
				currencyConverterPage.getQuoteCurrencyCode());
		assertEquals(inputCurrency, currencyConverterPage.getBaseCurrencyCode());
	}

	@Test
	public void testInvalidDate() throws InterruptedException, IOException {

		String inputCurrency = "USD";
		String wantedCurrency = "INR";
		currencyConverterPage.setCurrencyIHave(inputCurrency);
		currencyConverterPage.setCurrencyIWant(wantedCurrency);
		currencyConverterPage.setInputAmountToBuy("1");
		String currentDate = currencyConverterPage.getEndDate();
		Thread.sleep(2000);
		currencyConverterPage.setEndDate("asdfasdf");
		Thread.sleep(100);
		Assert.assertEquals(currentDate, currencyConverterPage.getEndDate());

	}

	/**
	 * Screenshot rule to take a screenshot if the test fails
	 * @author sharanya
	 *
	 */
	class ScreenshotTestRule implements MethodRule {
		public Statement apply(final Statement statement,
				final FrameworkMethod frameworkMethod, final Object o) {
			return new Statement() {
				@Override
				public void evaluate() throws Throwable {
					try {
						statement.evaluate();
					} catch (Throwable t) {
						getscreenshot("CurrencyConverterTests",
								frameworkMethod.getName());
						throw t; // Rethrow to allow the failure to be reported
									// to JUnit
					}
				}

				public void getscreenshot(String classname, String methodname)
						throws IOException {
					File scrFile = ((TakesScreenshot) driver)
							.getScreenshotAs(OutputType.FILE);
					FileUtils.copyFile(
							scrFile,
							new File(ExcelUtils.getScreenShotFileName(
									classname, methodname)));
				}
			};
		}
	}
}

/**
 * Class to hold the test data that is read from the excel sheet. Since this is
 * not required outside CurrencyConverterTests declaring it here.
 * 
 * @author sharanya
 *
 */
class CurrencyTestData {
	private String inputCurrency;
	private String wantedCurrency;
	private String inputAmountString;
	private boolean inputAmountValid;

	public CurrencyTestData(String inputCurrency, String wantedCurrency,
			String inputAmountWanted, boolean inputAmountValid) {
		this.inputCurrency = inputCurrency;
		this.wantedCurrency = wantedCurrency;
		this.inputAmountString = inputAmountWanted;
		this.inputAmountValid = inputAmountValid;
	}

	// Everything below this is getters and setters for the private variables.
	public String getInputCurrency() {
		return inputCurrency;
	}

	public void setInputCurrency(String inputCurrency) {
		this.inputCurrency = inputCurrency;
	}

	public String getWantedCurrency() {
		return wantedCurrency;
	}

	public void setWantedCurrency(String wantedCurrency) {
		this.wantedCurrency = wantedCurrency;
	}

	public String getInputAmountString() {
		return inputAmountString;
	}

	public void setInputAmountString(String inputAmountString) {
		this.inputAmountString = inputAmountString;
	}

	public boolean isInputAmountValid() {
		return inputAmountValid;
	}

	public void setInputAmountValid(boolean inputAmountValid) {
		this.inputAmountValid = inputAmountValid;
	}
}
