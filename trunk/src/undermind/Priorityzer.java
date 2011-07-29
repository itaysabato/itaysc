package undermind;

import eisbot.proxy.model.Unit;

import java.util.Comparator;

/**
 * Created By: Itay Sabato<br/>
 * Date: 22/07/11 <br/>
 * Time: 15:43 <br/>
 */
public class Priorityzer implements Comparator<Unit> {
    public int compare(Unit u1, Unit u2) {
        UnitClass unitClass1 =  Utils.classify(u1);
        UnitClass unitClass2 =  Utils.classify(u2);
        return unitClass1.ordinal() - unitClass2.ordinal();
    }
}
