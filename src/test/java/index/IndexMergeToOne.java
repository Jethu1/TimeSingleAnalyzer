package index;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import tokenizer.TimeAnalyzer;

import java.io.File;
import java.io.IOException;

/**
 * Created by jet on 2017/8/4.
 * 重新构建索引类，重点在于给传进来的item的 list 按时间顺序排序，不然索引的时候要出错。
 */
public class IndexMergeToOne {
    static Logger logger = LogManager.getLogger(IndexMergeToOne.class.getName());
    @Test
    public void index() throws IOException {
        //create a indexWriter"
        Directory directory = FSDirectory.open(new File("30index"));
        Analyzer timeAnalyzer = new TimeAnalyzer(Version.LUCENE_46);
        IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LUCENE_46, timeAnalyzer);
        writerConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
//        writerConfig.getMergePolicy();
        IndexWriter indexWriter = new IndexWriter(directory, writerConfig);
        indexWriter.forceMerge(1);
        //parser voice xml documents and then indexes  these documents.
        indexWriter.close();
    }
}
