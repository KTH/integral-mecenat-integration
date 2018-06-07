-- Hämta data för Mecenat för alla kurser med antagning.
-- OBS: En brist här är att vi inte tar hänsyn till antagningar som studenten
-- har lämnat återbud till. Det saknas information i uppföljningsdatabasen för
-- att kunna ta hänsyn till detta och det åtgärdas tidigast 2019.
-- Se: https://jira.its.umu.se/browse/LADOKSUPP-3657
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
    ,sum(tper.OMFATTNINGSVARDE) AS OMFATTNING
    ,cast(cast(SUM(tper.OMFATTNINGSVARDE) as decimal(8,2)) / 30 * 100 as decimal(8,2)) as OMFATTNING_PROCENT
    ,min(tper.FORSTA_UNDERVISNINGSDATUM) as STARTDATUM
    ,max(tper.SISTA_UNDERVISNINGSDATUM) as SLUTDATUM
from
    UPPFOLJNING.BI_FORVANTATTILLFALLESDELTAGANDEN ftf
    inner join UPPFOLJNING.BI_UTBILDNINGSTILLFALLEN utf on utf.UTBILDNINGSTILLFALLE_UID = ftf.UTBILDNINGSTILLFALLE_UID
    inner join UPPFOLJNING.BI_UTBILDNINGSTYPER utt on utt.UTBILDNINGSTYP_ID = utf.UTBILDNINGSTYP_ID
    inner join UPPFOLJNING.BI_UTBILDNINGSINSTANSER uti on uti.UTBILDNINGSINSTANS_UID = utf.UTBILDNINGSINSTANS_UID
    inner join UPPFOLJNING.BI_ENHETER enh on enh.ENHET_ID = uti.ENHET_ID
    inner join UPPFOLJNING.BI_UTBILDNINGSTILLFALLEN_TILLFALLESPERIODER utper on utper.UTBILDNINGSTILLFALLE_UID = utf.UTBILDNINGSTILLFALLE_UID
    inner join UPPFOLJNING.BI_TILLFALLESPERIODER tper on utper.TILLFALLESPERIOD_UID = tper.TILLFALLESPERIOD_UID
    inner join UPPFOLJNING.IO_STUDENTUPPGIFTER stud on stud.STUDENT_UID = ftf.STUDENT_UID   
where
    utt.GRUNDTYP = 'KURS'
    and (enh.ENHET_KOD = 'HP' or enh.ENHET_KOD = 'HP-K' or enh.ENHET_KOD = 'FUP')
    and tper.FORSTA_UNDERVISNINGSDATUM >= :#${header.terminStartDatum}
    and tper.SISTA_UNDERVISNINGSDATUM < :#${header.terminSlutDatum}
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