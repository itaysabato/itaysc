package undermind;

import eisbot.proxy.BWAPIEventListener;
import eisbot.proxy.JNIBWAPI;

/**
 * Created By: Itay Sabato<br/>
 * Date: 10/07/2011 <br/>
 * Time: 20:47:39 <br/>
 */
public class UndermindClient implements BWAPIEventListener {

    private final JNIBWAPI bwapi;

    private UndermindClient() {
        bwapi = new JNIBWAPI(this);
    }

    public static void main(String[] arguments) {
        UndermindClient undermindClient = new UndermindClient();
        undermindClient.start();
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
    }

    public void gameUpdate() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void gameEnded() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void keyPressed(int keyCode) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void matchEnded(boolean winner) {
        //To change body of implemented methods use File | Settings | File Templates.
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
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void unitEvade(int unitID) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void unitShow(int unitID) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void unitHide(int unitID) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void unitCreate(int unitID) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void unitDestroy(int unitID) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void unitMorph(int unitID) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
