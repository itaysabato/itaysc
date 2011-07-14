package undermind;

import eisbot.proxy.JNIBWAPI;

/**
 * Created By: Itay Sabato<br/>
 * Date: 14/07/11 <br/>
 * Time: 15:44 <br/>
 */
public class SoldierState {
        private final boolean nearCombative;
        private final boolean nearWorker;
        private final boolean nearStructure;

    private SoldierState(boolean nearCombative, boolean nearWorker, boolean nearStructure) {
        this.nearCombative = nearCombative;
        this.nearWorker = nearWorker;
        this.nearStructure = nearStructure;
    }

    public static  SoldierState getCurrentState(int unitID, SoldierState previousState, JNIBWAPI bwapi) {
          return new SoldierState(false,false,false);
    }

    public boolean isNearCombative() {
        return nearCombative;
    }

    public boolean isNearWorker() {
        return nearWorker;
    }

    public boolean isNearStructure() {
        return nearStructure;
    }
}
