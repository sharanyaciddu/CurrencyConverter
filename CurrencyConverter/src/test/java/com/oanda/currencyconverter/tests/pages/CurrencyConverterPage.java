package com.oanda.currencyconverter.tests.pages;

import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

/**
 * CurrencyConverter page object for http://www.oanda.com/currency/converter/
 * 
 * @author sharanya
 *
 */
public class CurrencyConverterPage {
	private WebDriver driver;
	public static String URL = "/currency/converter/";

	@FindBy(how = How.ID, using = "quote_currency_input")
	@CacheLookup
	private WebElement strQuotedCurrencyTextInput;

	@FindBy(how = How.ID, using = "base_currency_input")
	@CacheLookup
	private WebElement strBaseCurrencyTextInput;

	@FindBy(how = How.ID, using = "base_currency_input")
	@CacheLookup
	private WebElement input;

	@FindBy(how = How.ID, using = "infoDetails")
	@CacheLookup
	private WebElement strInfoDetailsText;

	@FindBy(how = How.ID, using = "quote_amount_input")
	@CacheLookup
	private WebElement strQuoteAmountInputText;

	@FindBy(how = How.ID, using = "base_amount_input")
	@CacheLookup
	private WebElement strBaseAmountInputText;

	@FindBy(how = How.ID, using = "sellMyCurrency")
	@CacheLookup
	private WebElement sellMyCurrencyLabel;

	@FindBy(how = How.XPATH, using = ".//*[@id='sellMyCurrencyGet']")
	@CacheLookup
	private WebElement strSellMyCurrencyGetLabel;

	@FindBy(how = How.XPATH, using = ".//*[@id='buyMyCurrencyCost']")
	@CacheLookup
	private WebElement strBuyMyCurrencyCostLabel;

	@FindBy(how = How.ID, using = "interbank_rates_input")
	@CacheLookup
	private WebElement interbankRate;

	@FindBy(how = How.ID, using = "flipper")
	@CacheLookup
	private WebElement flipperButton;

	@FindBy(how = How.ID, using = "quote_currency_code")
	@CacheLookup
	private WebElement quoteCurrencyCode;

	@FindBy(how = How.ID, using = "base_currency_code")
	@CacheLookup
	private WebElement baseCurrencyCode;

	@FindBy(how = How.ID, using = "end_date_input")
	@CacheLookup
	private WebElement endDateInput;

	public CurrencyConverterPage(WebDriver driver) {
		this.driver = driver;
	}

	/**
	 * Sets the value of the quoteCurrency input box. Once text is entered into
	 * the box, the narrowed down result is clicked on.
	 * 
	 * @param currency
	 */
	public void setCurrencyIHave(String currency) {
		strQuotedCurrencyTextInput.clear();
		strQuotedCurrencyTextInput.sendKeys(currency);
		// Check from the list of options in the drop down and click on the
		// correct one.
		WebElement quoteCurrencyDropdown = driver.findElement(By
				.xpath(".//*[@id='scroll-innerBox-1']"));
		List<WebElement> quoteCurrencyDropdownList = quoteCurrencyDropdown
				.findElements(By.xpath(".//span[@class='code_right']"));

		for (WebElement e : quoteCurrencyDropdownList) {
			if (e.getText().equals(currency)) {
				e.click();
				break;
			}
		}
	}

	/**
	 * Sets the value of the baseCurrency input box. Once text is entered into
	 * the box, the narrowed down result is clicked on.
	 * 
	 * @param currency
	 */
	public void setCurrencyIWant(String currency) {
		strBaseCurrencyTextInput.clear();
		strBaseCurrencyTextInput.sendKeys(currency);
		// Check from the list of options in the drop down and click on the
		// correct one.
		WebElement baseCurrencyDropdown = driver.findElement(By
				.xpath(".//*[@id='scroll-innerBox-2']"));
		List<WebElement> baseCurrencyDropdownList = baseCurrencyDropdown
				.findElements(By.xpath(".//span[@class='code_right']"));

		for (WebElement e : baseCurrencyDropdownList) {
			if (e.getText().equals(currency)) {
				e.click();
				break;
			}
		}
	}

	/**
	 * Sets the input amount to buy.
	 * 
	 * @param input
	 */
	public void setInputAmountToBuy(String input) {
		strQuoteAmountInputText.clear();
		strQuoteAmountInputText.sendKeys(input);
	}

	/**
	 * Sets the input amount to sell.
	 * 
	 * @param input
	 */
	public void setInputAmountToSell(String input) {
		strBaseAmountInputText.clear();
		strBaseAmountInputText.sendKeys(input);
	}

	/**
	 * Gets the info details
	 * 
	 * @return
	 */
	public String getInfoDetailsText() {
		return strInfoDetailsText.getText();
	}

	/**
	 * Gets the myCurrency label
	 * 
	 * @return
	 */
	public String getSellMyCurrencyGet() {
		return strSellMyCurrencyGetLabel.getText();
	}

	/**
	 * Gets the buyMyCurrencyCost label
	 * 
	 * @return
	 */
	public String getBuyMyCurrencyCostLabel() {
		return strBuyMyCurrencyCostLabel.getText();
	}

	/**
	 * Get sellMyCurrency label value.
	 * 
	 * @return
	 */
	public String getSellMyCurrency() {
		return sellMyCurrencyLabel.getText();
	}

	/**
	 * Set the interbank rate.
	 * 
	 * @param rate
	 */
	public void setInterbankRate(String rate) {
		interbankRate.click();
		interbankRate.clear();
		interbankRate.sendKeys(rate);
		sellMyCurrencyLabel.click();
	}

	/**
	 * Gets the interbank rate.
	 * 
	 * @return
	 */
	public String getInterbankRate() {
		return interbankRate.getText();
	}

	/**
	 * Checks if the date annotation warning box exists.
	 * 
	 * @return
	 */
	public boolean isWarningVisible() {
		return isElementPresent(By
				.cssSelector("#date_annotation > div.annotation_content > p"));
	}

	/**
	 * Checks if an element is present in the page or not.
	 * 
	 * @param by
	 * @return
	 */
	private boolean isElementPresent(By by) {
		try {
			return driver.findElement(by).isDisplayed();
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	/**
	 * Click the flip button.
	 */
	public void clickFlipButton() {
		flipperButton.click();
	}

	/**
	 * Get the value of quote_currency_code
	 * 
	 * @return
	 */
	public String getQuoteCurrencyCode() {
		return quoteCurrencyCode.getText();
	}

	/**
	 * Get the value of base_currency_code
	 * 
	 * @return
	 */
	public String getBaseCurrencyCode() {
		return baseCurrencyCode.getText();
	}
	
	/**
	 * Gets the end date
	 * @return
	 */
	public String getEndDate() {
		return endDateInput.getText();
	}
	
	/**
	 * Sets the end date
	 * @param date
	 */
	public void setEndDate(String date) {
		endDateInput.clear();
		endDateInput.sendKeys(date);
	}
}
