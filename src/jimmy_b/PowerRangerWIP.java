package jimmy_b;

import robocode.*;
import robocode.util.Utils;

import java.awt.*;

public class PowerRangerWIP extends AdvancedRobot {
    private Target currentTarget = new Target();
    private byte moveDirection = 1;
    private final double wallMargin = 50;
    private double bulletPower;
    private int totalNumberOfEnemies;
    private static int success;
    private static int fail;
    private byte bulletHit;
    private byte bulletMiss;
    private boolean reverseDirection = false;
    private boolean turning = false;

//    Condition timeToTurn = new Condition("time_to_turn") {
//        public boolean test() {
//            return (getX() < wallMargin || getX() > getBattleFieldWidth() - wallMargin ||
//                    getY() < wallMargin || getY() > getBattleFieldHeight() - wallMargin);
//        }
//    };

    public void run() {
        getStatistics();

        setAdjustRadarForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAllColors(Color.green);

        currentTarget.reset();

        // Vänder roboten rakt mot väggen närmast till vänster
        turnLeft(getHeading() % 90);

        while (true) {
            scanForNewTarget();
            if (getOthers() == 1)
                changeStrategy();
            if (!reverseDirection) {
                moveAlongTheWalls();
            }
//            addCustomEvent(timeToTurn);
            execute();
        }
    }

    private void getStatistics() {
        totalNumberOfEnemies = getOthers();

        // Om vår nuvarande strategi misslyckats mer än vad den har lyckats, och...
        if (fail > success &&
                // den har misslyckats i minst tre rundor
                fail > 3) {
            // Ändra riktning
            moveDirection *= -1;
            out.println("Changing direction...");
            // Nollställ statistiken
            success = 0;
            fail = 0;
        }
    }

//    public void onCustomEvent(CustomEvent e) {
//        if (!turning && e.getCondition().getName().equals("time_to_turn")) {
//            turning = true;
//            turnRight(90 * moveDirection);
//            removeCustomEvent(e.getCondition());
//        }
//        else
//            removeCustomEvent(e.getCondition());
//    }

    private void changeStrategy() {
        // Om vi missar fler kulor än vad vi träffar, och missar minst 10 kulor...
        if (!reverseDirection && bulletMiss > bulletHit && bulletMiss > 10) {
            reverseDirection = true;
            // ändra riktning
            moveDirection *= -1;
            // nollställ statistiken
            bulletMiss = 0;
            bulletHit = 0;
        }
        // Ramma motståndaren om vi fortfarande misslyckas träffa vår motståndare
        if (bulletMiss > bulletHit && bulletMiss > 10)
            ramEnemy();
    }

    private void scanForNewTarget() {
        // Om vi inte har någon måltavla
        if (currentTarget.none())
            // skanna efter en ny robot
            setTurnRadarRight(360);
    }

    public void onBulletHit(BulletHitEvent e) {
        if (getOthers() == 1)
            bulletHit++;
    }

    public void onBulletMissed(BulletMissedEvent e) {
        if (getOthers() == 1)
            bulletMiss++;
    }

    private void ramEnemy() {
        setTurnRight(currentTarget.getBearing());
        setAhead(currentTarget.getDistance() + 5);
        setTurnRadarRight(360);
        execute();
    }

    private void moveAlongTheWalls() {
        // Räknar ut hur roboten ska åka längst med väggarna
//        if(getX() > wallMargin || getX() < getBattleFieldWidth() - wallMargin ||
//                getY() > wallMargin || getY() < getBattleFieldHeight() - wallMargin)
//            turning = false;

//        (getX() < wallMargin && getY() < wallMargin) ||
//                (getX() < wallMargin && getY() > getBattleFieldHeight() - wallMargin) ||
//                (getX() > getBattleFieldWidth() - wallMargin && getY() > getBattleFieldHeight() - wallMargin) ||
//                (getX() > getBattleFieldWidth() - wallMargin && getY() < wallMargin)

        if(!turning && ((getX() < wallMargin || getX() > getBattleFieldWidth() - wallMargin) &&
                (getY() < wallMargin || getY() > getBattleFieldHeight() - wallMargin))){
            turning = true;
            setMaxVelocity(0);
            turnRight(90);
        }
        else
            turning = false;
        setMaxVelocity(Rules.MAX_VELOCITY);
        if (Utils.isNear(getHeading(), 0D) || Utils.isNear(getHeading(), 180D))
            setAhead((Math.max(getBattleFieldHeight() - getY(), getY()) - 28) * moveDirection);
        else
            setAhead((Math.max(getBattleFieldWidth() - getX(), getX()) - 28) * moveDirection);


//        turnRight(90 * moveDirection);

//        if(Utils.isNear(getHeading(), 0D) || Utils.isNear(getHeading(), 180D))
//            ahead((Math.max(getBattleFieldHeight() - getY(), getY()) - wallMargin) * moveDirection);
//        else
//            ahead((Math.max(getBattleFieldWidth() - getX(), getX()) - wallMargin) * moveDirection);
//        turnRight(90 * moveDirection);
//
//        if(Utils.isNear(getX(), wallMargin) || Utils.isNear(getX(), getBattleFieldWidth() - wallMargin) ||
//                Utils.isNear(getY(), wallMargin) || Utils.isNear(getY(), getBattleFieldHeight() - wallMargin))
//            turnRight(90 * moveDirection);
    }

    public void onScannedRobot(ScannedRobotEvent scannedRobot) {
        trackEnemy(scannedRobot);

        // Data för att kunna sikta på fienden
        double absBearing = scannedRobot.getBearingRadians() + getHeadingRadians();
        double latVel = scannedRobot.getVelocity() * Math.sin(scannedRobot.getHeadingRadians() - absBearing);
        double radarTurn = absBearing - getRadarHeadingRadians();

        // Beräkna firepower
        smartFire();

        // Sikta på busarna
        setTurnGunRightRadians(Utils.normalRelativeAngle(absBearing - getGunHeadingRadians() +
                Math.asin(latVel / (20 - 3 * bulletPower))));

        // Håll kvar radarn
        setTurnRadarRightRadians(Utils.normalRelativeAngle(radarTurn) * 2);
    }

    private void smartFire() {
        if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 20) {
            // Om vår target står still skjut med full kraft
            if (currentTarget.getVelocity() == 0)
                bulletPower = 3;
            else
                // Juste firepower till avståndet till vår target, eller...
                bulletPower = (Math.min(Math.min(500 / currentTarget.getDistance(), 3),
                        // skjut med minsta möjliga kraft för att döda
                        (currentTarget.getEnergy() / 4)));
            setFire(bulletPower);
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

    public void onRobotDeath(RobotDeathEvent e) {
        // if the enemy we were tracking died...
        if (e.getName().equals(currentTarget.getName())) {
            // clear tracking-info, so we can track another robot
            currentTarget.reset();
        }
    }

    public void onDeath(DeathEvent e) {
        if ((double) getOthers() < totalNumberOfEnemies * 0.33) {
            success++;
            out.println("Another successful round!");
        } else {
            fail++;
            out.println("Oh no! We've failed!");
        }
    }

    public void onWin(WinEvent e) {
        success++;
        out.println("Another successful round!");
    }

    @Override
    public void onBattleEnded(BattleEndedEvent e) {
        out.println("Successful rounds: " + success);
        out.println("Failed rounds: " + fail);
    }
}

