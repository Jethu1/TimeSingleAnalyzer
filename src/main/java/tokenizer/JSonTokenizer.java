package tokenizer;

import bean.RecogTextWordItem;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

/**
 * Created by jet on 2017/7/27.
 */
public class JSonTokenizer extends Tokenizer {


    private CharTermAttribute termAttr;
    private OffsetAttribute offsetAttr;

    private char[] buf = new char[20*4096];
    private int index = 0;
    private int finalOffset = 0;
    private StringBuilder sb=null;
    private ArrayList<RecogTextWordItem> timeItems=null;

    public JSonTokenizer(Reader input) {
        super(input);
        termAttr = this.addAttribute(CharTermAttribute.class);
        offsetAttr = this.addAttribute(OffsetAttribute.class);
    }

    @Override
    public final boolean incrementToken() throws IOException {
        clearAttributes();

        if(sb==null){
            sb = new StringBuilder();

        while (input.read(buf) > 0) {
            sb.append(buf);
        }
        if (!"".equals(sb.toString())) {
            timeItems = JSON.parseObject(sb.toString(), new TypeReference<ArrayList<RecogTextWordItem>>(){});
        }
     }

        if (timeItems != null && timeItems.size() > index) {
            RecogTextWordItem item = timeItems.get(index);
            termAttr.append(item.getWord());
            if(item.getBegin()>item.getEnd()){
                int temp= item.getEnd();
                item.setEnd(item.getBegin());
                item.setBegin(temp);
            }
            System.out.println(item.getWord()+" begin: "+item.getBegin()+" end: "+item.getEnd());
            offsetAttr.setOffset(item.getBegin(), item.getEnd());
            finalOffset = item.getEnd();
            index ++;
            return true;
        }

        return false;

    }

    public final void end() throws IOException {
        super.end();
        this.offsetAttr.setOffset(this.finalOffset, this.finalOffset);
    }

    public void reset() throws IOException {
        super.reset();
        this.index = 0;
        this.finalOffset = 0;
        this.buf=new char[20*4096];
       if(this.timeItems!=null){
           this.timeItems.clear();
       }
           if(this.sb!=null){
           this.sb=null;
//               this.sb.delete(0,sb.length());
       }
    }
}
