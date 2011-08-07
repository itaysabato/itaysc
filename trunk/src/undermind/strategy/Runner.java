package undermind.strategy;

import eisbot.proxy.model.Unit;
import undermind.dast.MyUnitStatus;
import undermind.utilities.Utils;
import undermind.utilities.MyUnitState;

import java.awt.*;
import java.util.Set;

/**
 * Created By: Itay Sabato<br/>
 * Date: 01/08/11 <br/>
 * Time: 23:17 <br/>
 */
public class Runner {
    private static int RUN_DIST = 150;
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

    public void run(Unit unit, MyUnitStatus status, Set<Unit> attackers) {
        double maxDist = 0;
        int bestGetAway = -1;

        //TODO: run towards choke point
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

    private void runAway(Unit unit, MyUnitStatus status, Point getAway) {
        status.setPreviousLocation(new Point(unit.getX(),unit.getY()));
        status.setHangCount(0);
        Point runTo = new Point(getAway.x + unit.getX(), getAway.y + unit.getY());
        status.setState(MyUnitState.RUNNING);
        status.setDestination(runTo);
        chief.bwapi.move(status.getUnitID(), runTo.x, runTo.y);
    }
}
