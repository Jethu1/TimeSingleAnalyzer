package bean;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.FieldInfo;

import java.io.Reader;


public class TimeField extends Field {
//用于
    public static final FieldType TYPE_NOT_STORED = new FieldType();
    public static final FieldType TYPE_STORED = new FieldType();

    public TimeField(String name, Reader reader) {
        super(name, reader, TYPE_NOT_STORED);
    }

    public TimeField(String name, String value, Store store) {
        super(name, value, store == Store.YES?TYPE_STORED:TYPE_NOT_STORED);

    }

    public TimeField(String name, TokenStream stream) {
        super(name, stream, TYPE_NOT_STORED);
    }

    static {
        TYPE_NOT_STORED.setIndexed(true);
        TYPE_NOT_STORED.setTokenized(true);
        TYPE_NOT_STORED.freeze();
        TYPE_STORED.setIndexed(true);
        TYPE_STORED.setTokenized(true);
        TYPE_STORED.setStored(true);

        TYPE_STORED.setStoreTermVectors(true);
        TYPE_STORED.setStoreTermVectorOffsets(true);
        TYPE_STORED.setIndexOptions(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        TYPE_STORED.freeze();
    }
}
