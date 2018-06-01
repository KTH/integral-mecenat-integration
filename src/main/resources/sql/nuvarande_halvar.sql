--
-- Hämtar innevarande halvår.
--
select p.period_kod, p.period_sv, p.startdatum, p.slutdatum
from UPPFOLJNING.BI_PERIODER p inner join UPPFOLJNING.BI_PERIODTYPER t on p.periodtyp_id = t.periodtyp_id 
where 
  t.periodtyp_kod = 'HALVÅR' 
  and p.startdatum <= :#${header.today}
  and p.slutdatum >=  :#${header.today}
