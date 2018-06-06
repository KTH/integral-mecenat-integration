--
-- Hämtar studie perioder, plockad från Ladok3 wiki och parametriserad
-- https://confluence.its.umu.se/confluence/display/LDSV/Generera+fil+till+Mecenat
--
-- Behöver headrar:
-- 'termin' = '20172'
-- 'startDatum' = '2017-08-28'
-- 'slutDatum' = '2018-01-14'
--

select
    s.personnummer
    ,s.fornamn
    ,s.careof
    ,s.utdelningsadress
    ,s.postnummer
    ,s.postort
    ,s.land
    ,s.telefonnummer
    ,s.epostadress
    ,a.STUDENT_UID
    ,a.enhet_kod
    ,a.YTTERSTA_KURSPAKETERING_KOD
    ,a.YTTERSTA_KURSPAKETERING_SV
    ,a.YTTERSTA_KURSPAKETERINGSTYP_KOD
    ,SUM(a.OMFATTNINGVARDE) AS OMFATTNING
    ,MIN(a.UTBILDNINGSTILLFALLE_STARTDATUM)  AS STARTDATUM
    ,MAX(a.UTBILDNINGSTILLFALLE_SLUTDATUM)    AS SLUTDATUM
from
    UPPFOLJNING.IO_STUDIEDELTAGANDE_ANTAGNING a
    inner join UPPFOLJNING.IO_STUDENTUPPGIFTER s on s.STUDENT_UID = a.STUDENT_UID
    inner join UPPFOLJNING.BI_UTBILDNINGSTYPER t on a.UTBILDNINGSTYP_KOD = t.UTBILDNINGSTYP_KOD
 where
    a.UTBILDNINGSTILLFALLE_STARTDATUM >= :#${header.terminStartDatum} and
    a.UTBILDNINGSTILLFALLE_STARTDATUM <= :#${header.terminSlutDatum} and
    a.ENHET_KOD = 'HP'
group by
    s.personnummer
    ,s.fornamn
    ,s.careof
    ,s.utdelningsadress
    ,s.postnummer
    ,s.postort
    ,s.land
    ,s.telefonnummer
    ,s.epostadress
    ,a.STUDENT_UID
    ,a.enhet_kod
    ,a.YTTERSTA_KURSPAKETERING_KOD
    ,a.YTTERSTA_KURSPAKETERING_SV
    ,a.YTTERSTA_KURSPAKETERINGSTYP_KOD
order by
    s.personnummer desc
