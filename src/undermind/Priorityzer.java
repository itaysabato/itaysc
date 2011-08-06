package undermind;

import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;
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

    public double getDangerLevel(Set<Unit> dangerousUnits, boolean countWorkers) {
        double dangerLevel = 0;
        for(Unit combative: dangerousUnits){
            if(combative.getTypeID() == UnitType.UnitTypes.Protoss_Zealot.ordinal()){
                dangerLevel += 1;
            }
            else if(combative.getTypeID() == UnitType.UnitTypes.Terran_Marine.ordinal()){
                dangerLevel += 0.6;
            }
            else if(combative.getTypeID() == UnitType.UnitTypes.Terran_Firebat.ordinal()){
                dangerLevel += 0.75;
            }
             else if(combative.getTypeID() == UnitType.UnitTypes.Terran_Bunker.ordinal()){
                dangerLevel += 0.1;
            }
            else if(countWorkers){
                if(combative.getTypeID() == UnitType.UnitTypes.Protoss_Probe.ordinal()){
                    dangerLevel += 0.7;
                }
                else if(combative.getTypeID() == UnitType.UnitTypes.Terran_SCV.ordinal()){
                    dangerLevel += 0.4;
                }
                else if(combative.getTypeID() == UnitType.UnitTypes.Zerg_Drone.ordinal()){
                    dangerLevel += 0.4;
                }
            }
        }
        return dangerLevel;
    }

    public int compare(Unit u1, Unit u2) {
        if(Utils.isNearHome(u1) != Utils.isNearHome(u2)){
                return Utils.isNearHome(u1) ? -1 : 1;
        }

        if(u1.getTypeID() == UnitType.UnitTypes.Protoss_Pylon.ordinal() && u2.getTypeID() == UnitType.UnitTypes.Protoss_Pylon.ordinal()){
            double d1 = chief.getEnemyKeeper().minimalGatewayDistance(u1);
            double d2 = chief.getEnemyKeeper().minimalGatewayDistance(u2);
            return d1 > d2 ?
                    1 : (d1 < d2 ? -1 : 0);
        }

//        if((u1.isAttacking() || u1.isStartingAttack()) != (u2.isAttacking() || u2.isStartingAttack())){
//                return (u1.isAttacking() || u1.isStartingAttack()) ? -1 : 1;
//        }

        UnitClass unitClass1 =  Utils.classify(u1);
        UnitClass unitClass2 =  Utils.classify(u2);

        return unitClass1.ordinal() - unitClass2.ordinal();
    }
}
