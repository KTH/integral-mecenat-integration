# Designval

Information hämtas ifrån den s.k. uppföljningsdatabasen i Ladok3. I dagsläget uppdateras den
en gång per dygn men enligt konsortiet är planen att den i närtid istället kommer att uppdateras
kontinuerligt.

I dagsläget hämtas för enkelhets skull hela utdraget en gång per timme under dagtid och skickas
till Mecenat.

### Övergripande programlogik uttryckt som EIP

![Enterprise Integration Pattern](integral-mecenat-eip.png)

## Information från Ladok

Informationen hämtas periodiskt enligt nedan beskrivning från *Uppföljningsdatabasen* med SQL. 
Detta är vad vi har kommit fram till på KTH, det är oklart för mig exakt hur generellt
tillämpbart detta är för andra lärosäten. Bilden är att det borde vara relativt generellt,
men avvikelser kan säkert förekomma.

### Valda perioder

Mecenat delar in året i två terminer, hösttermin och vårtermin. Programmet byter vid halvårsskiftena
och börjar rapportera för höstterminen från 1 juli och vårterminen 1 januari.

Vald ansats är att de studieperioder som väljs ut att ingå i terminen är de som har ett
startdatum i ett intervall som är lite förskjutet i förhållande till detta. För höstterminen
söks de studieperioder ut som börjar mellan 1 juli och 30 november. För vårterminen är det
istället de perioder som börjar mellan 1 december föregående år och 30 juni.

Anledningen till detta är att utresande studenter registreras på studieperioder som gäller
för det lärosäte de studerar på och kan skilja ifrån våra egna. Vår vårtermin startar t.ex. i
allmänhet ett par veckor in i januari, men på andra lärosäten utomlands kan den ha börjat 
redan i december.

På samma sätt kan inresande studenter påbörja sina studier med en kurs i en studieperiod som
startar före höstterminens ordinarie början. 

Dessutom har vi två _sommarperioder_, en efter vårterminen som åtminstone i någon mening 
anses höra till våren och en före höstterminen som anses höra till hösten.

Given algoritm försöker fånga dessa specialfall på ett sätt som ger ett rimligt utfall i den
information som skickas till Mecenat, men det finns säkert utrymme för förbättringar.

### Studenter i allmänhet

När studieomfattning räknas ut tittar vi på antagningar, eller s.k. förväntat deltagande, 
under perioden, se 
`src/main/resources/sql/antagningar.sql`.

Antagningar och poäng under terminen filtreras ut per studieperiod under den aktiva terminen i
meningen att studieperioden startar under aktuella period (se ovan).

En brist här är att uppföljningsdatabasen i dagsläget inte innehåller information om återbud.
Detta finns noterat i Ladok JIRA, https://jira.its.umu.se/browse/LADOKSUPP-3657 och bedöms att man
kan åtgärda tidigast våren 2019. Till dess kommer vi alltså att överskatta studiedeltagandet något.

### Inresande studenter

Registreras på kurser som alla andra och kommer med i resultatet från antagningar.sql.

Ett specialfall är att dessa studenter kan påbörja terminen tidigare än andra studenter med
en inledande kurs i svenska. För att få med dessa under höstterminen startar tidsintervallet
vi letar perioder tidigare dessa i datat som rapporteras in för hösten. Se Valda perioder
ovan.

### Utresande studenter

Registreras också på kurser som motsvarar deras studier utomlands. En hake här är att studieperioden
i dessa fall är studieperioden på det andra universitetet som kan ha en helt annan periodindelning
än vi. Den nuvarande logiken för start- och slutdatum behöver anpassas för att ta hänsyn till det.

Vår ansats är att starta tidsintervallet vi letar efter perioder i lite tidigare för att få med dessa,
se Valda perioder ovan.

### Forskarstuderande

För forskarstuderande registreras en särskild studieaktivitet i procent i Ladok som används
som studieomfattning, se `src/main/resources/sql/forskarstuderande.sql`. Studieperioden är
kalenderhalvår.

### Aggregering av information

Resultatet av dessa frågor aggregeras så att omfattningen från de olika frågorna för en student
som eventuellt finns dubbelt summeras.

## Konvertering till Mecenat

Informationen konverteras till Mecenats format enligt `Mecenat-Ladok-2018.xlsx` med hjälp av
en annoterad modell `MecenatCSVRecord` som ger stöd för konverteringen till CSV.

Se även `Mecenat-Filspec-CSV-2018` för detaljer om vissa fält.


## Programmets struktur

Uppkopplingen mot Uppföljningsdatabasen är en DB2-koppling tunnlad över SSL. Eftersom DB2-klienten
i sig saknar särskilt stöd för SSL måste det implementeras särskilt. I containern görs detta f.n.
genom att använda stunnel enligt dokumentation från Ladok-konsortiet, 
[Uppkoppling mot Uppföljningsdatabasen (Windows)](https://confluence.its.umu.se/confluence/display/LDSV/Uppkoppling+mot+UppfoljningsDB+Windows)

![Program struktur](integral-mecenat-integration.png)

En potentiell förbättring med minskade beroenden här är att istället etablera SSL-förbindelsen mot
Ladok med Java och göra sig av med beroendet mot stunnel.

Sjävla programmet är sedan baserat på Apache Camel/Spring Boot och alltihop är packat i en Docker
container som är baserad på Alpine Linux och OpenJDK.


## Framtida utveckling

En kanske mer elegant lösning vore att istället lyssna på händelser som rör antagning och studieaktivitet
i Ladoks ATOM feed, slå upp all relevant information om studenten i REST-gränssnittet och skicka till
Mecenat kontinuerligt.

Det kräver dock en något större apparat med state i feed-läsaren och olika lärosäten har lite olika
lösningar och arkitektur för hur detta görs, så vi har valt att avvakta med det.
