package legacy.searchbean.ncc;

import legacy.searchbean.SearchBean;

public class SimilarMajor extends SearchBean {

    @Override
    public String getIndexName() {
        return "ncc.SimilarMajor";
    }

    @Override
    public boolean isUpperCase() {
        return true;
    }

}
