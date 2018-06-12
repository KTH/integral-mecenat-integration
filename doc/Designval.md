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

### Studenter i allmänhet

Vi tittar i dagsläget enbart på antagningar, eller s.k. förväntat deltagande, se 
`src/main/resources/sql/antagningar.sql`.

Antagningar och poäng under terminen filtreras ut per studieperiod under den aktiva terminen i
meningen att studieperioden startar under terminen och avslutas före nästa termin. Detta kommer
att behöva anpassas, se not om *utresande studenter* nedan.

En brist här är att uppföljningsdatabasen i dagsläget inte innehåller information om återbud.
Detta finns noterat i Ladok JIRA, https://jira.its.umu.se/browse/LADOKSUPP-3657 och bedöms att man
kan åtgärda tidigast våren 2019. Till dess kommer vi alltså att överskatta studiedeltagandet något.

### Inresande studenter

Registreras på kurser som alla andra och kommer med i resultatet från antagningar.sql.

### Utresande studenter

Registreras också på kurser som motsvarar deras studier utomlands. En hake här är att studieperioden
i dessa fall är studieperioden på det andra universitetet som kan ha en helt annan periodindelning
än vi. Den nuvarande logiken för start- och slutdatum behöver anpassas för att ta hänsyn till det.

### Forskarstuderande

För forskarstuderande registreras en särskild studieaktivitet i procent i Ladok som används
som studieomfattning, se `src/main/resources/sql/forskarstuderande.sql`. Studieperioden är
kalenderhalvår.

### Aggregering av information

Resultatet av dessa frågor aggregeras så att omfattningen från de olika frågorna för en student
som eventuellt finns dubbelt summeras.

## Konvertering till Mecenat

Informationen konverteras till Mecenats format enligt `Mecenat-Filspec-CSV-2018.pdf` med hjälp av
en annoterad modell `MecenatCSVRecord` som ger stöd för konverteringen till CSV.


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
