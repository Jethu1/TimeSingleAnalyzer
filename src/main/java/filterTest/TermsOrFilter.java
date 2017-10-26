package filterTest;

import com.pachira.psae.common.StringUtils;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.FixedBitSet;
import org.apache.lucene.util.NumericUtils;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * 一域多值的Filter（包含多值中任意一值）
 *
 * @author    qinxiaoqing
 * @version   1.0
 * @date      2016/01/20
 * @see       Filter
 * @see       IndexField
 * @since     1.0
 */
public class TermsOrFilter extends Filter {

    private static Pattern ratioPat = Pattern.compile("(-?\\d+(\\.\\d+)?)%");

    /**
     * 索引域
     */
    private IndexField indexField;

    /**
     * 域值
     */
    private BytesRef[] bytesRefs;

    public TermsOrFilter(IndexField indexField, String[] values) {
        this.indexField = indexField;
        convertValue(values);
    }

    private void convertValue(String[] values) {
        bytesRefs = new BytesRef[values.length];
        for (int i = 0; i < values.length; i++) {
            bytesRefs[i] = new BytesRef(values[i]);
            if (IndexField.TYPE_INT.equals(indexField.getType())) {
                //整形
                NumericUtils.intToPrefixCoded(Integer.valueOf(values[i]), 0, bytesRefs[i]);
            } else if (IndexField.TYPE_FLOAT.equals(indexField.getType())) {
                //浮点
                NumericUtils.intToPrefixCoded(NumericUtils.floatToSortableInt(
                        ratioPat.matcher(values[i]).matches() ? (float) StringUtils.percentToDecimal(values[i])
                                : Float.valueOf(values[i])), 0, bytesRefs[i]);
            }
        }
    }

    @Override
    public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
        FixedBitSet result = new FixedBitSet(context.reader().maxDoc());

        Terms terms = context.reader().terms(indexField.getName());
        if (terms == null) {
            return null;
        }

        TermsEnum termsEnum = terms.iterator(null);
        if (termsEnum == null) {
            return null;
        }

        DocsEnum docs = null;
        for (BytesRef bytesRef : bytesRefs) {
            if (termsEnum.seekExact(bytesRef)) {
                docs = termsEnum.docs(acceptDocs, docs, DocsEnum.FLAG_NONE);
                while (docs.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
                    result.set(docs.docID());
                }
            }
        }
        return result;
    }
}