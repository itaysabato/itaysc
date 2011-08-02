package undermind;

import eisbot.proxy.model.Unit;

import java.awt.*;
import java.util.*;

/**
 * Created By: Itay Sabato<br/>
 * Date: 21/07/11 <br/>
 * Time: 14:44 <br/>
 */
public class EnemyKeeper {
    private  final ChiefOfStaff chief;
    private Map<Integer, Unit> spottedEnemies = new HashMap<Integer, Unit>();
    private Set<Unit> dangerousUnits;

    private static final double CLOSE = 1000;   //todo: bring it back?
    private static final int RADIUS = 120;

    public EnemyKeeper(ChiefOfStaff chief) {
        this.chief = chief;
    }

    public void loadEnemyUnits(ArrayList<Unit> enemyUnits) {
        clean();

        for(Unit unit: enemyUnits){
            if(!UndermindClient.getMyClient().isDestroyed(unit.getID())){
                spottedEnemies.put(unit.getID(),unit);
            }
        }

        dangerousUnits = new HashSet<Unit>();
        for (Unit unit: spottedEnemies.values()){
            if(Utils.classify(unit) == UnitClass.HARMFUL || unit.isAttacking() || unit.isStartingAttack()){
                dangerousUnits.add(unit);
            }
        }
    }

    private void clean() {
        for(Iterator<Map.Entry<Integer,Unit>> i = spottedEnemies.entrySet().iterator(); i.hasNext();){
            Map.Entry<Integer,Unit> entry =i.next();
            if(UndermindClient.getMyClient().isDestroyed(entry.getKey())
                    || Utils.isGone(entry.getValue(),chief.bwapi)){
                i.remove();
            }
        }
    }

    public Unit getCloseTarget(final double x, final double y) {
        Set<Unit> filtered = filterEnemies();

        if(filtered.isEmpty()){
//            Out.println("empty!");
            return null;
        }
        else {
//           chief. bwapi.setGameSpeed(-1);
//            Out.println("NOT empty!");
        }
        return Collections.min(filtered, new Comparator<Unit>() {
            public int compare(Unit u1, Unit u2) {
                int priorityComparison = chief.getPrioritizer().compare(u1,u2);
//                 Out.println("comparison: "+priorityComparison);
                if(priorityComparison == 0){
                    double d1 = Point.distance(u1.getX(),u1.getY(),x,y);
                    double d2 = Point.distance(u2.getX(),u2.getY(),x,y);

                    return d1 > d2 ?
                            1 : (d1 < d2 ? -1 : 0);
                }
                else {
                    return priorityComparison;
                }
            }
        });
    }

    private Set<Unit> filterEnemies() {
        Set<Unit> filtered = new HashSet<Unit>();
        for(Map.Entry<Integer, Unit> enemy: spottedEnemies.entrySet()){
            Unit unit = UndermindClient.getMyClient().bwapi.getUnit(enemy.getKey());

            if(unit == null){
                unit = enemy.getValue();
//                Out.println("using stored unit: "+Utils.unitToString(unit));
                chief.bwapi.drawCircle(unit.getX(),unit.getY(),50,0,false,false);
            }
            if(!unit.isInvincible() && !Utils.isFlyer(unit)){
                filtered.add(unit);
            }
        }
        return filtered;
    }

    public Set<Unit> getDangerousUnits() {
        return dangerousUnits;
    }

    public Set<Unit> getCloseAttackers(Unit myUnit) {
        Set<Unit> closeAttackers = new HashSet<Unit>();
        for(Unit enemyUnit: dangerousUnits){
//            Out.println("checking if "+Utils.unitToString(enemyUnit)+"is close to "+Utils.unitToString(myUnit));
            double d =Point.distance(myUnit.getX(),myUnit.getY(),enemyUnit.getX(),enemyUnit.getY());
//            Out.println("distance is: "+d);
            if(d <= RADIUS){
                closeAttackers.add(enemyUnit);
//                Out.println("yes");
            }
        }
        return closeAttackers;
    }
}
