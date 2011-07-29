package undermind;

import eisbot.proxy.model.Map;
import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created By: Itay Sabato<br/>
 * Date: 10/07/2011 <br/>
 * Time: 21:25:00 <br/>
 */
enum GamePhase {
    INIT {
        private static final int MINERAL_DIST = 600;
        private static final int DRONE_COUNT = 4;

        @Override
        protected void init() throws UndermindException {

        }

        @Override
        public GamePhase gameUpdate() throws UndermindException {
            setHome();
            collectMinerals();

            CREATE_POOL.init();
            Out.println("changed phase to CREATE_POOL");
            return CREATE_POOL;
        }

        private void setHome() throws UndermindException {
            for(Unit unit: UndermindClient.getMyClient().bwapi.getMyUnits()){
                UndermindClient.getMyClient().setMyHome(new Point(unit.getX(),unit.getY()));
                return;
            }
            throw new UndermindException("No Home!");
        }

        private void collectMinerals() throws UndermindException {
            int[] minerals = findMinerals();
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
                result[i] = Collections.min(fields, new Comparator<Unit>() {
                    public int compare(Unit u1, Unit u2) {
                    double d1 = Point.distance(u1.getX(),u1.getY(),home.x,home.y);
                    double d2 = Point.distance(u2.getX(),u2.getY(),home.x,home.y);

                    return d1 > d2 ?
                            1 : (d1 < d2 ? -1 : 0);
                    }
                }).getID();
            }
            return result;
        }
    },

    CREATE_POOL {
        private static final int POOL_PRICE = 200;
        private Point poolTile;

        @Override
        protected void init() throws UndermindException {
            poolDrone = -1;
            poolTile = null;
        }

        @Override
        public GamePhase gameUpdate() throws UndermindException {
            if(poolDrone < 0){
                for (Unit unit : UndermindClient.getMyClient().bwapi.getMyUnits()) {
                    if (unit.getTypeID() == UnitType.UnitTypes.Zerg_Drone.ordinal()) {
                        poolDrone = unit.getID();
                        Out.println("poolDrone id: "+poolDrone);
                        break;
                    }
                }
            }

            if( poolTile == null || !isGood(poolTile)){
                poolTile = findPoolTile();
            }

            if (UndermindClient.getMyClient().bwapi.getSelf().getMinerals() >= POOL_PRICE) {
                UndermindClient.getMyClient().bwapi.build(poolDrone, poolTile.x, poolTile.y, UnitType.UnitTypes.Zerg_Spawning_Pool.ordinal());
                SCOUT.init();
                SCOUT.poolDrone = poolDrone;
                Out.println("changed phase to SCOUT");
                return SCOUT;
            }
            return this;
        }

        private Point findPoolTile() throws UndermindException {
            Point initTile = null;
            for (Unit unit : UndermindClient.getMyClient().bwapi.getMyUnits()) {
                if (unit.getTypeID() == UnitType.UnitTypes.Zerg_Overlord.ordinal()) {
                    return  new Point(unit.getTileX(),unit.getTileY());
//                    initTile = new Point(unit.getTileX() - 5,unit.getTileY() - 5);
//                    break;
                }
            }
            if(initTile != null){
                for(int i = 0; i < 10; i++){
                    for(int j = 0; j < 10; j++){
                        Point tile = new Point(initTile.x + i,initTile.y + j);
                        if(isGood(tile)){
                            return tile;
                        }
                    }
                }
            }
            throw new UndermindException("no good tiles for spawning pool!");
        }

        private boolean isGood(Point tile) {
            Map map = UndermindClient.getMyClient().bwapi.getMap();
            return map.isBuildable(tile.x,tile.y)
                    &&  map.isBuildable(tile.x +1,tile.y)
                    &&  map.isBuildable(tile.x +2,tile.y)
                    && map.isBuildable(tile.x,tile.y+1)
                    &&  map.isBuildable(tile.x +1,tile.y+1)
                    &&  map.isBuildable(tile.x +2,tile.y+1);
        }
    },

    SCOUT {
        private int scout;
        private Set<Point> toScout;
        public Point next;public static final int SCOUTED_RADIUS = 150;

        @Override
        protected void init() throws UndermindException {
            scout = -1;
            toScout = Utils.getScoutingLocations(UndermindClient.getMyClient().bwapi);
            Out.println(toScout.toString());
            if(toScout.size() == 1){
                UndermindClient.getMyClient().setEnemyHome(toScout.iterator().next());
            }
            next = null;
        }

        @Override
        public GamePhase gameUpdate() throws UndermindException {
            if(UndermindClient.getMyClient().getEnemyHome() != null){
                return IDLE;
            }

            if(scout < 0){
                chooseScout();
            }

            if(scout >= 0){
                Unit scoutUnit = UndermindClient.getMyClient().bwapi.getUnit(scout);
                if(poolStarted() && scoutUnit != null
                        && (next == null || SCOUTED_RADIUS >= Point.distance(scoutUnit.getX(),scoutUnit.getY(),next.x,next.y))) {
                    scoutNext(scoutUnit);
                }
            }

            return this;
        }

        private boolean poolStarted() {
//            if( UndermindClient.getMyClient().bwapi.getUnit(poolDrone) == null){
//                Out.println("poolDrone null");
//            }
//            else if(UndermindClient.getMyClient().bwapi.getUnit(poolDrone).getTypeID() == UnitType.UnitTypes.Zerg_Spawning_Pool.ordinal()){
//                Out.println("poolDrone became pool");
//            }
//            else if(!UndermindClient.getMyClient().bwapi.getUnit(poolDrone).isExists()){
//                Out.println("poolDrone not exists");
//            }

            return  UndermindClient.getMyClient().bwapi.getUnit(poolDrone) == null || UndermindClient.getMyClient().bwapi.getUnit(poolDrone).getTypeID() == UnitType.UnitTypes.Zerg_Spawning_Pool.ordinal() || !UndermindClient.getMyClient().bwapi.getUnit(poolDrone).isExists();
        }

        private void scoutNext(Unit scoutUnit) {
            if(toScout == null || toScout.isEmpty()){
                Out.println("nothing to scout.");
                return;
            }

            if(toScout.size() == 1 && UndermindClient.getMyClient().getEnemyHome() == null){
                UndermindClient.getMyClient().setEnemyHome(toScout.iterator().next());
                Out.println("Only one location left. setting enemy home to: "+ UndermindClient.getMyClient().getEnemyHome());
                return;
            }

            next = Collections.min(toScout, new Comparator<Point>() {
                public int compare(Point o1, Point o2) {
                    double d1 = Point.distanceSq(o1.getX(),o1.getY(),UndermindClient.getMyClient().bwapi.getUnit(scout).getX(), UndermindClient.getMyClient().bwapi.getUnit(scout).getY());
                    double d2 = Point.distanceSq(o2.getX(),o2.getY(),UndermindClient.getMyClient().bwapi.getUnit(scout).getX(), UndermindClient.getMyClient().bwapi.getUnit(scout).getY());
                    return d1 > d2 ?
                            1 : (d1 < d2 ? -1 : 0);
                }
            });
            toScout.remove(next);
            UndermindClient.getMyClient().bwapi.rightClick(scout,next.x,next.y);
        }

        private void chooseScout() {
            for (Unit unit : UndermindClient.getMyClient().bwapi.getMyUnits()) {
                if (unit.getTypeID() == UnitType.UnitTypes.Zerg_Drone.ordinal()
                        && unit.getID() != poolDrone) {
                    scout = unit.getID();
                    Out.println("scout id: "+scout);
                    break;
                }
            }
        }
    },

    IDLE {
        @Override
        protected void init() throws UndermindException {

        }

        @Override
        public GamePhase gameUpdate() throws UndermindException {
            return this;
        }
    };

    protected int poolDrone = -1;

    public abstract GamePhase gameUpdate() throws UndermindException;
    protected abstract  void  init() throws UndermindException;

    public static GamePhase getInitialPhase()  {
        return INIT;
    }
}