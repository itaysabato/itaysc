package undermind;

import eisbot.proxy.model.Unit;

import java.awt.*;
import java.util.Set;

/**
 * Created By: Itay Sabato<br/>
 * Date: 01/08/11 <br/>
 * Time: 23:17 <br/>
 */
public class Runner {
    private static int RUN_DIST = 200;
    private static final int RUN_DIAG = (int) (RUN_DIST / 1.4142);
    private static final Point[] getAways = {
            new Point(RUN_DIST,0),
            new Point(0,RUN_DIST),
            new Point(0,-RUN_DIST),
            new Point(-RUN_DIST,0),
            new Point(-RUN_DIAG,-RUN_DIAG),
            new Point(-RUN_DIAG,RUN_DIAG),
            new Point(RUN_DIAG,-RUN_DIAG),
            new Point(RUN_DIAG,RUN_DIAG)
    };
    private final ChiefOfStaff chief;

    public Runner(ChiefOfStaff chief) {
        this.chief = chief;
    }

    public boolean shouldRun(Unit unit, Set<Unit> attackers) {
        if(attackers.isEmpty()){
            return false;
        }

        double dangerLevel = chief.getPrioritizer().getDangerLevel(attackers,true);
        return dangerLevel > 0;
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
