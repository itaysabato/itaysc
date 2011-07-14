package undermind;

import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.BaseLocation;
import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * Created By: Itay Sabato<br/>
 * Date: 10/07/2011 <br/>
 * Time: 21:25:00 <br/>
 */
enum GamePhase {
    INIT {
        private static final int MINERAL_DIST = 300;
        private static final int DRONE_COUNT = 4;

        @Override
        public GamePhase gameUpdate() throws UndermindException {
            Out.println("started");
            validate();
            Out.println("validated");
            collectMinerals();
            Out.println("collected");

            CREATE_POOL.init(bwapi);
            bwapi = null;
            Out.println("changed phase to CREATE_POOL");
            return CREATE_POOL;
        }

        private void collectMinerals() throws UndermindException {
            int[] minerals = findMinerals();
            int i = 0;

            for (Unit unit : bwapi.getMyUnits()) {
                if (unit.getTypeID() == UnitType.UnitTypes.Zerg_Drone.ordinal()) {
                    bwapi.rightClick(unit.getID(), minerals[i++]);
                }
            }
        }

        private int[] findMinerals() throws UndermindException {
            int[] result = new int[DRONE_COUNT];
            int i = 0;

            for (Unit minerals : bwapi.getNeutralUnits()) {
                if (minerals.getTypeID() == UnitType.UnitTypes.Resource_Mineral_Field.ordinal()) {
                    double distance = Utils.distance(minerals.getX(),minerals.getY(), bwapi.getMyUnits().get(0).getX(), bwapi.getMyUnits().get(0).getY());
                    if (distance < MINERAL_DIST) {
                        result[i++] =  minerals.getID();
                        if(i >= DRONE_COUNT){
                            return result;
                        }
                    }
                }
            }
            throw new UndermindException("Not enough mineral fields near by");
        }

    },

    CREATE_POOL {
        private int poolDrone;
        private static final int POOL_PRICE = 200;

        @Override
        protected void init(JNIBWAPI bwapi) throws UndermindException {
            super.init(bwapi);
            poolDrone = -1;
        }

        @Override
        public GamePhase gameUpdate() throws UndermindException {
            if(poolDrone < 0){
                for (Unit unit : bwapi.getMyUnits()) {
                    if (unit.getTypeID() == UnitType.UnitTypes.Zerg_Drone.ordinal()) {
                        poolDrone = unit.getID();
                        break;
                    }
                }
            }

            if (bwapi.getSelf().getMinerals() >= POOL_PRICE) {
                for (Unit unit : bwapi.getMyUnits()) {
                    if (unit.getTypeID() == UnitType.UnitTypes.Zerg_Overlord.ordinal()) {
                        bwapi.build(poolDrone, unit.getTileX(), unit.getTileY(), UnitType.UnitTypes.Zerg_Spawning_Pool.ordinal());

                        SCOUT.init(bwapi);
                        bwapi = null;
                        Out.println("changed phase to SCOUT");
                        return SCOUT;
                    }
                }
            }
            return this;
        }
    },

    //TODO: improve scouting so that scout only goes to the main base locations
    SCOUT {
        private int scout;
        private Set<BaseLocation> toScout;

        @Override
        protected void init(JNIBWAPI bwapi) throws UndermindException {
            super.init(bwapi);
            scout = -1;
            toScout = new HashSet<BaseLocation>(bwapi.getMap().getBaseLocations());
        }

        @Override
        public GamePhase gameUpdate() throws UndermindException {
            if(scout < 0){
                chooseScout();
            }

            if(scout >= 0){
                Unit scoutUnit = bwapi.getUnit(scout);
                if(scoutUnit != null && (scoutUnit.isGatheringMinerals()  || scoutUnit.isIdle())) {
                    scoutNext();
                }
            }

            return this;
        }

        private void scoutNext() {
            if(toScout == null || toScout.isEmpty()){
                return;
            }

            BaseLocation baseLocation = Collections.min(toScout,new Comparator<BaseLocation>() {
                public int compare(BaseLocation o1, BaseLocation o2) {
                    double d1 = Utils.distance(o1.getX(),o1.getY(),bwapi.getUnit(scout).getX(),bwapi.getUnit(scout).getY());
                    double d2 = Utils.distance(o2.getX(),o2.getY(),bwapi.getUnit(scout).getX(),bwapi.getUnit(scout).getY());
                    return d1 > d2 ?
                            1 : (d1 < d2 ? -1 : 0);
                }
            }) ;
            toScout.remove(baseLocation);
            bwapi.rightClick(scout,baseLocation.getX(),baseLocation.getY());
        }

        private void chooseScout() {
            for (Unit unit : bwapi.getMyUnits()) {
                if (unit.getTypeID() == UnitType.UnitTypes.Zerg_Drone.ordinal()
                        && unit.isGatheringMinerals() && !unit.isCarryingMinerals()) {
                    scout = unit.getID();
                    break;
                }
            }
        }
    },

    IDLE {
        @Override
        public GamePhase gameUpdate() throws UndermindException {
            return this;
        }
    };

    protected JNIBWAPI bwapi = null;

    public abstract GamePhase gameUpdate() throws UndermindException;

    protected  void  init(JNIBWAPI bwapi) throws UndermindException {
        if(bwapi == null){
            throw new UndermindException("null bwapi given to ["+this+"]");
        }
        this.bwapi = bwapi;
    }

    protected void validate() throws UndermindException {
        if(bwapi == null){
            throw new UndermindException("Illegal Phase access to ["+this+"]");
        }
    }

    public static GamePhase getInitialPhase(JNIBWAPI bwapi) throws UndermindException {
        INIT.init(bwapi);
        return INIT;
    }
}