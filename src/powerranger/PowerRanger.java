package powerranger;

import robocode.AdvancedRobot;
import robocode.BulletHitEvent;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;
import robocode.*;

import java.awt.*;


public class PowerRanger extends AdvancedRobot {
    private EnemyBot currentTarget = new EnemyBot();
    double shotBullet = 0;
    double hitEnemybyBullet = 0;
    double gothitbyBullet = 0;
    double missedBullet = 0;
    int dir = 1;
    double bulletPower = 1;
    double rand = 8;
    int timeToStop = 65;
    boolean haveAlreadyGotAnEnergyBuddy = false;

    public void run() {

        addCustomEvent(new Condition("energyBuddies") {                      //lägger till ett mycket viktigt och nödvändigt custom event.
            public boolean test() {
                return (currentTarget.getEnergy() == getEnergy());
            }
        });

        initialize();
        // Vänder roboten mot väggen
        turnLeft(getHeading() % 90);

        while (true) {
            // Skanna igen om det behövs
            setTurnRadarRight(Double.POSITIVE_INFINITY);
            System.out.println("Scanna igen!!");

            WallMovement();
            DodgeMovment();
        }
    }

    public void initialize() {
        // Let the robot body, gun, and radar turn independently of each other
        setAdjustRadarForGunTurn(true);
        setAdjustGunForRobotTurn(true);

        // Set robot colors
        setBodyColor(new Color(23,23,23));
        setGunColor(new Color(255,8,0));
        setRadarColor(new Color(253,88,0));
        setBulletColor(new Color(23,23,23));
        setScanColor(new Color(0,255,255));

    }

    public void WallMovement(){
        //Räknar ut hur jag ska åka längst väggarna
        if (Utils.isNear(getHeadingRadians(), 0D) || Utils.isNear(getHeadingRadians(), Math.PI)) {      //Utils.isNear returnerar true om differensen mellan de två argumenten är mindre än 1.0E-5, dvs 0.000010. Här betyder det true om vi är på väg (nästan) rakt norrut, eller (nästan) rakt söderut
            ahead((Math.max(getBattleFieldHeight() - getY(), getY()) - 28) * dir);              //variabeln dir är alltid 1, men vi sparar den för nu, ifall vi vill använda den för att byta riktning.
                                                                                                        //framåt (slagfältets höjd - vår höjd) eller (vår höjd - 28)
        } else {
            ahead((Math.max(getBattleFieldWidth() - getX(), getX()) - 28) * dir);
        }
        turnRight(90 * dir);
    }
    public void DodgeMovment(){
        //Ändrar hur den åker
        timeToStop--;
        if (timeToStop < 1) {
            rand = Math.random();
            if (rand > 0.5) {
                dir = 1;
                timeToStop = 45;
            }
            if (rand < 0.5) {
                dir = -1;
                timeToStop = 5;
            }
        }
    }


    public void onBulletMissed(BulletMissedEvent event) {
        missedBullet++;
    }
    public void onRoundEnded(RoundEndedEvent event) {
        Stats();
    }

    public void onScannedRobot(ScannedRobotEvent scannedRobot) {
        trackEnemy(scannedRobot);

        //data för att kunna sikta på fienden
        double absBearing = scannedRobot.getBearingRadians() + getHeadingRadians();
        double latVel = scannedRobot.getVelocity() * Math.sin(scannedRobot.getHeadingRadians() - absBearing);
        double radarTurn = absBearing - getRadarHeadingRadians();

        // Beräkna firepower
        smartFire();

        // Sikta på busarna
        setTurnGunRightRadians(Utils.normalRelativeAngle(absBearing - getGunHeadingRadians() +
                Math.asin(latVel / (20 - 3 * bulletPower))));

        // Håll kvar radarn
        setTurnRadarRightRadians(Utils.normalRelativeAngle(radarTurn) * 2);
    }

    private void smartFire() {
        if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 20) {
            // Om vår target står still skjut med full kraft
            if (currentTarget.getVelocity() == 0)
                bulletPower = 3;
            else
                // Juste firepower till avståndet till vår target, eller...
                bulletPower = (Math.min(Math.min(500 / currentTarget.getDistance(), 3),
                        // skjut med minsta möjliga kraft för att döda
                        (currentTarget.getEnergy() / 4)));

            setFire(bulletPower);
            shotBullet++;
        }
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

    public void onRobotDeath(RobotDeathEvent e) {
        // if the enemy we were tracking died...
        if (e.getName().equals(currentTarget.getName())) {
            // clear tracking-info, so we can track another robot
            currentTarget.reset();
        }
    }

    public void onBulletHit(BulletHitEvent e) {
        hitEnemybyBullet++;
    }

    private void Stats() {
        out.print("________________Round ended_________________________");
                        out.println("\nShots:" + shotBullet);
                        out.println("Hits:" + hitEnemybyBullet);
                        out.println("Misses:" + missedBullet);
                        out.println("Accuracy:" + (hitEnemybyBullet / shotBullet));
    }
    public void onCustomEvent(CustomEvent e) {

        if (e.getCondition().getName().equals("energyBuddies")) {

            while (!haveAlreadyGotAnEnergyBuddy) {
                energyBuddies(getEnergy(), currentTarget.getName());
                haveAlreadyGotAnEnergyBuddy = true;
            }

        }
    }
    public void energyBuddies (double energy, String name) {

        if (energy == getEnergy() && getEnergy() != 100 && getEnergy() != 0.0) {            //om scannad robot har lika mycket energi som vi och vår energi inte är 0 eller 100...
                                                                                            //...så känner vi en djup samhörighet med den.
            System.out.println("We both have " + (int)getEnergy() + " energy, " + currentTarget.getName() + ", we are energy buddies!");
            System.out.println("   ***     ***                   ***     ***                   ***     ***");
            System.out.println(" **   ** **   **               **   ** **   **               **   ** **   **");
            System.out.println("*       *       *             *       *       *             *       *       *");
            System.out.println("*               *             *               *             *               *");
            System.out.println(" *    LOVE     *               *     LOVE    *               *     LOVE    *");
            System.out.println("  **         **   ***     ***   **         **   ***     ***   **         **");
            System.out.println("    **     **   **   ** **   **   **     **   **   ** **   **   **     **");
            System.out.println("      ** **    *       *       *    ** **    *       *       *    ** **");
            System.out.println("        *      *               *      *      *               *      *");
            System.out.println("                *   Buddies   *               *     LOVE    *");
            System.out.println("   ***     ***   **         **   ***     ***   **         **   ***     ***");
            System.out.println(" **   ** **   **   **     **   **   ** **   **   **     **   **   ** **   **");
            System.out.println("*       *       *    ** **    *       *       *    ** **    *       *       *");
            System.out.println("*               *      *      *   Energy      *      *      *               *");
            System.out.println(" *     LOVE    *               *    buddies  *               *     LOVE    *");
            System.out.println("  **         **   ***     ***   **         **   ***     ***   **         **");
            System.out.println("    **     **   **   ** **   **   **     **   **   ** **   **   **     **");
            System.out.println("      ** **    *       *       *    ** **    *       *       *    ** **");
            System.out.println("        *      *               *      *      *    4-ever     *      *");
            System.out.println("                *     LOVE    *               *             *");
            System.out.println("                 **         **                 **         **");
            System.out.println("                   **     **                     **     **");
            System.out.println("                     ** **                         ** **");
            System.out.println("                       *                             *");


        }
    }

}






