# Dokumenation Calculon
### Beskriv, övergripande, robotens struktur/uppbyggnad.
Caluclon är en advanced Robot som anpassar sig efter antal fiender i matchen för att bestämma sitt rörelsemönster.
Roboten skjuter sparsamt med en SmartFire funktion och siktar med linjär aiming. 







### Beskriv de implementeringar som gjorts i samband med varje vald metod (till exempel onScannedRobot), beskriv här den strategi/taktik som ni valt för er robot avseende de metoder ni valt.
### Run 
I run methoden används Width lock radar. Denna radar försöker skanna av en fast avstånd till båda sidorna av fienden. 
 Dess ständiga rörelse innebär att radarn inte glider ifrån fienden och jag anser att denna är den bästa radarn för en robot som ska va bra emot både grupp och mot bara en fiende. 

Efter det scannas det för att avgöra antalet fiender. Är antalet fienden större än 3 används Wallmovement, detta då man vill hålla sig borta från fienderna men samtidigt skjuta.
Är antalet fiender större än 1 används CircleMovement, vilket är, precis som det låter, en method för att kunna åka i cirkulära banor kring fienderna. Detta är ett bra sätt att dogdga skott.
Slutligen, om det bara är en fiende kvar använder colcunon closingin, vilket är en strafing method där roboten sakta  närmar sig fienden. Syftet men det är att undvika skott samtidgt som att gå efter sista fienden.

###### initialize 
Här sätts både radarforgunturn och setadjustgunforrobot till true. Detta gör att robotens body, pistol och radar kan röra sig oberoende av varandra.
Det är viktig för alla typer av radar och för att kunna uppnå en bra träffsäkerhet.

### CircleMovement
En method som med hjälp av currenTarget ta reda på fiendens riktning och sedan cirkulera runt den. Tanken med denna method är att kunna undvika att bli träffad av skott, denna method används
vid mindre än 3 fiende och mer än en 1, allstå när 2 fiende är kvar. Anledning till detta är att varken strafing eller wallsmovment skulle kunna vara så effektik som denna,
Med strafing går man ut ifrån sin valda fiende, detta skulle i detta fall gör caluclon sårbar för andra bakom eller nära. Wallmovement kan i vissa fall vara effektiv här,
men circling är helt klart det bästa i 1v2, även om rörelsen här också går utifrån en fiende så är calcloun mindre sårbar med circling.

### Closingin
Här har vi strafing. Strafing innebär att man rör sig sida till sida i förhållande till sin fiende.
I closing in, åker calculon i förhållade till sitt target, men närmar sig target stegviss. 
I ett försök att vara mindre förutsägbar byter roboten rikting var 20nde tick. 

### Wallmovement
Denna method räknar ut höjden och bredden på slagsfälltet för att kunna åka längst väggarna.

### onScannedRobot 
Här scannas det effektiv genom att ta reda på vinkeln mot fienden, subtrahera nuvarade radarriktning och på det sättet
får rätt "turn". Man scannar från mitten av fienden  till vardera sidan, 36 här är mitten av fienden. 
```java
if (radarTurn < 0)
radarTurn -= extraturn;
else
radarTurn += extraturn;
```
Det som denna stycken kod gör är att justera radarvridning så att den scannar mycket längre i vald riktning, det vill
säga svängs det till vänster svängs det mer till vänster och likdant med höger. Så hår scannas ett större fält och på detta sätt minimeras risken att tappa bort fiender.
###### trackenemy
trackenemy är ett sätt att finna en måltavla, detta i samarbete med enemybot klassen.
ett nytt target söks ifall den nuvarande, har dött, är för långt bort, eller om vi redan har scannat av denna robot.
###### smartFire
Smartfire är en method som ta reda på gunheat, beräkna distans till fienden och utifrån det bestämmer hur hårt det ska skjutsas. Till exempel skjuts med full kraft ifall fienden är stillastående, detta då chansen att vi träffar är högt.
För att spara på sin Energie beräknas också hur mycket bulletpower som behövs för att skjuta någon, utan att slösa energie.
Till exempel undviks det att skjuta med mer bulletpower än det som behovs, för att inte slösa med energie. 

### onBattleEnded
Här skrivs ut lite statistik vid sluten av en hel match. Detta var väldigt bra under hela kodande, då det underlättar att prova olika
methoder och får ut lite mer information är robocodes egen info i mainbattlelog. 


### Beskriv hur ni gick till väga när ni som grupp planerade er robot (och dess taktik).
Tanken bakom Caluclon är att använda okomplicerat och lite kod men ändå få en bra ranking i matcherna.
Ursprugnlgien hade Calculon bara ett rörelsemönster vilket var Wallmovment, med detta klarade roboten sig fint, förutom ifall den mötte spinbot, en strafer eller crazy.
Detta för att roboten använder sig av linjär siktning vilket innebär att det skjuts dit fienden förutsags vara ifall fienden rör sig likadant.
Jag provade x-antal sätt att sikta och skjuta, men det som funkade bäst för calculon är ett infinty radar
och linjär siktning. Förståss finns det både för och nackdelar med detta, till exempel bli det svårt att
skjuta linjärt ifall man möter en robot som byter rörelsemönster jämnt. Calculon skulle har haft en 1v1
strategi, en var att ramma och strafa vid 1v1 en annan var ett spinbot liknade mönster. Problemet
som jag upplevde då var att min precision gick ner enormt, och att jag behövde lägga till oerhört mycket
kod för att få till det. Darför valde jag olika rörelsemönster istället och behöll linjär siktning. 


### Beskriv de eventuella problem som du/ni stötte på i samband med arbetet. Valde ni bort någon del som ni inte fick till? Vilken del var svårast att få till?
Jag tyckte att svårast va att komma fram till ideer och sätt att vidareutveckla roboten efter jag hade gjort det jag hade
tänkt i början. Gruppen jag gick med i hjälpte mycket med detta, då jag fick flera ideer samt kunde hjälpa andra att
vidareutveckla sina ideer. Efter det så var det lite svårt att få ner min tanke om olika strategie i koden, jag tänkte först
att jag skulle ha olike radars samt olika skjutningsmethoder och rörelsemönster. Det visade sig att det inte behövdes och det
bästa var att ändra rörelsemönstret. 

### Utifrån er plan samt de idéer ni hade från början, tycker du att ni uppnådde ert önskade resultat?