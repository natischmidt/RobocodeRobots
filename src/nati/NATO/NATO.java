package nati.NATO;

import robocode.*;
import robocode.robotinterfaces.IAdvancedEvents;
import robocode.robotinterfaces.IAdvancedRobot;
import robocode.util.Utils;

import java.awt.*;

public class NATO extends AdvancedRobot implements IAdvancedRobot, IAdvancedEvents {

    Enemy enemy = new Enemy(this);
    boolean peek;
    double moveAmount;
    double radarTurn;
    double bulletPower = 3;

    private static final int NO_PREDICTION = 0;
    private static final int APPROXIMATE = 1;
    private static final int LINEAR_AIM_AHEAD = 2;
    int aimingMode = APPROXIMATE ;
    public void run() {
        // default colors
        this.setBodyColor(Color.white);
        this.setGunColor(Color.white);
        this.setRadarColor(Color.white);
        this.setScanColor(Color.white);
        //Movment to go along the walls but the opposite direction than Walls, this because
        // 1. avoiding walls
        // 2. for any robot who has a strategy against walls and im sure everyone does
        this.moveAmount = Math.max(this.getBattleFieldWidth(), this.getBattleFieldHeight());
        this.peek = false;
        this.turnLeft(this.getHeading() % 90.0);
        this.ahead(this.moveAmount);
        this.peek = true;
        this.turnGunLeft(90.0);
        this.turnLeft(90.0);
        while (true) {
            this.peek = true;
            this.ahead(this.moveAmount);
            this.peek = false;
            this.turnLeft(90.0);
            execute();
        }


    }

    public void onScannedRobot(ScannedRobotEvent e) {
        enemy.setName(e.getName());
        enemy.update(e);
        //Try to keep radar on enemy
        radarTurn = this.getHeading() + e.getBearing() - this.getRadarHeading();
        radarTurn = Utils.normalRelativeAngleDegrees(radarTurn);
        setTurnRadarRight(4 * radarTurn);
        setTurnRight(e.getBearing() +90);
        //aim at target and fire
        if(aimingMode == NO_PREDICTION) {
            setTurnGunRight(Utils.normalRelativeAngleDegrees(this.getHeading() - this.getGunHeading() + e.getBearing()));
        } else if(aimingMode == APPROXIMATE) {
            Aim(enemy.getPredictedX(), enemy.getPredictedY());
        } else if(aimingMode == LINEAR_AIM_AHEAD) {
            double headOnBearing = getHeadingRadians() + e.getBearingRadians();
            double linearBearing = headOnBearing + Math.asin(e.getVelocity() / Rules.getBulletSpeed(bulletPower) * Math.sin(e.getHeadingRadians() - headOnBearing));
            setTurnGunRightRadians(Utils.normalRelativeAngle(linearBearing - getGunHeadingRadians()));
        }
        setFire(bulletPower);

    }
    public void onHitWall(HitWallEvent e){
    }

    public void onHitRobot(HitRobotEvent e) {
    }

    public void Aim (double x, double y) {
        double Dx = x - this.getX();
        double Dy = y - this.getY();
        double bulletHeadingDegree = Math.toDegrees(Math.atan2(Dx, Dy));
        double turnAngle = Utils.normalRelativeAngleDegrees(bulletHeadingDegree - this.getGunHeading());
        setTurnGunRight(turnAngle);
    }

    public void onHitByBullet(HitByBulletEvent event){
        this.setBodyColor(Color.red);
        this.setGunColor(Color.red);
        this.setRadarColor(Color.red);
        this.setScanColor(Color.red);
    }
}
