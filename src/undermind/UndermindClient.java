package undermind;

import eisbot.proxy.BWAPIEventListener;
import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.types.UnitType;

import java.awt.*;

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
    private Point myHome;
    private MapConstants mapConstants;


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
        bwapi.setGameSpeed(0);
        bwapi.loadMapData(true);
        currentPhase = GamePhase.getInitialPhase();
        attacker = new Attacker(bwapi);
        enemyHome = null;
        myHome = null;
         mapConstants = Utils.getMapConstantsFor(bwapi.getMap().getHash());
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
        if(bwapi.getUnit(unitID).getTypeID() == UnitType.UnitTypes.Zerg_Spawning_Pool.ordinal()){
            Out.println("discovered spawning pool: "+unitID+" completed: "+bwapi.getUnit(unitID).isCompleted());
        }
    }

    public void unitEvade(int unitID) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void unitShow(int unitID) {
        if(bwapi.getUnit(unitID).getTypeID() == UnitType.UnitTypes.Zerg_Spawning_Pool.ordinal()){
            Out.println("shown spawning pool: "+unitID+" completed: "+bwapi.getUnit(unitID).isCompleted());
        }
    }

    public void unitHide(int unitID) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void unitCreate(int unitID) {
        if(bwapi.getUnit(unitID).getTypeID() == UnitType.UnitTypes.Zerg_Spawning_Pool.ordinal()){
            Out.println("created spawning pool: "+unitID+" completed: "+bwapi.getUnit(unitID).isCompleted());
        }
    }

    public void unitDestroy(int unitID) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void unitMorph(int unitID) {
        if(bwapi.getUnit(unitID).getTypeID() == UnitType.UnitTypes.Zerg_Spawning_Pool.ordinal()){
            Out.println("morphed spawning pool: "+unitID+" completed: "+bwapi.getUnit(unitID).isCompleted());
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

    public void setMapConstants(MapConstants mapConstants) {
        this.mapConstants = mapConstants;
    }
}
