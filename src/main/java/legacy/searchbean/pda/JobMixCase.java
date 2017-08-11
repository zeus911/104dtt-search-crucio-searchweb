package legacy.searchbean.pda;

import legacy.searchbean.SearchBeanOldType;

public class JobMixCase extends SearchBeanOldType {

    @Override
    public String getIndexName() {
        return "pda.JobMixCase";
    }

    @Override
    public boolean isUpperCase() {
        return true;
    }
}
