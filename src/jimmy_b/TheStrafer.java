package jimmy_b;

import robocode.*;
import robocode.util.Utils;

import java.awt.*;

public class TheStrafer extends AdvancedRobot {
    Target currentTarget = new Target();
    byte moveDirection = 1;
    boolean ramTime = false;
    boolean gettingInPosition = false;
    int bulletMiss, bulletHit, hitByBullet;

    public void run() {
        // Let the radar/gun turn independent of the robot
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);
        setColors();

        while (true) {
            scanForNewTarget();
            moveRobot();
            execute();
        }
    }

    private void setColors() {
        setBodyColor(Color.black);
        setGunColor(Color.black);
        setRadarColor(Color.red);
        setBulletColor(Color.red);
        setScanColor(Color.red);
    }

    private void scanForNewTarget() {
        // If we have no target, or we're stuck
        if(currentTarget.none() || getVelocity() == 0)
            setTurnRadarRight(360);

        // Forget the target if the target is getting too far away
        if (getOthers() > 2 && currentTarget.getDistance() > 700) {
            currentTarget.reset();
//            setAhead(moveDirection);
        }
    }

    private void moveRobot() {
        // If there's only one enemy left and...
        if (getOthers() == 1 &&
                // we keep missing our target,
                ((bulletMiss > 7 && bulletMiss > bulletHit) ||
                        // or they hit us more than we hit them
                        (bulletHit < hitByBullet && hitByBullet > 7))) {
            // Go berserk!
            ramTime = true;
            resetStats();
            ramEnemy();
        } else if (!ramTime) {
            // They see me strafin', they hatin'...
            strafeEnemy();
        }

        while (ramTime) {
            // Turn on the police siren!
            setAllColors(Color.red);
            if(getTime() % 2 == 0)
                setAllColors(Color.blue);
            // If we're chasing the enemy, but we keep missing and...
            if ((bulletMiss > 5 && bulletMiss > bulletHit) ||
                    // our (relative) energy is getting dangerously low!
                    (getEnergy() < 40 && currentTarget.getEnergy() > getEnergy())) {
                // Stop ramming!
                resetStats();
                setColors();
                ramTime = false;
                out.println("Going back to strafing!");
                break;
            } else
                // Keep ramming!
                ramEnemy();
        }
    }


    private void ramEnemy() {
        if (currentTarget.getDistance() > 200 || getVelocity() == 0)
            setTurnRadarRight(360);
        setTurnRight(currentTarget.getBearing());
        setAhead(currentTarget.getDistance() + 5);
        execute();
    }

    private void strafeEnemy() {
        gettingInPosition = false;
        if (currentTarget.getDistance() < 100) {
            gettingInPosition = true;
            // Back a little...
            setTurnRight(currentTarget.getBearing());
            setBack(150 - currentTarget.getDistance());
        } else if (currentTarget.getDistance() > 500) {
            gettingInPosition = true;
            // Get closer...
            setTurnRight(currentTarget.getBearing());
            setAhead(currentTarget.getDistance() - 150);
        } else if(!gettingInPosition) {
            // Strafe...
            setTurnRight(currentTarget.getBearing() + 90);
            setAhead(100 * moveDirection);
            if (getTime() % 30 == 0)
                moveDirection *= -1;
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent scannedRobot) {
        // Check if we should change target
        if(getOthers() != 1)
            trackEnemy(scannedRobot);
        else
            // If there's only one robot left, just update the data on our target
            currentTarget.update(scannedRobot);

        double bulletPower = calculateBulletPower();
        double bulletSpeed = (20.0 - 3.0 * bulletPower);

        // Calculate the absolute bearing to our targets position
        double targetAbsoluteBearing = getHeadingRadians() + currentTarget.getBearingRadians();

        // Calculate how fast the target is moving parallel to our robot
        double targetLateralVelocity = currentTarget.getVelocity() *
                Math.sin(currentTarget.getHeadingRadians() - targetAbsoluteBearing);

        // Calculate the radians needed to turn our radar on the target
        double radarTurn = targetAbsoluteBearing - getRadarHeadingRadians();

        // Calculate the radians needed to turn to shoot at our targets predicted position,
        // assuming the target will continue moving at the same speed and direction
        double gunTurn = targetAbsoluteBearing - getGunHeadingRadians() + Math.asin(targetLateralVelocity / bulletSpeed);

        // Normalize the radians, so that our radar/gun will choose to turn the shortest amount of radians
        setTurnRadarRightRadians(Utils.normalRelativeAngle(radarTurn));
        setTurnGunRightRadians(Utils.normalRelativeAngle(gunTurn));
        setFire(bulletPower);

        /*double bulletSpeed = (20.0 - 3.0 * bulletPower);

        // Calculate the absolute bearing to our targets position
        double absoluteBearing = getHeadingRadians() + currentTarget.getBearingRadians();
        // Calculate the targets current X/Y position on the sine/cosine curve (in relation to our current position)
        double targetX = getX() + currentTarget.getDistance() * Math.sin(absoluteBearing);
        double targetY = getY() + currentTarget.getDistance() * Math.cos(absoluteBearing);

        // Set initial time and coordinates of our target
        double time = 0;
        double predictedX = targetX, predictedY = targetY;

        // Estimate the targets future position (assuming it will move in the same direction with the same speed)
        // adjusted for our bullets speed (depending on calculated bullet power needed)
        while((++time) * bulletSpeed <
                // returns the distance between our position and our targets predicted position
                Point2D.Double.distance(getX(), getY(), predictedX, predictedY)){
            // amplifies the sine/cosine curve of our targets heading radians with the speed of our target and...
            // keeps adding this to the targets predicted coordinates for every time-unit added
            predictedX += Math.sin(currentTarget.getHeadingRadians()) * currentTarget.getVelocity();
            predictedY += Math.cos(currentTarget.getHeadingRadians()) * currentTarget.getVelocity();
            // If our target is heading towards a wall...
            if(predictedX < 18.0 || predictedX > getBattleFieldWidth() - 18.0 ||
                    predictedY < 18.0 || predictedY > getBattleFieldHeight() - 18.0){
                // assume the target is going to stop, stop counting and set the estimated coordinates!
                predictedX = Math.min(Math.max(18.0, predictedX), getBattleFieldWidth() - 18.0);
                predictedY = Math.min(Math.max(18.0, predictedY), getBattleFieldHeight() - 18.0);
                break;
            }
        }
        // Calculate the absolute bearing to our targets predicted position
        double predictedAbsoluteBearing = Utils.normalAbsoluteAngle(
                Math.atan2(predictedX - getX(), predictedY - getY()));

        // Calculate the radians needed to turn our radar on the target
        double radarTurn = absoluteBearing - getRadarHeadingRadians();
        // Calculate the radians needed to turn to shoot at our targets predicted position
        double gunTurn = predictedAbsoluteBearing - getGunHeadingRadians();
        // Normalize the radians, so that our radar/gun will choose to turn the shortest amount of radians
        setTurnRadarRightRadians(Utils.normalRelativeAngle(radarTurn));
        setTurnGunRightRadians(Utils.normalRelativeAngle(gunTurn));
        setFire(bulletPower);*/
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

    @Override
    public void onRobotDeath(RobotDeathEvent e) {
        // if the enemy we were tracking died...
        if (e.getName().equals(currentTarget.getName())) {
            // clear tracking-info, so we can track another robot
            currentTarget.reset();
        }
    }

    private double calculateBulletPower() {
        // If the target isn't moving, shoot with full power!
        if (currentTarget.getVelocity() == 0)
            return 3.0;
        else
            // Adjust firepower to the distance of our target, or...
            return (Math.min(Math.min(500.0 / currentTarget.getDistance(), 3.0),
                    // shoot with the least amount of bullet power to kill off the target!
                    (currentTarget.getEnergy() / 4.0)));
    }

    private void resetStats() {
        bulletHit = 0;
        bulletMiss = 0;
        hitByBullet = 0;
    }

    @Override
    public void onBulletMissed(BulletMissedEvent e) {
        if (getOthers() == 1)
            bulletMiss++;
    }

    @Override
    public void onBulletHit(BulletHitEvent e) {
        if (getOthers() == 1)
            bulletHit++;
    }

    @Override
    public void onHitByBullet(HitByBulletEvent event) {
        if (getOthers() == 1)
            hitByBullet++;
    }

    @Override
    public void onHitRobot(HitRobotEvent e) {
        if (!ramTime)
            moveDirection *= -1;
        else if (!e.isMyFault() && getVelocity() == 0) {
            setTurnRadarRight(getHeading() - getRadarHeading() + e.getBearing());
            scan();
        }
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        if (!ramTime && !gettingInPosition)
            moveDirection *= -1;
    }
}