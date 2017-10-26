package tokenizer;

import bean.RecogTextWordItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

/**
 * Created by jet on 2017/7/27.
 */
public class TimeTokenizer extends Tokenizer {
    static Logger logger = LogManager.getLogger(TimeTokenizer.class.getName());
//    static Logger logger = LogManager.getLogger(TimeTokenizer.class.getName());
    String[] strs;
    private CharTermAttribute termAttr;
    private OffsetAttribute offsetAttr;

    private char[] buf = new char[20*4096];
    private int index = 0;
    private int count = 0;// count a words contains multiple terms
    private int finalOffset = 0;
    private StringBuilder sb=null;
    private ArrayList<RecogTextWordItem> timeItems=null;

    public TimeTokenizer(Reader input) {
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
           strs = sb.toString().substring(0,sb.toString().indexOf("}]")+1).split("},");
    }

        while (strs!=null&&strs.length > index) {
            RecogTextWordItem item = new RecogTextWordItem();
            String[] smallStr = strs[index].split(",");//smallStr is the single item.

            String[] stem3 = smallStr[3].split(":");//stem3[1] contains words
            String str = stem3[1].replaceAll("[\\pP\\p{Punct}]", "").replace(" ","");//delete interpunction
              if(str.length()>1&&count<str.length()){
                    int begin = 0,end=0;
                    item.setWord(String.valueOf(str.charAt(count)));
                    String[] stem1 = smallStr[0].split(":");
                    String[] stem2 = smallStr[2].split(":");
                    begin = Integer.parseInt(stem1[1]);
                    end = Integer.parseInt(stem2[1]);
                    if(end<begin){
                        int temp = begin;
                        begin = end;
                        end = temp;
                    }else if(end==begin){
                        index++;
                        continue;
                    }
                    item.setBegin(begin+count);
                    if(count==str.length()-1) {
                        item.setEnd(end);
                    }else {
                        item.setEnd(begin + count + 1);
                    }
                    termAttr.append(item.getWord());
                    offsetAttr.setOffset(item.getBegin(), item.getEnd());
                    finalOffset = item.getEnd();
                    count++;
                    if(count==str.length()){
                        index++;
                        count = 0;
                    }
                    return true;
            }

            item.setWord(str);
            String[] stem1 = smallStr[0].split(":");
            item.setBegin(Integer.parseInt(stem1[1]));
            String[] stem2 = smallStr[2].split(":");
            item.setEnd(Integer.parseInt(stem2[1]));

            termAttr.append(item.getWord());
            if(item.getBegin()>item.getEnd()){
                int temp= item.getEnd();
                item.setEnd(item.getBegin());
                item.setBegin(temp);
            }else if(item.getBegin()==item.getEnd()){
                    index++;
                    continue;
            }
//            logger.debug("begin: "+item.getBegin()+" end: "+ item.getEnd() +" word "+ strs[index]);
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
        this.count = 0;
        this.finalOffset = 0;
        this.buf=new char[30*4096];
       if(this.timeItems!=null){
           this.timeItems.clear();
       }

       if(this.strs!=null){
           this.strs=null;
       }

           if(this.sb!=null){
           this.sb=null;
       }
    }
}
