package com.napir.katalon

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import com.kms.katalon.core.annotation.Keyword
import com.napir.katalon.dto.DiffDBCsvDto
import com.napir.katalon.dto.DiffPdfDto
import com.testautomationguru.utility.CompareMode
import com.testautomationguru.utility.PDFUtil

import groovy.io.FileType
import internal.GlobalVariable
import io.cucumber.datatable.dependency.difflib.Delta
import io.cucumber.datatable.dependency.difflib.DiffUtils as diffUtil
import io.cucumber.datatable.dependency.difflib.Patch
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;
/**
 * 	ツールを比較し、異なるファイルを比較します 
 * @author byrson
 *
 */
public class DiffUtils {

	/**
	 * CustomKeywords.'com.napir.katalon.FileUtils.compareWithCsv'(GlobalVariable.GENERATE_FILE_PATH + File.separator + 'source', GlobalVariable.GENERATE_FILE_PATH, ['AUTOTEST002'])
	 * csvファイルを比較して、htmlを生成します 
	 * @param sourcePath ファイル path1
	 * @param comparePath ファイル path2
	 * @param testIds fileName
	 * @return
	 */
	@Keyword
	public static compareWithCsv(String sourcePath, String comparePath) {

		File sourceDir = new File(sourcePath)

		if(sourceDir.exists()) {
			List<DiffDBCsvDto> diffCsvDtoList = new ArrayList<DiffDBCsvDto>()

			Map<String,QuerySql> sqlMap = QuerySql.getQuerySqlMap();

			sourceDir.eachFileRecurse(FileType.FILES) { sourceFile ->
				List<String> sList = new ArrayList<>();
				List<String> cList = new ArrayList<>();

				String fileNmane = sourceFile.getName();

				String htmlName = "dif_" + fileNmane.substring(0, fileNmane.indexOf("."));

				DiffDBCsvDto diffDto = new DiffDBCsvDto();

				diffCsvDtoList.add(diffDto);

				diffDto.setPhysicalName(fileNmane.substring(0, fileNmane.indexOf(".")));

				QuerySql querySql =sqlMap.get(fileNmane.substring(0, fileNmane.indexOf(".")))

				if (null != querySql) {
					diffDto.setLogicalName(querySql.getLogicalName())
				}

				diffDto.setTbCsvUrl("./"+ htmlName+".html")

				File compare = new File(comparePath + File.separator + fileNmane)
				if(compare.exists() && compare.isFile()) {
					BufferedInputStream sourceBis = new BufferedInputStream(new FileInputStream(sourceFile))
					List<String> orgSourceList = sourceBis.readLines("UTF-8")
					println orgSourceList
					sList.addAll(orgSourceList)
					BufferedInputStream compareBis = new BufferedInputStream(new FileInputStream(compare))
					List<String> orgCompareList = compare.readLines("UTF-8")
					println orgCompareList
					cList.addAll(orgCompareList)

					Patch patch = diffUtil.diff(sList, cList)
					List<Map<String, String>> diffList = new ArrayList<>();
					for(Delta delta: patch.getDeltas()) {
						List<String> sourceList = new ArrayList<String>();
						List<String> compareList = new ArrayList<String>();
						int row = delta.getOriginal().getPosition()

						if(!delta.getOriginal().getLines().isEmpty()) {
							sourceList = Arrays.asList(delta.getOriginal().getLines().get(0).split(","));
						}

						if (!delta.getRevised().getLines().isEmpty()) {
							compareList = Arrays.asList(delta.getRevised().getLines().get(0).split(","));
						}

						// col 判断
						Patch childPatch = diffUtil.diff(sourceList, compareList)
						for(Delta cDelta: childPatch.getDeltas()) {
							cDelta.getOriginal().getLines().eachWithIndex {child,ind ->
								Map<String, String> objMap = new HashMap<>();
								int col = cDelta.getOriginal().getPosition() + 1
								objMap.put("row", row.toString())
								objMap.put("col", (col+ind).toString())
								objMap.put("oldVal", child.toString());
								objMap.put("val", cDelta.getRevised().getLines().size() > ind ? cDelta.getRevised().getLines().get(ind).toString() :"");
								diffList.add(objMap)
							}
						}
					}
					if (!diffList.isEmpty()) {
						diffDto.setIsDiffFlg(true);
					}

					FileUtils.generateCsvHTML(sourcePath, htmlName, sList, cList, diffList);
				}
			}

			FileUtils.generateDbCsvIndexHtml(diffCsvDtoList, sourcePath)
		}else {
			println "source is not found, check disk and verify the file exists"
		}
	}
	/*
	 @Keyword
	 public static compareWithCsv(String sourcePath, String comparePath, List testIds) {
	 testIds.each { testId ->
	 File sourceDir = new File(sourcePath + File.separator + testId)
	 if(sourceDir.exists()) {
	 List<String> sList = new ArrayList<>();
	 List<String> cList = new ArrayList<>();
	 sourceDir.eachFileRecurse(FileType.FILES) { sourceFile ->
	 File compare = new File(comparePath + File.separator + testId + File.separator + sourceFile.getName())
	 if(compare.exists() && compare.isFile()) {
	 BufferedInputStream sourceBis = new BufferedInputStream(new FileInputStream(sourceFile))
	 List<String> sourceList = sourceBis.readLines("UTF-8")
	 println sourceList
	 sList.addAll(sourceList)
	 BufferedInputStream compareBis = new BufferedInputStream(new FileInputStream(compare))
	 List<String> compareList = compare.readLines("UTF-8")
	 println compareList
	 cList.addAll(compareList)
	 }
	 }
	 Patch patch = diffUtil.diff(sList, cList)
	 List<Map<String, String>> diffList = new ArrayList<>();
	 for(Delta delta: patch.getDeltas()) {
	 int row = delta.getOriginal().getPosition()
	 List<String> sourceList = Arrays.asList(delta.getOriginal().getLines().get(0).split(","));
	 List<String> compareList = Arrays.asList(delta.getRevised().getLines().get(0).split(","));
	 // col 判断
	 Patch childPatch = diffUtil.diff(sourceList, compareList)
	 for(Delta cDelta: childPatch.getDeltas()) {
	 cDelta.getOriginal().getLines().eachWithIndex {child,ind ->
	 Map<String, String> objMap = new HashMap<>();
	 int col = cDelta.getOriginal().getPosition() + 1
	 objMap.put("row", row.toString())
	 objMap.put("col", (col+ind).toString())
	 objMap.put("oldVal", child.toString());
	 objMap.put("val", cDelta.getRevised().getLines().get(ind).toString());
	 diffList.add(objMap)
	 }
	 }
	 }
	 FileUtils.generateCsvHTML(comparePath, "res_" + testId, sList, cList, diffList);
	 }else {
	 println "source is not found, check disk and verify the file exists"
	 }
	 }
	 }
	 */
	/**
	 * CustomKeywords.'com.napir.katalon.FileUtils.compareWithPdf'(sourcePath + 'source', comparePath, 'T1. AWS_EBS扩容')
	 *PDFファイルを比較してから、HTMLを生成します 
	 * @param sourcePath ファイル path1
	 * @param comparePath ファイル path2
	 * @param testIds fileName
	 */
	@Keyword
	public static void compareWithPdf(String sourcePath, String comparePath, List testIds) {
		if(CommonUtils.isEmpty(sourcePath) || CommonUtils.isEmpty(comparePath) || CommonUtils.isEmpty(testIds)) {
			throw new RuntimeException("the parameters is empty, please verify.")
		}

		List<DiffPdfDto> handleList = new ArrayList<>();
		PDFUtil pdfUtil = new PDFUtil();
		// MODE pixel compare
		pdfUtil.setCompareMode(CompareMode.VISUAL_MODE)
		// ファイルを保存し、違いを強調表示します
		pdfUtil.highlightPdfDifference(true);
		// すべてのページを比較する
		pdfUtil.compareAllPages(true);

		// ループトラバーサル
		testIds.each{ testId ->
			File sourceDir = new File(sourcePath + File.separator + testId)
			if(sourceDir.exists()) {
				sourceDir.eachFileRecurse(FileType.FILES) { sourceFile ->
					File compare = new File(comparePath + File.separator + testId + File.separator + sourceFile.getName())
					if(compare.exists() && compare.isFile()) {
						List<String> imageList = new ArrayList<>();
						DiffPdfDto dp = new DiffPdfDto();
						String strDifImgDir = sourcePath + File.separator + "Diff"+ File.separator + testId;
						File difImgDir = new File(strDifImgDir);
						if (!difImgDir.exists()) {
							difImgDir.mkdirs();
						}
						pdfUtil.setImageDestinationPath(strDifImgDir);
						Boolean isDiff = pdfUtil.compare(sourceFile.getAbsolutePath(), compare.getAbsolutePath())
						if(!isDiff) {
							int count = 1;
							Boolean isEnd = false;
							while(!isEnd) {
								File img = new File(sourcePath + File.separator + "Diff" + File.separator + testId+ File.separator + sourceFile.getName() + count++ + "_diff.png");
								if(img.exists() && img.isFile()) {
									imageList.add("." + File.separator + "Diff" + File.separator + testId + File.separator + img.getName());
								}else {
									isEnd = true;
								}
							}
						}
						String sourceFileAbsolutepath = sourceFile.getAbsolutePath();

						String comparFileAbsolutePath = compare.getAbsolutePath();

						dp.setSourcePath("." + File.separator + testId + File.separator + sourceFile.getName());
						dp.setComparePath(".."+ File.separator + GlobalVariable.COMPARE_DATE + File.separator + testId + File.separator + sourceFile.getName());
						dp.setTestId(testId);
						dp.setPdfId(sourceFile.getName());
						dp.setIsDiff(isDiff);
						dp.setImageList(imageList);
						handleList.add(dp);
					}
				}
			}
		}

		if(handleList.size() > 0) {
			FileUtils.generatePdfHtml(handleList, sourcePath);
			return
		}
		println "pdf html will not generate"
	}

