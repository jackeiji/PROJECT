package com.napir.katalon

import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.napir.katalon.dto.DiffDBCsvDto
import com.napir.katalon.dto.DiffPdfDto

import groovy.xml.MarkupBuilder
import internal.GlobalVariable

/**
 * 	ファイルツール、htmlおよび対応するフォーマットファイルの生成など。 
 * @author byrson
 *
 */
public class FileUtils {

	// image diff カバレッジ率
	private static final Integer PERCENT_OF_OVER = 3;

	// down load suffix
	private static final String DOWNLOADING_SUFFIX1 = ".crdownload";

	// down load suffix
	private static final String DOWNLOADING_SUFFIX2 = ".tmp";

	// down load delay
	private static final Integer DOWNLOADING_DELAY = 30;

	/**
	 * 	CSVファイルを生成します。 
	 * @param path 
	 * @param content コンテンツ 
	 * @return
	 */
	public static generateCSV(String path,String name, String content) {
		File file = new File(path + File.separator + name + '.csv')
		if(!file.parentFile.exists()) {
			file.parentFile.mkdirs()
		}
		BufferedOutputStream fos = null
		try {
			fos = new BufferedOutputStream(new FileOutputStream(file));
			fos.write(0xef);
			fos.write(0xbb);
			fos.write(0xbf);
			fos.write(content.getBytes("UTF-8"));
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}finally {
			if(fos != null) {
				fos.close();
			}
		}
	}

	/**
	 * 	CSVファイルを生成します。
	 * @param path
	 * @param content コンテンツ
	 * @return
	 */
	public static generateCSV(String path, String content) {
		File file = new File(path + ".csv")
		if(!file.parentFile.exists()) {
			println file.parentFile.mkdirs()

		}
		BufferedOutputStream fos = null
		try {
			fos = new BufferedOutputStream(new FileOutputStream(file));
			fos.write(0xef);
			fos.write(0xbb);
			fos.write(0xbf);
			fos.write(content.getBytes("UTF-8"));
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}finally {
			if(fos != null) {
				fos.close();
			}
		}
	}

	/**
	 * CustomKeywords.'com.napir.katalon.ImageDiffUtils.ScreenInit'(GlobalVariable.GENERATE_IMAGE_PATH, 'AUTOTEST002')
	 *
	 *	全画面画像キャプチャ、画像比較のためにパスに保存 
	 * @param path ビルドパス 
	 * @param name ファイル名 
	 * @param numBegin numBegin番号で開始 
	 * @return numBegin结果数
	 */
	@Keyword
	public static Integer generateScreenShot(String path, String name, Integer numBegin = 0) {
		if(null == numBegin) {
			numBegin = 0
		}
		if(CommonUtils.isEmpty(name)) {
			name = "noneof";
		}
		File file = new File(path + File.separator + name) ;
		if(!file.exists()) {
			file.mkdir();
		}
		WebUI.maximizeWindow();
		Integer contentHeight = WebUI.getPageHeight() - 106 - 70
		println("contentHeight："+contentHeight)
		String js = "return document.getElementsByClassName('div-scroll main-body')[0].scrollHeight"
		Integer scrollHeight = WebUI.executeJavaScript(js, null)
		println("scrollHeight："+scrollHeight)
		Integer count = Math.ceil(scrollHeight / contentHeight)
		Integer ret = 0;
		for(int i=0;i<count;i++) {
			String scrollJs = "document.getElementsByClassName('div-scroll main-body')[0].scrollTop = " +contentHeight*i
			WebUI.executeJavaScript(scrollJs, null)
			WebUI.takeScreenshot(file.getAbsolutePath() + File.separator +name + "_" + (i+numBegin) + ".png");
			if (i == count - 1) {
				ret = i + numBegin + 1;
			}
		}
		println(name + "--> screen shoot is complete")
		return ret;
	}

