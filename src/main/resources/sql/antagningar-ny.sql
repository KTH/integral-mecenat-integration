select
    s.personnummer as PERSONNUMMER
    ,s.fornamn as FORNAMN
    ,s.efternamn as EFTERNAMN
    ,sum(a.OMFATTNINGVARDE) AS OMFATTNING
    ,cast(cast(SUM(a.OMFATTNINGVARDE) as decimal(8,2)) / 30 * 100 as decimal(6,0)) as OMFATTNING_PROCENT
    ,min(a.UTBILDNINGSTILLFALLE_STARTDATUM) as STARTDATUM
    ,max(a.UTBILDNINGSTILLFALLE_SLUTDATUM) as SLUTDATUM
from
    UPPFOLJNING.IO_STUDIEDELTAGANDE_ANTAGNING a
    inner join UPPFOLJNING.IO_STUDENTUPPGIFTER s on s.STUDENT_UID = a.STUDENT_UID
    inner join UPPFOLJNING.BI_UTBILDNINGSTYPER t on a.UTBILDNINGSTYP_KOD = t.UTBILDNINGSTYP_KOD
 where
    t.GRUNDTYP = 'KURS'
    and a.UTBILDNINGSTILLFALLE_STARTDATUM >= :#${header.terminStartDatum}
    and a.UTBILDNINGSTILLFALLE_SLUTDATUM < :#${header.terminSlutDatum}
    and (a.ENHET_KOD = 'HP' or a.ENHET_KOD = 'HP-K')
    and a.REGISTRERAD = 0 -- Betyder detta att vi eg bara behöver den här tabellen?
group by
    s.personnummer
    ,s.fornamn
    ,s.efternamn
order by
    s.personnummer desc
