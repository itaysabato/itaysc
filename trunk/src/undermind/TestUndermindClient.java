package undermind;

import eisbot.proxy.BWAPIEventListener;
import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType;
import undermind.strategy.AttackProducer;
import undermind.strategy.PreparationPhase;
import undermind.utilities.Out;
import undermind.utilities.UndermindException;
import undermind.utilities.Utils;

/**
 * Created By: Itay Sabato<br/>
 * Date: 10/07/2011 <br/>
 * Time: 20:47:39 <br/>
 */
public class TestUndermindClient implements BWAPIEventListener {

//    static {
//        System.loadLibrary("ExampleAIClient");
//    }

    private final JNIBWAPI bwapi;
    private PreparationPhase currentPhase = null;
    private AttackProducer attackProducer = null;

    private TestUndermindClient() throws UndermindException {
        bwapi = new JNIBWAPI(this);
        currentPhase = PreparationPhase.getInitialPhase();
        attackProducer = new AttackProducer(bwapi);
    }

    public static void main(String[] arguments) {
        try {
            Out.println("main");
            TestUndermindClient undermindClient = new TestUndermindClient();
            undermindClient.start();
        }
        catch(Exception e) {
            Out.println("Exception caught: "+e.getMessage());
            e.printStackTrace();
        }

    }

    private void start() {
        bwapi.start();
    }

    public void connected() {
        Out.println("connected");
        bwapi.loadTypeData();
    }

    public void gameStarted() {
		bwapi.enableUserInput();
		bwapi.enablePerfectInformation();
        bwapi.setGameSpeed(0);
        bwapi.loadMapData(true);
        Out.println("map is: ["+ Utils.getMapConstantsFor(bwapi.getMap().getHash())+"]");
    }

    public void gameUpdate() {
        try {
            for(Unit unit: bwapi.getMyUnits()){
                if(unit.getTypeID() == UnitType.UnitTypes.Zerg_Overlord.ordinal()){
                    Out.println("Overlord at (x,y)=("+unit.getX()+","+unit.getY()+").");
                }
            }

            for(Unit enemy : bwapi.getEnemyUnits()){
                for(Unit unit: bwapi.getMyUnits()){
                    if(UnitType.UnitTypes.values()[unit.getTypeID()] == UnitType.UnitTypes.Zerg_Zergling){
                        bwapi.attack(unit.getID(),enemy.getX(),enemy.getY());
                        break;
                    }
                }
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
        try {
            currentPhase = PreparationPhase.getInitialPhase();
        }
        catch(Exception e) {
            Out.println("Exception caught: "+e.getMessage());
            e.printStackTrace();
        }
        attackProducer = new AttackProducer(bwapi);
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
        }    }

    public void unitDestroy(int unitID) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void unitMorph(int unitID) {
        if(bwapi.getUnit(unitID).getTypeID() == UnitType.UnitTypes.Zerg_Spawning_Pool.ordinal()){
            Out.println("morphed spawning pool: " + unitID + " completed: " + bwapi.getUnit(unitID).isCompleted());
        }
    }
}
