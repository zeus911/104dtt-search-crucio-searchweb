package com.pdd.lucene.ripper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public abstract class Dictionary {

	private static final String	DICFILENAME	= "dic.ripper";
	public static Dictionary	DEFAULT_DICTIONARY;
	static {
		try {
			DEFAULT_DICTIONARY = Dictionary.getDictionary(Dictionary.class.getResourceAsStream("/com/pdd/lucene/ripper/dic.ripper"));
		} catch (final Exception e) {
		}
	}

	/**
	 * Complie dic text to dic obj.
	 * 
	 * @param dicTxtFile Dictionary text file
	 * @return Dictionary object file
	 * @throws IOException
	 */
	public static File complieDictionary(final File dicTxtFile) throws IOException {
		final File dstDir = new File(dicTxtFile.getParentFile(), ".comm");
		if (dstDir.isDirectory()) {
			dstDir.delete();
		} else {
			dstDir.mkdirs();
		}
		final String[] dicArr = makeDic(dicTxtFile);
		final File dstFile = new File(dstDir, DICFILENAME);
		final ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(dstFile, false));
		os.writeObject(dicArr);
		os.close();
		reviewTextFile(dicArr, dstDir);
		return dstFile;
	}

	public static Dictionary createDictionary(final File dicTxtFile) throws IOException {
		return new DictionaryImpl(makeDic(dicTxtFile));
	}

	public static Dictionary createDictionary(final String[] terms) {
		final HashSet<String> set = new HashSet<String>();
		for (final String element : terms) {
			final char[] ctmp = element.toLowerCase().toCharArray();
			for (int k = 0; k < ctmp.length; k++) {
				ctmp[k] = RipperTools.normalize(ctmp[k]);
			}
			set.add(new String(ctmp));
		}
		final String[] dicArr = set.toArray(new String[set.size()]);
		Arrays.sort(dicArr);
		return new DictionaryImpl(dicArr);
	}

	/**
	 * Complie dic text to dic obj.<br>
	 * 會移除英文單字及單一中文字的term, 為 RipperAnalyzer 設計, 可加速分詞過程.
	 * 
	 * @param dicTxtFile
	 * @return Dictionary object file
	 * @throws IOException
	 */
	public static File fixComplieDictionary(final File dicTxtFile) throws IOException {
		final File dstDir = new File(dicTxtFile.getParentFile(), ".ripper");
		if (dstDir.isDirectory()) {
			dstDir.delete();
		} else {
			dstDir.mkdirs();
		}
		final String[] dicArr = makeFixDic(dicTxtFile);
		final File dstFile = new File(dstDir, DICFILENAME);
		final ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(dstFile, false));
		os.writeObject(dicArr);
		os.close();
		reviewTextFile(dicArr, dstDir);
		return dstFile;
	}

	public static Dictionary getDictionary(final File dicObjFile) throws FileNotFoundException, IOException, ClassNotFoundException {
		final ObjectInputStream oi = new ObjectInputStream(new FileInputStream(dicObjFile));
		final String[] dicArr = (String[]) oi.readObject();
		oi.close();
		return new DictionaryImpl(dicArr);
	}

	public static Dictionary getDictionary(final InputStream resource) throws IOException, ClassNotFoundException {
		final ObjectInputStream oi = new ObjectInputStream(resource);
		final String[] dicArr = (String[]) oi.readObject();
		oi.close();
		return new DictionaryImpl(dicArr);
	}

	private static String[] makeDic(final File dicTxtFile) throws IOException {
		final HashSet<String> set = new HashSet<String>();
		final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dicTxtFile), "UTF-8"));
		String line;
		while ((line = br.readLine()) != null) {
			final String tmp = line.replace((char) 0xfeff, ' ').trim();
			if (tmp.length() == 0 || tmp.startsWith("//") || tmp.startsWith("﻿\\\\")) {
				continue;
			} else {
				set.add(tmp.toLowerCase());
			}
		}
		br.close();

		final String[] dicArr = set.toArray(new String[set.size()]);
		Arrays.sort(dicArr);
		return dicArr;
	}

	private static String[] makeFixDic(final File dicTxtFile) throws IOException {
		final HashSet<String> set = new HashSet<String>();
		final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dicTxtFile), "UTF-8"));
		String line;
		while ((line = br.readLine()) != null) {
			final String tmp = line.replace((char) 0xfeff, ' ').trim();
			if (tmp.length() == 0 || tmp.startsWith("//") || tmp.startsWith("﻿\\\\")) {
				continue;
			} else {
				if (tmp.length() == 1 && Character.UnicodeBlock.of(tmp.charAt(0)) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
					continue;
				}
				if (RipperTools.isAlphabetMixDigit(tmp) && tmp.split("\\s").length == 1) {
					continue;
				}
				final char[] ctmp = tmp.toLowerCase().toCharArray();
				for (int i = 0; i < ctmp.length; i++) {
					ctmp[i] = RipperTools.normalize(ctmp[i]);
				}
				set.add(new String(ctmp));
			}
		}
		br.close();

		final String[] dicArr = set.toArray(new String[set.size()]);
		Arrays.sort(dicArr);
		return dicArr;
	}

	private static void reviewTextFile(final String[] dicArr, final File dir) throws UnsupportedEncodingException, FileNotFoundException {
		final File n = new File(dir, "dic.txt");
		final PrintWriter p = new PrintWriter(new OutputStreamWriter(new FileOutputStream(n), "UTF-8"));
		for (final String s : dicArr) {
			p.println(s);
		}
		p.close();
	}

	public abstract ArrayList<String> find(char[] data, int idx);

	public abstract int maxTermLength();

	public abstract int size();
}
