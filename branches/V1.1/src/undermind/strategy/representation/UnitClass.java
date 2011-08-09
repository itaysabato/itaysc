package undermind.strategy.representation;

/**
 * Created By: Itay Sabato<br/>
 * Date: 22/07/11 <br/>
 * Time: 15:58 <br/>
 *
 *  This enum represents the different classifications
 *  of enemy units, ordered (ideally) by attack priority.
 */
public enum UnitClass {
    HARMFUL,
    ATTACKING_WORKER,
    SUPPLIER,
    WORKER,
    MAIN,
    HARMLESS_STRUCTURE,
    HARMLESS_UNIT
}
