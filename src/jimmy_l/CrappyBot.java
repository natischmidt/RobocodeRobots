package jimmy_l;

import jimmy_b.EnemyBot;
import robocode.*;
import java.awt.*;
import java.util.Random;



import static java.lang.Math.PI;

public class CrappyBot extends AdvancedRobot{
    private EnemyBot currentTarget = new EnemyBot();

    boolean movingForward;
    public static double _oppEnergy = 100.0;
    public double takeAim;
    public double cornerEscapeMultiplier;
    boolean haveAlreadyGotAnEnergyBuddy = false;
    int redCounter = 1;
    int greenCounter = 90;
    int blueCounter = 180;

    AdvancedRobot rbt = new AdvancedRobot();

    public void run() {


        Thread paintThread = new Thread(new Robotable() {
            public void run() {
                try {
                    setColor();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        paintThread.start();


        addCustomEvent(new Condition("energyBuddies") {
            public boolean test() {
                return (currentTarget.getEnergy() == getEnergy());
            }
        });

//        addCustomEvent(new Condition("crazyColors") {
//            public boolean test() {
//                return (true);
//            }
//        });


        while (true) {
            setAdjustGunForRobotTurn(true);
            setAdjustRadarForGunTurn(true);
            setAdjustRadarForRobotTurn(true);

//            if (getTime() % 2 == 0) {
//                    setColor();
//                }

//            addCustomEvent(new Condition("crazyColors") {
//                public boolean test() {
//                    return (true);
//                }
//            });

            setTurnRadarRight(100000);
            // Tell the game we will want to move ahead 40000 -- some large number
            setAhead(2000);

            movingForward = true;
            // Tell the game we will want to turn right 90
            setTurnRight(180);

            waitFor(new TurnCompleteCondition(this));



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
    public void onScannedRobot(ScannedRobotEvent e) {
        trackEnemy(e);

        setTurnRadarRight(100000);
        turnGunRightRadians(aimAtBearing(e.getBearingRadians()));
        smartFire(e.getDistance());

        dodgeTheBullets1v1(e.getBearingRadians());

    }


    public void onCustomEvent(CustomEvent e) {

        if (e.getCondition().getName().equals("energyBuddies")) {

            while (!haveAlreadyGotAnEnergyBuddy) {
                energyBuddies(getEnergy(), currentTarget.getName());
                haveAlreadyGotAnEnergyBuddy = true;
        }

        }

//        if (e.getCondition().getName().equals("crazyColors")) {
//
//            setColor();
//
//            }

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

//    public void setColor() {
//        // Set colors
//        setBodyColor(getRndColor());
//        setGunColor(getRndColor());
//        setRadarColor(getRndColor());
//        setBulletColor(getRndColor());
//        setScanColor(getRndColor());
//    }

    public void setColor() throws InterruptedException {
            while(true) {
                //Thread.sleep(200);
                System.out.println("hej");
                setBodyColor(getCrazyColor());
                setGunColor(getCrazyColor());
                setRadarColor(getCrazyColor());
                setBulletColor(getCrazyColor());
                setScanColor(getCrazyColor());
            }
        }

    public Color getCrazyColor() {
        int red;
        int green;
        int blue;

        if (redCounter < 255) {
            red = redCounter;
            redCounter++;
        } else {
            redCounter = 0;
            red = redCounter;
        }
        System.out.println("red: " + red);

        if (greenCounter < 254) {
            green = greenCounter;
            greenCounter = greenCounter +2;
        } else {
            greenCounter = 0;
            green = greenCounter;
        }
        System.out.println("green: " + green);

        if (blueCounter < 253) {
            blue = blueCounter;
            blueCounter = blueCounter + 3;
        } else {
            blueCounter = 0;
            blue = blueCounter;
        }
        System.out.println("blue: " + blue);
        return new Color(red, green, blue);
    }

    public Color getRndColor() {													//returnerar randomiserade värden 0-255
        Random random = new Random();
        int red = random.nextInt(255);
        int green = random.nextInt(255);
        int blue = random.nextInt(255);

        return new Color(red, green, blue);
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
        System.out.println("      ***                 |  R  I  P  |");
        System.out.println("                          | CrappyBot |");
        System.out.println("                          |           |");
        System.out.println("                        \\\\|           |//");
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
    }
    public void energyBuddies (double energy, String name) {

        if (energy == getEnergy() && getEnergy() != 100 && getEnergy() != 0.0) {
            System.out.println("We both have " + (int)getEnergy() + " energy, " + currentTarget.getName() + ", we are energy buddies!");
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


