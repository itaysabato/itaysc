package undermind.utilities;

import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.ChokePoint;
import eisbot.proxy.model.Region;
import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType;
import undermind.UndermindClient;
import undermind.strategy.representation.UnitClass;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created By: Itay Sabato<br/>
 * Date: 14/07/11 <br/>
 * Time: 16:57 <br/>
 *
 *  Global utility class (contains most of the ugliness).
 */
public class Utils {

    private static  final boolean[][] IS_POWERED = {
            {false,false,false,false,true,true,true,true,true,true,false,false,false,false,false},
            {false,true,true,true,true,true,true,true,true,true,true,true,true,false,false},
            {true,true,true,true,true,true,true,true,true,true,true,true,true,true,false},
            {true,true,true,true,true,false,false,false,false,false,true,true,true,true,true},
            {true,true,true,true,true,false,false,false,false,false,true,true,true,true,true},
            {true,true,true,true,true,false,false,false,false,false,true,true,true,true,true},
            {true,true,true,true,true,false,false,false,false,false,true,true,true,true,true},
            {true,true,true,true,true,true,true,true,true,true,true,true,true,true,false},
            {false,true,true,true,true,true,true,true,true,true,true,true,true,false,false},
            {false,false,false,false,true,true,true,true,true,true,false,false,false,false,false}
    };

    private static final java.util.Map<String, MapConstants> MAP_CONSTANTS_MAP = new HashMap<String, MapConstants>();
    private static final java.util.Map<Integer,UnitClass> UNIT_CLASS_MAP = new HashMap<Integer, UnitClass>();
    private static final int HOME_RADIUS = 650;
    private static final int DETECTION_RADIUS = 200;

    static {
        for(MapConstants mapConstants: MapConstants.values()){
            MAP_CONSTANTS_MAP.put(mapConstants.getHash(),mapConstants);
        }

        for(UnitType.UnitTypes type: UnitType.UnitTypes.values()){
            if(isStructure(type.ordinal())){
                UNIT_CLASS_MAP.put(type.ordinal(),UnitClass.HARMLESS_STRUCTURE);
            }
            else {
                UNIT_CLASS_MAP.put(type.ordinal(),UnitClass.HARMLESS_UNIT);
            }
        }

        UNIT_CLASS_MAP.put(UnitType.UnitTypes.Zerg_Drone.ordinal(),UnitClass.WORKER);
        UNIT_CLASS_MAP.put(UnitType.UnitTypes.Terran_SCV.ordinal(),UnitClass.WORKER);
        UNIT_CLASS_MAP.put(UnitType.UnitTypes.Protoss_Probe.ordinal(),UnitClass.WORKER);

        UNIT_CLASS_MAP.put(UnitType.UnitTypes.Terran_Supply_Depot.ordinal(),UnitClass.SUPPLIER);
        UNIT_CLASS_MAP.put(UnitType.UnitTypes.Protoss_Pylon.ordinal(),UnitClass.SUPPLIER);
        UNIT_CLASS_MAP.put(UnitType.UnitTypes.Zerg_Spawning_Pool.ordinal(),UnitClass.SUPPLIER);
        UNIT_CLASS_MAP.put(UnitType.UnitTypes.Zerg_Creep_Colony.ordinal(),UnitClass.SUPPLIER);


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
        if(unit.isAttacking() && unitClass == UnitClass.WORKER){
            unitClass =   UnitClass.ATTACKING_WORKER;
        }
        return unitClass;
    }

    public static boolean isStructure(Unit unit) {
        return isZergStructure(unit.getTypeID()) || isTerranStructure(unit.getTypeID()) || isProtossStructure(unit.getTypeID());
    }

    public static boolean isStructure(int unitTypeID) {
        return isZergStructure(unitTypeID) || isTerranStructure(unitTypeID) || isProtossStructure(unitTypeID);
    }

    public static boolean isProtossStructure(int unitTypeID) {
        return UnitType.UnitTypes.Protoss_Nexus.ordinal() <= unitTypeID
                && unitTypeID <= UnitType.UnitTypes.Protoss_Shield_Battery.ordinal();
    }

    private static boolean isTerranStructure(int unitTypeID) {
        return UnitType.UnitTypes.Zerg_Infested_Command_Center.ordinal() <= unitTypeID
                && unitTypeID <= UnitType.UnitTypes.Zerg_Extractor.ordinal();
    }

    private static boolean isZergStructure(int unitTypeID) {
        return UnitType.UnitTypes.Terran_Command_Center.ordinal() <= unitTypeID
                && unitTypeID <= UnitType.UnitTypes.Terran_Bunker.ordinal();
    }

    public static Set<Point> getScoutingLocations(JNIBWAPI bwapi) {
        Set<Point> locations = UndermindClient.getMyClient().getMapConstants().getStartLocations(bwapi);
        Iterator<Point> i = locations.iterator();
        while(i.hasNext()){
            Point point = i.next();
            if(HOME_RADIUS >= Point.distance(point.x,point.y,UndermindClient.getMyClient().getMyHome().x,UndermindClient.getMyClient().getMyHome().y)){
                i.remove();
                break;
            }
        }
        return locations;
    }

    // todo: add more flyers.
    public static boolean isFlyer(Unit unit) {
        return unit.getTypeID() == UnitType.UnitTypes.Zerg_Overlord.ordinal();
    }

    public static String unitToString(Unit unit) {
        if(unit == null){
            return "null unit";
        }
        return "[ Unit: id="+unit.getID()+", type="+ UnitType.UnitTypes.values()[unit.getTypeID()]+", classification="+classify(unit)+", position=("+unit.getX()+","+unit.getY()+")]";
    }

    // true if enemy unit is not where it was last spotted (or anywhere else).
    public static boolean isGone(Unit enemy, JNIBWAPI bwapi) {
        if(bwapi.getUnit(enemy.getID()) != null){
            return false;
        }
        else {
            for(Unit unit: bwapi.getMyUnits()){
                if(!UndermindClient.getMyClient().isDestroyed(unit.getID()) && Point.distance(unit.getX(),unit.getY(),enemy.getX(),enemy.getY()) < DETECTION_RADIUS){
                    return true;
                }
            }
        }
        return false;
    }

    public static String unitsToString(Set<Unit> units) {
        String s = "{";
        for (Unit unit: units){
            s = s+unitToString(unit)+";";
        }
        return s+"}";
    }

    public static boolean isNearHome(Unit unit) {
        return HOME_RADIUS >= Point.distance(unit.getX(),unit.getY(), UndermindClient.getMyClient().getMyHome().x, UndermindClient.getMyClient().getMyHome().y);
    }

    public static boolean isPowering(Unit pylon, Unit protossBuilding) {
        Point fieldCorner = new Point(pylon.getTileX() - 8, pylon.getTileY() - 5);
        int x = protossBuilding.getTileX() - fieldCorner.x;
        int y = protossBuilding.getTileY() - fieldCorner.y;

        return 0 <= x && x < IS_POWERED[0].length && 0 <= y && y < IS_POWERED.length
                && IS_POWERED[y][x];
    }

    public static Region getRegion(final int x, final int y) {
        return Collections.min(UndermindClient.getMyClient().bwapi.getMap().getRegions(), new Comparator<Region>() {
            public int compare(Region r1, Region r2) {
                double d1 = Point.distance(r1.getCenterX(),r1.getCenterY(),x,y);
                double d2 = Point.distance(r2.getCenterX(),r2.getCenterY(),x,y);

                return d1 > d2 ?
                        1 : (d1 < d2 ? -1 : 0);
            }
        });
    }
}
