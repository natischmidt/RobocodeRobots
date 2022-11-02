package nati.Calculon;
import robocode.*;
import robocode.util.Utils;
import java.awt.*;

public class Calculon extends AdvancedRobot{

    private EnemyBot currentTarget = new EnemyBot();
    double moveAmount;
    boolean peek;
    static double BulletsShot;
    static double hitEnemybyBullet;
    static double missedBullet;
    int moveDirection = 1;

    double normalizeBearing(double angle) {
        while (angle >  180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }


    public void run() {

        initialize();
        while (true) {
            if (getRadarTurnRemaining() == 0.0) {
                setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
            }
            scan();

            if (getOthers() > 3) {
                WallMovement();

            } else if (getOthers() > 1) {
                // dodging
                CircleMovement();

            } else if (getOthers() == 1) {
                //hunt him down
                CLosingin();


            }
            execute();
        }
    }

    public void initialize() {
        /*Här sätts både gun och radar till true, så att båda kan röra sig utan att vara bunden till det andra,
        det vill säga om det hade varit false rör sig båda tillsammans, sätta dessa på true underlättar
        träffsökerheten och rörelsemänster*/
        setAdjustRadarForGunTurn(true);
        setAdjustGunForRobotTurn(true);
        execute();

        // Med hjälp av RGB sätts färgerna till guld
        setBodyColor(new Color(218,165,32));
        setGunColor(new Color(218,165,32));
        setRadarColor(new Color(218,165,32));
        setBulletColor(new Color(218,165,32));
        setScanColor(new Color(218,165,32));

    }


    public void CircleMovement() {

        // switch directions if we've stopped
        if (getVelocity() == 0)
            moveDirection *= -1;

        // circle our enemy
        setTurnRight(currentTarget.getBearing() + 90);
        setAhead(1000 * moveDirection);
    }
    public void onRobotDeath(RobotDeathEvent e) {
        // om vår target har dött...
        if (e.getName().equals(currentTarget.getName())) {
            // rensa datan, så att vi kan få en ny måltavla!
            currentTarget.reset();
        }
    }

    public void CLosingin(){
        setTurnRight(normalizeBearing(currentTarget.getBearing() + 90 - (15 * moveDirection)));
        if (getTime() % 20 == 0) {
            moveDirection *= -1;
            setAhead(150 * moveDirection);

        }}

    public void WallMovement(){
        // Här bestämms rörelsemönstret, längst väggen motsols
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
        trackEnemy(e);
        //Graderna mot fienden
        double angletoEnemy= getHeadingRadians() + e.getBearingRadians();
        // detta minus nuvarande grader av radar heading som behövs för att vända, och normalize detta
        double radarTurn= Utils.normalRelativeAngle(angletoEnemy - getRadarHeadingRadians());
        //36.0 är mitten av fienden som scannas
        double extraturn = Math.min(Math.atan(36.0 / e.getDistance()) , Rules.RADAR_TURN_RATE_RADIANS);

        if (radarTurn < 0)
            radarTurn -= extraturn;
        else
            radarTurn += extraturn;
        // är det då vänster vänder vi oss mer till vänster och likadant med häger vilket ger ett stor scanning område
        setTurnRadarRightRadians(radarTurn);


        double headOnBearing = getHeadingRadians() + e.getBearingRadians();
        double bulletPower = 2;
        //Linear aiming, vilket innebär att det räknas ut vart fienden är på vög och det ör dör skottet åker
        double linearBearing = headOnBearing + Math.asin(e.getVelocity() / Rules.getBulletSpeed(bulletPower) * Math.sin(e.getHeadingRadians() - headOnBearing));
        setTurnGunRightRadians(Utils.normalRelativeAngle(linearBearing - getGunHeadingRadians()));
        smartFire();

    }


    private void trackEnemy(ScannedRobotEvent scannedRobot) {
        if (// Om vi saknar en target, eller...
                currentTarget.none() ||
                        // roboten som vi precis skannat befinner sig närmre oss, eller...
                        scannedRobot.getDistance() < currentTarget.getDistance() ||
                        // roboten som vi skannat har mindre energi kvar än vår target, eller...
                        scannedRobot.getEnergy() < currentTarget.getEnergy() ||
                        // vi skannade av den robot som vi redan trackar
                        scannedRobot.getName().equals(currentTarget.getName())
        ) {
            // uppdatera måltavlan!
            currentTarget.update(scannedRobot);
        }
    }

    public void onBulletHit(BulletHitEvent e) {
        hitEnemybyBullet++;
    }

    public void onBulletMissed(BulletMissedEvent event) {
        missedBullet++;
    }

    private void smartFire() {
        // The gun isn't overheated or too far away from the target
        if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 20) {
            // If the enemy isn't moving...
            if (currentTarget.getVelocity() == 0)
                setFire(3);
            else
                // Adjust firepower to the distance of our target, or...
                setFire(Math.min(Math.min(500 / currentTarget.getDistance(), 3),
                        // shoot with the least amount of bullet power to kill off the target!
                        (currentTarget.getEnergy() / 4)));
            BulletsShot++;
        }
    }

    @Override
    public void onBattleEnded(BattleEndedEvent event) {
        out.println("\n________________GAME OVER________________");
        out.println("\nTotal shots: " + (int) BulletsShot);
        out.println("Total hits: " + (int) hitEnemybyBullet);
        out.println("Total misses: " + (int)  missedBullet);
        out.println("Accuracy: " + Math.round((hitEnemybyBullet / BulletsShot) * 100) + " %");
    }

}
