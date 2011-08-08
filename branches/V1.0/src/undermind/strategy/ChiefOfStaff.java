package undermind.strategy;

import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType;
import undermind.strategy.representation.EnemyKeeper;
import undermind.strategy.representation.MyUnitKeeper;
import undermind.strategy.decision.Priorityzer;
import undermind.strategy.execution.Commander;
import undermind.strategy.execution.PreparationSequence;
import undermind.strategy.execution.Spawner;
import undermind.utilities.UndermindException;

/**
 * Created By: Itay Sabato<br/>
 * Date: 14/07/11 <br/>
 * Time: 15:15 <br/>
 *
 *   The chief of staff is the head of the army who puts everything
 *   together, besides some properties managed by the client.
 */
public class ChiefOfStaff {
    public final JNIBWAPI bwapi;
    private PreparationSequence preparationState;
    private MyUnitKeeper myUnitKeeper;
    private EnemyKeeper enemyKeeper;
    private Priorityzer prioritizer;
    private Commander commander;
    private Spawner spawner;

    public ChiefOfStaff(JNIBWAPI bwapi) {
        this.bwapi = bwapi;
        spawner = new Spawner(bwapi);
        myUnitKeeper = new MyUnitKeeper(this);
        enemyKeeper = new EnemyKeeper(this);
        commander = new Commander(this);
        prioritizer = new Priorityzer(this);
        preparationState = PreparationSequence.getInitialState();
    }

    public void gameUpdate() throws UndermindException {
        preparationState = preparationState.gameUpdate();
        if(preparationState == PreparationSequence.SCOUT){
            loadEnemyUnits();
            loadMyUnits();
            commandUnits();
            makeUnits();
        }
    }

    private void loadEnemyUnits() {
        enemyKeeper.loadEnemyUnits(bwapi.getEnemyUnits());
    }

    private void loadMyUnits() {
        myUnitKeeper.clean();
        for(Unit unit: bwapi.getMyUnits()){
            if(unit.getTypeID() == UnitType.UnitTypes.Zerg_Zergling.ordinal() || unit.getID() == preparationState.getScout()){
                myUnitKeeper.updateUnit(unit);
            }
        }
    }

    private void commandUnits() {
        commander.issueCommands();
    }

    private void makeUnits() {
        spawner.spawn();
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
