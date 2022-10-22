package jimmy_l;

import jimmy_b.EnemyBot;
import robocode.*;
import java.awt.*;



import static java.lang.Math.PI;

public class CrappyBot extends AdvancedRobot{
    private EnemyBot currentTarget = new EnemyBot();

    boolean movingForward;
    public static double _oppEnergy = 100.0;
    public double takeAim;
    public double cornerEscapeMultiplier;
    //EnergyBuddies energyBuddies;
    AdvancedRobot rbt = new AdvancedRobot();

    public void run() {

        setColor();

        addCustomEvent(new Condition("energyBuddies") {
            public boolean test() {
                return (currentTarget.getEnergy() == getEnergy());
            }
        });

        while (true) {
            setAdjustGunForRobotTurn(true);
            setAdjustRadarForGunTurn(true);
            setAdjustRadarForRobotTurn(true);

            setTurnRadarRight(100000);
            // Tell the game we will want to move ahead 40000 -- some large number
            setAhead(2000);

            movingForward = true;
            // Tell the game we will want to turn right 90
            setTurnRight(180);
            // At this point, we have indicated to the game that *when we do something*,
            // we will want to move ahead and turn right.  That's what "set" means.
            // It is important to realize we have not done anything yet!
            // In order to actually move, we'll want to call a method that
            // takes real time, such as waitFor.
            // waitFor actually starts the action -- we start moving and turning.
            // It will not return until we have finished turning.
            waitFor(new TurnCompleteCondition(this));
            // Note:  We are still moving ahead now, but the turn is complete.
            // Now we'll turn the other way...
            ///////////////////////////////setTurnLeft(180);
            // ... and wait for the turn to finish ...
            ///////////////////////////////waitFor(new TurnCompleteCondition(this));
            // ... then the other way ...
            ////////////////////////////setTurnRight(180);
            // .. and wait for that turn to finish.
            ////////////////////////////waitFor(new TurnCompleteCondition(this));
            // then back to the top to do it all again

        }
    }

    /**
     * onHitWall:  Handle collision with wall.
     */
    public void onHitWall(HitWallEvent e) {
        reverseDirection();
    }

    public void reverseDirection() {
        if (movingForward) {
            setBack(40000);
            movingForward = false;
        } else {
            setAhead(40000);
            movingForward = true;
        }
    }


    public void onScannedRobot(ScannedRobotEvent e) {

//        if (getEnergy() < 100 && e.getEnergy() == getEnergy()) {
//
//            addCustomEvent(new EnergyBuddies(rbt, e.getName(), getEnergy(), e.getEnergy()));
//        }
//        energyBuddies(e.getEnergy(), e.getName());

        setTurnRadarRight(100000);
        turnGunRightRadians(aimAtBearing(e.getBearingRadians()));
        smartFire(e.getDistance());

        dodgeTheBullets1v1(e.getBearingRadians());

    }


    public void onCustomEvent(CustomEvent e) {

        if (e.getCondition().getName().equals("energyBuddies")) {
            energyBuddies(getEnergy(), currentTarget.getName());        }
    }



    public void dodgeTheBullets1v1 (double enemyBearing) {


        while (getOthers() == 1) {
//            if ((getBattleFieldWidth() - getX()) < ((getBattleFieldWidth() / 8)) && (getBattleFieldHeight() - getY()) < ((getBattleFieldHeight() / 8))) {                    //om vi är i övre högra hörnet
//                cornerEscapeMultiplier = 1.25;                  //multiplicera med 1.25 för att åka in mot mitten
//                getOutOFTheCorner(cornerEscapeMultiplier);
//            } else if ((getBattleFieldWidth() - getX()) > ((getBattleFieldWidth() * 0.8)) && (getBattleFieldHeight() - getY()) < ((getBattleFieldHeight() / 8))) {          // om vi är i övre vänstra hörnet
//                cornerEscapeMultiplier = 0.75;                  //multiplicera med 0.75 för att åka in mot mitten
//                getOutOFTheCorner(cornerEscapeMultiplier);
//            } else if ((getBattleFieldWidth() - getX()) > ((getBattleFieldWidth() * 0.8)) && (getBattleFieldHeight() - getY()) < ((getBattleFieldHeight() * 0.8))) {        // om vi är i nedre vänstra hörnet
//                cornerEscapeMultiplier = 0.25;                  //multiplicera med 0.25 för att åka in mot mitten
//                getOutOFTheCorner(cornerEscapeMultiplier);
//            } else if ((getBattleFieldWidth() - getX()) > ((getBattleFieldWidth() / 8)) && (getBattleFieldHeight() - getY()) < ((getBattleFieldHeight() * 0.8))) {          //om vi är i nedre högra hörnet
//                cornerEscapeMultiplier = 1.75;                  //multiplicera med 1.75 för att åka in mot mitten
//                getOutOFTheCorner(cornerEscapeMultiplier);
//            }

            setTurnRightRadians(enemyBearing - (0.5 * PI));
            setAhead(2000);
            waitFor(new TurnCompleteCondition(this));

        }
    }

