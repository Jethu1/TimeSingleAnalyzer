/**
 * Created by jet on 2017/8/9.
 */
public class TokenizerTest {
    public static void main(String[] args) {
/*        String str = "分支";
        System.out.println(str.length());
        String s =  String.valueOf(str.charAt(0));
        System.out.println(s);*/


        String str = "!!！？？!!!!%*）%￥！KTV去'符号'标号！！    当然,，。!!..**半角";
        System.out.println(str);
        String str1 = str.replaceAll("[\\pP\\p{Punct}]", "").replace(" ","");
        System.out.println("str1:" + str1);


        String str2 = str.replaceAll("[//pP]", "");
        System.out.println("str2:" + str2);


        String str3 = str.replaceAll("[//p{P}]", "");
        System.out.println("str3:" + str3);
    }
}

