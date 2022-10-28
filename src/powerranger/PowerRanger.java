package powerranger;

import robocode.AdvancedRobot;
import robocode.BulletHitEvent;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;
import robocode.*;
import java.awt.*;
import java.util.Random;

public class PowerRanger extends AdvancedRobot {
    EnemyBot currentTarget = new EnemyBot();

    // För statistiken i slutet av rundan/battle
    double currentRoundBulletShot;
    double currentRoundBulletHit;
    double currentRoundBulletMiss;
    static double totalBulletShot;
    static double totalBulletHit;
    static double totalBulletMiss;

    // Data för att ändra strategi vid 1 vs 1
    boolean isStrafing = false;
    double bulletMiss;
    double bulletHit;
    byte moveDirection = 1;

    double bulletPower = 1;

    // Data för att bli mindre förutsägbar när vi rör oss längst med väggarna
    double rand = 8;
    int timeToStop = 65;
    int distansToWall = 28;

    boolean haveAlreadyGotAnEnergyBuddy = false;
    Random random = new Random();

    public void run() {

        addCustomEvent(new Condition("energyBuddies") {                                 //lägger till ett mycket viktigt och nödvändigt custom event.
            public boolean test() {
                return ((int)currentTarget.getEnergy() == (int)getEnergy());                             //returnerar true om vi har lika mycket energi som vårt target
            }
        });

        setAdjustRadarForGunTurn(true);
        setAdjustGunForRobotTurn(true);

        // Vänder roboten mot väggen
        turnLeft(getHeading() % 90);

        while (true) {
            // Skanna igen om det behövs
            if(currentTarget.none())
                setTurnRadarRight(Double.POSITIVE_INFINITY);

            // Ifall endast en fiende är kvar och vi har mindre än 20 i energi, eller...
            if (getOthers() == 1 && (getEnergy() < 20 ||
                    //vvi har missad mer än 10 skott och missat mer än vad vi har träffat
                    bulletMiss > bulletHit && bulletMiss > 10)){
                isStrafing = true;
                strafeEnemy();
            }
            else {
                WallMovement();
                DodgeMovment();
            }
            execute();
        }
    }

    public void WallMovement(){
        //Räknar ut hur jag ska åka längst väggarna
        if (Utils.isNear(getHeadingRadians(), 0D) || Utils.isNear(getHeadingRadians(), Math.PI)) {      //Utils.isNear returnerar true om differensen mellan de två argumenten är mindre än 1.0E-5, dvs 0.000010. I praktiken samma som == .Här betyder det true om vi är på väg (nästan) rakt norrut, eller (nästan) rakt söderut
            ahead((Math.max(getBattleFieldHeight() - getY(), getY()) - distansToWall));
            //framåt (slagfältets höjd - vår y position) eller (vår y position - distansToWall)
        } else {
            ahead((Math.max(getBattleFieldWidth() - getX(), getX()) - distansToWall));
        }
        turnRight(90);
    }
    public void DodgeMovment(){
        //Ändrar hur den åker
        timeToStop--;
        if (timeToStop < 1) {
            rand = Math.random();
            if (rand > 0.5) {
                setMaxVelocity(12);
                timeToStop = 65;
            }
            if (rand < 0.5) {
                setMaxVelocity(0);
                timeToStop = 5;
            }
        }
    }

