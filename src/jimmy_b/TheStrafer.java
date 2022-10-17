package jimmy_b;

import robocode.*;

import java.awt.*;

public class TheStrafer extends AdvancedRobot {
    private EnemyBot enemy = new EnemyBot();
    private byte moveDirection = 1;
    private boolean ramTime = false;
    private int missedShot = 0;

    public void run() {
        setAllColors(Color.black);
        setBulletColor(Color.red);
        setScanColor(Color.red);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);
        setAdjustGunForRobotTurn(true);
        enemy.reset();

        while (true) {
            if (enemy.none() || getVelocity() == 0)
                setTurnRadarRight(360);
            if (getOthers() > 1 && enemy.getDistance() > 700)
                enemy.reset();
            if (getTime() % 30 == 0)
                moveDirection *= -1;
            execute();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        trackEnemy(e);

        // Lock on target
        setTurnRadarRight(getHeading() - getRadarHeading() + enemy.getBearing());
        setTurnGunRight(getHeading() - getGunHeading() + enemy.getBearing());

        smartFire();

        if (getOthers() == 1 && (getEnergy() > enemy.getEnergy() * 2 || missedShot > 7)) {
            // Go berserk!
            ramEnemy();
        } else {
            // They see me strafin', they hatin'...
            strafeEnemy();
        }
    }

    private void trackEnemy(ScannedRobotEvent e) {
        if (// we have no enemy, or...
                enemy.none() ||
                        // the one we scanned is closer, or..
                        e.getDistance() < enemy.getDistance() ||
                        // the one we scanned has less energy, or...
                        e.getEnergy() < enemy.getEnergy() ||
                        // we found the one we're already tracking
                        e.getName().equals(enemy.getName())
        ) {
            // track robot!
            enemy.update(e);
        }
    }

    private void smartFire() {
        // The gun isn't overheated or too far away from the target
        if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 20) {
            // If the enemy isn't moving...
            if (enemy.getVelocity() == 0)
                setFire(3);
            else
                // Adjust firepower to the distance of our target
                setFire(Math.min(500 / enemy.getDistance(), 3));
        }
    }

    public void onBulletMissed(BulletMissedEvent e) {
        if (getOthers() == 1)
            missedShot++;
    }

    private void ramEnemy() {
        ramTime = true;
        setAllColors(Color.red);
        setTurnRight(enemy.getBearing());
        setAhead(enemy.getDistance() + 5);
        execute();
    }

    private void strafeEnemy() {
        if (enemy.getDistance() < 100) {
            // Back a little...
            setTurnRight(enemy.getBearing());
            setBack(100 - enemy.getDistance());
        } else if (enemy.getDistance() > 500) {
            // Get closer...
            setTurnRight(enemy.getBearing());
            setAhead(enemy.getDistance() - 100);
        }
        // Strafe...
        setTurnRight(enemy.getBearing() + 90);
        setAhead(100 * moveDirection);
    }

    public void onRobotDeath(RobotDeathEvent e) {
        // if the enemy we were tracking died...
        if (e.getName().equals(enemy.getName())) {
            // clear tracking-info, so we can track another robot
            enemy.reset();
        }
    }

    public void onHitRobot(HitRobotEvent e) {
        if (!ramTime)
            moveDirection *= -1;
    }
}