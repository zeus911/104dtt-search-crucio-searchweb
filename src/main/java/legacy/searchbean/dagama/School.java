package legacy.searchbean.dagama;

import legacy.searchbean.SearchBeanOldType;

public class School extends SearchBeanOldType {

    @Override
    public String getIndexName() {
        return "dagama.School";
    }

    @Override
    public boolean isUpperCase() {
        return true;
    }

}
