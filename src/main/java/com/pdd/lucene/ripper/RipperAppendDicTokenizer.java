package com.pdd.lucene.ripper;

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class RipperAppendDicTokenizer extends Tokenizer {

    private final CharTermAttribute         termAtt;
    private final CharArraySet              stopWords;
    private final RipperTokenizer           ripper;
    private final RipperDictionaryTokenizer dictk;
    private boolean                         ripFin = false;

    public RipperAppendDicTokenizer(final Reader in, final char[] keepSymbol, final CharArraySet stopWords, final Dictionary dic) {
        super(in);
        this.stopWords = stopWords;
        termAtt = getAttribute(CharTermAttribute.class);

        if (!in.markSupported()) {
            try {
                final CharArrayWriter cw = new CharArrayWriter();
                final char[] buf = new char[4096];
                int len = 0;
                while ((len = in.read(buf)) > -1) {
                    cw.write(buf, 0, len);
                }
                final Reader rr = new CharArrayReader(cw.toCharArray());
                this.reset(rr);
            } catch (final Exception e) {} finally {}
        }

        ripper = new RipperTokenizer(this, input, keepSymbol);
        dictk = new RipperDictionaryTokenizer(this, input, dic);
    }

    @Override
    public boolean incrementToken() throws IOException {
        while (true) {
            if (ripFin) {
                return dictk.incrementToken();
            } else {
                while (ripper.incrementToken()) {
                    if (stopWords.contains(termAtt.buffer(), 0, termAtt.length())) {
                        continue;
                    }
                    return true;
                }
                ripFin = true;
                input.reset();
            }
        }
    }
}
