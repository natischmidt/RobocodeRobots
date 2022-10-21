package jimmy_b;

import robocode.*;
import robocode.util.Utils;
import java.awt.*;

public class TheWallStrafer extends AdvancedRobot {
    private EnemyBot currentTarget = new EnemyBot();
    private byte moveDirectionWhileStrafing = 1;
    private byte moveDirectionWhileWalling = -1;
    private boolean ramTime = false;
    private int bulletMiss = 0;
    private int bulletHit = 0;
    private double totalNumberOfRobots;
    private int success;
    private int fail;
//    private double bulletPower;

    public void run() {
        totalNumberOfRobots = getOthers();

        setAllColors(Color.ORANGE);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);
        setAdjustGunForRobotTurn(true);
        currentTarget.reset();

        // Turn towards the wall
        turnLeft(getHeading() % 90);

        while (true) {
            // Change direction if the current strategy isn't working
            if (fail > success && fail > 3) {
                moveDirectionWhileWalling *= -1;
                // reset stats
                success = 0;
                fail = 0;
            }
            scanForNewTarget();
            moveRobot();
            execute();
        }
    }

    private void scanForNewTarget() {
        // If we have no target, or we're stuck
        if (currentTarget.none() || getVelocity() == 0)
            setTurnRadarRight(360);
        // Forget the target if the target is getting too far away
        if (getOthers() > 1 && currentTarget.getDistance() > 400)
            currentTarget.reset();
    }

    private void moveRobot() {
        if (getOthers() == 1) {
            // If there's only one enemy left and our energy is twice as much as theirs, or...
            if (getEnergy() > currentTarget.getEnergy() * 2 ||
                    // we keep missing our target
                    (bulletMiss > 5 && bulletMiss > bulletHit)) {
                // Go berserk!
                ramEnemy();
            } else {
                // They see me strafin', they hatin'...
                strafeEnemy();
            }
        } else {
            // Move along the walls...
            keepWalling();
        }
    }


    public void onScannedRobot(ScannedRobotEvent scannedRobot) {
        trackEnemy(scannedRobot);

        // Lock on target
        setTurnRadarRight(getHeading() - getRadarHeading() + currentTarget.getBearing());
        setTurnGunRight(getHeading() - getGunHeading() + currentTarget.getBearing());

        smartFire();

/*          Med AdvancedSteffoBot-radar:

            if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 20) {
            // If the enemy isn't moving...
            if (currentTarget.getVelocity() == 0)
                //setFire(3);
                bulletPower = 3;
            else
                // Adjust firepower to the distance of our target, or...
                bulletPower = (Math.min(Math.min(500 / currentTarget.getDistance(), 3),
                        // shoot the minimum amount of firepower to kill the target
                        (currentTarget.getEnergy() / 4)));
        }

        double absBearing = scannedRobot.getBearingRadians() + getHeadingRadians();
        double latVel = scannedRobot.getVelocity() * Math.sin(scannedRobot.getHeadingRadians() - absBearing);
        double radarTurn = absBearing - getRadarHeadingRadians();

        setTurnGunRightRadians(Utils.normalRelativeAngle(absBearing - getGunHeadingRadians() + Math.asin(latVel / (20 - 3 * bulletPower))));
        setFire(bulletPower);
        setTurnRadarRightRadians(Utils.normalRelativeAngle(radarTurn) * 2);*/

        if(getOthers() > 1 && currentTarget.getBearing() == 0 && currentTarget.getDistance() < 300)
            ramEnemy();
    }

    private void trackEnemy(ScannedRobotEvent scannedRobot) {
        if (// we have no enemy, or...
                currentTarget.none() ||
                        // the one we scanned is closer, or...
                        scannedRobot.getDistance() < currentTarget.getDistance() ||
                        // the one we scanned has less energy, or...
                        scannedRobot.getEnergy() < currentTarget.getEnergy() ||
                        // we found the one we're already tracking
                        scannedRobot.getName().equals(currentTarget.getName())
        ) {
            // track robot!
            currentTarget.update(scannedRobot);
        }
    }
    //powerranger
    private void smartFire() {
        // The gun isn't overheated or too far away from the target
        if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 20) {
            // If the enemy isn't moving...
            if (currentTarget.getVelocity() == 0)
                setFire(3);
            else
                // Adjust firepower to the distance of our target, or...
                setFire(Math.min(Math.min(500 / currentTarget.getDistance(), 3),
                        // shoot the minimum amount of firepower to kill the target
                        (currentTarget.getEnergy() / 4)));
        }
    }
    //powerranger

    public void onBulletMissed(BulletMissedEvent e) {
        if (getOthers() == 1)
            bulletMiss++;
    }

    public void onBulletHit(BulletHitEvent e) {
        if (getOthers() == 1)
            bulletHit++;
    }

    private void ramEnemy() {
        if(getOthers() == 1) {
            ramTime = true;
            setAllColors(Color.red);
        }
        setTurnRight(currentTarget.getBearing());
        setAhead(currentTarget.getDistance() + 5);
        setTurnRadarRight(360);
        execute();
    }

    private void strafeEnemy() {
        if (currentTarget.getDistance() < 100) {
            // Back a little...
            setTurnRight(currentTarget.getBearing());
            setBack(100 - currentTarget.getDistance());
        } else if (currentTarget.getDistance() > 500) {
            // Get closer...
            setTurnRight(currentTarget.getBearing());
            setAhead(currentTarget.getDistance() - 100);
        }
        // Strafe...
        setTurnRight(currentTarget.getBearing() + 90);
        setAhead(100 * moveDirectionWhileStrafing);
        if (getTime() % 30 == 0)
            moveDirectionWhileStrafing *= -1;
        execute();
    }

    private void keepWalling() {
        if (Utils.isNear(getHeadingRadians(), 0D) || Utils.isNear(getHeadingRadians(), Math.PI)) {
            ahead((Math.max(getBattleFieldHeight() - getY(), getY()) - 28) * moveDirectionWhileWalling);
        } else {
            ahead((Math.max(getBattleFieldWidth() - getX(), getX()) - 28) * moveDirectionWhileWalling);
        }
        turnRight(90 * moveDirectionWhileWalling);
        execute();
    }

    public void onRobotDeath(RobotDeathEvent e) {
        // if the enemy we were tracking died...
        if (e.getName().equals(currentTarget.getName())) {
            // clear tracking-info, so we can track another robot
            currentTarget.reset();
        }
    }

    public void onDeath(DeathEvent e) {
        if (getOthers() < totalNumberOfRobots * 0.33)
            success++;
        else
            fail++;
    }

    public void onHitRobot(HitRobotEvent e) {
        if (!ramTime)
            moveDirectionWhileStrafing *= -1;
        if(getOthers() > 1) {
            setTurnRadarRight(getHeading() - getRadarHeading() + e.getBearing());
            setTurnGunRight(getHeading() - getGunHeading() + e.getBearing());
            if(e.getBearing() == getHeading() || e.getBearing() == getHeading() * 180)
                setTurnRight(e.getBearing());
            execute();
        }
    }
}