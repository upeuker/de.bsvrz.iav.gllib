Versionsverlauf
===============


## Version 2.3.2

- Bugfix: Löschen von Ganglinien über die Anfrage-Schnittstelle wurde korrigiert 

## Version 2.3.1

- Ganglinienliste MQ in Derby wird angelegt, wenn sie noch nicht existiert und über die Datenverteiler-Schnittstelle
  angelegt werden soll

## Version 2.3.0

- Umstellung auf Java 8 und UTF-8

## Version 2.2.0

  - Umstellung auf Maven-Build

## Version 2.1.2

  - Hibernate-Entity-Manager wurde geschlossen und dann wiederverwendet (internal #2849)

## Version 2.1.1

  - Bei Abfrage von historischen Ganglinien konnte die SWE GlLernen abstürzen.
    (Mantis #2102)
  - Loggausgaben beim Unit-Test werden wieder angezeigt.
  - Überarbeitung der JUnit-Tests damit sie nach DAF-Upgrade wieder wie gewohnt
    funtionieren.

## Version 2.1.0

  - Betriebsmeldungen um Systemobjekt in ID ergänzen, wenn sinnvoll.
    (Mantis #2092)
  - Quelltext kompatibler mit Java 6 gemacht.

## Version 2.0.2

  - Speicherung der Ganglinien in Datenbank optimiert

## Version 2.0.0

  - Umstellung der Ganglinienspeicherung von der Behandlung als Parameter
    (atg.ganglinie) auf eine separate Speicherung in einer Derby DB.
  - Bereitstellung einer Datenverteiler-Schnittstelle für das Lesen und
    Schreiben von Ganglinien durch andere Applikationen
  - Verringerung der Stützstellenanzahl. Es ist Pro Approximationsverfahren per
    Kommdozeilenparameter einstellbar, welcher Aspekt von
    atg.verkehrsDatenKurzMq verwendet werden soll.
  - Vermeidung von Datenlücken. Die Verschmelzungsregeln des Ganglinienlernens
    wurde derart geändert:
    Alt: (Datenlücke + keine Datenlücke = Datenlücke),
    Neu: (Datenlücke + keine Datenlücke = keine Datenlücke)

## Version 1.3.0

  - Betriebsinformation und Prüfprozedur liegen jetzt im Zustand "akzeptiert"
    vor.

## Version 1.2.1

  - Betriebsinformation aktualisiert.

## Version 1.2.0

  - Anpassung an überarbeitetet Funktionsbibliothek de.bsvrz.sys.funclib.bitctrl
  - Pattern-Matching überarbeitet
  - Berechnung der Approximation durch B-Spline extrem beschleunigt
  - Prüfprozedur und Prüfprotokoll in Release aufgenommen
  - Betriebsinformation in Release aufgenommen

## Version 1.1.0

  - Umstellung auf neue Paketstruktur.
  - Erweiterung des Funktionsumfangs für SWE 5.1 und 5.3.

## Version 1.0.0

  - Erste Auslieferung

