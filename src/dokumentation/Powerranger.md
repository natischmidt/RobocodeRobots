
# Dokumenation Powerranger
### Beskriv, övergripande, robotens struktur/uppbyggnad.

Powerranger är en advanced robots vars rörelsemönster är längs väggarna. Utöver sitt Wallmovement har roboten
ett dogdgemovement vilket möliggör mindre förutsägbara rörelser. För sin träffsäkeret räknar roboten ut fiendens 
riktning samt kurs för att sedan med hjälp av latVel samla all data för att kunna träffa. LatVel är en variabel som 
beskriver fiendens relation till robotens egna riktning. För att sedan skjuta på ett effektiv sätt, har roboten metoden 
SmartFire, vilken tar hänsyn till Gunheat samt antal turns som är kvar i rörelsen av pistolen. På så sätt kan det 
skjutsas med mer kraft ifall fienden står still, och metoden justerar även skjutkraften genom att ta reda på distansen 
till fienden. Ett till sätt att inte slösa på sin egen energi är att roboten inte skjuter med mer skjutkraft än den som 
behövs, detta genom att ta reda på fiendens energi. Roboten har en Enemybot class och en Printout class. Enemybot-klassen
sparar undan information av avscannad och vald måltavla, vilken används i flera metoder. I printout finns det assci art, 
som skriver ut vid vinst, död och ifall någon fiende har exakt samma energi som powerranger. Roboten har samma strategi 
fram tills att det endast finns en fiende kvar och hans egen energi är låg eller fram tills att 10 skott har missats
och att fler skott har missats än träffat. I detta fall använder sig roboten av strategy strafing. Strafing ta roboten till rätt 
distans mot fienden och rör sig i relation till fienden i ett försök att inte bli träffad av skott.

### Beskriv de implementeringar som gjorts i samband med varje vald
metod (till exempel onScannedRobot), beskriv här den
strategi/taktik som ni valt för er robot avseende de metoder ni valt.

1.	Run – Här anropas nödvändiga methoder, roboten vänder sig mot väggen och Radar är en Infinity lock vilket innebär att det scannas efter fienden , och sedan scannas det bara av igen ifall
vi har tappat bort fienden.Genom att sätta setAdjustRadarForGunTurn och setAdjustGunForRobotTurn till true kan gun och radar röra sig utan förhållande till varandra. Detta betyder att roboten kan skjuta och röra sig fritt och inte röra sig med vapnat i samma riktning.
a)	Wallmovement() – Rörelsen längst väggarna berkänkas genom att ta reda på om roboten är nästan rakt söder eller norr ut, sen åker roboten framåt genom att ta battlefiedl höjd minus roboten y position eller x postionition minus 28 vilket är en marginal mot väggen. Slutligen åker roboten genom att vända sig till höger med en 90 grad vinkel antigen x eller y för att veta vilken vägg vi ska åka till
b)	Dodgemovement() -  Methoden har en räknare som börjar på 65. Varje gång metoden kallas dras 1 bort, och när räknaren är nere på 0 kallas Math.random()-metoden som ger ett double-värde mellan 0.0 och 1.0. Om det slumpade värdet är över 0.5 fortsätter roboten i fullfart, och räknaren börjar om igen från 65. Om det slumpade värdet är under 0.5 kommer maxfarten att sättas till 0, vilket gör att roboten saktar in. Räknaren sätts till 5, och metoden kallas på nytt och räknar ner. Sedan accelereras det igen. Denna method ska förhindra att andra robotor kan räkna ut nästa steg för vår robot, allstå vart den är om en visst antal ticks. Det är ett sätt att få vår robot att röra sig mindre förutsägbart.
c)	energyBuddies  public void energyBuddies (double energy, String name) – Detta är ett custom event som triggas ifall en fiende har samma energy som roboten, förutom när det är 0 eller 100, och då skrivs det ut ett fint bild i consolen.
2.	onScannedRobot
a)	trackenemy – Med hjälp av enemybot klassen, sätts en target av fienden vi har scannat. Sedan byts target ifall att en annan fiende befinner sig närmare, eller att en annan fiende har mindre energie än tidiagre target eller ifall vi scannaer en fiende som vi redan har scannat.
b)	strafeEnemy – efter att beräkna rätt distans mot fienden, åker roboten likt en triangel i förhållande till fienden.
c)	smartFire – Kolla att gunheat är 0 och sedan beroende på om fienden står till eller inte tas bulletPower och justeras till avståendet av fienden. Står fienden still skjuts med full kraft (3). Siktat klart, för vi kommer träffa de om vi står still skjuta med lagre kraft och inte skjuta med fullkraft om det inte behovs, 500 pixlar bort bli 1 skjutkraft, etc.
3.	onRobotDeath – Ifall fienden som var vår måltavla dör, letar vi efter ett nytt target.
4.	onBattleenden– Här skrivs ut antalet skott, träffen, missade skott samt precision av alla skott.


### Beskriv hur ni gick till väga när ni som grupp planerade er robot (och dess taktik).

Vid vårt först möte har vi kommit på en strategi samt diskuterade vilka methoder vi skulle vilja ha.
Vårt mål är att robotens rörelsemönster liknar Walls men motsols, att vi ska försöka implementera Sniper-beteende, 
det vill säga att kunna scanna av och sjukta andra robotar på avstånd och att vid 1v1 eller 1v2 kunna använda oss 
av wavesurfing. Utöver det har vi som mål att skapa metoder som kan rikta sig till robotor som har minst HP, och 
på det sättet kan vår robot får enkla kills samt extra poäng för det sista skotten. Vi har också tänkt på att vi
ska ramma ifall andra robotor befinner sig längs väggarna och förstås har koll på ifall dessa är framför eller 
bakom oss och ifall dessa skjuter på oss. Vi har satt som mål att uppfylla alla kraven för ett högre betyg.
Vi har över helgen delat upp oss i två mindre teams, där Jimmy L och Stefan ska försöka förstår samt få 
till wavesurfing och Jimmy B och jag ska gör detsamma med Sniper-beteendet. Sedan ska vi ha ett till möte 
och ser hur långt alla har kommit, samt om vi eventuellt måste byta något ifall vi har varit för ambitiösa. 

### Beskriv de eventuella problem som du/ni stötte på i samband med arbetet. Valde ni bort någon del som ni inte fick till? Vilken del var svårast att få till?

Vi kom fram till att wavesurfing har för avancerad matematik och vi kommer inte kunna använda det.Dock så
kommer vi att implementera strafing vilket är surfing liknade beteende och tanken är att vår robot byter 
rörelse samt angreppsmethod ifall det är 1 eller 2 andra robotor kvar. Utöver det har vi kunnat förstår 
olika sniper beteende och kommit fram med 2 sniper robots. Vi har allstå alla enskild jobbat på de olika 
beteende, haft lite återkoppling emellan och kommer nu har ett mini fight där alla tar in sin egen robot. 
Vinnaren av denna match för bestämma namnet på vår gemensam robot, nummer 2 kommer få bestämma färg. 
Efter turnering är tanken att vi ska köra lite Code along och på det sättet försöka skapa alla methoder 
och implentera allt vi har önskat i vår gemensamma robot.  Att skjuta fienden med minst HP är nåt vi v
alde bort också, det för att vi känner att roboten inte behövde den method då den klara sig utmärkt utan.
Vår tanke att kunna ramma fienden medans vi åker längst väggarna gick inte ihop, då det skulle krävas flera 
olika typer av radars.

### Utifrån er plan samt de idéer ni hade från början, tycker du att ni uppnådde ert önskade resultat?

