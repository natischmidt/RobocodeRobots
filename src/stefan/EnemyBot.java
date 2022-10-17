package stefan;

import robocode.ScannedRobotEvent;

public class EnemyBot {

    private String name;
    private double bearing;
    private double distance;
    private double energy;
    private double heading;
    private double velocity;

    public EnemyBot() {
        reset();
    }

    public void reset(){
        this.name = "";
        this.bearing = 0.0;
        this.distance = 0.0;
        this.energy = 0.0;
        this.heading = 0.0;
        this.velocity = 0.0;
    }

    public void update(ScannedRobotEvent e){
        this.name = e.getName();
        this.bearing = e.getBearing();
        this.distance = e.getDistance();
        this.energy = e.getEnergy();
        this.heading = e.getHeading();
        this.velocity = e.getVelocity();
    }

    public String getName() {
        return name;
    }

    public double getBearing() {
        return bearing;
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

    public double getVelocity() {
        return velocity;
    }

    public boolean none(){
        return name.equals("");
    }
}
