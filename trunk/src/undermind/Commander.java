package undermind;


import eisbot.proxy.model.Unit;

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
    private final Random random = new Random();

    public Commander(ChiefOfStaff chiefOfStaff) {
        chief = chiefOfStaff;
    }

    public void issueCommands() {
        UndermindClient client = UndermindClient.getMyClient();
        if(client.getEnemyHome() != null){
            List<ZerglingStatus> frees = new ArrayList<ZerglingStatus>();
            double centroidX = 0, centroidY = 0;
            Unit target = null;
            for(ZerglingStatus status: chief.getZerglingKeeper()){
                Unit unit = client.bwapi.getUnit(status.getUnitID());
                if(status.getState() == ZerglingState.NOOB){
                    status.setState(ZerglingState.IN_TRANSIT);
                    status.setDestination(new Point(client.getEnemyHome().x,client.getEnemyHome().y));
                    Out.println("sent noob: "+status.toString());
                    client.bwapi.move(status.getUnitID(), client.getEnemyHome().x, client.getEnemyHome().y);
                }
                else if(status.getState() == ZerglingState.FREE || status.getState() == ZerglingState.ATTACKING){
//                    client.bwapi.setGameSpeed(3);
                    if(unit != null && unit.isIdle()){
                        frees.add(status);
                        centroidX += unit.getX();
                        centroidY += unit.getY();
//                        if(status.getState() == ZerglingState.ATTACKING && unit.isIdle()){
//                            client.bwapi.attack(status.getUnitID(),status.getTarget());
//                        }
                    }
                }
                else if(status.getState() == ZerglingState.IN_TRANSIT){
                    if(unit != null && unit.isIdle()){
                        client.bwapi.move(status.getUnitID(), status.getDestination().x, status.getDestination().y);
                    }
                }
//                else if(status.getState() == ZerglingState.ATTACKING ){
//                    if(unit != null && unit.isIdle()){
//                        client.bwapi.attack(status.getUnitID(),status.getTarget());
//                    }
//                    if(target == null){
//                        target = client.bwapi.getUnit(status.getTarget());
//                    }
//                }
            }
            centroidX = centroidX / frees.size();
            centroidY = centroidY / frees.size();
            target = chief.getEnemyKeeper().getCloseTarget(centroidX,centroidY);

            if(target != null){
                for(ZerglingStatus status: frees){
                    status.setState(ZerglingState.ATTACKING);
                    status.setTarget(target.getID());
                    Out.println("sent : "+status.toString());
                    client.bwapi.attack(status.getUnitID(),target.getID());

                }
            }
            else {
                int x,y;
                do {
                    x = client.getEnemyHome().x + random.nextInt(500) - 300;
                    y = client.getEnemyHome().y + random.nextInt(500) - 300;
                } while(x < 0 || y < 0);
                Point dest = new Point(x,y);

                for(ZerglingStatus status: frees){
                    status.setState(ZerglingState.FREE);
                    status.setDestination(dest);
                    Out.println("sent : "+status.toString());
                    client.bwapi.attack(status.getUnitID(),x,y);
                }
            }
        }


    }
}
