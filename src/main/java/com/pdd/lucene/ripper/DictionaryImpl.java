package com.pdd.lucene.ripper;

import java.util.ArrayList;
import java.util.HashMap;

public class DictionaryImpl extends Dictionary {

	private static final int				TERMTABLE_SHIFT_LEN	= 4;
	private final String[]					dicArr;
	private final HashMap<Integer, Integer>	posMapS				= new HashMap<Integer, Integer>();
	private final HashMap<Integer, Integer>	posMapE				= new HashMap<Integer, Integer>();
	private int								maxTermLength;

	DictionaryImpl(final String[] termArray) {
		dicArr = termArray;
		initDicMap();
	}

	@Override
	public ArrayList<String> find(final char[] data, final int idx) {
		final int v = data[idx] >> TERMTABLE_SHIFT_LEN;
		if (!posMapS.containsKey(v)) {
			return null;
		}

		final ArrayList<String> res = new ArrayList<String>();
		for (int i = posMapS.get(v); i < posMapE.get(v); i++) {
			if (dicArr[i].charAt(0) == data[idx]) {
				int pos = 0;
				while (pos < dicArr[i].length() && idx + pos < data.length) {
					if (dicArr[i].charAt(pos) != data[idx + pos]) {
						break;
					}
					if (pos == dicArr[i].length() - 1) {
						if (!RipperTools.isCJK(dicArr[i])) {
							if (isBreak(idx + pos, data)) {
								res.add(dicArr[i]);
							}
						} else {
							res.add(dicArr[i]);
						}
						break;
					}
					pos++;
				}
			} else if (dicArr[i].charAt(0) > data[idx]) {
				break;
			}
		}
		return res;
	}

	private void initDicMap() {
		int pre = -1;
		for (int i = 0; i < dicArr.length; i++) {
			maxTermLength = Math.max(maxTermLength, dicArr[i].length());
			final int cur = dicArr[i].charAt(0) >> TERMTABLE_SHIFT_LEN;
			if (cur != pre) {
				posMapS.put(cur, i);
				if (pre > -1) {
					posMapE.put(pre, i);
				}
				pre = cur;
			}
		}
		posMapE.put(pre, dicArr.length);
	}

	private boolean isBreak(final int offset, final char[] data) {
		return offset + 1 >= data.length || !RipperTools.isAlphabet(data[offset + 1]) && !Character.isDigit(data[offset + 1]);
	}

	@Override
	public int maxTermLength() {
		return maxTermLength;
	}

	@Override
	public int size() {
		return dicArr.length;
	}

}
