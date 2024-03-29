package undermind.strategy.execution;

import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType;
import undermind.UndermindClient;
import undermind.utilities.UndermindException;
import undermind.utilities.Utils;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created By: Itay Sabato<br/>
 * Date: 10/07/2011 <br/>
 * Time: 21:25:00 <br/>
 *
 *  This enum is a sequential state machine which sets up
 *  the base at the start of the game and sends out the scout.
 */
public enum PreparationSequence {
    INIT {
        @Override
        protected void clear() throws UndermindException {}

        @Override
        public PreparationSequence gameUpdate() throws UndermindException {
            setHome();
            collectMinerals();

            MAKE_DRONE.minerals = minerals;
            MAKE_DRONE.clear();
            return MAKE_DRONE;
        }

        private void setHome() throws UndermindException {
            for(Unit unit: UndermindClient.getMyClient().bwapi.getMyUnits()){
                if(unit.getTypeID() == UnitType.UnitTypes.Zerg_Hatchery.ordinal()){
                    UndermindClient.getMyClient().setMyHome(new Point(unit.getX(),unit.getY()));
                    UndermindClient.getMyClient().setMyHomeTile(new Point(unit.getTileX(),unit.getTileY()));
                    return;
                }
            }
            throw new UndermindException("No Home!");
        }

        private void collectMinerals() throws UndermindException {
            minerals = findMinerals();
            int i = 0;

            for (Unit unit : UndermindClient.getMyClient().bwapi.getMyUnits()) {
                if (unit.getTypeID() == UnitType.UnitTypes.Zerg_Drone.ordinal()) {
                    UndermindClient.getMyClient().bwapi.rightClick(unit.getID(), minerals[i++]);
                }
            }
        }

        private int[] findMinerals() throws UndermindException {
            int[] result = new int[DRONE_COUNT];
            List<Unit> fields = new LinkedList<Unit>();

            for (Unit minerals : UndermindClient.getMyClient().bwapi.getNeutralUnits()) {
                if (minerals.getTypeID() == UnitType.UnitTypes.Resource_Mineral_Field.ordinal()) {
                    fields.add(minerals);
                }
            }

            final Point home = UndermindClient.getMyClient().getMyHome();
            for(int i = 0; i < result.length; i++){
                Unit minimalMineral = Collections.min(fields, new Comparator<Unit>() {
                    public int compare(Unit u1, Unit u2) {
                        double d1 = Point.distance(u1.getX(), u1.getY(), home.x, home.y);
                        double d2 = Point.distance(u2.getX(), u2.getY(), home.x, home.y);

                        return d1 > d2 ?
                                1 : (d1 < d2 ? -1 : 0);
                    }
                });
                result[i] = minimalMineral.getID();
                fields.remove(minimalMineral);
            }
            return result;
        }
    },

    MAKE_DRONE {
        int newDroneID;

        @Override
        protected void clear() throws UndermindException {
            newDroneID = -1;
        }

        @Override
        public PreparationSequence gameUpdate() throws UndermindException {
            if(newDroneID < 0){
                newDroneID = morphDrone();
            }

            if(newDroneID >= 0){
                if(UndermindClient.getMyClient().bwapi.getUnit(newDroneID).isGatheringMinerals()){
                    CREATE_POOL.clear();
                    return CREATE_POOL;
                }
                else if(UndermindClient.getMyClient().bwapi.getUnit(newDroneID).getTypeID() == UnitType.UnitTypes.Zerg_Drone.ordinal()){
                    UndermindClient.getMyClient().bwapi.rightClick(newDroneID, minerals[DRONE_COUNT-1]);
                }
            }
            return this;
        }

        private int morphDrone() {
            for (Unit unit : UndermindClient.getMyClient().bwapi.getMyUnits()) {
                if (unit.getTypeID() == UnitType.UnitTypes.Zerg_Larva.ordinal()) {
                    if (UndermindClient.getMyClient().bwapi.getSelf().getMinerals() >= 50) {
                        UndermindClient.getMyClient().bwapi.morph(unit.getID(), UnitType.UnitTypes.Zerg_Drone.ordinal());
                        return unit.getID();
                    }
                }
            }
            return -1;
        }
    },

    CREATE_POOL {
        private static final int POOL_PRICE = 200;
        @Override
        protected void clear() throws UndermindException {
            poolDrone = -1;
            poolTile = null;
        }

        @Override
        public PreparationSequence gameUpdate() throws UndermindException {
            if(poolDrone < 0){
                for (Unit unit : UndermindClient.getMyClient().bwapi.getMyUnits()) {
                    if (unit.getTypeID() == UnitType.UnitTypes.Zerg_Drone.ordinal()) {
                        poolDrone = unit.getID();
                        break;
                    }
                }
            }

            if( poolTile == null){
                poolTile = findPoolTile();
            }

            if (UndermindClient.getMyClient().bwapi.getSelf().getMinerals() >= POOL_PRICE) {
                UndermindClient.getMyClient().bwapi.build(poolDrone, poolTile.x, poolTile.y, UnitType.UnitTypes.Zerg_Spawning_Pool.ordinal());
                SCOUT.clear();
                SCOUT.poolDrone = poolDrone;
                return SCOUT;
            }
            return this;
        }

        private Point findPoolTile() throws UndermindException {
            for (Unit unit : UndermindClient.getMyClient().bwapi.getMyUnits()) {
                if (unit.getTypeID() == UnitType.UnitTypes.Zerg_Overlord.ordinal()) {
                    return  new Point(unit.getTileX(),unit.getTileY());
                }
            }
            return UndermindClient.getMyClient().getMyHomeTile();
        }
    },

