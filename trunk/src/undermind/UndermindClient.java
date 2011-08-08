package undermind;

import eisbot.proxy.BWAPIEventListener;
import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.Unit;
import undermind.strategy.ChiefOfStaff;
import undermind.utilities.MapConstants;
import undermind.utilities.Utils;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created By: Itay Sabato<br/>
 * Date: 10/07/2011 <br/>
 * Time: 20:47:39 <br/>
 *
 *  The main AI client singleton which listens to BWAPI call backs.
 */
public class UndermindClient implements BWAPIEventListener {

    private static boolean singleGame =false;
    private static final UndermindClient myClient = new UndermindClient();
    public final JNIBWAPI bwapi;
    private ChiefOfStaff chief;
    private Point enemyHome;
    private Point enemyTemp;
    private Point myHome;
    private Point myHomeTile;
    private MapConstants mapConstants;
    private Set<Integer> destroyed;

    private UndermindClient() {
        bwapi = new JNIBWAPI(this);
    }

    public static void main(String[] arguments) {
        try {
            if(arguments.length > 0){
                  singleGame = true;
            }
            myClient.start();
        }
        catch(Exception e) {
            System.err.println("Exception caught: "+e.getMessage());
            e.printStackTrace();
        }
    }

    private void start() {
        bwapi.start();
    }

    public void connected() {
        bwapi.loadTypeData();
    }

    public void gameStarted() {
        try {
            bwapi.setGameSpeed(0);
            bwapi.loadMapData(true);
            initFields();
        }
        catch (Exception e){
            System.err.println("Exception caught: "+e.getMessage());
            e.printStackTrace();
        }
    }

    private void initFields() {
        chief = new ChiefOfStaff(bwapi);
        destroyed = new HashSet<Integer>();
        mapConstants = Utils.getMapConstantsFor(bwapi.getMap().getHash());
        enemyHome = null;
        enemyTemp = null;
        myHome = null;
    }

    public void gameUpdate() {
        try {
            chief.gameUpdate();
        }
        catch(Exception e) {
            System.err.println("Exception caught: "+e.getMessage());
            e.printStackTrace();
        }
    }

    public void unitDiscover(int unitID) {
        if(enemyHome == null){
            Unit unit =(bwapi.getUnit(unitID));
            if( bwapi.getPlayer(unit.getPlayerID()).isEnemy()){
                if(Utils.isStructure(bwapi.getUnit(unitID))){
                    enemyHome = new Point(unit.getX(),unit.getY());
                }
                else if(enemyTemp != null) {
                    enemyTemp =  new Point(unit.getX(),unit.getY());
                }
            }
        }
    }

    public void unitDestroy(int unitID) {
        destroyed.add(unitID);
    }

    public void gameEnded() {
        if(singleGame){
            System.exit(0);
        }
    }

    public Point getEnemyHome() {
        return enemyHome;
    }

    public void setEnemyHome(Point enemyHome) {
        this.enemyHome = enemyHome;
    }

    public static UndermindClient getMyClient() {
        return myClient;
    }

    public Point getMyHome() {
        return myHome;
    }

    public void setMyHome(Point myHome) {
        this.myHome = myHome;
    }

    public MapConstants getMapConstants() {
        return mapConstants;
    }

    public boolean isDestroyed(int unitID) {
        return destroyed.contains(unitID);
    }

    public Point getEnemyTemp() {
        return enemyTemp;
    }

    public Point getMyHomeTile() {
        return myHomeTile;
    }

    public void setMyHomeTile(Point myHomeTile) {
        this.myHomeTile = myHomeTile;
    }

    public void unitMorph(int unitID) {}
    public void unitEvade(int unitID) {}
    public void unitShow(int unitID) {}
    public void unitHide(int unitID) {}
    public void unitCreate(int unitID) {}
    public void keyPressed(int keyCode) {}
    public void matchEnded(boolean winner) {}
    public void playerLeft(int id) {}
    public void nukeDetect(int x, int y) {}
    public void nukeDetect() {}
}
