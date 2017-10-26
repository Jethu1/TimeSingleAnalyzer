package search;


import filter.TimeSingleFilterTest;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by jet on 2017/8/4.
 */
public class searchTest1 {

    @Test
    public void testSearch() {
        DirectoryReader directoryReader = null;
        //设置索引目录
        try {
//            Directory directory = FSDirectory.open(new File("2kIndex"));  //1000文档
            Directory directory = FSDirectory.open(new File("D:\\100voice"));
//            Directory directory = FSDirectory.open(new File("30index"));
//            Directory directory = FSDirectory.open(new File("3index"));
            //创建IndexReader
            directoryReader = DirectoryReader.open(directory);
            //根据IndexReader创建IndexSearch
            IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
            //创建搜索的Query
            String[] str = new String[1];
            str[0] = "登";
//            str[1] = "份";
//            str[2] = "证";
//            str[3] = "号";
//            str[4] = "码";
            boolean[] b1 = new boolean[2];
            System.out.println(b1[0]);
            TimeSingleFilterTest filter = new TimeSingleFilterTest("content",str,0,99966000);
            TotalHitCountCollector countCollector = new TotalHitCountCollector();
            countCollector.acceptsDocsOutOfOrder();
            Query query = new FilteredQuery(new MatchAllDocsQuery(),filter);
            Long begin = System.currentTimeMillis();
//          TopDocs docs= indexSearcher.search(query,10);
            indexSearcher.search(query,countCollector);
            Long begin2 = System.currentTimeMillis();
            System.out.println("my search time : "+ (begin2-begin)+" result: "+countCollector.getTotalHits());


/*
    SpanNearQuery: documents and searching time.
  */
            SpanQuery[] queries = new SpanQuery[1];
            queries[0] = new SpanTermQuery(new Term("content","身"));
//            queries[1] = new SpanTermQuery(new Term("content","记"));
//            queries[2] = new SpanTermQuery(new Term("content","人"));
            SpanNearQuery spanNearQuery = new SpanNearQuery(queries,0,true);
            TermQuery query1 = new TermQuery(new Term("content","身"));
            TopDocs hits=indexSearcher.search(query1,3);
            System.out.println("total documents: "+hits.totalHits);
            Long mid = System.currentTimeMillis();
            System.out.println("spanNearQuery search time: "+ (mid-begin2));

//            System.out.println("Timefilter time: "+filter.sum);
//            System.out.println("查找到的文档总共有: " + topDocs.totalHits);

/*            int count =0;
            System.out.println("hitMap: "+filter.getHitMap().size());
           Iterator<Map.Entry<Integer, List<TimeItem>>> it = filter.getHitMap().entrySet().iterator();
            while (it.hasNext()) {
                count++;
                Map.Entry<Integer, List<TimeItem>> entry = it.next();
                System.out.println("文档"+((int)entry.getKey())+"的具体时间位置： "  + entry.getValue());

                Document doc = indexSearcher.doc((int)entry.getKey());
                System.out.println(doc.get("content"));
              }*/
//            System.out.println("文档个数： "+count);
            System.out.println("sum1: "+filter.sum1+" sum2: "+filter.sum2+" sum3: "+filter.sum3+" sum4: "+filter.sum4+" count: "/*+filter.count*/);

            String countstr = "";
            //根据TopDocs获取ScoreDoc对象
/*            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                        countstr+=","+scoreDoc.doc;
                      System.out.println("文档编号： "+scoreDoc.doc);  // 根据文档的标识获取文档
                Document doc = indexSearcher.doc(scoreDoc.doc);
                String time = doc.get("content");
                System.out.println("文档号为： "+scoreDoc.doc+" 整篇文档的分数:"+scoreDoc.score+" 内容： "+time);
            }*/
            System.out.println("over");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (directoryReader != null)
                    directoryReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
