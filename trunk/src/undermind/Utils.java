package undermind;

import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created By: Itay Sabato<br/>
 * Date: 14/07/11 <br/>
 * Time: 16:57 <br/>
 */
public class Utils {
    private static final java.util.Map<String, MapConstants> MAP_CONSTANTS_MAP = new HashMap<String, MapConstants>();
    private static final java.util.Map<Integer,UnitClass> UNIT_CLASS_MAP = new HashMap<Integer, UnitClass>();
    private static final int HOME_RADIUS = 300;

    static {
        for(MapConstants mapConstants: MapConstants.values()){
            MAP_CONSTANTS_MAP.put(mapConstants.getHash(),mapConstants);
        }

        UNIT_CLASS_MAP.put(UnitType.UnitTypes.Zerg_Drone.ordinal(),UnitClass.WORKER);
        UNIT_CLASS_MAP.put(UnitType.UnitTypes.Terran_SCV.ordinal(),UnitClass.WORKER);
        UNIT_CLASS_MAP.put(UnitType.UnitTypes.Protoss_Probe.ordinal(),UnitClass.WORKER);

        UNIT_CLASS_MAP.put(UnitType.UnitTypes.Terran_Supply_Depot.ordinal(),UnitClass.SUPPLIER);
        UNIT_CLASS_MAP.put(UnitType.UnitTypes.Protoss_Pylon.ordinal(),UnitClass.SUPPLIER);

        UNIT_CLASS_MAP.put(UnitType.UnitTypes.Terran_Command_Center.ordinal(),UnitClass.MAIN);
        UNIT_CLASS_MAP.put(UnitType.UnitTypes.Protoss_Nexus.ordinal(),UnitClass.MAIN);
        UNIT_CLASS_MAP.put(UnitType.UnitTypes.Zerg_Hatchery.ordinal(),UnitClass.MAIN);
        UNIT_CLASS_MAP.put(UnitType.UnitTypes.Zerg_Lair.ordinal(),UnitClass.MAIN);


        UNIT_CLASS_MAP.put(UnitType.UnitTypes.Zerg_Zergling.ordinal(),UnitClass.HARMFUL);
        UNIT_CLASS_MAP.put(UnitType.UnitTypes.Zerg_Hydralisk.ordinal(),UnitClass.HARMFUL);
        UNIT_CLASS_MAP.put(UnitType.UnitTypes.Zerg_Sunken_Colony.ordinal(),UnitClass.HARMFUL);

        UNIT_CLASS_MAP.put(UnitType.UnitTypes.Terran_Marine.ordinal(),UnitClass.HARMFUL);
        UNIT_CLASS_MAP.put(UnitType.UnitTypes.Terran_Bunker.ordinal(),UnitClass.HARMFUL);
        UNIT_CLASS_MAP.put(UnitType.UnitTypes.Terran_Firebat.ordinal(),UnitClass.HARMFUL);

        UNIT_CLASS_MAP.put(UnitType.UnitTypes.Protoss_Zealot.ordinal(),UnitClass.HARMFUL);
        UNIT_CLASS_MAP.put(UnitType.UnitTypes.Protoss_Photon_Cannon.ordinal(),UnitClass.HARMFUL);
        UNIT_CLASS_MAP.put(UnitType.UnitTypes.Protoss_Dragoon.ordinal(),UnitClass.HARMFUL);
    }

    public static MapConstants getMapConstantsFor(String hash){
        return MAP_CONSTANTS_MAP.get(hash);
    }

    public static UnitClass classify(Unit unit) {
        UnitClass unitClass = UNIT_CLASS_MAP.get(unit.getTypeID());
        Out.println("unit class is: "+unitClass+" type is: "+ UnitType.UnitTypes.values()[unit.getTypeID()]);
        return unitClass == null ? UnitClass.HARMLESS_UNIT : unitClass;
    }

    public static boolean isStructure(Unit unit) {
        return isZergStructure(unit) || isTerranStructure(unit) || isProtossStructure(unit);
    }

    private static boolean isProtossStructure(Unit unit) {
        return UnitType.UnitTypes.Protoss_Nexus.ordinal() <= unit.getTypeID()
                && unit.getTypeID() <= UnitType.UnitTypes.Protoss_Shield_Battery.ordinal();
    }

    private static boolean isTerranStructure(Unit unit) {
        return UnitType.UnitTypes.Zerg_Infested_Command_Center.ordinal() <= unit.getTypeID()
                && unit.getTypeID() <= UnitType.UnitTypes.Zerg_Extractor.ordinal();
    }

    private static boolean isZergStructure(Unit unit) {
        return UnitType.UnitTypes.Terran_Command_Center.ordinal() <= unit.getTypeID()
                && unit.getTypeID() <= UnitType.UnitTypes.Terran_Bunker.ordinal();
    }

    public static Set<Point> getScoutingLocations(JNIBWAPI bwapi) {
        Set<Point> locations = UndermindClient.getMyClient().getMapConstants().getStartLocations(bwapi);
        Iterator<Point> i = locations.iterator();
        Out.println(locations.toString());
        while(i.hasNext()){
            Point point = i.next();
            if(HOME_RADIUS >= Point.distance(point.x,point.y,UndermindClient.getMyClient().getMyHome().x,UndermindClient.getMyClient().getMyHome().y)){
                i.remove();
                break;
            }
        }
        return locations;
    }

    public static boolean isFlyer(Unit unit) {
        return unit.getTypeID() == UnitType.UnitTypes.Zerg_Overlord.ordinal();
    }
}
