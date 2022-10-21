package Powerranger;

import robocode.AdvancedRobot;
import robocode.BulletHitEvent;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;
import robocode.*;
import robocode.util.*;
import java.awt.*;
import java.util.Objects;


public class powerranger extends AdvancedRobot {
    private EnemyBot currentTarget = new EnemyBot();
    int dir = 1;
    double bulletPower = 1;
    boolean RamIt = false;
    double rand = 8;
    int timeToStop = 65;

        public void run() {
            setAdjustRadarForRobotTurn(true);
            setAdjustRadarForGunTurn(true);
            setBodyColor(Color.black);
            setGunColor(Color.red);
            setRadarColor(Color.orange);
            setBulletColor(Color.black);
            setScanColor(Color.cyan);


            //Vänder mig åt rätt håll.
            turnLeft(getHeading() % 90);

            while (true) {
                //Scanna igen om det behövs
                setTurnRadarRight(Double.POSITIVE_INFINITY);
                System.out.println("Scanna igen!!");
                //Räknar ut hur jag ska åka längst väggarna


                if (Utils.isNear(getHeadingRadians(), 0D) || Utils.isNear(getHeadingRadians(), Math.PI)) {
                    ahead((Math.max(getBattleFieldHeight() - getY(), getY()) - 28) * dir);
                } else {
                    ahead((Math.max(getBattleFieldWidth() - getX(), getX()) - 28) * dir);
                }
                turnRight(90 * dir);

                }
            }



    public void onScannedRobot(ScannedRobotEvent scannedRobot) {
        trackEnemy(scannedRobot);

        //data för att kunna sikta på fienden
        double absBearing = scannedRobot.getBearingRadians() + getHeadingRadians();
        double latVel = scannedRobot.getVelocity() * Math.sin(scannedRobot.getHeadingRadians() - absBearing);
        double radarTurn = absBearing - getRadarHeadingRadians();

        //Saktar ner lite då och då.
        if (timeToStop < 1) {
            rand = Math.random();
            if (rand > 0.5) {
                setMaxVelocity(12);
            }
            if (rand < 0.5) {
                setMaxVelocity(2);
            }
        }
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
    public void onBulletHit(BulletHitEvent e) {

                    }

                    public void onRobotDeath(RobotDeathEvent event) {

                    }

                    private void Stats() {
                        out.print("_______Round ended________");
//                        out.println("Shots:" + shotBullet);
//                        out.println("Hits:" + hitEnemybyBullet);
//                        out.println("Misses:" + missedBullet);
//                        out.println("Hit by Enemy:" + gothitbyBullet);
//                        out.println("Accuracy:" + (hitEnemybyBullet / shotBullet));
                    }


                }






