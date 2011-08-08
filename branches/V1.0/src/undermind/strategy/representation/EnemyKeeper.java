package undermind.strategy.representation;

import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType;
import undermind.strategy.ChiefOfStaff;
import undermind.UndermindClient;
import undermind.utilities.Utils;

import java.awt.*;
import java.util.*;

/**
 * Created By: Itay Sabato<br/>
 * Date: 21/07/11 <br/>
 * Time: 14:44 <br/>
 *
 *  This class stores information about enemy units
 *  which is updated every frame.
 */
public class EnemyKeeper {
    private  final ChiefOfStaff chief;
    private Rectangle enemyHomeBounds;
    private Map<Integer, Unit> spottedEnemies = new HashMap<Integer, Unit>();

    private static final int ATTACKER_RADIUS = 150;
    private static final int ENEMY_MARGIN = 500;
    private Point enemyMainLocation = null;

    public EnemyKeeper(ChiefOfStaff chief) {
        this.chief = chief;
    }

    public void loadEnemyUnits(ArrayList<Unit> enemyUnits) {
        clean();
        for(Unit unit: enemyUnits){
            if(!UndermindClient.getMyClient().isDestroyed(unit.getID())){
                spottedEnemies.put(unit.getID(),unit);
            }
            if(Utils.classify(unit) == UnitClass.MAIN && enemyMainLocation == null){
                enemyMainLocation = new Point(unit.getX(),unit.getY());
            }
        }
        calculateEnemyTerritory();
    }

    private void clean() {
        for(Iterator<Map.Entry<Integer,Unit>> i = spottedEnemies.entrySet().iterator(); i.hasNext();){
            Map.Entry<Integer,Unit> entry = i.next();
            if(UndermindClient.getMyClient().isDestroyed(entry.getKey())
                    || Utils.isGone(entry.getValue(),chief.bwapi)){
                i.remove();
            }
        }
    }

    private void calculateEnemyTerritory() {
        enemyHomeBounds = null;
        for (Unit unit: spottedEnemies.values()){
            if(Utils.isStructure(unit)){
                if(enemyHomeBounds == null){
                    enemyHomeBounds = new Rectangle(new Point(unit.getX(),unit.getY()));
                }
                else {
                    enemyHomeBounds.add(new Point(unit.getX(),unit.getY()));
                }
            }
        }
        if(enemyHomeBounds != null){
            enemyHomeBounds = new Rectangle(enemyHomeBounds.x - ENEMY_MARGIN,enemyHomeBounds.y - ENEMY_MARGIN, enemyHomeBounds.width + (2* ENEMY_MARGIN), enemyHomeBounds.height + (2* ENEMY_MARGIN));
        }
    }

    public Unit getCloseTarget(final double x, final double y, boolean isForDrone) {
        Set<Unit> filtered = filterTargets(isForDrone);

        if(filtered.isEmpty()){
            return null;
        }

        //  ordered firstly by priority and secondly by distance.
        return Collections.min(filtered, new Comparator<Unit>() {
            public int compare(Unit u1, Unit u2) {
                int priorityComparison = chief.getPrioritizer().compare(u1,u2);
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

    private Set<Unit> filterTargets(boolean isForDrone) {
        Set<Unit> filtered = new HashSet<Unit>();
        for(Map.Entry<Integer, Unit> enemy: spottedEnemies.entrySet()){
            Unit unit = UndermindClient.getMyClient().bwapi.getUnit(enemy.getKey());
            if(unit == null){
                unit = enemy.getValue();
            }
            if(!unit.isInvincible() && !Utils.isFlyer(unit) && !unit.isLifted() && (!isForDrone || Utils.isStructure(unit))){
                filtered.add(unit);
            }
        }
        return filtered;
    }

    public Set<Unit> getCloseAttackers(Unit myUnit) {
        Set<Unit> closeAttackers = new HashSet<Unit>();
        for(Unit enemyUnit: spottedEnemies.values()){
            UnitClass unitClass = Utils.classify(enemyUnit);

            if(unitClass == UnitClass.HARMFUL || unitClass == UnitClass.ATTACKING_WORKER || unitClass == UnitClass.WORKER){
                double d =Point.distance(myUnit.getX(),myUnit.getY(),enemyUnit.getX(),enemyUnit.getY());
                if(d <= ATTACKER_RADIUS){
                    closeAttackers.add(enemyUnit);
                }
            }
        }
        return closeAttackers;
    }

    //  counts how many buildings this pylon powers
    public int getPoweringCount(Unit pylon) {
        int poweringCount = 0;
        for(Unit unit: spottedEnemies.values()){
            if(unit.getTypeID() != UnitType.UnitTypes.Protoss_Nexus.ordinal()
                    && unit.getTypeID() != UnitType.UnitTypes.Protoss_Pylon.ordinal()
                    && Utils.isProtossStructure(unit.getTypeID())
                    && Utils.isPowering(pylon, unit)){
                poweringCount++;
            }
        }
        return poweringCount;
    }

    public Unit getEnemyUnit(int unitID) {
        return spottedEnemies.get(unitID);
    }

    public Rectangle getEnemyHomeBounds() {
        return enemyHomeBounds;
    }

    public Point getEnemyMainLocation() {
        return enemyMainLocation;
    }
}
