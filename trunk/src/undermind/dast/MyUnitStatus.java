package undermind.dast;

import eisbot.proxy.model.Unit;
import undermind.utilities.MyUnitState;

import java.awt.*;

/**
 * Created By: Itay Sabato<br/>
 * Date: 21/07/11 <br/>
 * Time: 15:15 <br/>
 */
public class MyUnitStatus {
    private final int unitID;
    private MyUnitState state = MyUnitState.NOOB;
    private Point destination = null;
    private int target = -1;
    private Point previousLocation;
    private long hangCount;
    private static final int HANG_RADIUS = 75;
    private static final long HANG_LIMIT = 400;

    @Override
    public String toString() {
        return "[MyUnitStatus: id="+unitID+" state="+state+" destination="+destination+" target="+target+" ]";
    }

    public MyUnitStatus(int unitID) {
        this.unitID = unitID;
    }

    public Point getDestination() {
        return destination;
    }

    public void setDestination(Point destination) {
        this.destination = destination;
    }

    public MyUnitState getState() {
        return state;
    }

    public void setState(MyUnitState state) {
        this.state = state;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getUnitID() {
        return unitID;
    }

    public void setPreviousLocation(Point previousLocation) {
        this.previousLocation = previousLocation;
    }

    public boolean isStuck(Unit unit) {
        if(Point.distance(unit.getX(),unit.getY(),previousLocation.x,previousLocation.y) <= HANG_RADIUS){
            hangCount++;
            return hangCount > HANG_LIMIT;
        }
        else {
            previousLocation = new Point(unit.getX(),unit.getY());
            hangCount = 0;
            return false;
        }
    }

    public void setHangCount(int hangCount) {
        this.hangCount = hangCount;
    }
}
