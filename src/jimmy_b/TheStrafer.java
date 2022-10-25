package jimmy_b;

import robocode.*;
import robocode.util.Utils;

import java.awt.*;

public class TheStrafer extends AdvancedRobot {
    private EnemyBot currentTarget = new EnemyBot();
    private byte moveDirection = 1;
    private boolean ramTime = false;
    private int bulletMiss = 0;
    private int bulletHit = 0;

    public void run() {
        setAllColors(Color.black);
        setBulletColor(Color.red);
        setScanColor(Color.red);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);

        currentTarget.reset();

        while (true) {
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
        if (getOthers() > 1 && currentTarget.getDistance() > 700)
            currentTarget.reset();
    }

    private void moveRobot() {
        // If there's only one enemy left and our energy is twice as much as theirs, or...
        if (getOthers() == 1 && (getEnergy() > currentTarget.getEnergy() * 2 ||
                // we keep missing our target
                (bulletMiss > 5 && bulletMiss > bulletHit))) {
            // Go berserk!
            ramEnemy();
        } else {
            // They see me strafin', they hatin'...
            strafeEnemy();
        }
    }

    public void onScannedRobot(ScannedRobotEvent scannedRobot) {
        trackEnemy(scannedRobot);

        // Lock on target
        setTurnRadarRight(getHeading() - getRadarHeading() + currentTarget.getBearing());
        setTurnGunRight(getHeading() - getGunHeading() + currentTarget.getBearing());

        smartFire();
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
        }
    }

    public void onBulletMissed(BulletMissedEvent e) {
        if (getOthers() == 1)
            bulletMiss++;
    }

    public void onBulletHit(BulletHitEvent e) {
        if (getOthers() == 1)
            bulletHit++;
    }

    private void ramEnemy() {
        ramTime = true;
        setAllColors(Color.red);
        setTurnRight(currentTarget.getBearing());
        setAhead(currentTarget.getDistance() + 5);
        setTurnRadarRight(360);
        execute();
    }

    private void strafeEnemy() {
        if (currentTarget.getDistance() < 200) {
            // Back a little...
            setTurnRight(currentTarget.getBearing());
            setBack(200 - currentTarget.getDistance());
        } else if (currentTarget.getDistance() > 500) {
            // Get closer...
            setTurnRight(currentTarget.getBearing());
            setAhead(currentTarget.getDistance() - 200);
        }
        // Strafe...
        setTurnRight(currentTarget.getBearing() + 90);
        setAhead(100 * moveDirection);
        if (getTime() % 30 == 0)
            moveDirection *= -1;
    }

    public void onRobotDeath(RobotDeathEvent e) {
        // if the enemy we were tracking died...
        if (e.getName().equals(currentTarget.getName())) {
            // clear tracking-info, so we can track another robot
            currentTarget.reset();
        }
    }

    public void onHitRobot(HitRobotEvent e) {
        if (!ramTime)
            moveDirection *= -1;
    }
}