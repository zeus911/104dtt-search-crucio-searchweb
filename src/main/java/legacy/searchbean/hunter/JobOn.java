package legacy.searchbean.hunter;

import legacy.searchbean.SearchBeanOldType;

public class JobOn extends SearchBeanOldType {

    @Override
    public String getIndexName() {
        return "pda.JobOn";
    }

    @Override
    public boolean isUpperCase() {
        return true;
    }

}
