package undermind;

import eisbot.proxy.model.Unit;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created By: Itay Sabato<br/>
 * Date: 14/07/11 <br/>
 * Time: 16:25 <br/>
 */
public class Commander {
    private final ChiefOfStaff chief;
    private final Explorer explorer;
    private final Runner runner;
    private int[] batches = {6,6,6,6,6};
    private int batchIndex = 0;

    public Commander(ChiefOfStaff chiefOfStaff) {
        chief = chiefOfStaff;
        explorer = new Explorer();
        runner = new Runner(chiefOfStaff);
    }

    //TODO: unjam units
    public void issueCommands() {
//        Out.println("issuing commands");
        boolean sentNOOBs = false;
        Point enemyHome = UndermindClient.getMyClient().getEnemyHome();

        if(enemyHome != null){
//            Out.println("got enemy home");
            List<ZerglingStatus> active = new ArrayList<ZerglingStatus>();
            double centroidX = 0, centroidY = 0;

            for(ZerglingStatus status: chief.getZerglingKeeper()){
                Unit unit = chief.bwapi.getUnit(status.getUnitID());
                if(unit == null){
                    continue;
                }
                chief.bwapi.drawText(unit.getX() + 18,unit.getY(),status.getState().name(),false);
                Set<Unit> attackers = chief.getEnemyKeeper().getCloseAttackers(unit);
                if(runner.shouldRun(unit,attackers)){
                    runner.run(unit,status,attackers);
                    continue;
                }

                switch (status.getState()) {
                    case NOOB:
                        if(chief.getZerglingKeeper().getNOOBCount()  >= batches[batchIndex]){
                            sentNOOBs = true;
                            commandNoob(status, enemyHome);
                        }
                        break;

                    case IN_TRANSIT:
                        commandInTransit(unit, status);
                        break;

                    case RUNNING:
                    case ATTACKING:
                    case FREE:
                        active.add(status);
                        centroidX += unit.getX();
                        centroidY += unit.getY();
                        break;

                }
            }

            if(active.size() > 0){
                centroidX = centroidX / active.size();
                centroidY = centroidY / active.size();
                doSomething(active, (int) centroidX, (int) centroidY);
            }
        }
        else if(UndermindClient.getMyClient().getEnemyTemp() != null){
            Out.println("going for temp");
            for(ZerglingStatus status: chief.getZerglingKeeper()){
                commandNoob(status,UndermindClient.getMyClient().getEnemyTemp());
            }
        }
        if(sentNOOBs && batchIndex < batches.length -1){
            batchIndex++;
        }
    }

    private void commandRunning(Unit unit, ZerglingStatus status) {
        if(unit != null && unit.isIdle()){
            if(!runner.run(unit,status,chief.getEnemyKeeper().getCloseAttackers(unit))){
                status.setState(ZerglingState.FREE);
            }
        }
    }

    private void doSomething(List<ZerglingStatus> active, int centroidX, int centroidY) {
        chief.getPrioritizer().preProcess(chief.getEnemyKeeper());
        Unit target = chief.getEnemyKeeper().getCloseTarget(centroidX,centroidY);

        if(target != null){

            if(Utils.isNearHome(target)){
                Out.println("targeting near home unit: "+Utils.unitToString(target));
            }

            for(ZerglingStatus status: active){
                if(status.getState() == ZerglingState.ATTACKING){
                    Unit unit = chief.bwapi.getUnit(status.getUnitID());
                    if(unit != null && !unit.isIdle()){
                        Unit secondaryTarget = chief.bwapi.getUnit(status.getTarget());
                        if(secondaryTarget != null && shouldSwitchTarget(target,secondaryTarget)){
                            Out.println("target switched to: "+Utils.unitToString(target));
                            attack(status, target);
                        }
                    }
                    else {
                        attack(status,target);
                    }
                }
                else {
                    attack(status,target);
                }
            }
        }
        else {
            Point dest = explorer.findDestination(centroidX,centroidY);
            for(ZerglingStatus status: active){
                status.setState(ZerglingState.IN_TRANSIT);
                status.setDestination(dest);
                Out.println("exploring : "+status.toString());
                chief.bwapi.attack(status.getUnitID(),dest.x,dest.y);
            }
        }
    }

    private void attack(ZerglingStatus status, Unit target) {
//        Out.println("sending : " + status.toString());
        status.setState(ZerglingState.ATTACKING);
        status.setTarget(target.getID());
        status.setDestination(new Point(target.getX(),target.getY()));
        if(chief.bwapi.getUnit(target.getID()) != null && target.isVisible()) {
//            Out.println("target visible: "+Utils.unitToString(target));
            chief.bwapi.attack(status.getUnitID(), target.getID());
        }
        else {
//            Out.println("target not visible"+Utils.unitToString(target));
            chief.bwapi.attack(status.getUnitID(), target.getX(),target.getY());
        }
    }

    private boolean shouldSwitchTarget(Unit to, Unit from) {
        return to.getID() != from.getID() && chief.getPrioritizer().compare(to,from) < 0;
    }

    private void commandInTransit(Unit unit, ZerglingStatus status) {
        if(unit != null && unit.isIdle()){
            chief.bwapi.attack(status.getUnitID(), status.getDestination().x, status.getDestination().y);
        }
    }

    private void commandNoob(ZerglingStatus status, Point enemyHome) {
        status.setState(ZerglingState.IN_TRANSIT);
        status.setDestination(new Point(enemyHome.x,enemyHome.y));
//        Out.println("sent: "+status.toString());
        chief.bwapi.attack(status.getUnitID(), enemyHome.x, enemyHome.y);
    }
}