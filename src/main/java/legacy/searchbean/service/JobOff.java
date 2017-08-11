package legacy.searchbean.service;

import legacy.searchbean.SearchBean;

public class JobOff extends SearchBean {

    @Override
    public String getIndexName() {
        return "service.JobOff";
    }

    @Override
    public boolean isUpperCase() {
        return false;
    }

}
