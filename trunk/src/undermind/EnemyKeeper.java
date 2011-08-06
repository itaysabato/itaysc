package undermind;

import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType;
import eisbot.proxy.util.BWColor;

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
    private static final int RADIUS = 100;

    public EnemyKeeper(ChiefOfStaff chief) {
        this.chief = chief;
    }

    public void loadEnemyUnits(ArrayList<Unit> enemyUnits) {
        clean();

        for(Unit unit: enemyUnits){
            if(!UndermindClient.getMyClient().isDestroyed(unit.getID())){
                if(unit.isUnpowered()){
                    chief.bwapi.drawCircle(unit.getX(),unit.getY(),50, BWColor.RED,true,false);
                }
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
            return null;
        }

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

    private Set<Unit> filterEnemies() {
        Set<Unit> filtered = new HashSet<Unit>();
        for(Map.Entry<Integer, Unit> enemy: spottedEnemies.entrySet()){
            Unit unit = UndermindClient.getMyClient().bwapi.getUnit(enemy.getKey());

            if(unit == null){
                unit = enemy.getValue();
                chief.bwapi.drawCircle(unit.getX(),unit.getY(),50,BWColor.WHITE,false,false);
            }
            if(!unit.isInvincible() && !Utils.isFlyer(unit) && !unit.isLifted()){
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
        for(Unit enemyUnit: spottedEnemies.values()){
            double d =Point.distance(myUnit.getX(),myUnit.getY(),enemyUnit.getX(),enemyUnit.getY());
            if(d <= RADIUS){
                closeAttackers.add(enemyUnit);
            }
        }
        return closeAttackers;
    }

    public int getPoweringCount(Unit pylon) {
        int poweringCount = 0;
        for(Unit unit: spottedEnemies.values()){
                 if(unit.getTypeID() != UnitType.UnitTypes.Protoss_Nexus.ordinal()
                         && Utils.isProtossStructure(unit.getTypeID())
                         && Utils.isPowering(pylon,unit)){
                        poweringCount++;
                 }
        }
        return poweringCount;
    }

    public Unit getEnemyUnit(int unitID) {
        return spottedEnemies.get(unitID);
    }
}
