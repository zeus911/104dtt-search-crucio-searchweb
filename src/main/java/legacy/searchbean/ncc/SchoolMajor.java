package legacy.searchbean.ncc;

import legacy.searchbean.SearchBean;

public class SchoolMajor extends SearchBean {

    @Override
    public String getIndexName() {
        return "ncc.SchoolMajor";
    }

    @Override
    public boolean isUpperCase() {
        return true;
    }

}
