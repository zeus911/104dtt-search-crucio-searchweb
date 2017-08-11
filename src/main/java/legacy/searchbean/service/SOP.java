package legacy.searchbean.service;

import legacy.searchbean.SearchBean;

public class SOP extends SearchBean {

    @Override
    public String getIndexName() {
        return "service.SOP";
    }

    @Override
    public boolean isUpperCase() {
        return true;
    }

}
