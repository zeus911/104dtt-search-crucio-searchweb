package legacy.searchbean.pda;

import legacy.searchbean.SearchBeanOldType;

public class CustprofileOn extends SearchBeanOldType {

    @Override
    public String getIndexName() {
        return "pda.CustProfileOn";
    }

    @Override
    public boolean isUpperCase() {
        return true;
    }

}
