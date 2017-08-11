package legacy.searchbean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.jdom.Document;

import pdd.crucio.restful.NonLogException;

public class TwJobOn extends SearchBeanOldType {

    public static class TopSkillPair implements Comparable<TopSkillPair> {
        private final String sSkillName;
        private final int    iSkillCount;

        public TopSkillPair(final String sSkillName, final int iSkillCount) {
            this.sSkillName = sSkillName;
            this.iSkillCount = iSkillCount;
        }

        @Override
        public int compareTo(final TopSkillPair o) {
            return o.iSkillCount - iSkillCount;
        }

        public int getSkillCount() {
            return iSkillCount;
        }

        public String getSkillName() {
            return sSkillName;
        }
    }

    private static HashMap<Term, String> skillMap;     // 技能專長對應表
    private static FieldMap              mainMap;      // keyword = MAIN_JOBLIST_XML 時的輸出欄位
    private static FieldMap              introduceMap; // keyword = INTRODUCE_JOBLIST_XML 時的輸出欄位
    static {
        skillMap = new HashMap<>();
        skillMap.put(new Term("OPTIONS_OFFICE_APP", "1"), "Word");
        skillMap.put(new Term("OPTIONS_OFFICE_APP", "2"), "Excel");
        skillMap.put(new Term("OPTIONS_OFFICE_APP", "4"), "PowerPoint");
        skillMap.put(new Term("OPTIONS_OFFICE_APP", "8"), "Outlook");
        skillMap.put(new Term("OPTIONS_OFFICE_APP", "16"), "Project");
        skillMap.put(new Term("OPTIONS_OS", "1"), "Wint NT");
        skillMap.put(new Term("OPTIONS_OS", "2"), "Unix");
        skillMap.put(new Term("OPTIONS_OS", "4"), "Linux");
        skillMap.put(new Term("OPTIONS_OS", "8"), "DOS");
        skillMap.put(new Term("OPTIONS_OS", "16"), "OS/2");
        skillMap.put(new Term("OPTIONS_OS", "32"), "Novell");
        skillMap.put(new Term("OPTIONS_OS", "64"), "Mac OS");
        skillMap.put(new Term("OPTIONS_OS", "128"), "Sun OS");
        skillMap.put(new Term("OPTIONS_OS", "256"), "Windows 98");
        skillMap.put(new Term("OPTIONS_OS", "512"), "Windows 2000");
        skillMap.put(new Term("OPTIONS_OS", "1024"), "Solaris");
        skillMap.put(new Term("OPTIONS_OS", "2048"), "FreeBSD");
        skillMap.put(new Term("OPTIONS_OS", "4096"), "AIX");
        skillMap.put(new Term("OPTIONS_OS", "8192"), "WIN CE");
        skillMap.put(new Term("OPTIONS_DB", "1"), "Dbase");
        skillMap.put(new Term("OPTIONS_DB", "2"), "FoxPro");
        skillMap.put(new Term("OPTIONS_DB", "4"), "MS SQL");
        skillMap.put(new Term("OPTIONS_DB", "8"), "Oracle");
        skillMap.put(new Term("OPTIONS_DB", "16"), "Access");
        skillMap.put(new Term("OPTIONS_DB", "32"), "Informix");
        skillMap.put(new Term("OPTIONS_DB", "64"), "DB2");
        skillMap.put(new Term("OPTIONS_DB", "128"), "Visual Foxpro");
        skillMap.put(new Term("OPTIONS_DB", "256"), "MySQL");
        skillMap.put(new Term("OPTIONS_PROGRAM", "1"), "C/C++");
        skillMap.put(new Term("OPTIONS_PROGRAM", "2"), "Assembly");
        skillMap.put(new Term("OPTIONS_PROGRAM", "4"), "VB");
        skillMap.put(new Term("OPTIONS_PROGRAM", "8"), "Delphi");
        skillMap.put(new Term("OPTIONS_PROGRAM", "16"), "VB/Java Script");
        skillMap.put(new Term("OPTIONS_PROGRAM", "32"), "Perl");
        skillMap.put(new Term("OPTIONS_PROGRAM", "64"), "Java");
        skillMap.put(new Term("OPTIONS_PROGRAM", "128"), "Clipper");
        skillMap.put(new Term("OPTIONS_PROGRAM", "256"), "ASP");
        skillMap.put(new Term("OPTIONS_PROGRAM", "512"), "XML");
        skillMap.put(new Term("OPTIONS_PROGRAM", "1024"), "CGI");
        skillMap.put(new Term("OPTIONS_PROGRAM", "2048"), "PalmOS");
        skillMap.put(new Term("OPTIONS_PROGRAM", "4096"), "LotusNotes");
        skillMap.put(new Term("OPTIONS_PROGRAM", "8192"), "WML");
        skillMap.put(new Term("OPTIONS_PROGRAM", "16384"), "JSP");
        skillMap.put(new Term("OPTIONS_PROGRAM", "32768"), "Visual C++");
        skillMap.put(new Term("OPTIONS_CAD", "1"), "PhotoShop");
        skillMap.put(new Term("OPTIONS_CAD", "2"), "Corel Draw");
        skillMap.put(new Term("OPTIONS_CAD", "4"), "AutoCAD");
        skillMap.put(new Term("OPTIONS_CAD", "8"), "Painter");
        skillMap.put(new Term("OPTIONS_CAD", "16"), "Illustrator");
        skillMap.put(new Term("OPTIONS_CAD", "32"), "Free Hand");
        skillMap.put(new Term("OPTIONS_CAD", "64"), "ImageReady");
        skillMap.put(new Term("OPTIONS_CAD", "128"), "PhotoImpact");
        skillMap.put(new Term("OPTIONS_CAD", "256"), "3Dmax");
        skillMap.put(new Term("OPTIONS_CAD", "512"), "MAYA");
        skillMap.put(new Term("OPTIONS_CAD", "1024"), "Lightwave");
        skillMap.put(new Term("OPTIONS_CAD", "2048"), "Softimage");
        skillMap.put(new Term("OPTIONS_CAD", "4096"), "QuarkXPress");
        skillMap.put(new Term("OPTIONS_CAD", "8192"), "Pagemaker");
        skillMap.put(new Term("OPTIONS_HOMEPAGE", "1"), "FrontPage");
        skillMap.put(new Term("OPTIONS_HOMEPAGE", "2"), "HTML");
        skillMap.put(new Term("OPTIONS_HOMEPAGE", "4"), "DHTML");
        skillMap.put(new Term("OPTIONS_HOMEPAGE", "8"), "Fireworks");
        skillMap.put(new Term("OPTIONS_HOMEPAGE", "16"), "Flash");
        skillMap.put(new Term("OPTIONS_HOMEPAGE", "32"), "Dreamweaver");
        skillMap.put(new Term("OPTIONS_HOMEPAGE", "64"), "NetObjects Fusion");
        skillMap.put(new Term("OPTIONS_HOMEPAGE", "128"), "GoLive");
        skillMap.put(new Term("OPTIONS_MEDIA", "1"), "Authorwave");
        skillMap.put(new Term("OPTIONS_MEDIA", "2"), "Director");
        skillMap.put(new Term("OPTIONS_MEDIA", "4"), "Premiere");
        skillMap.put(new Term("OPTIONS_MEDIA", "8"), "After Effects");
        skillMap.put(new Term("OPTIONS_MEDIA", "16"), "Cakewalk");

        mainMap = new FieldMap();
        mainMap.put("ADDRESS");
        mainMap.put("ADDR_INDZONE");
        mainMap.put("ADDR_NO");
        mainMap.put("ADDR_NO_DESCRIPT");
        mainMap.put("AGEMAX");
        mainMap.put("AGEMIN");
        mainMap.put("APPEAR_DATE");
        mainMap.put("CUSTNO");
        mainMap.put("D2");
        mainMap.put("DESCRIPTION");
        mainMap.put("INDCAT");
        mainMap.put("JOB");
        mainMap.put("JOBCAT1");
        mainMap.put("JOBCAT2");
        mainMap.put("JOBCAT3");
        mainMap.put("JOBNO", "RETURNID");
        mainMap.put("JOB_ADDRESS");
        mainMap.put("JOB_ADDR_NO");
        mainMap.put("JOB_ADDR_NO_DESCRIPT");
        mainMap.put("JOB_INDUSTRY_DESCRIPT");
        mainMap.put("LOGO");
        mainMap.put("MAJOR_CAT_DESCRIPT");
        mainMap.put("NAME");
        mainMap.put("OPTIONS_EDU");
        mainMap.put("OPTIONS_ZONE");
        mainMap.put("PERIOD");
        mainMap.put("ROLE");
        mainMap.put("SALARY_LOW");
        mainMap.put("SALARY_HIGH");
        mainMap.put("WORKTIME");
        mainMap.put("OFFDUTYTIME");
        mainMap.put("ONDUTYTIME");
        mainMap.put("LAT");
        mainMap.put("LON");
        mainMap.put("INDUSTRY");
        mainMap.put("S2");
        mainMap.put("STARTBY");

        introduceMap = new FieldMap();
        introduceMap.put("ADDR_NO");
        introduceMap.put("AGEMAX");
        introduceMap.put("AGEMIN");
        introduceMap.put("APPEAR_DATE");
        introduceMap.put("D2");
        introduceMap.put("DESCRIPTION");
        introduceMap.put("JOB");
        introduceMap.put("JOBCAT_DESCRIPT");
        introduceMap.put("JOBNO", "RETURNID");
        introduceMap.put("JOB_ADDR_NO_DESCRIPT");
        introduceMap.put("MODIFYDATE");
        introduceMap.put("OPTIONS_EDU");
        introduceMap.put("ORDERNO");
        introduceMap.put("PERIOD");
        introduceMap.put("ROLE");
        introduceMap.put("SALARY_LOW");
        introduceMap.put("SALARY_HIGH");
    }

