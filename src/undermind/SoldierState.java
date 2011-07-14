package undermind;

import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.Unit;

/**
 * Created By: Itay Sabato<br/>
 * Date: 14/07/11 <br/>
 * Time: 15:44 <br/>
 */
public class SoldierState {
    private boolean nearCombative = false;
    private boolean nearWorker = false;
    private boolean nearStructure = false;

//    private SoldierState(boolean nearCombative, boolean nearWorker, boolean nearStructure) {
//        this.nearCombative = nearCombative;
//        this.nearWorker = nearWorker;
//        this.nearStructure = nearStructure;
//    }

    public static  SoldierState getCurrentState(int unitID, SoldierState previousState, JNIBWAPI bwapi) {
        SoldierState state = new SoldierState();
        for(Unit unit: bwapi.getEnemyUnits()){
            double distance = Utils.distance(bwapi.getUnit(unitID).getX(),bwapi.getUnit(unitID).getY(), unit.getX(),unit.getY());
            if(Utils.isWorker(unit)){
                state.nearWorker = state.nearWorker || distance <= Utils.getMaxCloseDistanceFrom(unit);
            }
            else if(Utils.isStructure(unit)){
                state.nearStructure = state.nearStructure || distance <= Utils.getMaxCloseDistanceFrom(unit);
            }
            else if(Utils.isCombative(unit)){
                state.nearCombative = state.nearCombative || distance <= Utils.getMaxCloseDistanceFrom(unit);
            }
        }
        return state;
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
