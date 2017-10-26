package util;

import bean.DocPosition;

import java.io.IOException;

/**
 * Created by jet on 2017/8/10.
 */
public class SearchUtils {


    /**
     * 计算指定词是否在文档中出现(即指定词中的term位置是否依次相邻)
     * @param docPositions  term位置信息数组
     * @return                该词在文档中出现返回true,否则返回false
     * @throws IOException
     */
    public static boolean findFollowPosition(DocPosition[] docPositions, int start, int end) throws IOException {
        //如果位置信息为空，直接返回false
        if(docPositions == null || docPositions.length < 1) {
            return false;
        }

        //遍历第一个term的位置，计算相邻位置
        while (docPositions[0].nextPosition()
                && docPositions[0].position() >= start
                && docPositions[0].position() <= end) {
            boolean flag = true;
            for (int i = 1; i < docPositions.length; i++) {
                while (docPositions[i].position() <= docPositions[i - 1].position()) {
                    if (!docPositions[i].nextPosition()) {
                        break;
                    }
                }

                //如果后一个term和前一个term的位置相差不为1，说明两个term不相邻，直接跳出当前循环
                if (docPositions[i - 1].position() + 1 != docPositions[i].position()) {
                    flag = false;
                    break;
                }
            }

            //如果flag为true，说明已找到相邻的位置，直接返回true
            if (flag) {
                return true;
            }
        }

        return false;
    }
}
