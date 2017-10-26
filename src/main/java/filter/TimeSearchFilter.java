package filter;

import bean.DocPosition;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.FixedBitSet;
import util.SearchUtils;

import java.io.IOException;

/**
 * Created by jet on 2017/7/27.
 */
public class TimeSearchFilter extends Filter {


    private String field;

    private String value="";

    private int begin;

    private int end;

//    public ArrayList<Integer> list= new ArrayList<Integer>();
    String string="";

    public   Long sum1=0l;
    public   Long sum2=0l;
    public   Long sum3=0l;
    public   Long sum4=0l;
    public int count=0;
    private String[] words;

    public TimeSearchFilter(String field, String[] words, int begin, int end) {
        this.field = field;
        this.begin = begin;
        this.end = end;
        this.words = words;
        for (int i = 0; i < words.length; i++) {
            value+=words[i];
        }
    }

    //context相当于段，Lucene索引按段读取
    @Override
    public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
        count++;
        Long begin1 = System.currentTimeMillis();
        if(words.length== 0) {
            return null;
        }

        FixedBitSet result = new FixedBitSet(context.reader().maxDoc());  //文档号集合，作为结果返回
        Terms terms = context.reader().terms(field);//获取指定域的倒排词表
        if (terms == null) {
            return null;
        }

        TermsEnum termsEnum = terms.iterator(null);//单字的迭代器
        if (termsEnum == null) {
            return null;
        }

        //获取每个词的文档号和词语集合
        DocPosition[] docPos = new DocPosition[words.length];
        for (int i = 0; i < words.length; i++) {
            BytesRef bytesRef = new BytesRef(words[i]);
            if (!termsEnum.seekExact(bytesRef)) {
                return null;
            }
            docPos[i] = new DocPosition(words[i], termsEnum.docsAndPositions(acceptDocs, null));
        }//一个词对应的单字的文档号集合
        Long begin2 = System.currentTimeMillis();
        sum1+=(begin2-begin1);//找到各个字对应的文档号的时间
        //统计命中文档
        Long begin4 = System.currentTimeMillis();
        while (docPos[0].nextDoc()) {

            //获取当前docID
            int docID = docPos[0].doc();

//            if(docID>50000)
//                System.out.println(docID);
            boolean hit = true;
            for (int i = 1; i < docPos.length; i++) {
                //将其后的term跳转到当前文档位置
                //如果返回false说明文档中没有该term,直接返回
                if (!docPos[i].skipTo(docID)) {
                    hit = false;
                    break;
                }
            }

            if(!hit) {
                continue;
            }
            boolean flag = false;
            //计算前、后的字是否依次相邻，循环是判断一篇文档是否有多个长词满足条件
            Long begin7 = System.currentTimeMillis();
                if (SearchUtils.findFollowPosition(docPos, 0, Integer.MAX_VALUE)) {
                    if(begin<=docPos[0].getDocsEnum().startOffset()&&docPos[docPos.length - 1].getDocsEnum().endOffset()<=end){
//                      System.out.println("第一个词位置： "+docPos[0].position()+"最后一个词位置： "+docPos[docPos.length-1].position());
                        flag = true;
                    }
                }

            Long begin5 =System.currentTimeMillis();
            sum2+=(begin5-begin7);//判断字是否顺序依次相邻，将词放入list。
            if(flag){
                result.set(docID);
            }
        }
        Long begin6 = System.currentTimeMillis();
        sum3+=(begin6-begin4);//判断字是否顺序依次相邻，将词放入list和放入map
        Long begin3 = System.currentTimeMillis();
        sum4+=begin3-begin1;//整个寻找长词放入result的过程。
        return result;
    }
}