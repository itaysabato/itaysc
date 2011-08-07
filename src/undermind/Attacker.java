package undermind;

import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType;

import java.util.LinkedList;

/**
 * Created By: Itay Sabato<br/>
 * Date: 14/07/11 <br/>
 * Time: 13:18 <br/>
 */
public class Attacker {
    private JNIBWAPI bwapi;
    private ChiefOfStaff chief;
    private boolean canSpwan;

    public Attacker(JNIBWAPI bwapi) {
        this.bwapi = bwapi;
        chief = new ChiefOfStaff(bwapi);
        canSpwan = false;
    }

    public void gameUpdate() {

        chief.gameUpdate();

        if(!canSpwan){
            for(Unit unit: bwapi.getMyUnits()){
                if(unit.getTypeID() == UnitType.UnitTypes.Zerg_Spawning_Pool.ordinal() && unit.isCompleted()){
                    Out.println("updated spawning pool "+unit.getID());
                    canSpwan = true;
                }
            }
        }

        if(canSpwan){
            int mineralCount = bwapi.getSelf().getMinerals();
            if(bwapi.getSelf().getSupplyTotal() <= bwapi.getSelf().getSupplyUsed() && mineralCount >= 100){
                for(Unit unit: bwapi.getMyUnits()){
                    if(unit.getTypeID() == UnitType.UnitTypes.Zerg_Larva.ordinal()){
                        bwapi.morph(unit.getID(), UnitType.UnitTypes.Zerg_Overlord.ordinal());
                        return;
                    }
                }
            }
            if(mineralCount >= 50 && bwapi.getSelf().getSupplyTotal() > bwapi.getSelf().getSupplyUsed()){
                for(Unit unit: bwapi.getMyUnits()){
                    if(unit.getTypeID() == UnitType.UnitTypes.Zerg_Larva.ordinal() && !UndermindClient.getMyClient().isDestroyed(unit.getID())){
                        bwapi.morph(unit.getID(), UnitType.UnitTypes.Zerg_Zergling.ordinal());
                    }
                }
            }
        }
    }
}
