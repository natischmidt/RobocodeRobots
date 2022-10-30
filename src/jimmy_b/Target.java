package jimmy_b;

import robocode.ScannedRobotEvent;

public class Target {

    private String name;
    private double bearing;
    private double bearingRadians;
    private double distance;
    private double energy;
    private double heading;
    private double headingRadians;
    private double velocity;

    public Target() {
        reset();
    }

    public void reset(){
        this.name = "";
        this.bearing = 0.0;
        this.bearingRadians = 0.0;
        this.distance = 0.0;
        this.energy = 0.0;
        this.heading = 0.0;
        this.headingRadians = 0.0;
        this.velocity = 0.0;
    }

    public void update(ScannedRobotEvent scannedRobot){
        this.name = scannedRobot.getName();
        this.bearing = scannedRobot.getBearing();
        this.bearingRadians = scannedRobot.getBearingRadians();
        this.distance = scannedRobot.getDistance();
        this.energy = scannedRobot.getEnergy();
        this.heading = scannedRobot.getHeading();
        this.headingRadians = scannedRobot.getHeadingRadians();
        this.velocity = scannedRobot.getVelocity();
    }

    public String getName() {
        return name;
    }

    public double getBearing() {
        return bearing;
    }

    public double getBearingRadians() {
        return bearingRadians;
    }

    public double getDistance() {
        return distance;
    }

    public double getEnergy() {
        return energy;
    }

    public double getHeading() {
        return heading;
    }

    public double getHeadingRadians() {
        return headingRadians;
    }

    public double getVelocity() {
        return velocity;
    }

    public boolean none(){
        return name.equals("");
    }
}
