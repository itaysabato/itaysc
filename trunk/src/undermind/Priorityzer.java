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
    private Map<UnitClass,Integer> unitClassPriorities = new EnumMap<UnitClass, Integer>(UnitClass.class);

    public void preProcess(EnemyKeeper enemyKeeper){
        double dangerLevel = getDangerLevel(enemyKeeper.getDangerousUnits(),false);
        setPriorities(dangerLevel);
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
            else if(combative.getTypeID() == UnitType.UnitTypes.Zerg_Zergling.ordinal()){
                dangerLevel += 0.25;
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

    private void setPriorities(double dangerLevel) {
        if(dangerLevel > 2){
            unitClassPriorities.put(UnitClass.WORKER,0);
            unitClassPriorities.put(UnitClass.ATTACKING_WORKER,1);
            unitClassPriorities.put(UnitClass.HARMFUL,2);
            unitClassPriorities.put(UnitClass.SUPPLIER,3);
        }
        else if(dangerLevel == 0){
            unitClassPriorities.put(UnitClass.ATTACKING_WORKER,0);
            unitClassPriorities.put(UnitClass.SUPPLIER,1);
            unitClassPriorities.put(UnitClass.WORKER,2);
            unitClassPriorities.put(UnitClass.HARMFUL,3);
        }
        else {
            unitClassPriorities.put(UnitClass.HARMFUL,0);
            unitClassPriorities.put(UnitClass.ATTACKING_WORKER,1);
            unitClassPriorities.put(UnitClass.WORKER,2);
            unitClassPriorities.put(UnitClass.SUPPLIER,3);
        }
        unitClassPriorities.put(UnitClass.MAIN,4);
        unitClassPriorities.put(UnitClass.HARMLESS_STRUCTURE,5);
        unitClassPriorities.put(UnitClass.HARMLESS_UNIT,6);
    }

    public int compare(Unit u1, Unit u2) {
        boolean nearHome1 = Utils.isNearHome(u1);
        boolean nearHome2 = Utils.isNearHome(u2);

        if(nearHome1 || nearHome2){
            if(nearHome1 && nearHome2){
                return 0;
            }
            else {
                return nearHome1 ? -1 : 1;
            }
        }

        UnitClass unitClass1 =  Utils.classify(u1);
        UnitClass unitClass2 =  Utils.classify(u2);
        return unitClassPriorities.get(unitClass1) - unitClassPriorities.get(unitClass2);
    }
}
