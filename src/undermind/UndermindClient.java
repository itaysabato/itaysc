package undermind;

import eisbot.proxy.BWAPIEventListener;
import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created By: Itay Sabato<br/>
 * Date: 10/07/2011 <br/>
 * Time: 20:47:39 <br/>
 */
public class UndermindClient implements BWAPIEventListener {
    private static final UndermindClient myClient = new UndermindClient();

    public final JNIBWAPI bwapi;
    private GamePhase currentPhase;
    private Attacker attacker;
    private Point enemyHome;
    private Point enemyTemp;
    private Point myHome;
    private MapConstants mapConstants;
    private Set<Integer> destroyed;

    private UndermindClient() {
        bwapi = new JNIBWAPI(this);
    }

    public static void main(String[] arguments) {
        try {
//            Out.println("this is NOT a test!");
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
        bwapi.drawIDs(true);
        bwapi.drawTargets(true);
        bwapi.drawHealth(false);
        bwapi.setGameSpeed(0);
        bwapi.loadMapData(true);
        currentPhase = GamePhase.getInitialPhase();
        attacker = new Attacker(bwapi);
        destroyed = new HashSet<Integer>();
        mapConstants = Utils.getMapConstantsFor(bwapi.getMap().getHash());
        enemyHome = null;
        myHome = null;
        Out.println("map is: ["+mapConstants+"]");
    }

    public void gameUpdate() {
        try {
            currentPhase = currentPhase.gameUpdate();
            if(currentPhase.ordinal() >= GamePhase.SCOUT.ordinal()){
                attacker.gameUpdate();
            }
        }
        catch(Exception e) {
            Out.println("Exception caught: "+e.getMessage());
            e.printStackTrace();
        }
    }

    public void gameEnded() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void keyPressed(int keyCode) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void matchEnded(boolean winner) {
        Out.println("winner: "+winner);
    }

    public void playerLeft(int id) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void nukeDetect(int x, int y) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void nukeDetect() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void unitDiscover(int unitID) {
        if(enemyHome == null){
            Unit unit =(bwapi.getUnit(unitID));
            if( bwapi.getPlayer(unit.getPlayerID()).isEnemy()){
                if(Utils.isStructure(bwapi.getUnit(unitID))){
                    enemyHome = new Point(unit.getX(),unit.getY());
                    Out.println("discovered enemyHome at: "+enemyHome);
                }
                else {
                    enemyTemp =  new Point(unit.getX(),unit.getY());
                    Out.println("discovered enemyTemp at: "+enemyTemp);
                }
            }
        }
    }

    public void unitEvade(int unitID) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void unitShow(int unitID) {
    }

    public void unitHide(int unitID) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void unitCreate(int unitID) {
    }

    public void unitDestroy(int unitID) {
        destroyed.add(unitID);
    }

    public void unitMorph(int unitID) {
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

    public void setMapConstants(MapConstants mapConstants) {
        this.mapConstants = mapConstants;
    }

    public boolean isDestroyed(int unitID) {
        return destroyed.contains(unitID);
    }

    public Point getEnemyTemp() {
        return enemyTemp;
    }
}
