package undermind;

import eisbot.proxy.model.Unit;

import java.awt.*;

/**
 * Created By: Itay Sabato<br/>
 * Date: 21/07/11 <br/>
 * Time: 15:15 <br/>
 */
public class ZerglingStatus {
    private final int unitID;
    private ZerglingState state = ZerglingState.NOOB;
    private Point destination = null;
    private int target = -1;
    private Point runningDestination;
    private Point previousLocation;
    private long hangCount;
    private static final int HANG_RADIUS = 75;
    private static final long HANG_LIMIT = 500;

    @Override
    public String toString() {
        return "[ZerglingStatus: id="+unitID+" state="+state+" destination="+destination+" target="+target+" ]";
    }

    public ZerglingStatus(int unitID) {
        this.unitID = unitID;
    }

    public Point getDestination() {
        return destination;
    }

    public void setDestination(Point destination) {
        this.destination = destination;
    }

    public ZerglingState getState() {
        return state;
    }

    public void setState(ZerglingState state) {
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

    public Point getPreviousLocation() {
        return previousLocation;
    }

    public void setPreviousLocation(Point previousLocation) {
        this.previousLocation = previousLocation;
    }

    public Point getRunningDestination() {
        return runningDestination;
    }

    public void setRunningDestination(Point runningDestination) {
        this.runningDestination = runningDestination;
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
