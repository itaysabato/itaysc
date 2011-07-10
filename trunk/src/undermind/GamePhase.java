package undermind;

import eisbot.proxy.JNIBWAPI;
import eisbot.proxy.model.Unit;
import eisbot.proxy.types.UnitType;

/**
 * Created By: Itay Sabato<br/>
 * Date: 10/07/2011 <br/>
 * Time: 21:25:00 <br/>
 */
enum GamePhase {
    INIT {
        private static final int MINERAL_DIST = 300;
        public static final int DRONE_COUNT = 5;            //todo: find out the number of drones

        @Override
        public GamePhase gameUpdate() throws UndermindException {
            validate();
            collectMinerals();

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
                    double distance = Math.sqrt(Math.pow(minerals.getX() - bwapi.getMyUnits().get(0).getX(), 2) + Math.pow(minerals.getY() - bwapi.getMyUnits().get(0).getY(), 2));
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

        private int poolDrone = -1;
        private static final int POOL_PRICE = 200;

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

    SCOUT {
        @Override
        public GamePhase gameUpdate() throws UndermindException {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
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
