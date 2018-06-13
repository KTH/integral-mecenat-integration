-- Hämtar studieaktivitet för forskarstuderande.
select
    stud.personnummer
    ,stud.fornamn
    ,stud.efternamn
    ,stud.careof
    ,stud.utdelningsadress
    ,stud.postnummer
    ,stud.postort
    ,stud.land
    ,stud.epostadress
--    ,cast((sum(sta.PROCENT) * 30 / 100) as DECIMAL(8,2)) as OMFATTNING
    ,cast(sum(sta.PROCENT) as DECIMAL(8,2)) as OMFATTNING_PROCENT
    ,min(stp.STARTDATUM) as STARTDATUM
    ,max(stp.SLUTDATUM) as SLUTDATUM
from UPPFOLJNING.BI_STUDIEAKTIVITETER sta
    inner join UPPFOLJNING.IO_STUDENTUPPGIFTER stud on stud.STUDENT_UID = sta.STUDENT_UID
    inner join UPPFOLJNING.BI_PERIODER stp on stp.PERIOD_ID = sta.PERIOD_ID
where
    stp.STARTDATUM >= :#${header.periodStartDatum}
    and stp.STARTDATUM < :#${header.periodSlutDatum}
group by
    stud.personnummer
    ,stud.fornamn
    ,stud.efternamn
    ,stud.careof
    ,stud.utdelningsadress
    ,stud.postnummer
    ,stud.postort
    ,stud.land
    ,stud.epostadress
order by stud.personnummer asc