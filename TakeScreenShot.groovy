package com.napir.katalon
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.checkpoint.CheckpointFactory
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testcase.TestCaseFactory
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testdata.TestDataFactory
import com.kms.katalon.core.testobject.ObjectRepository
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile

import org.apache.commons.lang3.StringUtils

import internal.GlobalVariable

import org.openqa.selenium.WebElement
import org.openqa.selenium.WebDriver
import org.openqa.selenium.By

import com.kms.katalon.core.mobile.keyword.internal.MobileDriverFactory
import com.kms.katalon.core.webui.driver.DriverFactory

import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty

import com.kms.katalon.core.mobile.helper.MobileElementCommonHelper
import com.kms.katalon.core.util.KeywordUtil

import com.kms.katalon.core.webui.exception.WebElementNotFoundException


class TakeScreenShot {
	/**
	 * ピクチャー取得処理 
	 * ①指定画像ピクチャーパスが存在しない場合指定パスに作成する
	 * ②指定画像ピクチャーパスが存在する場合比較ピクチャーを作成する
	 * @param keyWord keywordApi
	 * @param filpath ファイルパス
	 */
	public static final IMGE_PNG_SUFFIX = ".png"

	@Keyword
	def void takeScreenShot(Object keyWord, String testCase , String filName) {

		if (StringUtils.isEmpty(testCase) || StringUtils.isEmpty(filName) || keyWord == null) {
			throw new RuntimeException("the parameters is empty, please verify.")
		}
		String filPath = GlobalVariable.GENERATE_IMAGE_PATH + "source" + File.separator  + testCase + File.separator + filName + IMGE_PNG_SUFFIX;
		if (fileIsExists(filPath)) {
			filPath = GlobalVariable.GENERATE_IMAGE_PATH + testCase + File.separator + filName + IMGE_PNG_SUFFIX;
		}
		this.doScreenshot(keyWord, filPath)
	}

	def private void doScreenshot(Object keyWord, String filPath) {
		println filPath;
		println keyWord;
		println keyWord.is(Mobile);
		if(keyWord.is(WebUI)) {
			WebUI.takeScreenshot(filPath)
		}
		if (keyWord.is(Mobile)) {
			Mobile.takeScreenshot(filPath)
		}
		// TODO
		/**
		 * surfaceどうするの
		 if (keyWord instanceof  Windows) {
		 keyWord.
		 }
		 */
	}

	def private Boolean fileIsExists(String path) {
		if(!CommonUtils.isEmpty(path)) {
			File file = new File(path);
			if(file != null && file.isFile()) {
				return true;
			}
		}
		return false;
	}
}