	/**
	 * 	 スクリーンショットの違いを比較する 
	 *
	 * @param expectedPath (sourcepath)
	 * @param path (comparePath)
	 * @param testIds スクリーンショットのテストID（サブパス）を比較します 
	 *
	 */
	public static compareWithImage(String expectedPath, String path, List testIds) {

		// スクリーンショット比較情報リスト
		def list = []
		// テストID（サブパス）ループ
		testIds.each { testId ->
			// スクリーンショットのパス
			def dir = new File(path + File.separator + testId)
			// スクリーンショットファイルループ
			dir.eachFileRecurse(FileType.FILES) { file ->
				// 比較情報マップ
				def data = [:]
				// テストID
				data.put('testId', testId)
				println file
				// スクリーンショットID
				//				def screenshotId = file.getName().split(File.separator);
				def screenshotId = file.getName();
				println screenshotId
				data.put('screenshotId', screenshotId)
				// ベンチマークのスクリーンショットを読む
				println expectedPath + File.separator + testId + File.separator + screenshotId
				BufferedImage expectedImage = ImageIO.read(new File(expectedPath + File.separator + testId + File.separator + screenshotId));
				//				data.put('expectedImage', expectedPath + File.separator + testId + File.separator + screenshotId)
				data.put('expectedImage', '.' + File.separator + testId + File.separator + screenshotId)
				// スクリーンショットを読む
				println path + File.separator + testId + File.separator + screenshotId
				BufferedImage actualImage = ImageIO.read(new File(path + File.separator + testId + File.separator + screenshotId));
				//				data.put('actualImage', path + File.separator + testId + File.separator + screenshotId)
				println(expectedPath.substring(expectedPath.lastIndexOf(File.separator)))
				data.put('actualImage', '..' +path.substring(path.lastIndexOf(File.separator)) + File.separator + testId + File.separator + screenshotId)
				// 比較開始
				ImageDiffer imgDiff = new ImageDiffer();
				ImageDiff diff = imgDiff.makeDiff(expectedImage, actualImage);
				// 違いがある場合
				if(diff.hasDiff()) {
					// 差のパーセンテージを計算する
					int diffSize = diff.getDiffSize();
					int area = diff.getMarkedImage().getWidth() * diff.getMarkedImage().getHeight();
					Double diffRatio = diffSize / area * 100;
					BigDecimal bd = new BigDecimal(diffRatio);
					BigDecimal bdUp = bd.setScale(2, BigDecimal.ROUND_UP);
					data.put('diffPercent', bdUp.doubleValue());
				} else { // 不存在差异
					data.put('diffPercent', 0.0);
				}
				// 比較画像の生成
				BufferedImage diffImage = diff.getMarkedImage();
				println expectedPath + File.separator + testId + '_DIFF' + File.separator + 'DIFF_' + screenshotId
				def newDir = new File(expectedPath + File.separator + testId + '_DIFF');
				newDir.mkdir();
				ImageIO.write(diffImage, "PNG", new File(expectedPath + File.separator + testId + '_DIFF' + File.separator + 'DIFF_' + screenshotId));
				//				data.put('diffImage', path + File.separator + testId + '_DIFF' + File.separator + 'DIFF_' + screenshotId)
				data.put('diffImage', '.' + File.separator + testId + '_DIFF' + File.separator + 'DIFF_' + screenshotId)

				list.add(data)
			}
		}
		// 差分リストHTML生成
		FileUtils.generateImageHTML(list, expectedPath)
	}
}
