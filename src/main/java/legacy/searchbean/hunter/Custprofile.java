package legacy.searchbean.hunter;

import legacy.searchbean.SearchBeanOldType;

public class Custprofile extends SearchBeanOldType {

    @Override
    public String getIndexName() {
        return "pda.CustProfileNew";
    }

    @Override
    public boolean isUpperCase() {
        return true;
    }

}