	/**
	 * 	Htmlファイルを生成します。 
	 * @param results
	 * @param path
	 * @return
	 */
	public static generateCsvHTML(String path, String name, List<String> sList, List<String> cList, List<Map<String, String>> diffList) {
		def sw = new StringWriter();
		def html = new MarkupBuilder(sw);

		List<String> sheaderList = Arrays.asList(sList.get(0).split(","));
		List<String> cheaderList = Arrays.asList(cList.get(0).split(","));
		sList.remove(0)
		cList.remove(0)

		// HTML生成
		html.html {
			// Head
			head {
				meta (charset:'UTF-8')
				meta('http-equiv':"X-UA-Compatible", content:"IE=edge")
				meta(name:"viewport", content:"width=device-width, initial-scale=1.0")
				title('Screen shot image diff result')
				style {
					mkp.yield( "@import url('https://fonts.googleapis.com/css?family=Assistant'); " )
					mkp.yield( "body { background: #eee; font-family: Assistant, sans-serif; font-size: 14px; } " )
					mkp.yield( ".cell-1 { border-collapse: separate; border-spacing: 0 4em; background: #ffffff; border-bottom: 5px solid transparent; background-clip: padding-box; cursor: pointer }" )
					mkp.yield( "thead { background: #dddcdc }" )
				}
				script (type:"text/javascript", src:"https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js") {
					mkp.yield( " " )
				}
				link (rel:"stylesheet", href:"https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css") {
				}
				script (type:"text/javascript", src:"https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.bundle.min.js") {
					mkp.yield( " " )
				}
				link (rel:"shortcut icon", href:"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css") {
				}
				script(type: "text/javascript"){
					mkp.yield( "window.onload=function(){var l=document.querySelector('#stable');var r=document.querySelector('#ctable');var scale=(l.scrollHeight-l.clientHeight)/(r.scrollHeight-r.clientHeight);var flag=true;l.addEventListener('mouseover',function(e){flag=false;l.addEventListener('scroll',function(e){if(!flag){r.scrollTop=l.scrollTop;r.scrollLeft=l.scrollLeft}})});r.addEventListener('mouseover',function(e){flag=true;r.addEventListener('scroll',function(e){if(flag){l.scrollTop=r.scrollTop;l.scrollLeft=r.scrollLeft}})})};" )
				}
			}
			// Body
			body {
				div(class:"container-fluid mt-5") {
					div(class:"col-md-12"){
						ul{
							diffList.each{ diff ->
								li{
									mkp.yield("row: "+ diff.get("row") + ", col: " + diff.get("col") + ", " + diff.get("oldVal") + " to " + diff.get("val"))
								}
							}
						}
					}
					div(class:"d-flex justify-content-center") {
						// source table
						div(class:"col-md-6") {
							div(class:"rounded") {
								div(id:"stable", class:"table-responsive ", style:"max-height: 80vh") {
									span{
										mkp.yield( "source" )
									}
									table(class:"table table-striped") {
										thead {
											tr {
												th(class:"text-center", style:"") {
													mkp.yield("INDEX")
												}
												sheaderList.each{ header ->
													th(class:"text-center", style:"") {
														mkp.yield(header)
													}
												}
											}
										}
										tbody(class:"table-body") {
											sList.eachWithIndex { source,row ->
												tr(class:"cell-1") {
													td(class:"text-center", style:"") {
														mkp.yield(row+1)
													}
													Arrays.asList(source.split(",")).eachWithIndex { tdSou,col ->
														(null == diffList.find {diff->diff.get("row").equals((row+1).toString()) && diff.get("col").equals((col+1).toString())}) ?
														td(class:"text-center", style:"") {
															mkp.yield(tdSou)
														}:td(class:"text-center", style:"background-color:red;color: #fff") {
															mkp.yield(tdSou)
														}
													}
												}
											}
										}
									}
								}
							}
						}
						// compare table
						div(class:"col-md-6") {
							div(class:"rounded") {
								div(id:"ctable", class:"table-responsive ", style:"max-height: 80vh") {
									span{
										mkp.yield( "compare" )
									}
									table(class:"table table-striped") {
										thead {
											tr {
												th(class:"text-center", style:"") {
													mkp.yield("INDEX")
												}
												cheaderList.each{ header ->
													th(class:"text-center", style:"") {
														mkp.yield(header)
													}
												}
											}
										}
										tbody(class:"table-body") {
											cList.eachWithIndex { compare,row ->
												tr(class:"cell-1") {
													td(class:"text-center", style:"") {
														mkp.yield(row+1)
													}
													Arrays.asList(compare.split(",")).eachWithIndex { tdCom,col ->
														(null == diffList.find {diff->diff.get("row").equals((row+1).toString()) && diff.get("col").equals((col+1).toString())}) ?
														td(class:"text-center", style:"") {
															mkp.yield(tdCom)
														}:td(class:"text-center", style:"background-color:red;color: #fff") {
															mkp.yield(tdCom)
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		def f = new File(path + File.separator + name + ".html")
		f.write(sw.toString())
	}

	/**
	 * 	生成pdf diff html
	 * @return
	 */
	public static generatePdfHtml(List<DiffPdfDto> handleList, String path) {

		def sw = new StringWriter();
		def html = new MarkupBuilder(sw);

		def sysdate = CommonUtils.getSysDate();
		// HTML
		html.html {
			// Head
			head {
				meta (charset:'UTF-8')
				meta('http-equiv':"X-UA-Compatible", content:"IE=edge")
				meta(name:"viewport", content:"width=device-width, initial-scale=1.0")
				title('Screen shot image diff result')
				style {
					mkp.yield( "@import url('https://fonts.googleapis.com/css?family=Assistant'); " )
					mkp.yield( "body { background: #eee; font-family: Assistant, sans-serif; font-size: 14px; } " )
					mkp.yield( ".cell-1 { border-collapse: separate; border-spacing: 0 4em; background: #ffffff; border-bottom: 5px solid transparent; background-clip: padding-box; cursor: pointer }" )
					mkp.yield( "thead { background: #dddcdc }" )
					mkp.yield( ".table-elipse { cursor: pointer }" )
					mkp.yield( "#demo { -webkit-transition: all 0.3s ease-in-out; -moz-transition: all 0.3s ease-in-out; -o-transition: all 0.3s 0.1s ease-in-out; transition: all 0.3s ease-in-out }" )
					mkp.yield( ".row-child { background-color: #000; color: #fff }" )
					mkp.yield( ".image { height: 200px; width: 400px; }" )
					mkp.yield( ".image1 { height: 100px; width: 200px; }" )
				}
				script (type:"text/javascript", src:"https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js") {
					mkp.yield( " " )
				}
				link (rel:"stylesheet", href:"https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css") {
				}
				script (type:"text/javascript", src:"https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.bundle.min.js") {
					mkp.yield( " " )
				}
				link (rel:"shortcut icon", href:"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css") {
				}
			}
			// Body
			body {
				div(class:"container mt-5") {
					div(class:"d-flex justify-content-center row") {
						div(class:"col-md-20") {
							div(class:"rounded") {
								div(class:"table-responsive table-borderless") {
									table(class:"table") {
										thead {
											tr {
												th(class:"text-center", style:"width: 80px;") {
													mkp.yield( "番号" )
												}
												th(style:"width: 150px;") {
													mkp.yield( "テストケースID" )
												}
												th(style:"width: 150px;") {
													mkp.yield( "PdfID" )
												}
												th(style:"width: 100px;") {
													mkp.yield( "比較結果" )
												}
												th(style:"width: 180px;") {
													mkp.yield( "時間" )
												}
												th {
												}
											}
										}
										tbody(class:"table-body") {
											def count = 0
											handleList.eachWithIndex { d,ind ->
												tr(class:"cell-1", 'data-toggle':"collapse", 'data-target':"#demo" + count) {
													td(class:"text-center") {
														mkp.yield( count + 1 )
													}
													td {
														mkp.yield( d.getTestId() )
													}
													td {
														mkp.yield( d.getPdfId() )
													}
													td {
														if (!d.getIsDiff()) {
															span(class:"badge badge-danger") {
																mkp.yield( "Failed" )
															}
														} else {
															span(class:"badge badge-success") {
																mkp.yield( "Success" )
															}
														}
													}
													td {
														mkp.yield( sysdate )
													}
													td(class:"table-elipse", 'data-toggle':"collapse", 'data-target':"#demo") {
														i(class:"fa fa-ellipsis-h text-black-50") {
														}
													}
												}
												tr(id:"demo" + count, class:"collapse cell-1") {
													td(colspan:"4") {
														a(href:d.getSourcePath(), target:"_blank"){
															mkp.yield( "source pdf" )
														}
													}
													td(colspan:"4") {
														a(href:d.getComparePath(), target:"_blank"){
															mkp.yield( "compare pdf" )
														}
													}
												}
												d.getImageList().each { ig ->
													tr(id:"demo" + count, class:"collapse cell-1") {
														td(colspan:"8") {
															img(class:"img-thumbnail", style:"width:650px",src:ig)
														}
													}

												}
												count++
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		def f = new File(path + File.separator + "diffPdf.html")
		f.write(sw.toString())
	}

	/**
	 * DBCSVIndexhtml作成処理
	 * @param handleList
	 * @param path
	 * @return
	 */
	public static generateDbCsvIndexHtml(List<DiffDBCsvDto> handleList, String path) {

		def sw = new StringWriter();
		def html = new MarkupBuilder(sw);

		def sysdate = CommonUtils.getSysDate();
		// HTML
		html.html {
			// Head
			head {
				meta (charset:'UTF-8')
				meta('http-equiv':"X-UA-Compatible", content:"IE=edge")
				meta(name:"viewport", content:"width=device-width, initial-scale=1.0")
				title('Screen shot image diff result')
				style {
					mkp.yield( "@import url('https://fonts.googleapis.com/css?family=Assistant'); " )
					mkp.yield( "body { background: #eee; font-family: Assistant, sans-serif; font-size: 14px; } " )
					mkp.yield( ".cell-1 { border-collapse: separate; border-spacing: 0 4em; background: #ffffff; border-bottom: 5px solid transparent; background-clip: padding-box; cursor: pointer }" )
					mkp.yield( "thead { background: #dddcdc }" )
					mkp.yield( ".table-elipse { cursor: pointer }" )
					mkp.yield( "#demo { -webkit-transition: all 0.3s ease-in-out; -moz-transition: all 0.3s ease-in-out; -o-transition: all 0.3s 0.1s ease-in-out; transition: all 0.3s ease-in-out }" )
					mkp.yield( ".row-child { background-color: #000; color: #fff }" )
					mkp.yield( ".image { height: 200px; width: 400px; }" )
					mkp.yield( ".image1 { height: 100px; width: 200px; }" )
				}
				script (type:"text/javascript", src:"https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js") {
					mkp.yield( " " )
				}
				link (rel:"stylesheet", href:"https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css") {
				}
				script (type:"text/javascript", src:"https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.bundle.min.js") {
					mkp.yield( " " )
				}
				link (rel:"shortcut icon", href:"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css") {
				}
			}
			// Body
			body {
				div(class:"container mt-5") {
					div(class:"d-flex justify-content-center row") {
						div(class:"col-md-20") {
							div(class:"rounded") {
								div(class:"table-responsive table-borderless") {
									table(class:"table") {
										thead {
											tr {
												th(class:"text-center", style:"width: 80px;") {
													mkp.yield( "番号" )
												}
												th(style:"width: 150px;") {
													mkp.yield( "テーブル物理名" )
												}
												th(style:"width: 150px;") {
													mkp.yield( "テーブル論理名" )
												}
												th(style:"width: 100px;") {
													mkp.yield( "比較結果" )
												}
												th(style:"width: 180px;") {
													mkp.yield( "時間" )
												}
												th {
												}
											}
										}
										tbody(class:"table-body") {
											def count = 0
											handleList.eachWithIndex { d,ind ->
												tr(class:"cell-1") {
													td(class:"text-center") {
														mkp.yield( count + 1 )
													}
													td {
														a(href:d.getTbCsvUrl(), target:"_blank"){
															mkp.yield( d.getPhysicalName())
														}
													}
													td {
														mkp.yield( d.getLogicalName() )
													}
													td {
														if (d.getIsDiffFlg()) {
															span(class:"badge badge-danger") {
																mkp.yield( "Failed" )
															}
														} else {
															span(class:"badge badge-success") {
																mkp.yield( "Success" )
															}
														}
													}
													td {
														mkp.yield( sysdate )
													}
												}
												count++
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		def f = new File(path + File.separator + "index.html")
		f.write(sw.toString())
	}

	/**
	 *	 差分リストHTML生成画像 diff html 
	 * @param results 相違点情報 
	 * @param path パス
	 *
	 */
	public static generateImageHTML(List results, String path) {

		def sw = new StringWriter();
		def html = new MarkupBuilder(sw);

		def sysdate = CommonUtils.getSysDate();
		// HTML
		html.html {
			// Head
			head {
				meta (charset:'UTF-8')
				meta('http-equiv':"X-UA-Compatible", content:"IE=edge")
				meta(name:"viewport", content:"width=device-width, initial-scale=1.0")
				title('Screen shot image diff result')
				style {
					mkp.yield( "@import url('https://fonts.googleapis.com/css?family=Assistant'); " )
					mkp.yield( "body { background: #eee; font-family: Assistant, sans-serif; font-size: 14px; } " )
					mkp.yield( ".cell-1 { border-collapse: separate; border-spacing: 0 4em; background: #ffffff; border-bottom: 5px solid transparent; background-clip: padding-box; cursor: pointer }" )
					mkp.yield( "thead { background: #dddcdc }" )
					mkp.yield( ".table-elipse { cursor: pointer }" )
					mkp.yield( "#demo { -webkit-transition: all 0.3s ease-in-out; -moz-transition: all 0.3s ease-in-out; -o-transition: all 0.3s 0.1s ease-in-out; transition: all 0.3s ease-in-out }" )
					mkp.yield( ".row-child { background-color: #000; color: #fff }" )
					mkp.yield( ".image { height: 200px; width: 400px; }" )
					mkp.yield( ".image1 { height: 100px; width: 200px; }" )
				}
				script (type:"text/javascript", src:"https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js") {
					mkp.yield( " " )
				}
				link (rel:"stylesheet", href:"https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css") {
				}
				script (type:"text/javascript", src:"https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.bundle.min.js") {
					mkp.yield( " " )
				}
				link (rel:"shortcut icon", href:"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css") {
				}
			}
			// Body
			body {
				div(class:"container mt-5") {
					div(class:"d-flex justify-content-center row") {
						div(class:"col-md-20") {
							div(class:"rounded") {
								div(class:"table-responsive table-borderless") {
									table(class:"table") {
										thead {
											tr {
												th(class:"text-center", style:"width: 80px;") {
													mkp.yield( "番号" )
												}
												th(style:"width: 150px;") {
													mkp.yield( "テストケースID" )
												}
												th(style:"width: 150px;") {
													mkp.yield( "ScreenshotID" )
												}
												th(style:"width: 100px;") {
													mkp.yield( "比較結果" )
												}
												th(style:"width: 220px;") {
													mkp.yield( "不同点のパーセント（％）" )
												}
												th(style:"width: 200px;") {
													mkp.yield( "不同点のインメージ" )
												}
												th(style:"width: 180px;") {
													mkp.yield( "時間" )
												}
												th {
												}
											}
										}
										tbody(class:"table-body") {
											def count = 0
											results.each { d ->
												tr(class:"cell-1", 'data-toggle':"collapse", 'data-target':"#demo" + count) {
													td(class:"text-center") {
														mkp.yield( count + 1 )
													}
													td {
														mkp.yield( d.get('testId') )
													}
													td {
														mkp.yield( d.get('screenshotId') )
													}
													td {
														if (d.get('diffPercent') >= PERCENT_OF_OVER) {
															span(class:"badge badge-danger") {
																mkp.yield( "Failed" )
															}
														} else {
															span(class:"badge badge-success") {
																mkp.yield( "Success" )
															}
														}
													}
													td {
														mkp.yield( d.get('diffPercent') )
													}
													td {
														img(class:"image1", src:d.get('diffImage'))
													}
													td {
														mkp.yield( sysdate )
													}
													td(class:"table-elipse", 'data-toggle':"collapse", 'data-target':"#demo") {
														i(class:"fa fa-ellipsis-h text-black-50") {
														}
													}
												}
												tr(id:"demo" + count, class:"collapse cell-1 row-child") {
													td(colspan:"4") {
														img(class:"image", src:d.get('expectedImage'))
													}
													td(colspan:"4") {
														img(class:"image", src:d.get('actualImage'))
													}
												}

												count++
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		def f = new File(path + File.separator + "index.html")
		f.write(sw.toString())
	}

	/**
	 * 	ファイルをダウンロードしてファイル名を付けます。
	 * @param testObject click,Button
	 * @param fileName 
	 * @param movPath required(False) ファイル転送場所 
	 */
	@Keyword
	public static downloadAndFileName(String testObject, String fileName, String movPath = "") {
		File dir = new File(GlobalVariable.DOWNLOAD_PATH);
		if(!dir.isDirectory()) {
			throw new RuntimeException("GlobalVariable.DOWNLOAD_PATH is not valid, please check it out");
		}
		List<File> fList = dir.listFiles().toList();
		WebUI.click(findTestObject(testObject))
		int count = 0;
		while(count < DOWNLOADING_DELAY) {
			WebUI.delay(1);
			count++;
			println count;
			File cDir = new File(GlobalVariable.DOWNLOAD_PATH);
			List<File> cList = dir.listFiles().toList();
			if(!CommonUtils.isEmpty(cList)) {
				// ソート
				cList.sort { -it.lastModified() }
				File nFile = cList.get(0);
				println nFile.getName() + ", fList.contains(nFile):" + fList.contains(nFile);
				if(nFile.getName().indexOf(DOWNLOADING_SUFFIX1) == -1 && nFile.getName().indexOf(DOWNLOADING_SUFFIX2) == -1 && !fList.contains(nFile)) {
					println "copy:"+nFile + ", to:"+ movPath ?: GlobalVariable.DOWNLOAD_PATH;
					// ファイルを移行するかどうか。movPathを確認してください
					if(!CommonUtils.isEmpty(movPath)) {
						copy(GlobalVariable.DOWNLOAD_PATH + nFile.getName(), movPath+ File.separator + fileName + nFile.getName().substring(nFile.getName().lastIndexOf(".")));
					}else {
						copy(GlobalVariable.DOWNLOAD_PATH + nFile.getName(), GlobalVariable.DOWNLOAD_PATH + fileName + nFile.getName().substring(nFile.getName().lastIndexOf(".")));
					}
					println "del:"+nFile;
					nFile.delete();
					break;
				}
			}
			// ファイルがダウンロードされていない場合は、完了です。
			if(count == DOWNLOADING_DELAY) {
				throw new RuntimeException("File not found, download maybe not complete");
			}
		}


	}

	/**
	 * copy
	 * @param srcPath
	 * @param destPath
	 */
	private static copy(String src, String dest) {
		def f = new File(src)
		def desk = new File(dest)
		if (!desk.getParentFile().exists()) {
			desk.getParentFile().mkdirs()
		}

		if (!desk.exists()) {
			desk.createNewFile()
		}

		f.withDataInputStream { input ->
			desk.withDataOutputStream { output ->
				output << input
			}
		}
	}

}
