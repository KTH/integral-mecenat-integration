--
-- Hämtar studieperioder. Är det användbart?
--
select distinct 
    min(FORSTA_UNDERVISNINGSDATUM) as START_DATUM,
    max(SISTA_UNDERVISNINGSDATUM) as SLUT_DATUM
from
    UPPFOLJNING.BI_TILLFALLESPERIODER
where
    FORSTA_REGISTRERINGSDATUM is not null
    and FORSTA_UNDERVISNINGSDATUM <= :#${header.today}
    and SISTA_UNDERVISNINGSDATUM >= :#${header.today}
