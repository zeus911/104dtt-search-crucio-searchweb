package legacy.searchbean.dagama;

import legacy.searchbean.SearchBeanOldType;

public class JobAc extends SearchBeanOldType {

    @Override
    public String getIndexName() {
        return "dagama.JobAc";
    }

    @Override
    public boolean isUpperCase() {
        return true;
    }

}
