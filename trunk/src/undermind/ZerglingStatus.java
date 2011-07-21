package undermind;

import java.awt.*;

/**
 * Created By: Itay Sabato<br/>
 * Date: 21/07/11 <br/>
 * Time: 15:15 <br/>
 */
public class ZerglingStatus {
    private ZerglingState state = ZerglingState.NOOB;
    private Point destination = null;
    private int target = -1;
    private final int unitID;

    @Override
    public String toString() {
        return "[ id="+unitID+" state="+state+" destination="+destination+" target="+target+" ]";
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
}
