package undermind.strategy.representation;

import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType;
import undermind.strategy.ChiefOfStaff;
import undermind.UndermindClient;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created By: Itay Sabato<br/>
 * Date: 21/07/11 <br/>
 * Time: 14:43 <br/>
 *
 *  This class stores information about my units
 *  which is updated every frame.
 */
public class MyUnitKeeper implements Iterable<MyUnitStatus> {
    private int NOOBCount;
    private final ChiefOfStaff chief;
    private Map<Integer,MyUnitStatus> myUnitStatusMap = new HashMap<Integer, MyUnitStatus>();

    private static final int CLOSE = 60;

    public MyUnitKeeper(ChiefOfStaff chief) {
        this.chief = chief;
    }

    public void clean() {
        NOOBCount = 0;
        Iterator<Map.Entry<Integer,MyUnitStatus>> i = myUnitStatusMap.entrySet().iterator();
        while(i.hasNext()){
            if(UndermindClient.getMyClient().isDestroyed(i.next().getKey())){
                i.remove();
            }
        }
    }

    public void updateUnit(Unit unit) {
        if(UndermindClient.getMyClient().isDestroyed(unit.getID())){
            return;
        }

        MyUnitStatus status = myUnitStatusMap.get(unit.getID());
        if(status == null){
            status = new MyUnitStatus(unit.getID());
            myUnitStatusMap.put(unit.getID(), status);
        }

        if(status.getState() == MyUnitState.NOOB){
            if(unit.getTypeID() == UnitType.UnitTypes.Zerg_Zergling.ordinal()){
                NOOBCount++;
            }
        }
        else if(status.getState() == MyUnitState.IN_TRANSIT){
            updateInTransit(unit, status);
        }
        else if(status.getState() == MyUnitState.ATTACKING){
            updateAttacking(unit, status);
        }
        else if(status.getState() == MyUnitState.EXPLORING){
            updateExploring(unit, status);
        }
        else if(status.getState() == MyUnitState.RUNNING){
            updateRunning(unit, status);
        }
    }

    private void updateInTransit(Unit unit, MyUnitStatus status) {
        if(unit.isAttacking() || CLOSE >= Point.distance(status.getDestination().x, status.getDestination().y, unit.getX(), unit.getY())){
            status.setState(MyUnitState.FREE);
        }
    }

    private void updateAttacking(Unit unit, MyUnitStatus status) {
        if(chief.getEnemyKeeper().getEnemyUnit(status.getTarget()) == null || unit.isIdle()){
            status.setTarget(-1);
            status.setState(MyUnitState.FREE);
        }
        else{
            Unit target = UndermindClient.getMyClient().bwapi.getUnit(status.getTarget());
            if(target != null && target.isVisible()) {
                status.setDestination(new Point(target.getX(),target.getY()));
            }
        }
    }

    private void updateExploring(Unit unit, MyUnitStatus status) {
        if(unit.isAttacking()
                || CLOSE >= Point.distance(status.getDestination().x, status.getDestination().y, unit.getX(), unit.getY())
                || status.isStuck(unit)){
            status.setState(MyUnitState.FREE);
            status.setPreviousLocation(new Point(unit.getX(),unit.getY()));
            status.setHangCount(0);
        }
    }

    private void updateRunning(Unit unit, MyUnitStatus status) {
        if(unit.isIdle() || unit.isAttacking()
                || CLOSE >= Point.distance(status.getDestination().x, status.getDestination().y, unit.getX(), unit.getY())
                || status.isStuck(unit)){

            status.setPreviousLocation(new Point(unit.getX(),unit.getY()));
            status.setHangCount(0);
            status.setDestination(null);
            status.setState(MyUnitState.RAN);
        }
    }

    public Iterator<MyUnitStatus> iterator() {
        return myUnitStatusMap.values().iterator();
    }

    public int getNOOBCount() {
        return NOOBCount;
    }
}

