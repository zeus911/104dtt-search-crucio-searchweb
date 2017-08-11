package legacy.searchbean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import com.pdd.search.client.SearchAdapter;

import pdd.crucio.restful.NonLogException;

public class TwCustprofile extends SearchBeanOldType {

    private static final String JOB_URL = "search.s104.com.tw/SearchWeb";
    private static FieldMap     map;
    static {
        map = new FieldMap();
        map.put("RETURNID");
        map.put("ADDRESS");
        map.put("ADDR_IND_NO_DESCRIPT");
        map.put("ADDR_NO");
        map.put("ADDR_NO_DESCRIPT");
        map.put("COUNTS");
        map.put("INDCAT");
        map.put("INDUSTRY");
        map.put("INDUSTRY_DESCRIPT");
        map.put("INDUSTRY_DESCRIPT_M");
        map.put("INVOICE");
        map.put("NAME");
        map.put("SWITCH");// 20070322
        map.put("S1");// 20070525
        map.put("ZONE");
        map.put("LAT");
        map.put("LON");
    }

    @Override
    protected ByteArrayOutputStream defaultStyleOutput(final String outputFunction, final Query mainQuery, final TopDocs hits, final ArrayList<ScoreDoc> sdList)
                    throws CorruptIndexException, IOException, InterruptedException, ExecutionException, NonLogException {
        switch (outputFunction) {
            case "CUSTPROFILE_JOBCOUNTS":
                return getOuputStream(getOldTypeXml(modify(sdList), hits.totalHits, mainQuery, map, "BODY", "XMLITEM"));
            default:
                return super.defaultStyleOutput(outputFunction, mainQuery, hits, sdList);
        }
    }

    @Override
    public String getIndexName() {
        return "pda.CustProfileNew";
    }

    @Override
    public boolean isUpperCase() {
        return true;
    }

    private Document[] modify(final ArrayList<ScoreDoc> sdList) throws CorruptIndexException, IOException {
        final Document[] docs = new Document[sdList.size()];
        if (sdList.size() > 0) {
            for (int i = 0; i < sdList.size(); i++) {
                docs[i] = searcher.doc(sdList.get(i).doc);
            }
            final StringBuilder sb = new StringBuilder();
            for (final Document doc : docs) {
                sb.append("@^#CUSTNO:").append(doc.get("RETURNID"));
            }
            final String ans = SearchAdapter.search(JOB_URL, getExecuteFrom(), "TwJobOn", "1", "0", "", "", "+(SWITCH:ON*)-(SWITCH:ONTO)" + sb.toString());
            final String[] tmp = ans.split(Pattern.quote("@^#"));
            for (int i = 0; i < docs.length; i++) {
                docs[i].add(new Field("COUNTS", tmp[i + 1], Store.YES, Index.NO));
            }
        }
        return docs;
    }

}
