package com.pdd.search.client;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.commons.codec.binary.Base64;

/**
 * Client 連接程式 for Search Web.<br>
 * 新增傳入 String[][] 讓 ColdFusion 帶參數進來.<br>
 * 暫時沒開放傳入 HashMap.<br>
 * 
 * 第二版, 壓縮傳輸.<br>
 * 第三版, 加上註冊資訊介面, 拿掉 HashMap 參數<br>
 * 
 * @author Hades.Chen
 * @since JDK 1.4
 */
public class SearchAdapter {

	/**
	 * ENCODING TYPE.
	 */
	private static final String	ENCODING_TYPE	= "UTF-8";

	/**
	 * Version.
	 */
	private static final long	VERSION			= 5;

	/**
	 * 
	 * @param bb
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static String base64Encode(final byte[] bb) throws UnsupportedEncodingException {
		final byte[] bs = Base64.encodeBase64Chunked(bb);
		return urlEncode(new String(bs, ENCODING_TYPE));
	}

	/**
	 * @param str
	 * @return string after encode
	 * @throws UnsupportedEncodingException
	 */
	private static String base64Encode(final String str) throws UnsupportedEncodingException {
		return base64Encode(str.getBytes(ENCODING_TYPE));
	}

	private static byte[] compress(final boolean isCompress, final String str) throws UnsupportedEncodingException {
		if (!isCompress) {
			return str.getBytes(ENCODING_TYPE);
		}
		final Deflater compresser = new Deflater(9);
		compresser.setInput(str.getBytes(ENCODING_TYPE));
		compresser.finish();

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		final byte[] buffer = new byte[4096];
		int len = 0;
		while ((len = compresser.deflate(buffer)) > 0) {
			output.write(buffer, 0, len);
		}
		return output.toByteArray();
	}

	private static String decompress(final boolean isCompress, final byte[] bb) throws DataFormatException, UnsupportedEncodingException {
		if (!isCompress) {
			return new String(bb, ENCODING_TYPE).trim();
		}
		final Inflater decompresser = new Inflater();
		decompresser.setInput(bb);

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		final byte[] buffer = new byte[4096];
		int len = 0;
		while ((len = decompresser.inflate(buffer)) > 0) {
			output.write(buffer, 0, len);
		}
		decompresser.end();
		return output.toString(ENCODING_TYPE).trim();
	}

	// /**
	// * @return HostAddress
	// * @throws UnknownHostException
	// */
	// private static String getHostAddress() throws UnknownHostException {
	// return InetAddress.getLocalHost().getHostAddress();
	// }

	// /**
	// * The Name of class which call this SearchAdapter.
	// *
	// * @return Script Name
	// */
	// private static String getScriptName() {
	// String result = null;
	// final StackTraceElement[] aryStackTrace = new Throwable().getStackTrace();
	// for (final StackTraceElement element : aryStackTrace) {
	// final StackTraceElement st = element;
	// if (!(result = st.getClassName()).equals(SearchAdapter.class.getName())) {
	// break;
	// }
	// }
	// return new StringBuffer("/").append(result.replace('.', '/')).append(".class").toString();
	// }

	/**
	 * Maybe We use this for checking SearchAdapter Version of Client.
	 * 
	 * @return version of SearchAdapter
	 */
	public static long getVersion() {
		// if (VERSION == 100) {
		// /* TODO. */
		// }
		return VERSION;
	}

	// /**
	// * 取得指定 PK 的 XML 文件.
	// * <ul>
	// * 使用此方法前必需先行註冊, 否則將得到程式未註冊的錯誤.
	// * </ul>
	// *
	// * @param isCompress 壓縮傳輸
	// * @param url 伺服器位址
	// * @param id 文件編號, 通常是資料庫中的 PK 值
	// * @param indexType 名稱
	// * @param serverName
	// * @param scriptName
	// * @return 字串型態的 XML
	// */
	// private static String getXml(final boolean isCompress, final String url, final String id, final String indexType, String serverName, String scriptName) {
	// try {
	// if (url.equalsIgnoreCase("search.e104.com.tw")) {
	// serverName = "lab";
	// scriptName = "/lab.class";
	// }
	// /* Main args. */
	// final StringBuffer sb = new StringBuffer();
	// sb.append("PrimaryID=").append(id);
	// sb.append("&IndexType=").append(indexType);
	// sb.append("&ServerName=").append(serverName);
	// sb.append("&ScriptName=").append(scriptName);
	// sb.append("&Version=").append(getVersion());
	// sb.append("&compress=").append(isCompress);
	// // /* Append args. */
	// // if (args != null) {
	// // final String[] keys = (String[]) args.keySet().toArray(new String[0]);
	// // for (int i = 0; i < keys.length; i++) {
	// // sb.append("&" + keys[i] + "=").append(urlEncode((String) args.get(keys[i])));
	// // }
	// // }
	// /* Open Connection. */
	// return openConnection(isCompress, "http://" + url + "/GetXml", sb.toString());
	// } catch (final Exception e) {
	// return "ERROR." + e.toString();
	// }
	// }

	// public static String getXml(final String url, final String id, final String indexType) {
	// try {
	// return getXml(false, url, id, indexType, getHostAddress(), getScriptName());
	// } catch (final Exception e) {
	// return "ERROR." + e.toString();
	// }
	// }

	// public static String getXml(final String url, final String id, final String indexType, final String serverName, final String scriptName) {
	// return getXml(false, url, id, indexType, serverName, scriptName);
	// }

