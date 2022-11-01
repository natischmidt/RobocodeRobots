# Dokumenation Calculon
### Beskriv, övergripande, robotens struktur/uppbyggnad.
Caluclon är en advanced Robot anpassar sig efter antal fienden i matchen för att bestämma sitt rörelsemönster.
Roboten skjuter sparsamt med en SmartFire funktion och sikta med linjear aiming. 







### Beskriv de implementeringar som gjorts i samband med varje vald metod (till exempel onScannedRobot), beskriv här den strategi/taktik som ni valt för er robot avseende de metoder ni valt.
### Run 
I run methoden används Width lock radar. Denna radar försöker skanna ett fast avstånd till båda sidorna av fienden. 
 Dess ständiga rörelse innebär att radarn inte glider ifrån fienden och jag anser denna som den bästa radar för en robot som ska både var bra emot grupp och mot bara en fiende. 

Efter det scannas det av efter antal fienden. Är antalet fienden större än 3 används Wallmovement, detta då man vill hålla sig borta från alla men samtidigt skjuta.
Är antalet fienden större än 1 används CircleMovement, vilket är precis som det låter en method för att kunna åka circla runt fienderna detta är ett bra sätt att dogdga skott.
Och slutlgien om det bara är en fiende kvar andäner colcunon closingin vilket är en strafing method som sakta  närmare sig fienden. Sytet men den är att doggda skott samtidgt med att gå efter sista fienden.

###### initialize 
Här sätts både radarforgunturn och setadjustgunforrobot till true detta gör att robotens body, pistol och radar kan röra sig oberoende av varandra.
Det är viktig för alla typer av radar och för att kunna uppnå en bra träffsäkerhet.

### CircleMovement
En method som med hjälp av currenTarget ta reda på fiendens riktning och sedan circla runt den. Tanken med denna method är att kunna undiika skott, denna method anropas
vid mindre än 3 fiende och mer än en 1, allstå när 2 fiende är kvar. Anledning till detta är att varken strafing eller wallsmovment skulle kunna va så effektik som denna,
Med strafing går man ut ifrån sin valda fiende, detta skulle i detta fall gör caluclon sårbar för andra bakom eller nära. Wallmovement kan i vissa fall vara effektiv här,
men circling är helt klart det bästa i 1v2, även om rörelsen här också går utifrån en fiende så är calcloun mindre sårbar med circling här. 

### Closingin
Här har vi strafing, strafing innebär att man rör sig sida till sida i förhållande till sin fiende.
I closing in, åker calculon i förhållade till sitt target, men närmar sig target stegviss. 
I ett försök att vara mindre förutsägbar byter roboten rikting var 20.ende tick. 

### Wallmovement
Denna method räknar ut höjden och bredden på slagsfälltet för att kunna åka längst väggarna.

### onScannedRobot 
Här scannas det effektiv genom att ta reda på vinkeln mot fienden, subtrahera nuvarade radarriktning och på det sättet
får rätt "turn". Man scannar från mitten av fienden  till vardera sidan 36 i här är mitten av fienden. 
```java
if (radarTurn < 0)
radarTurn -= extraturn;
else
radarTurn += extraturn;
```
Det som denna stycken kod gör är att justera radarvridning så att den scannar mycket längre i vald riktning, det vill
säga svängs det till vänster svängs det mer till vänster och likdant med höger. Så hår scanans en stor bit och på detta 
sätt mininimerkas risken att tappa bort fienden.
###### trackenemy
trackenemy är ett sätt att finna en måltavla, detta i samarbete med enemybot klassen.
ett nytt target söks ifall den nuvarande, har dött, ör för långt bort, eller om vi redan har scannat av denna robot.
###### smartFire
Smartfire är en method som ta reda på gunheat, berkäna distans till fienden och utifrån det bestämmer hur hårt det ska
skjutsas. Till exempel skjuts med full kraft ifall fienden är stillastående, detta då chansen att vi träffar är högt.
För att spara på sin Energie beräknas också hur mycket bulletpower som behävs för att skjuta någon, utan att slösa energie.
Exempelvis för att undvika att skjuta medd fullkraft om fienden har mindre än 3 kvar i energie. 

### onBattleEnded
Här skrivs ut lite statistik vid sluten av en hel match. Detta var väldigt bra under hela kodande, då det underlättar att prova olika
methoder och får ut lite mer information är robocodes egen info i mainbattlelog. 


### Beskriv hur ni gick till väga när ni som grupp planerade er robot (och dess taktik).
Tanken bakom Caluclon är att använda okomplicerat  kod men ändå få en bra ranking i matcherna.
Ursprugnlgien hade Calculon bara ett rörelsemönster vilket var Wallmovment, med detta klarade roboten sig fint, förutom ifall den mötte spinbot, en strafer eller crazy.
Detta för att roboten använder sig av linjär siktning vilket innebär att det skjuts dit fienden förutsags vara ifall fienden rör sig likadant.
Jag provade x-antal sätt att sikta och skjuta, men det som funkade bäst för calculon är ett infinty radar
och linjär siktning. Förståss finns det både för och nackdelar med detta, till exempel bli det svårt att
skjuta linjärt ifall man möter en robot som byter rörelsemönster jämnt. Calculon skulle har haft en 1v1
strategi, en var att ramma och strafa vid 1v1 en annan var ett spinbot liknade mönster. Problemet
som jag upplevde då var att min precision gick ner enormt, och att jag behövde lägga till oerhört mycket
kod för att få till det.



