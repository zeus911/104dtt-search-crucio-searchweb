package legacy.searchbean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import pdd.crucio.restful.NonLogException;

public class TwFunction01 extends SearchBeanOldType {

    @Override
    protected ByteArrayOutputStream defaultStyleOutput(final String outputFunction, final Query mainQuery, final TopDocs hits, final ArrayList<ScoreDoc> sdList)
                    throws CorruptIndexException, IOException, InterruptedException, ExecutionException, NonLogException {
        return super.defaultStyleOutput("ID", mainQuery, hits, sdList);
    }

    @Override
    public String getIndexName() {
        return "Function01";
    }

    @Override
    public boolean isUpperCase() {
        return true;
    }

}
