package legacy.searchbean.ncc;

import legacy.searchbean.SearchBean;

public class EduMajor extends SearchBean {

    @Override
    public String getIndexName() {
        return "ncc.EduMajor";
    }

    @Override
    public boolean isUpperCase() {
        return true;
    }

}
