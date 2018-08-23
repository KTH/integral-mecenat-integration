# Release notes

## 1.1.12

* Fix bug in SQL introduced in 1.1.10.

## 1.1.11

* Added a couple of debug logs.

## 1.1.10

* Fix issue where students where considered active during the period
  when there was no active registration. Issue being the student was
  registered on some other study period on a course spanning multiple
  periods.

## 1.1.9

* Fix broken test after half year turn date.

## 1.1.8

* Reschedule to 09.30 weekday mornings (timezone Europe/Stockholm)
  as agreed with Mecenat.

## 1.1.7

* Change filename and path according to requirement from Mecenat.

## 1.1.6

* Retry forever on JDBC connection errors.
* Fix issue where country "SE" got propagated to CSV output.

## 1.1.5

* Fix timezone of timestamps in filename.

## 1.1.4

* Fix issue with $ in propoerties.

## 1.1.3

* Add error handler on all routes.

## 1.1.2

* Miscellaneous fixes to error handling.

## 1.1.0

* Amended for new requirements for Mecenat file format.

## 1.0.2

* Ladok3 production environment.

## 1.0.1

* Fix: courses starting in P0 (beginning of summer) belongs to spring term.

## 1.0.0

First major release, will go into production.

* Update documentation.
* Prefer old exchange in aggregator.

## 0.2.3

* Add more regression tests.

## 0.2.2

* Add more regression tests.
* Fix issue where aggregation did not consider the largest
  date interval among records.
* Update documentation about selected study periods.

## 0.2.1

* Externalize all endpoints and start to add regression tests.

## 0.2.0

Release candidate.

* Filter students to include only students who are registered on
  some activity in the period.

## 0.1.1

* Bugfix: make FTPS truststore configuration work in container.

## 0.1.0

Getting closer to release candidate.

* Fetches data for all categories of students.
* Working file upload.
* Store last file in container for debug.
* Make bunyan logging configurable.
* Updated documentation.

Remains: tweak study periods for exchange programme students.

## 0.0.4

Real and proper output.

* Proper implementation of Mecenat CSV file format.
* New working query to fetch expected attendency for students.

Remains: research students, erasmus and upload data somewhere.

## 0.0.3

No hard coded data.

* Hard coded values for term and intervals are removed.
* Configure data source rather than sql component.

## 0.0.2

Somewhat more complete working skeleton for workshop 2018-05-31.

* Use a real parameterized SQL query from Ladok3 web site for mecenat.
* Add Terena CA chain for verification of server certificate.

## 0.0.1

Intial release, just to get someting to run.