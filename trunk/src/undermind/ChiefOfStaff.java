package undermind;

import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.Map;
import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created By: Itay Sabato<br/>
 * Date: 14/07/11 <br/>
 * Time: 15:15 <br/>
 */
public class ChiefOfStaff {
    private JNIBWAPI bwapi;
    private ZerglingKeeper zerglingKeeper;
    private EnemyKeeper enemyKeeper;
    private Commander commander;

    public ChiefOfStaff(JNIBWAPI bwapi) {
        this.bwapi = bwapi;
        zerglingKeeper = new ZerglingKeeper();
        enemyKeeper = new EnemyKeeper();
        commander = new Commander(this);
    }

    public void gameUpdate() {
        loadMyUnits();
        loadEnemyUnits();
        issueCommands();
    }

    private void loadMyUnits() {
        zerglingKeeper.clean();
        for(Unit unit: bwapi.getMyUnits()){
            Out.println("unit type: "+UnitType.UnitTypes.values()[unit.getTypeID()]);
             if(unit.getTypeID() == UnitType.UnitTypes.Zerg_Zergling.ordinal()){
                 Out.println("found zergling: "+unit.getID());
                 zerglingKeeper.updateZergling(unit);
             }
        }
        Out.println("loaded");
    }

    private void loadEnemyUnits() {
        enemyKeeper.loadEnemyUnits(bwapi.getEnemyUnits());
    }

    private void issueCommands() {
        commander.issueCommands();
    }

    public ZerglingKeeper getZerglingKeeper() {
        return zerglingKeeper;
    }

    public void setZerglingKeeper(ZerglingKeeper zerglingKeeper) {
        this.zerglingKeeper = zerglingKeeper;
    }

    public EnemyKeeper getEnemyKeeper() {
        return enemyKeeper;
    }

    public void setEnemyKeeper(EnemyKeeper enemyKeeper) {
        this.enemyKeeper = enemyKeeper;
    }
}
