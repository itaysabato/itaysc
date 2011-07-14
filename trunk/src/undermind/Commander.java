package undermind;

import eisbot.proxy.JNIBWAPI;

/**
 * Created By: Itay Sabato<br/>
 * Date: 14/07/11 <br/>
 * Time: 16:25 <br/>
 */
public class Commander {
    private JNIBWAPI bwapi;

    public Commander(JNIBWAPI bwapi) {
        this.bwapi = bwapi;
    }

    public void command(Integer id, SoldierState state) {

    }
}
