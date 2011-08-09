package undermind.strategy.execution;

import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType;
import undermind.UndermindClient;

/**
 * Created By: Itay Sabato<br/>
 * Date: 14/07/11 <br/>
 * Time: 13:18 <br/>
 *
 * This class is in charge of continuously
 * spawning zerglings and overlords.
 */
public class Spawner {
    private JNIBWAPI bwapi;
    private boolean poolReady;

    public Spawner(JNIBWAPI bwapi) {
        this.bwapi = bwapi;
        poolReady = false;
    }

    public void spawn() {
        if(!poolReady){
            for(Unit unit: bwapi.getMyUnits()){
                if(unit.getTypeID() == UnitType.UnitTypes.Zerg_Spawning_Pool.ordinal() && unit.isCompleted()){
                    poolReady = true;
                }
            }
        }

        if(poolReady){
            int mineralCount = bwapi.getSelf().getMinerals();
            // make overlord:
            if(bwapi.getSelf().getSupplyTotal() <= bwapi.getSelf().getSupplyUsed() && mineralCount >= 100){
                for(Unit unit: bwapi.getMyUnits()){
                    if(unit.getTypeID() == UnitType.UnitTypes.Zerg_Larva.ordinal()){
                        bwapi.morph(unit.getID(), UnitType.UnitTypes.Zerg_Overlord.ordinal());
                        return;
                    }
                }
            }
            // make zerglings:
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