    SCOUT {
        private Set<Point> toScout;
        private Point next;
        private boolean[][] canBuild = null;
        private Point approximateCreepCorner = null;

        private static final int SCOUTED_RADIUS = 150;

        @Override
        protected void clear() throws UndermindException {
            scout = -1;
            next = null;
            canBuild = null;
            approximateCreepCorner = new Point(UndermindClient.getMyClient().getMyHomeTile().x - 8,UndermindClient.getMyClient().getMyHomeTile().y - 5);
            toScout = Utils.getScoutingLocations(UndermindClient.getMyClient().bwapi);
            if(toScout.size() == 1){
                UndermindClient.getMyClient().setEnemyHome(toScout.iterator().next());
            }
        }

        @Override
        public PreparationSequence gameUpdate() throws UndermindException {

            if(!isPoolStarted()){
                Unit poolDroneUnit = UndermindClient.getMyClient().bwapi.getUnit(poolDrone);
                if(poolDroneUnit != null && (poolDroneUnit.isIdle() || poolDroneUnit.isGatheringMinerals())){
                    poolTile = tryNextTile();
                    UndermindClient.getMyClient().bwapi.build(poolDrone, poolTile.x, poolTile.y, UnitType.UnitTypes.Zerg_Spawning_Pool.ordinal());
                }
            }

            if(scout < 0){
                chooseScout();
            }

            if(scout >= 0 && UndermindClient.getMyClient().getEnemyHome() == null){
                Unit scoutUnit = UndermindClient.getMyClient().bwapi.getUnit(scout);
                if(scoutUnit != null
                        && (next == null || SCOUTED_RADIUS >= Point.distance(scoutUnit.getX(),scoutUnit.getY(),next.x,next.y))) {
                    scoutNext(scoutUnit);
                }
                else if(scoutUnit != null && scoutUnit.isIdle()){
                    UndermindClient.getMyClient().bwapi.rightClick(scout, next.x, next.y);
                }
            }

            return this;
        }

        private Point tryNextTile() throws UndermindException {
            if(canBuild == null) {
                canBuild = new boolean[12][20];
                for(boolean[] line: canBuild){
                    Arrays.fill(line,true);
                }
                for(Unit unit: UndermindClient.getMyClient().bwapi.getMyUnits()){
                    if(unit.getTypeID() != UnitType.UnitTypes.Zerg_Overlord.ordinal()){
                        int x = unit.getTileX() - approximateCreepCorner.x;
                        int y = unit.getTileY() - approximateCreepCorner.y;
                        if(0 <= x && 0 <= y && y < canBuild.length && x < canBuild[0].length){
                            canBuild[y][x] = false;
                        }
                    }
                }
            }

            for(int i = 0; i < canBuild.length - 1; i++){
                for(int j = 0; j < canBuild[0].length - 2; j++){
                    if(isGoodTile(i,j)){
                        canBuild[i][j] = false;
                        return new Point(j + approximateCreepCorner.x, i + approximateCreepCorner.y);
                    }
                }
            }
            throw new UndermindException("No tiles to build!!!");
        }

        private boolean isGoodTile(int i, int j) {
            return canBuild[i][j] && canBuild[i][j+1] && canBuild[i][j+2]
                    && canBuild[i+1][j] && canBuild[i+1][j+1] && canBuild[i+1][j+2];
        }

        private boolean isPoolStarted() {
            return  UndermindClient.getMyClient().bwapi.getUnit(poolDrone) == null
                    || UndermindClient.getMyClient().bwapi.getUnit(poolDrone).getTypeID() == UnitType.UnitTypes.Zerg_Spawning_Pool.ordinal()
                    || !UndermindClient.getMyClient().bwapi.getUnit(poolDrone).isExists();
        }

        private void chooseScout() {
            for (Unit unit : UndermindClient.getMyClient().bwapi.getMyUnits()) {
                if (unit.getTypeID() == UnitType.UnitTypes.Zerg_Drone.ordinal()
                        && unit.getID() != poolDrone) {
                    scout = unit.getID();
                    break;
                }
            }
        }

        private void scoutNext(final Unit scoutUnit) {
            if(toScout == null || toScout.isEmpty()){
                return;
            }

            if(toScout.size() == 1 && UndermindClient.getMyClient().getEnemyHome() == null){
                UndermindClient.getMyClient().setEnemyHome(toScout.iterator().next());
                return;
            }

            next = Collections.min(toScout, new Comparator<Point>() {
                public int compare(Point o1, Point o2) {
                    double d1 = Point.distanceSq(o1.getX(),o1.getY(),scoutUnit.getX(), scoutUnit.getY());
                    double d2 = Point.distanceSq(o2.getX(),o2.getY(),scoutUnit.getX(), scoutUnit.getY());
                    return d1 > d2 ?
                            1 : (d1 < d2 ? -1 : 0);
                }
            });
            toScout.remove(next);
            UndermindClient.getMyClient().bwapi.rightClick(scout,next.x,next.y);
        }
    };

    protected static final int DRONE_COUNT = 5;

    protected int[] minerals;
    protected int poolDrone = -1;
    protected Point poolTile = null;
    protected int scout;

    public static PreparationSequence getInitialState()  {
        return INIT;
    }

    protected abstract  void clear() throws UndermindException;
    public abstract PreparationSequence gameUpdate() throws UndermindException;

    public int getScout() {
        return scout;
    }
}