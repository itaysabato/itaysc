package undermind.strategy;

import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType;
import undermind.dast.EnemyKeeper;
import undermind.dast.MyUnitKeeper;

/**
 * Created By: Itay Sabato<br/>
 * Date: 14/07/11 <br/>
 * Time: 15:15 <br/>
 */
public class ChiefOfStaff {
    public final JNIBWAPI bwapi;
    private MyUnitKeeper myUnitKeeper;
    private EnemyKeeper enemyKeeper;
    private Commander commander;
    private Priorityzer prioritizer;

    public ChiefOfStaff(JNIBWAPI bwapi) {
        this.bwapi = bwapi;
        myUnitKeeper = new MyUnitKeeper(this);
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
        myUnitKeeper.clean();
        for(Unit unit: bwapi.getMyUnits()){
             if(unit.getTypeID() == UnitType.UnitTypes.Zerg_Zergling.ordinal() || unit.getID() == PreparationPhase.SCOUT.getScout()){
                 myUnitKeeper.updateUnit(unit);
             }
        }
    }

    private void loadEnemyUnits() {
        enemyKeeper.loadEnemyUnits(bwapi.getEnemyUnits());
    }

    private void issueCommands() {
        commander.issueCommands();
    }

    public MyUnitKeeper getMyUnitKeeper() {
        return myUnitKeeper;
    }

    public EnemyKeeper getEnemyKeeper() {
        return enemyKeeper;
    }

    public Priorityzer getPrioritizer() {
        return prioritizer;
    }
}
