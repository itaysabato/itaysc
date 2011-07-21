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
    private Set<Integer> warriors = new HashSet<Integer>();
    private Set<Integer> workers = new HashSet<Integer>();
    private Set<Integer> suppliers = new HashSet<Integer>();
    private Set<Integer> mains = new HashSet<Integer>();
    private Set<Integer> others = new HashSet<Integer>();
    private static final double CLOSE = 1000;

    public void loadEnemyUnits(ArrayList<Unit> enemyUnits) {
        clean();
        for(Unit unit: enemyUnits){
            if(!UndermindClient.getMyClient().isDestroyed(unit.getID())){
                if(Utils.isCombative(unit)){
                    warriors.add(unit.getID());
                }
                else if(Utils.isWorker(unit)){
                    workers.add(unit.getID());
                }
                else if(Utils.isSupplier(unit)){
                    suppliers.add(unit.getID());
                }
                else if(Utils.isMain(unit)){
                    mains.add(unit.getID());
                }
                else {
                    others.add(unit.getID());
                }
            }
        }
    }

    private void clean() {
        clean(workers);
        clean(warriors);
        clean(suppliers);
        clean(mains);
        clean(others);
    }

    private void clean(Set<Integer> units) {
        for(Iterator<Integer> i = units.iterator(); i.hasNext();){
            if(UndermindClient.getMyClient().isDestroyed(i.next())){
                i.remove();
            }
        }
    }

    public Unit getCloseTarget(double x, double y) {
        Unit[] closests = findClosests(x, y);
        for(Unit target: closests){
            if(target != null && Point.distance(target.getX(),target.getY(),x,y) <= CLOSE){
                return target;
            }
        }
        return closests[0];
    }

    private Unit[] findClosests(double x, double y) {
        return new Unit[]{
                findClosest(filter(warriors),x,y),
                findClosest(filter(workers),x,y),
                findClosest(filter(suppliers),x,y),
                findClosest(filter(mains),x,y),
                findClosest(filter(others),x,y)
        };
    }

    private Unit findClosest(Set<Unit> units, final double x, final double y) {
        if(units.isEmpty()){
            return null;
        }

        return Collections.min(units, new Comparator<Unit>() {
            public int compare(Unit u1, Unit u2) {
                double d1 = Point.distance(u1.getX(),u1.getY(),x,y);
                double d2 = Point.distance(u2.getX(),u2.getY(),x,y);
                return d1 > d2 ?
                        1 : (d1 < d2 ? -1 : 0);
            }
        });
    }

    private Set<Unit> filter(Set<Integer> units) {
        Set<Unit> filtered = new HashSet<Unit>();
        for(int id: units){
            Unit unit = UndermindClient.getMyClient().bwapi.getUnit(id);
            if(unit != null && unit.isVisible() && !unit.isInvincible() && !Utils.isFlyer(unit)){
                filtered.add(unit);
            }
        }
        return filtered;
    }
}
