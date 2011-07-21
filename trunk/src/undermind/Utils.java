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
    private static final java.util.Map<String, MapConstants> MAP_CONSTANTS_MAP;
    private static final int HOME_RADIUS = 300;

    static {
        MAP_CONSTANTS_MAP = new HashMap<String, MapConstants>();
        for(MapConstants mapConstants: MapConstants.values()){
            MAP_CONSTANTS_MAP.put(mapConstants.getHash(),mapConstants);
        }
    }

    public static MapConstants getMapConstantsFor(String hash){
       return MAP_CONSTANTS_MAP.get(hash);
    }

    public static double distance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public static boolean isWorker(Unit unit) {
        return unit.getTypeID() == UnitType.UnitTypes.Zerg_Drone.ordinal()
                || unit.getTypeID() == UnitType.UnitTypes.Terran_SCV.ordinal()
                ||  unit.getTypeID() == UnitType.UnitTypes.Protoss_Probe.ordinal();
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

    public static boolean isCombative(Unit unit) {
        return !isWorker(unit) && !isStructure(unit) && !isLamer(unit);
    }

    public static boolean isLamer(Unit unit) {
        return unit.getTypeID() == UnitType.UnitTypes.Zerg_Egg.ordinal()
                || unit.getTypeID() == UnitType.UnitTypes.Zerg_Larva.ordinal()
                || unit.getTypeID() == UnitType.UnitTypes.Terran_Medic.ordinal();
    }

    public static double getMaxCloseDistanceFrom(Unit unit) {
        if(isWorker(unit) || isStructure(unit)){
            return 8;
        }
        else return 0;
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

    public static boolean isSupplier(Unit unit) {
        return unit.getTypeID() == UnitType.UnitTypes.Zerg_Drone.ordinal()
                || unit.getTypeID() == UnitType.UnitTypes.Terran_SCV.ordinal()
                || unit.getTypeID() == UnitType.UnitTypes.Protoss_Probe.ordinal();
    }

    public static boolean isMain(Unit unit) {
        return unit.getTypeID() == UnitType.UnitTypes.Zerg_Hatchery.ordinal()
                || unit.getTypeID() == UnitType.UnitTypes.Zerg_Lair.ordinal()
                || unit.getTypeID() == UnitType.UnitTypes.Protoss_Nexus.ordinal()
                || unit.getTypeID() == UnitType.UnitTypes.Terran_Command_Center.ordinal();
    }

    public static boolean isFlyer(Unit unit) {
        return unit.getTypeID() == UnitType.UnitTypes.Zerg_Overlord.ordinal();
    }
}
