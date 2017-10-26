package index;

import bean.RecogTextWordItem;
import bean.RecognizeResult;
import bean.TimeField;
import net.sf.json.JSONArray;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.xml.sax.InputSource;
import tokenizer.TimeAnalyzer;
import util.RecognizeXMLParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by jet on 2017/8/4.
 * 重新构建索引类，重点在于给传进来的item的 list 按时间顺序排序，不然索引的时候要出错。
 */
public class IndexTest3 {
    static Logger logger = LogManager.getLogger(IndexTest3.class.getName());
    @Test
    public void index() throws IOException {
        //create a indexWriter"
        Directory directory = FSDirectory.open(new File("2kIndex"));
        Analyzer timeAnalyzer = new TimeAnalyzer(Version.LUCENE_46);
        IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LUCENE_46, timeAnalyzer);
        writerConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
//        writerConfig.getMergePolicy();
        IndexWriter indexWriter = new IndexWriter(directory, writerConfig);
        indexWriter.forceMerge(1);
        //parser voice xml documents and then indexes  these documents.
        File fileDir = new File("E:\\voiceText1");
//        File fileDir = new File("file");
        File[] xmlFiles = fileDir.listFiles();
        Long begin = System.currentTimeMillis();
        List<RecognizeResult> recognizeResults= new ArrayList<RecognizeResult>();
        InputSource source = new InputSource();
        //parse xml into RAM
        for (int i=0;i<xmlFiles.length;i++){
            if(xmlFiles[i].getName().endsWith(".xml")){
                FileInputStream stream = new FileInputStream(xmlFiles[i]);
                source.setByteStream(stream);
              RecognizeXMLParser.parseXML(source, recognizeResults, true);
            }
        }
        String[] text = new String[xmlFiles.length];
        int index=0;
        Document[] doc = new Document[xmlFiles.length*30];
        List<RecogTextWordItem> lists = new ArrayList<RecogTextWordItem>(500*xmlFiles.length);

        //construct a lists to store all the items
            for (RecognizeResult result:recognizeResults
                 ) {
               List<RecogTextWordItem> list= result.getOneBest().get("n0");
                list.addAll(result.getOneBest().get("n1"));
                Collections.sort(list, new Comparator<RecogTextWordItem>() {
                    public int compare(RecogTextWordItem o1, RecogTextWordItem o2) {
                        if(o1.getBegin()>o2.getBegin()){
                            return  1;
                        }else if(o1.getBegin()==o2.getBegin()){
                            return 0;
                        }else
                        return -1;
                    }
                });
                text[index]= JSONArray.fromObject(list).toString();
//                System.out.println(text[index]);
                index++;
            }
            //loop 300 times to index docs
        Long begin3 = System.currentTimeMillis();
        for (int j = 0; j < 1; j++) {
//         Long begin1 = System.currentTimeMillis();
        // build 1000 random text according to lists.
        for (int i = 0; i < xmlFiles.length; i++) {
            doc[i+j*xmlFiles.length] = new Document();
            doc[i+j*xmlFiles.length].add(new TimeField("content",text[i], Field.Store.YES));
         }

//            System.out.println("构建1000个随机数组时间： "+(System.currentTimeMillis()-begin1));
            Long begin2 = System.currentTimeMillis();
            for (int k = 0; k < xmlFiles.length; k++) {
                indexWriter.addDocument(doc[k+j*xmlFiles.length]);
            }
            indexWriter.commit();
            System.out.println("索引文档总数： "+(j+1)*1000);
        }
        System.out.println("索引3万总时间为： "+(System.currentTimeMillis()-begin3)/60000);
        indexWriter.close();
    }
}