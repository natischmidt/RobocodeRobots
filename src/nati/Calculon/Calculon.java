package nati.Calculon;
import robocode.*;
import robocode.util.Utils;
import java.awt.*;

public class Calculon extends AdvancedRobot{
    double moveAmount;
    boolean peek;
    // stats :
    double shotBullet = 0;
    double hitEnemybyBullet = 0;
    double gothitbyBullet = 0;
    double missedBullet = 0;


    public void run() {

        initialize();
        while (true) {
            Radar();
            Movement();
            scan();
            execute();

//            addCustomEvent(new Condition("CooledGun") {
//                @Override
//                public boolean test() {
//                    return (getGunHeat() == 0);
//                }
        }
    }

//        do{
//            //similar to turnmultiplierlock but with a wider scanning ability
//            //Turn the radar if thre are no more turns,
//            // starts if it stops and at the start of a round
//            //
//            if (getRadarTurnRemaining() == 0.0) {
//                setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
//            }
//            execute();
//            Movement();
//            execute();
//        } while(true);
//    }
//  custom event till powerranger with stats on eevery round end
//    public void onCustomEvent(CustomEvent e) {
//        double bulletPower = 3;
//        if (e.getCondition().getName().equals("CooledGun")){
//              setFire(bulletPower);
//            shotBullet++;
//        }
//    }


    public void initialize() {
        // Let the robot body, gun, and radar turn independently of each other
        setAdjustRadarForGunTurn(true);
        setAdjustGunForRobotTurn(true);

        // Set robot colors
        setBodyColor(new Color(218,165,32));
        setGunColor(new Color(218,165,32));
        setRadarColor(new Color(218,165,32));
        setBulletColor(new Color(218,165,32));
        setScanColor(new Color(218,165,32));

    }
    public void Radar() {
        if (getRadarTurnRemaining() == 0.0) {
            setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
        }

    }


    public void Movement(){
        this.moveAmount = Math.max(this.getBattleFieldWidth(), this.getBattleFieldHeight());
        this.peek = false;
        this.turnLeft(this.getHeading() % 90.0);
        this.ahead(this.moveAmount);
        this.peek = true;
        this.turnGunLeft(90.0);
        this.turnLeft(90.0);
        this.peek = true;
        this.ahead(this.moveAmount);
        this.peek = false;
        this.turnLeft(90.0);
    }
    public void onScannedRobot(ScannedRobotEvent e) {

        //absolute angle towards the enemy
        double angletoEnemy= getHeadingRadians() + e.getBearingRadians();
        // minus current radar heading to turn requierd so we face the enemy making sure it normalized
        double radarTurn= Utils.normalRelativeAngle(angletoEnemy - getRadarHeadingRadians());
        //36.0 is the units from the center of the enemy we scan
        double extraturn = Math.min(Math.atan(36.0 / e.getDistance()) , Rules.RADAR_TURN_RATE_RADIANS);
        radarTurn += (radarTurn < 0 ? -extraturn : extraturn);
        // if its left turn more left, if its right turn more right giving a good and wide  sweep
        setTurnRadarRightRadians(radarTurn);
//Testing shooting
        double headOnBearing = getHeadingRadians() + e.getBearingRadians();
        double bulletPower = 2;
        double linearBearing = headOnBearing + Math.asin(e.getVelocity() / Rules.getBulletSpeed(bulletPower) * Math.sin(e.getHeadingRadians() - headOnBearing));
        setTurnGunRightRadians(Utils.normalRelativeAngle(linearBearing - getGunHeadingRadians()));
        setFire(bulletPower);
        shotBullet++;

    }

    public void onBulletHit(BulletHitEvent e) {
        hitEnemybyBullet++;
    }

    public void onHitbyBullet(BulletHitEvent e) {
        gothitbyBullet++;
    }
    public void onBulletMissed(BulletMissedEvent event) {
        missedBullet++;
    }

    // for custom event
    public void onRoundEnded(RoundEndedEvent event) {
        out.print("_______Round ended________");
        out.println("Shots:" + shotBullet);
        out.println("Hits:" + hitEnemybyBullet);
        out.println("Misses:" + missedBullet);
        out.println("Hit by Enemy:" + gothitbyBullet);
        out.println("Accuracy:" + (hitEnemybyBullet/ shotBullet));

    }

}
