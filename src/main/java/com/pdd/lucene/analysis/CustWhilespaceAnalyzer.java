package com.pdd.lucene.analysis;

import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceTokenizer;
import org.apache.lucene.util.Version;

public class CustWhilespaceAnalyzer extends Analyzer {

    public static final Set<?> ENGLISH_STOP_WORDS_SET;

    static {
        final List<String> stopWords = Arrays.asList("a", "an", "and", "are", "as", "at", "be", "but", "by", "for", "if", "in", "into", "is", "it", "no", "not", "of", "on", "or", "such", "that",
                        "the", "their", "then", "there", "these", "they",
                        "this", "to", "was", "will", "with", "@", "Co.", "ltd.", "inc.", "corp.");
        final CharArraySet stopSet = new CharArraySet(Version.LUCENE_36, stopWords, false);
        ENGLISH_STOP_WORDS_SET = CharArraySet.unmodifiableSet(stopSet);
    }

    @Override
    public TokenStream tokenStream(final String fieldname, final Reader reader) {
        TokenStream ts = new WhitespaceTokenizer(Version.LUCENE_36, reader);
        ts = new LowerCaseFilter(Version.LUCENE_36, ts);
        ts = new StopFilter(Version.LUCENE_36, ts, ENGLISH_STOP_WORDS_SET);
        return ts;
    }

}
