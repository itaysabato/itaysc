package undermind;

import eisbot.proxy.model.Unit;

import java.awt.*;
import java.util.Collections;
import java.util.Set;

/**
 * Created By: Itay Sabato<br/>
 * Date: 01/08/11 <br/>
 * Time: 23:17 <br/>
 */
public class Runner {
    private static final Point[] getAways = {
            new Point(200,0),
            new Point(0,200),
            new Point(0,-200),
            new Point(-200,0),
            new Point(-141,-141),
            new Point(-141,141),
            new Point(141,-141),
            new Point(141,141)
    };
    private final ChiefOfStaff chief;

    public Runner(ChiefOfStaff chief) {
        this.chief = chief;
    }

    public boolean shouldRun(Unit unit, Set<Unit> attackers) {
//        Out.println("checking attackers for: "+Utils.unitToString(unit));
        if(attackers.isEmpty()){
            return false;
        }
        Out.println("got attackers for: "+Utils.unitToString(unit));
        Out.println("attackers: "+Utils.unitsToString(attackers));
        double dangerLevel = chief.getPrioritizer().getDangerLevel(attackers,true);
        return dangerLevel > 2;
    }

    public void run(Unit unit, ZerglingStatus status, Set<Unit> attackers) {
        Out.println("finding where to run: "+Utils.unitToString(unit));
        double maxDist = 0;
        int bestGetAway = -1;

    //TODO: run towards target
        for(int i = 0; i < getAways.length; i++){
            Point runTo = new Point(unit.getX()+getAways[i].x, unit.getY()+getAways[i].y);
            double d = 0;
            for(Unit attacker: attackers){
                d+= Point.distance(runTo.x,runTo.y,attacker.getX(),attacker.getY());
            }
            d = d / ((double) attackers.size());
            if(maxDist < d){
                maxDist = d;
                bestGetAway = i;
            }
        }
        if(bestGetAway >= 0){
            runAway(unit,status,getAways[bestGetAway]);
        }
    }

    private void runAway(Unit unit, ZerglingStatus status, Point getAway) {
        Point runTo = new Point(getAway.x + unit.getX(), getAway.y + unit.getY());
        status.setState(ZerglingState.RUNNING);
        status.setDestination(runTo);
        Out.println("running: "+Utils.unitToString(unit)+status);
        chief.bwapi.move(status.getUnitID(), runTo.x, runTo.y);
    }
}