    @Override
    protected ByteArrayOutputStream defaultStyleOutput(final String outputFunction, final Query mainQuery, final TopDocs hits, final ArrayList<ScoreDoc> sdList)
                    throws CorruptIndexException, IOException, InterruptedException, ExecutionException, NonLogException {
        switch (outputFunction) {
            case "MAIN_JOBLIST_XML":
                final Document xml = getOldTypeXml(sdList, hits.totalHits, mainQuery, mainMap, "JOBLIST", "JOBITEM");
                xml.getRootElement().setAttribute("TOPSKILL", getTopSkillString(new CachingWrapperFilter(new QueryWrapperFilter(mainQuery))));
                return getOuputStream(xml);
            case "INTRODUCE_JOBLIST_XML":
                return getOuputStream(getOldTypeXml(sdList, hits.totalHits, mainQuery, introduceMap, "JOBLIST", "JOBITEM"));
            case "TOPSKILL":
                return getOuputStream(getSerialString(sdList, hits.totalHits, mainQuery) + ", TOPSKILL:" + getTopSkillString(new CachingWrapperFilter(new QueryWrapperFilter(mainQuery))));
            case "XML":
                return getOuputStream(getOldTypeXml(sdList, hits.totalHits, mainQuery, getFieldMap(), "JOBLIST", "JOBITEM"));
            default:
                return super.defaultStyleOutput(outputFunction, mainQuery, hits, sdList);
        }
    }

    @Override
    public String getIndexName() {
        return "pda.JobOn";
    }

    private String getTopSkillString(final Filter filter) throws IOException {
        final ArrayList<TopSkillPair> skillCountList = new ArrayList<>();
        for (final Term key : skillMap.keySet()) {
            final int count = searchInResultCount(new TermQuery(key), filter);
            if (count > 0) {
                skillCountList.add(new TopSkillPair(skillMap.get(key), count));
            }
        }
        Collections.sort(skillCountList);
        final StringBuilder sb = new StringBuilder();
        for (int idx = 0; idx < skillCountList.size() && idx < 10; idx++) {
            sb.append(';').append(skillCountList.get(idx).getSkillName() + ":" + skillCountList.get(idx).getSkillCount());
        }
        return sb.length() == 0 ? "" : sb.substring(1);
    }

    @Override
    public boolean isUpperCase() {
        return true;
    }

}
