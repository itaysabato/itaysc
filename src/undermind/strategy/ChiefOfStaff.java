package undermind.strategy;

import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType;
import undermind.dast.EnemyKeeper;
import undermind.dast.ZerglingKeeper;

/**
 * Created By: Itay Sabato<br/>
 * Date: 14/07/11 <br/>
 * Time: 15:15 <br/>
 */
public class ChiefOfStaff {
    public final JNIBWAPI bwapi;
    private ZerglingKeeper zerglingKeeper;
    private EnemyKeeper enemyKeeper;
    private Commander commander;
    private Priorityzer prioritizer;

    public ChiefOfStaff(JNIBWAPI bwapi) {
        this.bwapi = bwapi;
        zerglingKeeper = new ZerglingKeeper(this);
        enemyKeeper = new EnemyKeeper(this);
        commander = new Commander(this);
        prioritizer = new Priorityzer(this);
    }

    public void gameUpdate() {
        loadEnemyUnits();
        loadMyUnits();
        issueCommands();
    }

    private void loadMyUnits() {
        zerglingKeeper.clean();
        for(Unit unit: bwapi.getMyUnits()){
             if(unit.getTypeID() == UnitType.UnitTypes.Zerg_Zergling.ordinal()){
                 zerglingKeeper.updateZergling(unit);
             }
            else if(unit.getID() == PreparationPhase.SCOUT.getScout()){
                 zerglingKeeper.updateZergling(unit);
             }
        }
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

    public Priorityzer getPrioritizer() {
        return prioritizer;
    }
}