    public void onScannedRobot(ScannedRobotEvent scannedRobot) {
        trackEnemy(scannedRobot);

        setColor();                             //bli schnygg

        //data för att kunna sikta på fienden
        double absBearing = scannedRobot.getBearingRadians() + getHeadingRadians();
        double latVel = scannedRobot.getVelocity() * Math.sin(scannedRobot.getHeadingRadians() - absBearing);
        double radarTurn = absBearing - getRadarHeadingRadians();

        //Vill kalla den här ifrån också
        if(!isStrafing)
            DodgeMovment();

        // Beräkna firepower
        smartFire();

        // Sikta på busarna
        setTurnGunRightRadians(Utils.normalRelativeAngle(absBearing - getGunHeadingRadians() +
                Math.asin(latVel / (20 - 3 * bulletPower))));

        // Håll kvar radarn
        setTurnRadarRightRadians(Utils.normalRelativeAngle(radarTurn) * 2);
    }
    private void strafeEnemy() {
        if (currentTarget.getDistance() < 200) {
            // Backa lite...
            setTurnRight(currentTarget.getBearing());
            setBack(200 - currentTarget.getDistance());
        } else if (currentTarget.getDistance() > 500) {
            // Kom närmre...
            setTurnRight(currentTarget.getBearing());
            setAhead(currentTarget.getDistance() - 200);
        }
        // Strafe...
        setTurnRight(currentTarget.getBearing() + 90);
        setAhead(100 * moveDirection);
        if (getTime() % 30 == 0)
            moveDirection *= -1;
    }
    private void smartFire() {
        if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 20) {
            // Om vår target står still skjut med full kraft
            if (currentTarget.getVelocity() == 0)
                bulletPower = 3;
            else
                // Justera firepower efter avståndet till vår target, eller...
                bulletPower = (Math.min(Math.min(500 / currentTarget.getDistance(), 3),
                        // skjut med minsta möjliga kraft för att döda
                        (currentTarget.getEnergy() / 4)));
            setFire(bulletPower);
            totalBulletShot++;
            currentRoundBulletShot++;
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
        // om vår target har dött...
        if (e.getName().equals(currentTarget.getName())) {
            // rensa datan, så att vi kan få en ny måltavla!
            currentTarget.reset();
        }
    }
    public void onWin(WinEvent e) {
        PrintOut.printOnWin();
    }

    public void onBulletHit(BulletHitEvent e) {
        totalBulletHit++;
        currentRoundBulletHit++;
        if(getOthers() == 1)
            bulletHit++;
    }

    public void onBulletMissed(BulletMissedEvent event) {
        totalBulletMiss++;
        currentRoundBulletMiss++;
        if(getOthers() == 1)
            bulletMiss++;
    }

    @Override
    public void onHitByBullet(HitByBulletEvent event) {
        super.onHitByBullet(event);
        haveAlreadyGotAnEnergyBuddy = false;

    }

    public void onCustomEvent(CustomEvent e) {

        if (e.getCondition().getName().equals("energyBuddies")) {

            while (!haveAlreadyGotAnEnergyBuddy) {

                energyBuddies(getEnergy());
                haveAlreadyGotAnEnergyBuddy = true;
            }

        }
    }
    @Override
    public void onRoundEnded(RoundEndedEvent event) {
        out.println("\n________________Round " + (getRoundNum() + 1) + "________________");
        out.println("\nShots: " + (int) currentRoundBulletShot);
        out.println("Hits: " + (int) currentRoundBulletHit);
        out.println("Misses: " + (int) currentRoundBulletMiss);
        out.println("Accuracy: " + Math.round((currentRoundBulletHit / currentRoundBulletShot) * 100) + " %");
    }

    @Override
    public void onBattleEnded(BattleEndedEvent event) {
        out.println("\n________________GAME OVER________________");
        out.println("\nTotal shots: " + (int) totalBulletShot);
        out.println("Total hits: " + (int) totalBulletHit);
        out.println("Total misses: " + (int) totalBulletMiss);
        out.println("Accuracy: " + Math.round((totalBulletHit / totalBulletShot) * 100) + " %");
    }

    public void setColor() {                                                        //anropar getRndColor för att sätta ny färg
        for (int i = 0; i < 100; i++) {

            setBodyColor(getRndColor());
            setGunColor(getRndColor());
            setRadarColor(getRndColor());
            setBulletColor(getRndColor());
            setScanColor(getRndColor());
        }
    }
    public Color getRndColor() {													//returnerar randomiserade värden 0-255
        int red = random.nextInt(255);
        int green = random.nextInt(255);
        int blue = random.nextInt(255);

        return new Color(red, green, blue);
    }

    public void energyBuddies (double energy) {

        if (energy == getEnergy() && getEnergy() != 100 && getEnergy() != 0.0) {            //om scannad robot har lika mycket energi som vi och vår energi inte är 0 eller 100...
            //...så känner vi en djup samhörighet med den.
            System.out.println("We both have " + (int)getEnergy() + " energy, " + currentTarget.getName() + ", we are energy buddies! \n");
            PrintOut.printOnMadeABuddyForLife();
        }
    }
    public void onDeath(DeathEvent e) {
        PrintOut.printOnDeath();
    }

}