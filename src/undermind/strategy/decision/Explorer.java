package undermind.strategy.decision;

import eisbot.proxy.model.BaseLocation;
import undermind.UndermindClient;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created By: Itay Sabato<br/>
 * Date: 22/07/11 <br/>
 * Time: 13:57 <br/>
 *
 *  Decides which parts of the map to explore.
 */
public class Explorer {
    private static final Random random = new Random();
    private List<Point> toExplore = null;
    private static final int RANDOMS = 11;

    private void loadLocations() {
        List<BaseLocation> temp = UndermindClient.getMyClient().bwapi.getMap().getBaseLocations();
        toExplore = new LinkedList<Point>();
        for(BaseLocation baseLocation: temp){
            Point point = new Point(baseLocation.getX(),baseLocation.getY());
            toExplore.add(point);
            for(int i = 0; i < RANDOMS; i++){
                toExplore.add(findRandomDestination(point.x,point.y));
            }
        }
    }

    public Point findDestination(final double aroundX, final double aroundY, boolean isForDrone) {
        if(isForDrone){
            return findRandomDestination((int)aroundX,(int)aroundY);
        }

        if(toExplore == null || toExplore.isEmpty()){
            loadLocations();
        }

        Point result = Collections.min(toExplore, new Comparator<Point>() {
            public int compare(Point o1, Point o2) {
                double d1 = Point.distanceSq(o1.getX(),o1.getY(),UndermindClient.getMyClient().getEnemyHome().x, UndermindClient.getMyClient().getEnemyHome().y);
                double d2 = Point.distanceSq(o2.getX(),o2.getY(),UndermindClient.getMyClient().getEnemyHome().x, UndermindClient.getMyClient().getEnemyHome().y);
                return d1 > d2 ?
                        1 : (d1 < d2 ? -1 : 0);
            }
        });

        toExplore.remove(result);
        return result;
    }

    public Point findRandomDestination(int aroundX, int aroundY) {
        int x,y;
        do {
            x = aroundX + random.nextInt(500) - (250);
            y = aroundY + random.nextInt(500) - (250);
        } while(x < 0 || y < 0);

        return new Point(x,y);
    }
}
