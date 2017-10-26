package bean;

/**
 * Created by jet on 2017/7/27.
 */
public class TimeItem {
    private  int begin;
    private  int end;
    private String text;

    @Override
    public String toString() {
        return "TimeItem{" +
                "begin=" + begin +
                ", end=" + end +
                ", text='" + text + '\'' +
                '}';
    }

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
