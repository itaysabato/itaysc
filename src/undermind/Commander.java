package undermind;

import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.BaseLocation;
import eisbot.proxy.model.Unit;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created By: Itay Sabato<br/>
 * Date: 14/07/11 <br/>
 * Time: 16:25 <br/>
 */
public class Commander {
    private JNIBWAPI bwapi;

    public Commander(JNIBWAPI bwapi) {
        this.bwapi = bwapi;
    }

    public void command(Integer unitID, SoldierState state) {
        Unit unit = bwapi.getUnit(unitID);
        if(unit == null){
            return;
        }

        if(state.isNearCombative()){
            runAway(unit);
        }
        else if(unit.isIdle()) {
            if(state.isNearWorker()){
                attackNearestWorker(unit);
            }
            else {
                Unit worker = findEnemyWorker();
                if(worker != null){
                    bwapi.attackMove(unitID,worker.getX(),worker.getY());
                }
                else if(state.isNearStructure()){
                    attackNearestStructure(unit);
                }
                else if(unit.isIdle()){
                    Unit structure = findEnemyStructure();
                    if(structure != null){
                        bwapi.attackMove(unitID,structure.getX(),structure.getY());
                    }
                    explore(unit);
                }
            }
        }
    }

    private Unit findEnemyStructure() {
        for(Unit enemyUnit: bwapi.getEnemyUnits()){
            if(Utils.isStructure(enemyUnit)){
                return enemyUnit;
            }
        }
        return null;
    }

    private void runAway(Unit unit) {
        //To change body of created methods use File | Settings | File Templates.
    }

    private void attackNearestWorker(Unit unit) {
        List<Unit> workers = new LinkedList<Unit>();
        for(Unit enemyUnit: bwapi.getEnemyUnits()){
            if(Utils.isWorker(enemyUnit)){
                workers.add(enemyUnit);
            }
        }
        Unit worker = Collections.min(workers, getUnitComparator(unit)) ;
        bwapi.attackMove(unit.getID(),worker.getX(),worker.getY());
    }

    private Comparator<Unit> getUnitComparator(final Unit unit) {
        return new Comparator<Unit>() {
            public int compare(Unit o1, Unit o2) {
                double d1 = Utils.distance(o1.getX(), o1.getY(), unit.getX(), unit.getY());
                double d2 = Utils.distance(o2.getX(), o2.getY(), unit.getX(),unit.getY());
                return d1 > d2 ?
                        1 : (d1 < d2 ? -1 : 0);
            }
        };
    }

    private Unit findEnemyWorker() {
        for(Unit enemyUnit: bwapi.getEnemyUnits()){
            if(Utils.isWorker(enemyUnit)){
                return enemyUnit;
            }
        }
        return null;
    }

    private void attackNearestStructure(Unit unit) {
        List<Unit> structures = new LinkedList<Unit>();
        for(Unit enemyUnit: bwapi.getEnemyUnits()){
            if(Utils.isStructure(enemyUnit)){
                structures.add(enemyUnit);
            }
        }
        Unit structure = Collections.min(structures, getUnitComparator(unit)) ;
        bwapi.attackMove(unit.getID(),structure.getX(),structure.getY());
    }
    private void explore(Unit unit) {
        for (Unit enemy : bwapi.getEnemyUnits()) {
            if(enemy.isExists()){
                bwapi.attackMove(unit.getID(), enemy.getX(), enemy.getY());
                break;
            }
        }
    }




}
