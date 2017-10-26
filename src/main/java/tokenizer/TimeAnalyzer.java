package tokenizer;

import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by jet on 2017/7/27.
 */
public class TimeAnalyzer extends StopwordAnalyzerBase {

    public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;
    private int maxTokenLength;
    public static final CharArraySet STOP_WORDS_SET;
    private  Version matchVersion;

    public TimeAnalyzer(Version matchVersion, CharArraySet stopWords) {
        super(matchVersion,stopWords);
        this.matchVersion=matchVersion;
        this.maxTokenLength = 255;
    }

    public TimeAnalyzer(Version matchVersion) {
        this(matchVersion, STOP_WORDS_SET);
    }


    public void setMaxTokenLength(int length) {
        this.maxTokenLength = length;
    }

    public int getMaxTokenLength() {
        return this.maxTokenLength;
    }

    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        final TimeTokenizer src = new TimeTokenizer(reader);
        StandardFilter tok = new StandardFilter(this.matchVersion,src);
        LowerCaseFilter tok1 = new LowerCaseFilter(this.matchVersion, tok);
        final StopFilter tok2 = new StopFilter(this.matchVersion, tok1, this.stopwords);
        return new TokenStreamComponents(src, tok2) {
            protected void setReader(Reader reader) throws IOException {
                super.setReader(reader);
            }
        };
    }

    static {
        STOP_WORDS_SET = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
    }

}
