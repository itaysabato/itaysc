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
    private Map<Integer, Point> spottedEnemies = new HashMap<Integer, Point>();
    private static final double CLOSE = 1000;   //todo: bring it back?

    public EnemyKeeper(ChiefOfStaff chief) {
        this.chief = chief;
    }

    public void loadEnemyUnits(ArrayList<Unit> enemyUnits) {
        clean();
        for(Unit unit: enemyUnits){
            Out.println("enemy: "+unit.getID()+" of type: "+ UnitType.UnitTypes.values()[unit.getTypeID()]+" is present");
            chief.bwapi.drawCircle(unit.getX(),unit.getY(),20,1,false,false);

            if(!UndermindClient.getMyClient().isDestroyed(unit.getID())){
                spottedEnemies.put(unit.getID(),new Point(unit.getX(),unit.getY()));
            }
        }
    }

    private void clean() {
        for(Iterator<Map.Entry<Integer,Point>> i = spottedEnemies.entrySet().iterator(); i.hasNext();){
            if(UndermindClient.getMyClient().isDestroyed(i.next().getKey())){
                i.remove();
            }
        }
    }

    public Unit getCloseTarget(final double x, final double y) {
        Set<Unit> filtered = filterEnemies();

        if(filtered.isEmpty()){
            Out.println("empty!");
            return null;
        }
        else {
           chief. bwapi.setGameSpeed(-1);
            Out.println("NOT empty!");
        }
        return Collections.min(filtered, new Comparator<Unit>() {
            public int compare(Unit u1, Unit u2) {
                int priorityComparison = chief.getPrioritizer().compare(u1,u2);
                 Out.println("comparison: "+priorityComparison);
                if(priorityComparison == 0){
                    Point p1 = spottedEnemies.get(u1.getID());
                    double d1 = Point.distance(p1.x,p1.y,x,y);

                    Point p2 = spottedEnemies.get(u2.getID());
                    double d2 = Point.distance(p2.x,p2.y,x,y);

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
        for(int id: spottedEnemies.keySet()){
            Unit unit = UndermindClient.getMyClient().bwapi.getUnit(id);
            if(unit != null &&  !unit.isInvincible() && !Utils.isFlyer(unit)){
                filtered.add(unit);
            }
            else {
                if(unit != null){
                    Out.println("unit "+unit.getID()+" of type: "+ UnitType.UnitTypes.values()[unit.getTypeID()]+"is filtered out because invincible? "+unit.isInvincible());
                }
                else {
                    Out.println("unit "+id+"is filtered out because it was null! ");
                }
            }
        }
        return filtered;
    }

    public Point getCachedLocation(int id) {
        return spottedEnemies.get(id);
    }
}
