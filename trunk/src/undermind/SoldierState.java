//package undermind;
//
//import eisbot.proxy.JNIBWAPI;
//import eisbot.proxy.model.Unit;
//
///**
// * Created By: Itay Sabato<br/>
// * Date: 14/07/11 <br/>
// * Time: 15:44 <br/>
// */
//public class SoldierState {
//    private boolean nearCombative = false;
//    private boolean nearWorker = false;
//    private boolean nearStructure = false;
//
////    private SoldierState(boolean nearCombative, boolean nearWorker, boolean nearStructure) {
////        this.nearCombative = nearCombative;
////        this.nearWorker = nearWorker;
////        this.nearStructure = nearStructure;
////    }
//
//    public static  SoldierState getCurrentState(Unit unit, SoldierState previousState, JNIBWAPI bwapi) {
//        SoldierState state = new SoldierState();
//        if(unit != null && bwapi.getEnemyUnits() != null){
//            for(Unit enemyUnit: bwapi.getEnemyUnits()){
//                if(enemyUnit.isExists()){
//                    double distance = Utils.distance(unit.getX(),unit.getY(), enemyUnit.getX(),enemyUnit.getY());
//                    if(Utils.isWorker(enemyUnit)){
//                        state.nearWorker = state.nearWorker || distance <= Utils.getMaxCloseDistanceFrom(enemyUnit);
//                    }
//                    else if(Utils.isStructure(enemyUnit)){
//                        state.nearStructure = state.nearStructure || distance <= Utils.getMaxCloseDistanceFrom(enemyUnit);
//                    }
//                    else if(Utils.isCombative(enemyUnit)){
//                        state.nearCombative = state.nearCombative || distance <= Utils.getMaxCloseDistanceFrom(enemyUnit);
//                    }
//                }
//            }
//        }
//        return state;
//    }
//
//    public boolean isNearCombative() {
//        return nearCombative;
//    }
//
//    public boolean isNearWorker() {
//        return nearWorker;
//    }
//
//    public boolean isNearStructure() {
//        return nearStructure;
//    }
//}
