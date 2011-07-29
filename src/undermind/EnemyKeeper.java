package undermind;

import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType;

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
    private static final double CLOSE = 1000;   //todo: bring it back?

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
            Out.println("NOT empty!");
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
                Out.println("using stored unit: "+Utils.unitToString(unit));
                chief.bwapi.drawCircle(unit.getX(),unit.getY(),50,0,false,false);
            }
            if(unit != null && !unit.isInvincible() && !Utils.isFlyer(unit)){
                filtered.add(unit);
//                if(nulls.contains(id)){
//                    Out.println("null unit is back! ["+id+","+ UnitType.UnitTypes.values()[unit.getTypeID()]+"]");
//                    nulls.remove(id);
//                }
            }
            else {
//                if(unit != null){
//                    Out.println(Utils.unitToString(unit)+"is filtered out because invincible? "+unit.isInvincible());
//                }
//                else {
//                    nulls.add(id);
//                    Out.println("unit "+id+"is filtered out because it was null! ");
//                }
            }
        }
        return filtered;
    }
}
