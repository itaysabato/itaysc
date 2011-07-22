package undermind;

import eisbot.proxy.model.Unit;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created By: Itay Sabato<br/>
 * Date: 21/07/11 <br/>
 * Time: 14:43 <br/>
 */
public class ZerglingKeeper implements Iterable<ZerglingStatus> {
    private Map<Integer,ZerglingStatus> zerglingStatusMap = new HashMap<Integer, ZerglingStatus>();
    private static final int CLOSE = 300;

    public void clean() {
        Iterator<Map.Entry<Integer,ZerglingStatus>> i = zerglingStatusMap.entrySet().iterator();
        while(i.hasNext()){
            if(UndermindClient.getMyClient().isDestroyed(i.next().getKey())){
                i.remove();
            }
        }
    }

    public void updateZergling(Unit zergling) {
        if(UndermindClient.getMyClient().isDestroyed(zergling.getID())){
            return;
        }
        ZerglingStatus status = zerglingStatusMap.get(zergling.getID());
        if(status == null){
            zerglingStatusMap.put(zergling.getID(),new ZerglingStatus(zergling.getID()));
            Out.println("kept zergling: "+zergling.getID());
        }
        else if(status.getState() == ZerglingState.IN_TRANSIT &&
                (zergling.isAttacking() || CLOSE >= Point.distance(status.getDestination().x,status.getDestination().y,zergling.getX(),zergling.getY()))){
            status.setDestination(null);
            status.setState(ZerglingState.FREE);
        }
        else if(status.getState() == ZerglingState.ATTACKING){
            Unit target = UndermindClient.getMyClient().bwapi.getUnit(status.getTarget());
            if(target == null || UndermindClient.getMyClient().isDestroyed(target.getID())){
                status.setTarget(-1);
                status.setState(ZerglingState.FREE);
            }
        }
    }

    public Iterator<ZerglingStatus> iterator() {
        return zerglingStatusMap.values().iterator();
    }
}

