--
-- Hämtar nästa termin.
--
select p.period_kod, p.startdatum, p.slutdatum
from UPPFOLJNING.BI_PERIODER p inner join UPPFOLJNING.BI_PERIODTYPER t on p.periodtyp_id = t.periodtyp_id 
where 
  t.periodtyp_kod = 'TERMIN' 
  and p.startdatum > :#${header.today}
order by p.startdatum asc
fetch first 1 rows only