package undermind.strategy;

import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType;
import undermind.UndermindClient;
import undermind.dast.ZerglingStatus;
import undermind.utilities.Out;
import undermind.utilities.ZerglingState;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created By: Itay Sabato<br/>
 * Date: 14/07/11 <br/>
 * Time: 16:25 <br/>
 */
public class Commander {
    private final ChiefOfStaff chief;
    private final Explorer explorer;
    private final Runner runner;
    private int[] batches = {2};
    private int batchIndex = 0;

    public Commander(ChiefOfStaff chiefOfStaff) {
        chief = chiefOfStaff;
        explorer = new Explorer();
        runner = new Runner(chiefOfStaff);
    }

    public void issueCommands() {
        boolean sentNOOBs = false;
        Point enemyHome = UndermindClient.getMyClient().getEnemyHome();

        if(enemyHome != null){
            List<ZerglingStatus> active = new ArrayList<ZerglingStatus>();
            List<ZerglingStatus> transits = new ArrayList<ZerglingStatus>();
            double centroidX = 0, centroidY = 0;

            for(ZerglingStatus status: chief.getZerglingKeeper()){
                Unit unit = chief.bwapi.getUnit(status.getUnitID());
                if(unit == null){
                    continue;
                }
                chief.bwapi.drawText(unit.getX() + 18,unit.getY(),status.getState().name(),false);
                if(unit.getTypeID() == UnitType.UnitTypes.Zerg_Drone.ordinal()){
                    handleDrone(status, unit);
                    continue;
                }

                switch (status.getState()) {
                    case NOOB:
                        commandNoob(status, enemyHome);
                        if(chief.getZerglingKeeper().getNOOBCount()  >= batches[batchIndex]){
                            sentNOOBs = true;
                            commandNoob(status, enemyHome);
                        }
                        break;

                    case IN_TRANSIT:
                        transits.add(status);
                        commandInTransit(unit, status);
                        break;

                    case EXPLORING:
                        transits.add(status);
                        commandExploring(unit, status);
                        break;

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
                doSomething(active, transits, centroidX, centroidY, false);
            }
        }
        else if(UndermindClient.getMyClient().getEnemyTemp() != null){
            for(ZerglingStatus status: chief.getZerglingKeeper()){
                commandNoob(status,UndermindClient.getMyClient().getEnemyTemp());
            }
        }
        if(sentNOOBs){
            UndermindClient.getMyClient().numBatches++;
        }

        if(sentNOOBs && batchIndex < batches.length -1){
            batchIndex++;
        }
    }

    private void handleDrone(ZerglingStatus status, Unit unit) {
        Set<Unit> attackers = chief.getEnemyKeeper().getCloseAttackers(unit);
        if(runner.shouldRun(unit, attackers)){
            runner.run(unit,status,attackers);
            return;
        }

        switch (status.getState()) {
            case NOOB:
                commandNoob(status, UndermindClient.getMyClient().getEnemyHome());
                break;

            case IN_TRANSIT:
                commandInTransit(unit, status);
                break;

            case EXPLORING:
                commandExploring(unit, status);
                break;

            case RAN:
            case ATTACKING:
            case FREE:
                doSomething(Collections.singletonList(status), Collections.<ZerglingStatus>emptyList(), unit.getX(), unit.getY(), true);
                break;
        }
    }

    private void doSomething(List<ZerglingStatus> active, List<ZerglingStatus> transits, double centroidX, double centroidY, boolean isDrone) {
        Unit target = chief.getEnemyKeeper().getCloseTarget(centroidX,centroidY,isDrone);

        if(target != null && (!isDrone || active.get(0).getState() != ZerglingState.RAN)){
            for(ZerglingStatus status: transits){
                Unit unit = chief.bwapi.getUnit(status.getUnitID());
                status.setPreviousLocation(new Point(unit.getX(),unit.getY()));
                status.setHangCount(0);
                attack(status, target);
            }

            for(ZerglingStatus status: active){
                if(status.getState() == ZerglingState.ATTACKING){
                    handleAttacking(target, status);
                }
                else {
                    attack(status,target);
                }
            }
        }
        else {
            Point enemyMain = chief.getEnemyKeeper().getEnemyMain();
            if(enemyMain == null){
                enemyMain = new Point((int)centroidX,(int)centroidY);
            }
            Point dest = explorer.findDestination(enemyMain.x,enemyMain.y,isDrone);
            Out.println("dest: " + dest);
            for(ZerglingStatus status: active){
                status.setState(ZerglingState.EXPLORING);
                status.setDestination(dest);
                Unit unit = chief.bwapi.getUnit(status.getUnitID());
                status.setPreviousLocation(new Point(unit.getX(),unit.getY()));
                status.setHangCount(0);
                chief.bwapi.attack(status.getUnitID(), dest.x, dest.y);
            }
        }
    }

    private void handleAttacking(Unit target, ZerglingStatus status) {
        Unit unit = chief.bwapi.getUnit(status.getUnitID());
        if(unit != null && !unit.isIdle()){
            Unit currentTarget = chief.getEnemyKeeper().getEnemyUnit(status.getTarget());
            if(shouldSwitchTarget(target,currentTarget)){
                attack(status, target);
            }
        }
        else {
            attack(status,target);
        }
    }

    private void attack(ZerglingStatus status, Unit target) {
        status.setState(ZerglingState.ATTACKING);
        status.setTarget(target.getID());
        status.setDestination(new Point(target.getX(),target.getY()));
        if(chief.bwapi.getUnit(target.getID()) != null && target.isVisible()) {
            chief.bwapi.attack(status.getUnitID(), target.getID());
        }
        else {
            status.setState(ZerglingState.FREE);
            chief.bwapi.attack(status.getUnitID(), target.getX(), target.getY());
        }
    }

    private boolean shouldSwitchTarget(Unit to, Unit from) {
        return to.getID() != from.getID() && chief.getPrioritizer().compare(to,from) < 0;
    }

    private void commandExploring(Unit unit, ZerglingStatus status) {
        if(unit != null && unit.isIdle()){
            chief.bwapi.attack(status.getUnitID(), status.getDestination().x, status.getDestination().y);
        }
    }

    private void commandInTransit(Unit unit, ZerglingStatus status) {
        if(unit != null && unit.isIdle()){
            chief.bwapi.attack(status.getUnitID(), status.getDestination().x, status.getDestination().y);
        }
    }

    private void commandNoob(ZerglingStatus status, Point enemyHome) {
        status.setState(ZerglingState.IN_TRANSIT);
        status.setDestination(new Point(enemyHome.x,enemyHome.y));
        chief.bwapi.attack(status.getUnitID(), enemyHome.x, enemyHome.y);
    }
}
