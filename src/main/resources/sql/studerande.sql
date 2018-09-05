-- Hämta data för Mecenat för alla kurser med antagning.
-- OBS: En brist här är att vi inte tar hänsyn till antagningar som studenten
-- har lämnat återbud till. Det saknas information i uppföljningsdatabasen för
-- att kunna ta hänsyn till detta och det åtgärdas tidigast 2019.
-- Se: https://jira.its.umu.se/browse/LADOKSUPP-3657
select
    student_uid
    ,stud.personnummer
    ,stud.fornamn
    ,stud.efternamn
    ,stud.careof
    ,stud.utdelningsadress
    ,stud.postnummer
    ,stud.postort
    ,stud.land
    ,stud.epostadress
    ,OMFATTNINGSVARDE
from
    UPPFOLJNING.IO_STUDENTUPPGIFTER stud
    inner join
        (select
            -- Vy över alla studenter och deras summerade omfattningsvärde.
            UID
            ,cast(cast(SUM(OMFATTNINGSVARDE) as decimal(8,2)) / 30 * 100 as decimal(8,2)) as OMFATTNINGSVARDE
        from
            (select
                -- Vy över alla studenter och omfattningsvärden för de kurser de är 
                -- antagna till, där vi också tar hänsyn till anpassad omfattning.
                reg.STUDENT_UID as UID
                ,case when reg.ANPASSAD = 1 then reg.ANPASSATOMFATTNINGSVARDE else tper.OMFATTNINGSVARDE end as OMFATTNINGSVARDE
            from
                UPPFOLJNING.BI_FORVANTATTILLFALLESDELTAGANDEN ftf
                inner join UPPFOLJNING.BI_UTBILDNINGSTILLFALLEN utf on utf.UTBILDNINGSTILLFALLE_UID = ftf.UTBILDNINGSTILLFALLE_UID
                inner join UPPFOLJNING.BI_UTBILDNINGSTYPER utt on utt.UTBILDNINGSTYP_ID = utf.UTBILDNINGSTYP_ID
                inner join UPPFOLJNING.BI_UTBILDNINGSINSTANSER uti on uti.UTBILDNINGSINSTANS_UID = utf.UTBILDNINGSINSTANS_UID
                inner join UPPFOLJNING.BI_ENHETER enh on enh.ENHET_ID = uti.ENHET_ID
                inner join UPPFOLJNING.BI_UTBILDNINGSTILLFALLEN_TILLFALLESPERIODER utper on utper.UTBILDNINGSTILLFALLE_UID = utf.UTBILDNINGSTILLFALLE_UID
                inner join UPPFOLJNING.BI_TILLFALLESPERIODER tper on utper.TILLFALLESPERIOD_UID = tper.TILLFALLESPERIOD_UID
                inner join UPPFOLJNING.BI_REGISTRERINGAR reg on reg.FORVANTATTILLFALLESDELTAGANDE_UID = ftf.FORVANTATTILLFALLESDELTAGANDE_UID
            where
                 utt.GRUNDTYP = 'KURS'
                 -- Vilka kurser är studiemedelsberättigade?
                and (enh.ENHET_KOD = 'HP' or enh.ENHET_KOD = 'HP-K' or enh.ENHET_KOD = 'FUP')
                and tper.FORSTA_UNDERVISNINGSDATUM >= :#${header.periodStartDatum}
                and tper.FORSTA_UNDERVISNINGSDATUM < :#${header.periodSlutDatum}
                and ftf.student_uid in
                    (select unique regx.student_uid
                    -- Filtrera på studenter som är registrerade på åtminstone något utbildningstillfälle under perioden.
                    from
                        UPPFOLJNING.BI_REGISTRERINGAR regx
                        inner join UPPFOLJNING.BI_UTBILDNINGSTILLFALLEN utfx on utfx.UTBILDNINGSTILLFALLE_UID = reg.KURSTILLFALLE_UID
                        inner join UPPFOLJNING.BI_UTBILDNINGSTYPER uttx on uttx.UTBILDNINGSTYP_ID = utfx.UTBILDNINGSTYP_ID
                        inner join UPPFOLJNING.BI_UTBILDNINGSINSTANSER utix on utix.UTBILDNINGSINSTANS_UID = reg.UTBILDNINGSINSTANS_UID
                        inner join UPPFOLJNING.BI_ENHETER enhx on enhx.ENHET_ID = utix.ENHET_ID
                    where
                        uttx.GRUNDTYP = 'KURS'
                        and (enhx.ENHET_KOD = 'HP' or enhx.ENHET_KOD = 'HP-K' or enhx.ENHET_KOD = 'FUP')
                        and regx.STUDIEPERIOD_STARTDATUM >= :#${header.periodStartDatum}
                        and regx.STUDIEPERIOD_STARTDATUM < :#${header.periodSlutDatum}))
        group by UID)
    on stud.STUDENT_UID = UID
order by stud.personnummer asc


-- Hämta data för Mecenat för alla kurser med antagning.
-- OBS: En brist här är att vi inte tar hänsyn till antagningar som studenten
-- har lämnat återbud till. Det saknas information i uppföljningsdatabasen för
-- att kunna ta hänsyn till detta och det åtgärdas tidigast 2019.
-- Se: https://jira.its.umu.se/browse/LADOKSUPP-3657
select
    student_uid
    ,stud.personnummer
    ,stud.fornamn
    ,stud.efternamn
    ,stud.careof
    ,stud.utdelningsadress
    ,stud.postnummer
    ,stud.postort
    ,stud.land
    ,stud.epostadress
    ,cast(cast(SUM(OMFATTNINGSVARDE) as decimal(8,2)) / 30 * 100 as decimal(8,2))
from
    UPPFOLJNING.IO_STUDENTUPPGIFTER stud
    inner join
    (select
        -- Det här skapar en vy över alla studenter och omfattningsvärden för de kurser de är 
        -- antagna till, där vi också tar hänsyn till anpassad omfattning.
        reg.STUDENT_UID as UID
        ,case when reg.ANPASSAD = 1 then reg.ANPASSATOMFATTNINGSVARDE else tper.OMFATTNINGSVARDE end as OMFATTNINGSVARDE
    from
        UPPFOLJNING.BI_FORVANTATTILLFALLESDELTAGANDEN ftf
        inner join UPPFOLJNING.BI_UTBILDNINGSTILLFALLEN utf on utf.UTBILDNINGSTILLFALLE_UID = ftf.UTBILDNINGSTILLFALLE_UID
        inner join UPPFOLJNING.BI_UTBILDNINGSTYPER utt on utt.UTBILDNINGSTYP_ID = utf.UTBILDNINGSTYP_ID
        inner join UPPFOLJNING.BI_UTBILDNINGSINSTANSER uti on uti.UTBILDNINGSINSTANS_UID = utf.UTBILDNINGSINSTANS_UID
        inner join UPPFOLJNING.BI_ENHETER enh on enh.ENHET_ID = uti.ENHET_ID
        inner join UPPFOLJNING.BI_UTBILDNINGSTILLFALLEN_TILLFALLESPERIODER utper on utper.UTBILDNINGSTILLFALLE_UID = utf.UTBILDNINGSTILLFALLE_UID
        inner join UPPFOLJNING.BI_TILLFALLESPERIODER tper on utper.TILLFALLESPERIOD_UID = tper.TILLFALLESPERIOD_UID
        inner join UPPFOLJNING.BI_REGISTRERINGAR reg on reg.FORVANTATTILLFALLESDELTAGANDE_UID = ftf.FORVANTATTILLFALLESDELTAGANDE_UID
    where
            utt.GRUNDTYP = 'KURS'
        -- Vilka kurser är studiemedelsberättigade?
        and (enh.ENHET_KOD = 'HP' or enh.ENHET_KOD = 'HP-K' or enh.ENHET_KOD = 'FUP')
        and tper.FORSTA_UNDERVISNINGSDATUM >= :#${header.periodStartDatum}
        and tper.FORSTA_UNDERVISNINGSDATUM < :#${header.periodSlutDatum}
        and ftf.student_uid in (
            select unique regx.student_uid
            -- Filtrera på studenter som är registrerade på åtminstone något utbildningstillfälle under perioden.
            from
                UPPFOLJNING.BI_REGISTRERINGAR regx
                inner join UPPFOLJNING.BI_UTBILDNINGSTILLFALLEN utfx on utfx.UTBILDNINGSTILLFALLE_UID = reg.KURSTILLFALLE_UID
                inner join UPPFOLJNING.BI_UTBILDNINGSTYPER uttx on uttx.UTBILDNINGSTYP_ID = utfx.UTBILDNINGSTYP_ID
                inner join UPPFOLJNING.BI_UTBILDNINGSINSTANSER utix on utix.UTBILDNINGSINSTANS_UID = reg.UTBILDNINGSINSTANS_UID
                inner join UPPFOLJNING.BI_ENHETER enhx on enhx.ENHET_ID = utix.ENHET_ID
            where
                uttx.GRUNDTYP = 'KURS'
                and (enhx.ENHET_KOD = 'HP' or enhx.ENHET_KOD = 'HP-K' or enhx.ENHET_KOD = 'FUP')
                and regx.STUDIEPERIOD_STARTDATUM >= :#${header.periodStartDatum}
                and regx.STUDIEPERIOD_STARTDATUM < :#${header.periodSlutDatum})
       ) on stud.STUDENT_UID = UID
group by
    student_uid
    ,stud.personnummer
    ,stud.fornamn
    ,stud.efternamn
    ,stud.careof
    ,stud.utdelningsadress
    ,stud.postnummer
    ,stud.postort
    ,stud.land
    ,stud.epostadress
order by stud.personnummer asc