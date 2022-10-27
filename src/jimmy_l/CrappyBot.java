package jimmy_l;

import jimmy_b.EnemyBot;
import robocode.*;
import java.awt.*;
import java.util.Random;


public class CrappyBot extends AdvancedRobot{

    private EnemyBot currentTarget = new EnemyBot();
    boolean movingForward;
    public static double _oppEnergy = 100.0;
    boolean haveAlreadyGotAnEnergyBuddy = false;
    int red;
    int green;
    int blue;
    int redCounter = 1;
    int greenCounter = 90;
    int blueCounter = 180;
    boolean[] runThread = {true};
    int turnDirection = 1;
    int moveDirection = 1;
    final boolean[] token = {true};
    int threadCount = 0;
    boolean gameIsOver = false;


    public void run() {
        boolean start = true;




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
//            Thread paintThread = new Thread(new Robotable() {
//                public void run() {
//                                //Thread.sleep(100);
//                    try {
//                        setColor();
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//
//                }
//            });
        //paintThread.start();


            //execute();

//            if (getOthers() == 1) {
//                try {
//                    paintThread.join();
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//            if (getTime() % 2 == 0) {
//                    setColor();
//                }

//            addCustomEvent(new Condition("crazyColors") {
//                public boolean test() {
//                    return (true);
//                }
//            });
            setTurnRadarRight(100000);
            //avoidWalls();
            //moveDirection = avoidWalls();

            ahead(moveDirection * 2000);

            movingForward = true;
            // Tell the game we will want to turn right 90
            setTurnRight(180);
            execute();

        }
    }

    public void ahead(int direction) {
        setAhead(2000 * direction);
        waitFor(new TurnCompleteCondition(this));
    }
    public void onHitWall(HitWallEvent e) {
        setColor();
        reverseDirection();
    }

    public void reverseDirection() {
        if (movingForward) {
            setTurnRight(30);
            setBack(40000);
            movingForward = false;
        } else {
            setTurnRight(30);
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
        synchronized(runThread) {
            setColor();

        }
        trackEnemy(e);
        //setColor();


        if (currentTarget.getEnergy() == 0) {
            if (e.getBearing() >= 0) {
                turnDirection = 1;
            } else {
                turnDirection = -1;
            }
            setTurnRight(e.getBearing());
            setAhead(e.getDistance() + 5);
            execute();

        }
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

    public void onHitRobot(HitRobotEvent e) {

        if (e.isMyFault()) {
            reverseDirection();
        }
    }

//    public void setColor() {
//        // Set colors
//        setBodyColor(getRndColor());
//        setGunColor(getRndColor());
//        setRadarColor(getRndColor());
//        setBulletColor(getRndColor());
//        setScanColor(getRndColor());
//    }

    // throws InterruptedException
    public void setColor() {

            for (int i = 0; i < 10000; i++) {

                setBodyColor(getRndColor());
                setGunColor(getRndColor());
                setRadarColor(getRndColor());
                setBulletColor(getRndColor());
                setScanColor(getRndColor());

            }

        }
    public Color getRndColor() {													//returnerar randomiserade värden 0-255
        Random random = new Random();
        int red = random.nextInt(255);
        int green = random.nextInt(255);
        int blue = random.nextInt(255);

        return new Color(red, green, blue);
    }

    public Color getCrazyColor() {
        //int red;
        //int green;
        //int blue;

        red = colorChecker(redCounter, 255, 1);
        redCounter = red;
        System.out.println("red: " + red);
//        if (redCounter < 255) {
//            red = redCounter;
//            redCounter++;
//        } else {
//            redCounter = 0;
//            red = redCounter;
//        }
        green = colorChecker(greenCounter, 254, 2);
        greenCounter = green;
        System.out.println("green: " + green);

//        if (greenCounter < 254) {
//            green = greenCounter;
//            greenCounter = greenCounter +2;
//        } else {
//            greenCounter = 0;
//            green = greenCounter;
//        }
        blue = colorChecker(blueCounter, 253, 3);
        blueCounter = blue;
        System.out.println("blue: " + blue);
//        if (blueCounter < 253) {
//            blue = blueCounter;
//            blueCounter = blueCounter + 3;
//        } else {
//            blueCounter = 0;
//            blue = blueCounter;
//        }

        return new Color(red, green, blue);
    }

//    public Color getCrazyColor() {
//        int red = colorChecker(redCounter, 255, 1);
//        int  green = colorChecker(greenCounter, 255, 1);
//        int blue = colorChecker(blueCounter, 255, 1);
//        return new Color(red, green, blue);
//    }

        public int colorChecker (int colorCounter, int checkThis, int addAmount) {
            int colorOk;
            if (colorCounter < checkThis) {
                colorOk = colorCounter + addAmount;
            } else {
                colorOk = 0;
            }
            return colorOk;
        }



    public void onWin(WinEvent e) {
        gameIsOver = true;

        while (true) {
            turnRight(25);
            turnRight(-25);
        }

    }
    public void onDeath(DeathEvent e) {
        gameIsOver = true;
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

    @Override
    public void onBattleEnded(BattleEndedEvent e) {
        gameIsOver = true;

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
//    public Color getRndColor() {													//returnerar randomiserade värden 0-255
//        Random random = new Random();
//        int red = random.nextInt(255);
//        int green = random.nextInt(255);
//        int blue = random.nextInt(255);
//
//        return new Color(red, green, blue);
//    }


}


