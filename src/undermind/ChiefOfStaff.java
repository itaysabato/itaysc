package undermind;

import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.Map;
import eisbot.proxy.model.Unit;

import java.util.HashMap;

/**
 * Created By: Itay Sabato<br/>
 * Date: 14/07/11 <br/>
 * Time: 15:15 <br/>
 */
public class ChiefOfStaff {
    private JNIBWAPI bwapi;
    private java.util.Map<Integer,SoldierState> soldierStateMap;
    private Commander commander;

    public ChiefOfStaff(JNIBWAPI bwapi) {
        this.bwapi = bwapi;
        soldierStateMap = new HashMap<Integer, SoldierState>();
        commander = new Commander(bwapi);
    }

    public void recruit(Unit unit){
        if(!soldierStateMap.containsKey(unit.getID())){
            soldierStateMap.put(unit.getID(),SoldierState.getCurrentState(unit.getID(),null,bwapi));
        }
    }

    public void gameUpdate() {
        updateStates();
        issueCommands();
    }

    private void updateStates() {
        for(java.util.Map.Entry<Integer,SoldierState> IDtoState: soldierStateMap.entrySet()){
                IDtoState.setValue(SoldierState.getCurrentState(IDtoState.getKey(), IDtoState.getValue(),bwapi));
        }
    }

    private void issueCommands() {
        for(java.util.Map.Entry<Integer,SoldierState> IDtoState: soldierStateMap.entrySet()){
            commander.command(IDtoState.getKey(), IDtoState.getValue());
        }
    }
}
