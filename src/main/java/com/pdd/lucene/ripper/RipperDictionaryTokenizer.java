package com.pdd.lucene.ripper;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeSource;

public class RipperDictionaryTokenizer extends Tokenizer {

    private final char[]      ioBuffer      = new char[4096];
    private final Dictionary  dic;
    private CharTermAttribute termAtt;
    private OffsetAttribute   offsetAtt;
    private TypeAttribute     typeAtt;
    private ArrayList<String> dicTerms;
    private boolean           jump          = false;
    private boolean           ioBufferEnd   = false;
    private int               offset        = 0, bufferIndex = 0, dataLen = 0;
    private char[]            data;
    private boolean           longTermFirst = false;

    public RipperDictionaryTokenizer(final AttributeSource attSrc, final Reader in, final Dictionary dic) {
        super(attSrc, in);
        this.dic = dic;
        initAttribute();
    }

    public RipperDictionaryTokenizer(final Reader in, final Dictionary dic) {
        super(in);
        this.dic = dic;
        initAttribute();
    }

    public RipperDictionaryTokenizer(final Reader in, final Dictionary dic, final boolean longTermFirst) {
        super(in);
        this.dic = dic;
        this.longTermFirst = longTermFirst;
        initAttribute();
    }

    @Override
    public final void end() {
        final int finalOffset = correctOffset(offset);
        offsetAtt.setOffset(finalOffset, finalOffset);
    }

    private boolean inc() {
        if (++bufferIndex >= data.length) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean incrementToken() throws IOException {
        clearAttributes();
        typeAtt.setType("DIC");

        while (true) {

            if (!ioBufferEnd && dic.maxTermLength() > dataLen - bufferIndex) {
                offset += bufferIndex;
                dataLen = dataLen - bufferIndex;
                System.arraycopy(ioBuffer, bufferIndex, ioBuffer, 0, dataLen);
                final int readLen = input.read(ioBuffer, dataLen, ioBuffer.length - dataLen);
                if (readLen == -1) {
                    ioBufferEnd = true;
                } else {
                    dataLen += readLen;
                }
                bufferIndex = 0;
                data = RipperTools.lowercaseNormalize(ioBuffer, 0, dataLen);
            }

            if (dicTerms != null && dicTerms.size() > 0) {
                final String term = dicTerms.remove(0);
                final int start = bufferIndex - 1;
                final int end = start + term.length();
                termAtt.setEmpty().append(term);
                offsetAtt.setOffset(offset + start, offset + end);
                return true;
            }

            if (bufferIndex >= data.length) {
                return false;
            }

            if (jump) {
                while (RipperTools.isAlphabet(data[bufferIndex])) {
                    if (!inc()) {
                        return false;
                    }
                }
                jump = false;
            }

            while (Character.isWhitespace(data[bufferIndex])) {
                if (!inc()) {
                    return false;
                }
            }

            if (RipperTools.isAlphabet(data[bufferIndex])) {
                jump = true;
            } else {
                jump = false;
            }
            dicTerms = dic.find(data, bufferIndex++);
            if (longTermFirst && dicTerms != null) {
                Collections.reverse(dicTerms);
            }
        }
    }

    private void initAttribute() {
        offsetAtt = addAttribute(OffsetAttribute.class);
        termAtt = addAttribute(CharTermAttribute.class);
        typeAtt = addAttribute(TypeAttribute.class);
    }

    @Override
    public void reset(final Reader input) throws IOException {
        super.reset(input);
        bufferIndex = 0;
        offset = 0;
        dataLen = 0;
    }
}
