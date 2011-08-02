package undermind;

import java.awt.*;
import java.util.Random;

/**
 * Created By: Itay Sabato<br/>
 * Date: 22/07/11 <br/>
 * Time: 13:57 <br/>
 */
public class Explorer {
    private static final Random random = new Random();

    //todo: go to base locations
    public Point findDestination(double currentX, double currentY) {
        int x,y;
        do {
            x = UndermindClient.getMyClient().getEnemyHome().x + random.nextInt(1000) - 500;
            y = UndermindClient.getMyClient().getEnemyHome().y + random.nextInt(1000) - 500;
        } while(x < 0 || y < 0);
        return new Point(x,y);
    }
}
