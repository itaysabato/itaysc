package undermind;

import eisbot.proxy.model.Unit;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

/**
 * Created By: Itay Sabato<br/>
 * Date: 01/08/11 <br/>
 * Time: 23:17 <br/>
 */
public class Runner {
    private static final Point[] getAways = {
            new Point(100,0),
            new Point(0,100),
            new Point(0,-100),
            new Point(-100,0),
            new Point(-71,-71),
            new Point(-71,71),
            new Point(71,-71),
            new Point(71,71)
    };
    private final ChiefOfStaff chief;
    private static final int CLOSER = 10;

    public Runner(ChiefOfStaff chief) {
        this.chief = chief;
    }

    public boolean shouldRun(Unit unit, Set<Unit> attackers) {
//        Out.println("checking attackers for: "+Utils.unitToString(unit));
        if(attackers.isEmpty()){
            return false;
        }
//        Out.println("got attackers for: "+Utils.unitToString(unit));
//        Out.println("attackers: "+Utils.unitsToString(attackers));
        double dangerLevel = chief.getPrioritizer().getDangerLevel(attackers,false);
        return dangerLevel > 2;
    }

    public boolean run(Unit unit, ZerglingStatus status, Set<Unit> attackers) {
        Point2D bestDestination = findBestDestination(unit,status,attackers);
        if(bestDestination == null){
            Out.println("no where to run!!! ahhhhaaa!!!!");
            return false;
        }
        else {
            runAway(unit,status,new Point((int) bestDestination.getX(),(int) bestDestination.getY()));
            return true;
        }
    }

    private Point2D findBestDestination(Unit unit, final ZerglingStatus status, Set<Unit> attackers) {
        List<Line2D.Double> trajectories = getTrajectories(unit.getX(), unit.getY());

        if(status.getState() != ZerglingState.NOOB && status.getState() != ZerglingState.FREE){

            Collections.sort(trajectories, new Comparator<Line2D.Double>() {
                public int compare(Line2D.Double l1, Line2D.Double l2) {
                    double d1 = Point.distance(status.getDestination().getX(),status.getDestination().getY(),l1.x2,l1.y2);
                    double d2 = Point.distance(status.getDestination().getX(),status.getDestination().getY(),l2.x2,l2.y2);

                    return d1 > d2 ?
                            1 : (d1 < d2 ? -1 : 0);
                }
            });
        }

        for(Line2D trajectory: trajectories){
            boolean safe = true;
            for(Unit attacker: attackers){
                if(trajectory.ptLineDist(attacker.getX(),attacker.getY())
                        < (Point.distance(attacker.getX(),attacker.getY(),unit.getX(),unit.getY()) - CLOSER)){
                    safe = false;
                    break;
                }
            }
            if(safe){
                return trajectory.getP2();
            }
        }
        return trajectories.get(0).getP2();
    }

    private List<Line2D.Double> getTrajectories(int x, int y) {
        List<Line2D.Double> trajectories = new ArrayList<Line2D.Double>(getAways.length);

        for(Point getAway: getAways){
            trajectories.add(new Line2D.Double(x,y,x+getAway.x,y+getAway.y));
        }
        return trajectories;
    }

    private void runAway(Unit unit, ZerglingStatus status, Point runTo) {
        status.setState(ZerglingState.RUNNING);
        status.setDestination(runTo);
//        Out.println("running: "+Utils.unitToString(unit)+status);
        chief.bwapi.move(status.getUnitID(), runTo.x, runTo.y);
    }
}
