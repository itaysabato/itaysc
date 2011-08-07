package undermind.strategy;

import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType;
import undermind.strategy.ChiefOfStaff;
import undermind.utilities.UnitClass;
import undermind.utilities.Utils;

import java.util.Comparator;
import java.util.Set;

/**
 * Created By: Itay Sabato<br/>
 * Date: 22/07/11 <br/>
 * Time: 15:43 <br/>
 */
public class Priorityzer implements Comparator<Unit> {
    private final ChiefOfStaff chief;

    public Priorityzer(ChiefOfStaff chief) {
        this.chief = chief;
    }

    public int compare(Unit u1, Unit u2) {

        if(u1.getTypeID() == UnitType.UnitTypes.Protoss_Pylon.ordinal() && u2.getTypeID() == UnitType.UnitTypes.Protoss_Pylon.ordinal()){
            double poweringCount1 = chief.getEnemyKeeper().getPoweringCount(u1);
            double poweringCount2 = chief.getEnemyKeeper().getPoweringCount(u2);
            return poweringCount1 > poweringCount2 ?
                    -1 : (poweringCount1 < poweringCount2 ? 1 : 0);
        }

        UnitClass unitClass1 =  Utils.classify(u1);
        UnitClass unitClass2 =  Utils.classify(u2);

        if((unitClass1 == UnitClass.HARMFUL) != (unitClass2 == UnitClass.HARMFUL)){
            return (unitClass1 == UnitClass.HARMFUL) ? -1 : 1;
        }

        if(withinBounds(u1) != withinBounds(u2)){
            return withinBounds(u1) ? -1 : 1;
        }

        return unitClass1.ordinal() - unitClass2.ordinal();
    }

    public boolean withinBounds(Unit unit) {
        return chief.getEnemyKeeper().getEnemyHomeBounds() == null
                || Utils.isNearHome(unit)
                || chief.getEnemyKeeper().getEnemyHomeBounds().contains(unit.getX(), unit.getY());
    }
}
