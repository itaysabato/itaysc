package undermind.strategy.decision;

import eisbot.proxy.model.ChokePoint;
import eisbot.proxy.model.Unit;
import undermind.utilities.Utils;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created By: Itay Sabato<br/>
 * Date: 01/08/11 <br/>
 * Time: 23:17 <br/>
 *
 *  Decides where to run.
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
    public Point getDestination(Unit unit, Set<Unit> attackers) {
        double maxDist = 0;
        Point bestGetAway = null;

        List<ChokePoint> chokePoints = Utils.getRegion(unit.getX(),unit.getY()).getChokePoints();

        for (ChokePoint chokePoint : chokePoints) {
            Point center = new Point(chokePoint.getCenterX(), chokePoint.getCenterY());

            if(Point.distance(center.x,center.y,unit.getX(),unit.getY()) <= chokePoint.getRadius()){
                return getRunFromChokePoint(unit, attackers);
            }

            double d = 0;
            for (Unit attacker : attackers) {
                d += Point.distance(center.x, center.y, attacker.getX(), attacker.getY());
            }
            d = d / ((double) attackers.size());
            if (maxDist < d) {
                maxDist = d;
                bestGetAway = new Point(chokePoint.getCenterX(), chokePoint.getCenterY());
            }
        }
        return bestGetAway;
    }

    public Point getRunFromChokePoint(Unit unit, Set<Unit> attackers) {
        double maxDist = 0;
        int bestGetAway = 0;

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
        return new Point(getAways[bestGetAway].x + unit.getX(), getAways[bestGetAway].y + unit.getY());
    }
}
