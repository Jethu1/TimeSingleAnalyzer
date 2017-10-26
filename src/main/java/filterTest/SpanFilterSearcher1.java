package filterTest;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.File;
import java.io.IOException;



/**
 * Created by jet on 2017/7/16.
 */
public class SpanFilterSearcher1 {

    //这个方法是搜索索引的方法，传入索引路径和查询表达式
    @Test
    public  void search() throws IOException, ParseException {

        Directory dir = FSDirectory.open(new File("30index"));
        IndexSearcher indexSearcher = new IndexSearcher(DirectoryReader.open(dir));
    /*
     *   SpanNearQuery: documents and searching time.
     */
        // these cache and pasd olicy instances can be shared across several queries and readers
        // it is fine to eg. store them into static variablessd



        int a=0;
        String[] str = new String[2];
        str[0] = "IPMC";
        str[1] = "BOCOM_CC";
//        str[2] = "1";
        IndexField indexField = new IndexField();
        indexField.setName("name");
        indexField.setType("String");

        String[] str1 = new String[3];
        str1[0] = "3.5";
        str1[1] = "4.5";
        str1[2] = "5.5";
//        str1[3] = "6.5";
//        str1[4] = "7.5";
//        str1[5] = "8.5";
        IndexField indexField1 = new IndexField();
        indexField1.setName("IntV");
        indexField1.setType("float");
//        str[2] = "1";
//        str[2] = "5";
//        str[2] = "不如你";
//        NearAfterQuery nearQuery = new NearAfterQuery("contents",str,10,true);
//        TermFreqQuery freqQuery = new TermFreqQuery("contents",list,1,300);
//        TermOffsetQuery offsetQuery = new TermOffsetQuery("contents",list,0,6000);

        TermsOrFilter orQuery = new TermsOrFilter(indexField1,str1);
//        SpanFilterQuery1 spanFilterQuery = new SpanFilterQuery1("contents",str,20,true);

        TotalHitCountCollector countCollector = new TotalHitCountCollector();
        Query filteredQuery = new FilteredQuery(new MatchAllDocsQuery(),orQuery);
        Long begin = System.currentTimeMillis();
        TopDocs hits=indexSearcher.search(filteredQuery,10);
        Long mid = System.currentTimeMillis();
//        indexSearcher.search(nearQuery,countCollector);
        Long end = System.currentTimeMillis();

        System.out.println("total documents: "+hits.totalHits);
        System.out.println("FilterQuery search time: "+ (mid-begin)+"   The second time to search: "+(end-mid));

    }
}
