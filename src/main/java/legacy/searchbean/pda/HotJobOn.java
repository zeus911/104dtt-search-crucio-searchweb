package legacy.searchbean.pda;

import legacy.searchbean.SearchBeanOldType;

public class HotJobOn extends SearchBeanOldType {

	@Override
	public String getIndexName() {
		return "pda.HotJobOn";
	}

    @Override
    public boolean isUpperCase() {
        return true;
    }

}
