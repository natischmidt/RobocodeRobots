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

                addCustomEvent(new Condition("YaDead") {
                    @Override
                    public boolean test() {
                        return (!Objects.equals(getName(), " "));
                    }

                    public void onCustomEvent(CustomEvent e) {

                        if (e.getCondition().getName().equals("YaDead")) {
                            Stats();
                        }
                    }





                    public void onScannedRobot(ScannedRobotEvent e) {
                        //data för att kunna sikta på fienden
                        double absBearing = e.getBearingRadians() + getHeadingRadians();
                        double latVel = e.getVelocity() * Math.sin(e.getHeadingRadians() - absBearing);
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


                        if (e.getDistance() > 200) {
                            bulletPower = 1;
                        }
                        if (e.getDistance() < 200) {
                            bulletPower = 2;
                        }
                        if (e.getDistance() < 100) {
                            bulletPower = 3;
                        }

                        setTurnGunRightRadians(Utils.normalRelativeAngle(absBearing - getGunHeadingRadians() + Math.asin(latVel / (20 - 3 * bulletPower)))); //Siktar på busarna
                        setFire(bulletPower); //Skjuter!
                        setTurnRadarRightRadians(Utils.normalRelativeAngle(radarTurn) * 2); // Hålla kvar radarn

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
            }





