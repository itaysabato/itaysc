package undermind;

import eisbot.proxy.model.BaseLocation;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created By: Itay Sabato<br/>
 * Date: 22/07/11 <br/>
 * Time: 13:57 <br/>
 */
public class Explorer {
    private static final Random random = new Random();
    private List<Point> toExplore = null;
    private int height;
    private int width;
    private int distance = 100;

    private static final int HOME_RADIUS = 300;

    public void init() {
        height = 3000;
        width = 3000;

        List<BaseLocation> temp = UndermindClient.getMyClient().bwapi.getMap().getBaseLocations();
        toExplore = new LinkedList<Point>();
        for(BaseLocation baseLocation: temp){
            Point point = new Point(baseLocation.getX(),baseLocation.getY());
            if(HOME_RADIUS < Point.distance(point.x,point.y,UndermindClient.getMyClient().getMyHome().x,UndermindClient.getMyClient().getMyHome().y)){
                toExplore.add(point);
            }
            else {
                Out.println(point+" was filtered from exploring for being home");
            }
        }

    }

    public Point findDestination(final double currentX, final double currentY) {
        if(toExplore == null){
            init();
        }

        if(toExplore.isEmpty()){
            init();
            return findRandomDestination((int)currentX,(int)currentY);
        }
        else {
            Point result = Collections.min(toExplore, new Comparator<Point>() {
                public int compare(Point o1, Point o2) {
                    double d1 = Point.distanceSq(o1.getX(),o1.getY(),currentX, currentY);
                    double d2 = Point.distanceSq(o2.getX(),o2.getY(),currentX, currentY);
                    return d1 > d2 ?
                            1 : (d1 < d2 ? -1 : 0);
                }
            });
            toExplore.remove(result);
            return result;
        }
    }

    public Point findRandomDestination(int currentX, int currentY) {
        int x,y;
        if(distance > height){
            distance = 100;
        }
        do {
            x = currentX + random.nextInt(distance) - (distance/2);
            y = currentY + random.nextInt(distance) - (distance/2);
        } while(x < 0 || y < 0);

        distance += 100;
        return new Point(x,y);
    }
}