	// public static String getXmlDeflate(final String url, final String id, final String indexType) {
	// try {
	// return getXml(true, url, id, indexType, getHostAddress(), getScriptName());
	// } catch (final Exception e) {
	// return "ERROR." + e.toString();
	// }
	// }

	// public static String getXmlDeflate(final String url, final String id, final String indexType, final String serverName, final String scriptName) {
	// return getXml(true, url, id, indexType, serverName, scriptName);
	// }

	/**
	 * @param url
	 * @param paramString
	 * @return String of Result
	 * @throws DataFormatException
	 * @throws UnsupportedEncodingException
	 */
	private static String openConnection(final boolean isCompress, final String url, final String paramString) throws UnsupportedEncodingException, DataFormatException {
		/* Return Result String. */
		final ByteArrayOutputStream result = new ByteArrayOutputStream();
		URLConnection conn = null;
		OutputStreamWriter out = null;
		BufferedInputStream input = null;

		try {
			conn = new URL(url).openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=" + ENCODING_TYPE);

			out = new OutputStreamWriter(conn.getOutputStream(), ENCODING_TYPE);
			out.write(paramString);
			out.flush();
			out.close();
			out = null;

			input = new BufferedInputStream(conn.getInputStream(), 4096);

			final byte[] buffer = new byte[4096];
			int count = 0;
			while ((count = input.read(buffer)) != -1) {
				result.write(buffer, 0, count);
			}
			input.close();
			input = null;
		} catch (final Exception e) {
			return "ERROR." + e.toString();
		} finally {
			try {
				if (input != null) {
					input.close();
					input = null;
				}
			} catch (final IOException e) {
			}
			try {
				if (out != null) {
					out.close();
					out = null;
				}
			} catch (final IOException e) {
			}
		}
		return decompress(isCompress, result.toByteArray());
	}

	/**
	 * Index 查詢功能.
	 * 
	 * @param isCompress 壓縮傳輸
	 * @param url 伺服器位址
	 * @param executeFrom 使用者自訂義欄位, 以便除錯時找出執行來源
	 * @param indexType Index 名稱
	 * @param page 欲顯示的頁次
	 * @param pageRecords 每頁的筆數
	 * @param sortField 指定排序欄位, 可指定多個排序欄位, 以逗號分隔
	 * @param sortMode 排序模式, 必為 「ASC」 或 「DESC」, 個數必需與排序欄位相等
	 * @param condition 使用者輸入的查詢字串, 支援 「@^#」 分隔的子查詢
	 * @param function 擴充功能選項, 請參閱 webcontent 文件
	 * @return 查詢結果
	 */
	private static String search(final boolean isCompress, final String url, final String executeFrom, final String indexType, final String page, final String pageRecords, final String sortField, final String sortMode, final String condition,
			final String function) {
		try {
			if (executeFrom == null || executeFrom.trim().isEmpty()) {
				return "ERROR. Require executeFrom.";
			}
			if (indexType == null || indexType.trim().isEmpty()) {
				return "ERROR. Require indexType.";
			}			
			if (condition == null || condition.trim().isEmpty()) {
				return "ERROR. Require condition.";
			}
			/* Main args. */
			final StringBuffer sb = new StringBuffer();
			sb.append("executefrom=").append(base64Encode(executeFrom));
			sb.append("&indextype=").append(indexType);
			sb.append("&page=").append(page.length() == 0 ? "1" : page);
			sb.append("&pagerecords=").append(pageRecords.length() == 0 ? "25" : pageRecords);
			sb.append("&sortfield=").append(urlEncode(sortField));
			sb.append("&sortmode=").append(urlEncode(sortMode));
			sb.append("&searchcondition=").append(base64Encode(compress(isCompress, condition)));
			sb.append("&keyword=").append(base64Encode(function));
			sb.append("&encodingtype=").append(ENCODING_TYPE);
			sb.append("&compress=").append(isCompress);			
			/* Open Connection. */
			return openConnection(isCompress, "http://" + url + "/IndexSearch", sb.toString());
		} catch (final Exception e) {
			return "ERROR." + e.toString();
		}
	}

	public static String search(final String url, final String executeFrom, final String indexType, final String page, final String pageRecords, final String sortField, final String sortMode, final String condition) {
		return search(false, url, executeFrom, indexType, page, pageRecords, sortField, sortMode, condition, "");
	}

	public static String search(final String url, final String executeFrom, final String indexType, final String page, final String pageRecords, final String sortField, final String sortMode, final String condition, final String function) {
		return search(false, url, executeFrom, indexType, page, pageRecords, sortField, sortMode, condition, function);
	}

	public static String searchDeflate(final String url, final String executeFrom, final String indexType, final String page, final String pageRecords, final String sortField, final String sortMode, final String condition) {
		return search(true, url, executeFrom, indexType, page, pageRecords, sortField, sortMode, condition, "");
	}

	public static String searchDeflate(final String url, final String executeFrom, final String indexType, final String page, final String pageRecords, final String sortField, final String sortMode, final String condition, final String function) {
		return search(true, url, executeFrom, indexType, page, pageRecords, sortField, sortMode, condition, function);
	}

	/**
	 * @param str
	 * @return string after encode
	 * @throws UnsupportedEncodingException
	 */
	private static String urlEncode(final String str) throws UnsupportedEncodingException {
		return URLEncoder.encode(str, ENCODING_TYPE);
	}
}
