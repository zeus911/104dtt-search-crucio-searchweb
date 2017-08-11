package legacy.searchbean.ncc;

import legacy.searchbean.SearchBean;

public class SimilarSchool extends SearchBean {

    @Override
    public String getIndexName() {
        return "ncc.SimilarSchool";
    }

    @Override
    public boolean isUpperCase() {
        return true;
    }

}