    public void getOutOFTheCorner (double multiplier) {
        setTurnRightRadians((multiplier *PI - getHeadingRadians()));

        setAhead(550);
        waitFor(new TurnCompleteCondition(this));

    }


    public void onHitByBullet (HitByBulletEvent event) {


    }

    public void onHitRobot(HitRobotEvent e) {

        if (e.isMyFault()) {
            reverseDirection();
        }

        if ((e.isMyFault()) && (Math.abs(e.getBearing())) <= 90) {				//om det var vi som orsakade krocken, och andra roboten är framför oss
            setBack(100);
        } else if ((e.isMyFault()) && (Math.abs(e.getBearing()) >= 90)) {		//om det var vi som orsakade krocken, och andra roboten är bakom oss
            setAhead(100);
        } else if (e.getBearing() > -90 && e.getBearing() <= 90) {                      //om det inte var vi som orsakade krocken, , kolla om fienden är framför eller bakom oss, sedan fly.
            setBack(100);
        } else {
            setAhead(100);
        }
        setTurnGunRight(aimAtBearing(e.getBearing()));										//sikta på andra roboten
        fire(3);
    }

    public void smartFire(double robotDistance) {
        if (robotDistance > 250 || getEnergy() < 15) {
            fire(1);
        } else if (robotDistance > 100) {
            fire(2);
        } else {
            fire(3);
        }
    }
    public double aimAtBearing (double enemyBearing) {

        takeAim = enemyBearing - getGunHeadingRadians();
        if (takeAim > PI) {
            takeAim = (PI *2) - enemyBearing + getGunHeadingRadians();
        }
        return takeAim;
    }

    public void setColor() {
        // Set colors
        setBodyColor(new Color(0, 0, 100));
        setGunColor(new Color(0, 0, 0));
        setRadarColor(new Color(0, 0, 0));
        setBulletColor(new Color(255, 0, 0));
        setScanColor(new Color(255, 255, 255));
    }

    public void onWin(WinEvent e) {													//vid vinst: skjut som en galning
        while (true) {
            turnRight(25);
            turnRight(-25);
        }

    }
    public void onDeath(DeathEvent e) {
        System.out.println("                               -|-");
        System.out.println("                                |");
        System.out.println("                            .-'~~~`-.");
        System.out.println("                          .'         `.");
        System.out.println("                          |  R  I  P  |");
        System.out.println("                          | CrappyBot |");
        System.out.println("                          |           |");
        System.out.println("                        \\\\|           |//");
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
    }
    public void energyBuddies (double energy, String name) {
        if (energy == getEnergy() && getEnergy() != 100) {
            System.out.println("We both have " + getEnergy() + " energy, " + name + ", we are energy buddies!");
            System.out.println("   ***     ***                   ***     ***                   ***     ***");
            System.out.println(" **   ** **   **               **   ** **   **               **   ** **   **");
            System.out.println("*       *       *             *       *       *             *       *       *");
            System.out.println("*               *             *               *             *               *");
            System.out.println(" *     LOVE    *               *     LOVE    *               *     LOVE    *");
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


