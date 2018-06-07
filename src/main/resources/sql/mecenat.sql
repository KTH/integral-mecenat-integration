--
-- Hämtar studie perioder, plockad från Ladok3 wiki och parametriserad
-- https://confluence.its.umu.se/confluence/display/LDSV/Generera+fil+till+Mecenat
--
-- Behöver headrar:
-- 'termin' = '20172'
-- 'startDatum' = '2017-08-28'
-- 'slutDatum' = '2018-01-14'
--
select distinct
  stud.personnummer
  ,substr(stud.efternamn, 1, 50)          AS efternamn
  ,substr(stud.fornamn, 1, 50)            AS fornamn
  ,substr(stud.careof, 1, 50)             AS COadress
  ,substr(stud.utdelningsadress, 1, 50)   AS gatuadress
  ,substr(stud.postnummer, 1, 10)         AS postnummer
  ,stud.postort
  ,CASE WHEN upper(stud.land) = 'SVERIGE' OR upper(stud.land) = 'SE'
    THEN ''
   ELSE stud.land END                      AS land
  ,substr(stud.epostadress, 1, 50)        AS epostadress
  ,stud.telefonnummer                     AS "TELEFON/MOBIL"
  ,reg.YTTERSTA_KURSPAKETERING_KOD
  ,reg.YTTERSTA_KURSPAKETERING_SV
  ,reg.YTTERSTA_KURSPAKETERINGSTYP_KOD
  ,CASE WHEN ((cast(reg.REGISTRERAD_OMFATTNING AS DECIMAL(9, 2)) / 30.0) * 100) > 100
    THEN 100
   WHEN reg.REGISTRERAD_OMFATTNING IS NULL
    THEN 0
   ELSE (cast(reg.REGISTRERAD_OMFATTNING AS DECIMAL(9, 2)) / 30.0) * 100
   END                                    AS REGISTRERING_OMFATTNING_PROCENT
  ,cast(:#${header.termin} as CHAR(5))    AS termin
  ,reg.STUDIEPERIOD_STARTDATUM            AS from
  ,reg.STUDIEPERIOD_SLUTDATUM             AS tom
FROM (
   SELECT
     r.STUDENT_UID,
     r.enhet_kod,
     r.YTTERSTA_KURSPAKETERING_KOD,
     r.YTTERSTA_KURSPAKETERING_SV,
     r.YTTERSTA_KURSPAKETERINGSTYP_KOD,
     SUM(r.REGISTRERING_OMFATTNINGSVARDE) AS REGISTRERAD_OMFATTNING,
     MIN(r.STUDIEPERIOD_STARTDATUM)       AS STUDIEPERIOD_STARTDATUM,
     MAX(r.STUDIEPERIOD_SLUTDATUM)        AS STUDIEPERIOD_SLUTDATUM
   FROM UPPFOLJNING.IO_REGISTRERING r
   WHERE
     r.STUDIEPERIOD_STARTDATUM >= :#${header.terminStartDatum} 
     AND r.STUDIEPERIOD_SLUTDATUM <= :#${header.terminSlutDatum}
     AND r.ENHET_KOD = 'HP'
     AND r.REGTYP <> 'OM'
   GROUP BY
    r.STUDENT_UID, r.enhet_kod
    ,r.YTTERSTA_KURSPAKETERING_KOD
    ,r.YTTERSTA_KURSPAKETERING_SV
    ,r.YTTERSTA_KURSPAKETERINGSTYP_KOD
  ) reg
  INNER JOIN UPPFOLJNING.IO_STUDENTUPPGIFTER stud ON stud.STUDENT_UID = reg.STUDENT_UID
ORDER BY personnummer ASC
