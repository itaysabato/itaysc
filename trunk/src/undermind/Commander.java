package undermind;


import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created By: Itay Sabato<br/>
 * Date: 14/07/11 <br/>
 * Time: 16:25 <br/>
 */
public class Commander {
    private final ChiefOfStaff chief;
    private final Explorer explorer;
    private final Priorityzer priorityzer;

    public Commander(ChiefOfStaff chiefOfStaff) {
        chief = chiefOfStaff;
        explorer = new Explorer();
        priorityzer = new Priorityzer();
    }

    public void issueCommands() {
        Point enemyHome = UndermindClient.getMyClient().getEnemyHome();

        if(enemyHome != null){
            List<ZerglingStatus> idles = new ArrayList<ZerglingStatus>();
            double centroidX = 0, centroidY = 0;

            for(ZerglingStatus status: chief.getZerglingKeeper()){
                Unit unit = chief.bwapi.getUnit(status.getUnitID());
                if(unit == null){
                    continue;
                }
                chief.bwapi.drawText(unit.getX() + 18,unit.getY(),status.getState().name(),false);

                switch (status.getState()) {
                    case NOOB:
                        commandNoob(unit, status, enemyHome);
                        break;

                    case IN_TRANSIT:
                        commandInTransit(unit, status);
                        break;

                    case ATTACKING:
                        if(!unit.isIdle()){
                            break;
                        }
                    case FREE:
                        idles.add(status);
                        centroidX += unit.getX();
                        centroidY += unit.getY();
                        break;
                }
            }

            if(idles.size() > 0){
                centroidX = centroidX / idles.size();
                centroidY = centroidY / idles.size();
                doSomthing(idles, centroidX, centroidY);
            }
        }
        else if(UndermindClient.getMyClient().getEnemyTemp() != null){
            Out.println("going for temp");
            for(ZerglingStatus status: chief.getZerglingKeeper()){
                commandNoob(chief.bwapi.getUnit(status.getUnitID()),status,UndermindClient.getMyClient().getEnemyTemp());
            }
        }
    }

    private void doSomthing(List<ZerglingStatus> idles, double centroidX, double centroidY) {
        Unit target = chief.getEnemyKeeper().getCloseTarget(centroidX,centroidY);

        if(target != null){
            Out.println("chosen target is: "+target.getID()+"of type: "+ UnitType.UnitTypes.values()[target.getTypeID()]);
            for(ZerglingStatus status: idles){
                attack(status,target);
            }

            for(ZerglingStatus status: chief.getZerglingKeeper()){
                if(status.getState() == ZerglingState.ATTACKING){
                    Unit secondaryTarget = chief.bwapi.getUnit(status.getTarget());
                    if(secondaryTarget != null && shouldSwitchTarget(target,secondaryTarget)){
                        Out.println("target switched to u1");
                        attack(status, target);
                    }
                }
            }
        }
        else {
            Point dest = explorer.findDestination(centroidX,centroidY);
            for(ZerglingStatus status: idles){
                status.setState(ZerglingState.FREE);
                status.setDestination(dest);
                Out.println("exploring : "+status.toString());
                chief.bwapi.attack(status.getUnitID(),dest.x,dest.y);
            }
        }
    }

    private void attack(ZerglingStatus status, Unit target) {
        Out.println("sending : " + status.toString());
        status.setState(ZerglingState.ATTACKING);
        status.setTarget(target.getID());
        if(target.isVisible()) {
            Out.println("target visible");
            chief.bwapi.attack(status.getUnitID(), target.getID());
        }
        else {
            Out.println("target not visible");
            Point lastSeen = chief.getEnemyKeeper().getCachedLocation(target.getID());
            chief.bwapi.attack(status.getUnitID(), lastSeen.x,lastSeen.y);
        }
        Out.println("sent : " + status.toString());
    }

    private boolean shouldSwitchTarget(Unit to, Unit from) {
        return to.getID() != from.getID() && chief.getPrioritizer().compare(to,from) < 0;
    }

    private void commandInTransit(Unit unit, ZerglingStatus status) {
        if(unit != null && unit.isIdle()){
            chief.bwapi.attack(status.getUnitID(), status.getDestination().x, status.getDestination().y);
        }
    }

    private void commandNoob(Unit unit, ZerglingStatus status, Point enemyHome) {
        status.setState(ZerglingState.IN_TRANSIT);
        status.setDestination(new Point(enemyHome.x,enemyHome.y));
        Out.println("sent: "+status.toString());
        chief.bwapi.attack(status.getUnitID(), enemyHome.x, enemyHome.y);
    }
}