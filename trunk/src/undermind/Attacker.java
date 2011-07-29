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
        }

        if(!canSpwan){
            for(Unit unit: bwapi.getMyUnits()){
                if(unit.getTypeID() == UnitType.UnitTypes.Zerg_Spawning_Pool.ordinal() && unit.isCompleted()){
                    Out.println("updated spawning pool "+unit.getID());
                    canSpwan = true;
                }
            }
        }

        if(canSpwan){
            if(bwapi.getSelf().getSupplyTotal() < bwapi.getSelf().getSupplyUsed() + 2 && bwapi.getSelf().getMinerals() >= 100){
                for(Unit unit: bwapi.getMyUnits()){
                    if(unit.getTypeID() == UnitType.UnitTypes.Zerg_Larva.ordinal()){
                        bwapi.morph(unit.getID(), UnitType.UnitTypes.Zerg_Overlord.ordinal());
                        break;
                    }
                }
            }
            if(bwapi.getSelf().getMinerals() >= 150 && bwapi.getSelf().getSupplyTotal() >= bwapi.getSelf().getSupplyUsed() + 3){
                for(Unit unit: bwapi.getMyUnits()){
                    if(unit.getTypeID() == UnitType.UnitTypes.Zerg_Larva.ordinal()){
                        bwapi.morph(unit.getID(), UnitType.UnitTypes.Zerg_Zergling.ordinal());
                    }
                }
                spwaned = true;
            }

        }
    }
}
