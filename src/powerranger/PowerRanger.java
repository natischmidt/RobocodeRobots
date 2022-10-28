package powerranger;

import net.sf.robocode.battle.Battle;
import robocode.AdvancedRobot;
import robocode.BulletHitEvent;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;
import robocode.*;
import java.awt.*;
import java.util.Random;


public class PowerRanger extends AdvancedRobot {
    EnemyBot currentTarget = new EnemyBot();

    // För statistiken i slutet av rundan
    double totalBulletShot = 0;
    double totalBulletHit = 0;
    double totalBulletMiss = 0;

    // Data för att ändra strategi vid 1 vs 1
    boolean strafing = false;
    double bulletMiss;
    double bulletHit;
    byte moveDirectionWhileStrafing = 1;

    double bulletPower = 1;

    // Data för att bli mindre förutsägbar när vi rör oss längst med väggarna
    double rand = 8;
    int timeToStop = 65;
    int distansToWall = 28;

    boolean haveAlreadyGotAnEnergyBuddy = false;


    public void run() {

        addCustomEvent(new Condition("energyBuddies") {                                 //lägger till ett mycket viktigt och nödvändigt custom event.
            public boolean test() {
                return (currentTarget.getEnergy() == getEnergy());                             //returnerar true om vi har lika mycket energi som vårt target
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

            //Ifall endast en fiende är kvar och vi har mindre än 20 energi, eller...
            if (getOthers() == 1 && (getEnergy() < 20 ||
                    //vi har missad mer än 10 skott och missat mer än vad vi har träffat
                    bulletMiss > bulletHit && bulletMiss > 10)){
                strafing = true;
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
            ahead((Math.max(getBattleFieldHeight() - getY(), getY()) - distansToWall) * 1);              //variabeln dir är alltid 1, men vi sparar den för nu, ifall vi vill använda den för att byta riktning.
            //framåt (slagfältets höjd - vår y position) eller (vår y position - distansToWall)
        } else {
            ahead((Math.max(getBattleFieldWidth() - getX(), getX()) - distansToWall) * -1);
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
        if(!strafing)
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
        setAhead(100 * moveDirectionWhileStrafing);
        if (getTime() % 30 == 0)
            moveDirectionWhileStrafing *= -1;
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
            totalBulletShot++;
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
    public void onWin(WinEvent e) {
        PrintOut.printOnWin();
        while (true) {
            turnRight(25);
            turnRight(-25);
        }

    }

    public void onBulletHit(BulletHitEvent e) {
        totalBulletHit++;
        if(getOthers() == 1)
            bulletHit++;
    }

    public void onBulletMissed(BulletMissedEvent event) {
        totalBulletMiss++;
        if(getOthers() == 1)
            bulletMiss++;
    }

    public void onCustomEvent(CustomEvent e) {

        if (e.getCondition().getName().equals("energyBuddies")) {

            while (!haveAlreadyGotAnEnergyBuddy) {
                energyBuddies(getEnergy(), currentTarget.getName());
                haveAlreadyGotAnEnergyBuddy = true;
            }

        }
    }
    public void onBattleEnded(BattleEndedEvent event) {
        out.print("________________GAME OVER ________________________");
        out.println("\nShots:" + totalBulletShot);
        out.println("Hits:" + totalBulletHit);
        out.println("Misses:" + totalBulletMiss);
        out.println("Accuracy:" + (totalBulletHit / totalBulletShot));
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
        Random random = new Random();
        int red = random.nextInt(255);
        int green = random.nextInt(255);
        int blue = random.nextInt(255);

        return new Color(red, green, blue);
    }

    public void energyBuddies (double energy, String name) {

        if (energy == getEnergy() && getEnergy() != 100 && getEnergy() != 0.0) {            //om scannad robot har lika mycket energi som vi och vår energi inte är 0 eller 100...
            //...så känner vi en djup samhörighet med den.
            System.out.println("We both have " + (int)getEnergy() + " energy, " + currentTarget.getName() + ", we are energy buddies!");

            PrintOut.printOnMadeABuddyForLife();


        }
    }
    public void onDeath(DeathEvent e) {
        PrintOut.printOnDeath();
    }

}