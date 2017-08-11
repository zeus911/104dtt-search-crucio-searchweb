package com.pdd.lucene.ripper;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.Version;

import h4de5.lucene.analysis.LuceneTool;

public class RipperAppendDicAnalyzer extends Analyzer {

    private static final char[]   KEEP_SYMBOL = new char[] { '#', '\'', '.' };
    private static final String[] STOP_WORDS  = { "a", "an", "and", "are", "at", "be", "but", "by", "for", "in", "into", "is", "no", "not", "of", "or", "s", "such", "t", "that", "the", "their",
                    "then", "there", "these", "they", "this", "to",
                    "was", "will", "with" };
    
    private final Version         ver         = LuceneTool.LUCENE_VER;
    private final Dictionary      dic;
    private final CharArraySet    stopWords;

    public RipperAppendDicAnalyzer() {
        this(Dictionary.DEFAULT_DICTIONARY);
    }

    public RipperAppendDicAnalyzer(final Dictionary dic) {
        this.dic = dic;
        stopWords = (CharArraySet) StopFilter.makeStopSet(ver, STOP_WORDS, true);
    }

    @Override
    public TokenStream tokenStream(final String fieldname, final Reader reader) {
        TokenStream ts = new RipperAppendDicTokenizer(reader, KEEP_SYMBOL, stopWords, dic);
        ts = new LowerCaseFilter(ver, ts);
        ts = new SingleQuoteFilter(ts, true);
        ts = new DotFilter(ts, true);
        return ts;
    }
}
