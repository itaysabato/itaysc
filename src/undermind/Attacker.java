package undermind;

import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType;

/**
 * Created By: Itay Sabato<br/>
 * Date: 14/07/11 <br/>
 * Time: 13:18 <br/>
 */
public class Attacker {
    private JNIBWAPI bwapi;
    private ChiefOfStaff chief;
    private boolean canSpwan;
    private boolean spwaned;

    public Attacker(JNIBWAPI bwapi) {
        this.bwapi = bwapi;
        chief = new ChiefOfStaff(bwapi);
        canSpwan = false;
        spwaned = false;
    }

    public void gameUpdate() {

        if(spwaned){
            chief.gameUpdate();
            for (Unit unit : bwapi.getMyUnits()) {
                if (unit.getTypeID() == UnitType.UnitTypes.Zerg_Zergling.ordinal()) {
                      chief.recruit(unit);
                }
            }
//            for (Unit enemy : bwapi.getEnemyUnits()) {
//                for (Unit unit : bwapi.getMyUnits()) {
//                    if (unit.getTypeID() == UnitType.UnitTypes.Zerg_Zergling.ordinal() && unit.isIdle()) {
//                        bwapi.attackMove(unit.getID(), enemy.getX(), enemy.getY());
//                        break;
//                    }
//                }
//            }
        }

        if(!canSpwan){
            for(Unit unit: bwapi.getMyUnits()){
                if(unit.getTypeID() == UnitType.UnitTypes.Zerg_Spawning_Pool.ordinal() && unit.isCompleted()){
                    Out.println("updated spawning pool "+unit.getID());
                    canSpwan = true;
                }
            }
        }

        if(canSpwan && bwapi.getSelf().getMinerals() >= 150){
            Out.println("Minerals at zergling spawning: "+bwapi.getSelf().getMinerals());
            for(Unit unit: bwapi.getMyUnits()){
                if(unit.getTypeID() == UnitType.UnitTypes.Zerg_Larva.ordinal()){
                    bwapi.morph(unit.getID(), UnitType.UnitTypes.Zerg_Zergling.ordinal());
                }
            }
            spwaned = true;
        }
    }
}
