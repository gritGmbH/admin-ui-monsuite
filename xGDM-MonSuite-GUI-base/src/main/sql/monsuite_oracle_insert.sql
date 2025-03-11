---
-- #%L
-- xGDM-MonSuite GUI (Base)
-- %%
-- Copyright (C) 2022 - 2025 grit GmbH
-- %%
-- Permission is hereby granted, free of charge, to any person obtaining a copy
-- of this software and associated documentation files (the "Software"), to deal
-- in the Software without restriction, including without limitation the rights
-- to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
-- copies of the Software, and to permit persons to whom the Software is
-- furnished to do so, subject to the following conditions:
-- 
-- The above copyright notice and this permission notice shall be included in
-- all copies or substantial portions of the Software.
-- 
-- THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
-- IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
-- FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
-- AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
-- LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
-- OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
-- THE SOFTWARE.
-- #L%
---
-----------------------
--- GDI_SENSOR_TYP ---
-----------------------
Insert into GDI_SENSOR_TYP (SENSOR_TYP_ID,NAME) values ('WFSBASIC','WFS Prüfung');
Insert into GDI_SENSOR_TYP (SENSOR_TYP_ID,NAME) values ('WMSBASIC','WMS Prüfung');
Insert into GDI_SENSOR_TYP (SENSOR_TYP_ID,NAME) values ('HTTPCHK','HTTP Server/Connector Prüfung');
Insert into GDI_SENSOR_TYP (SENSOR_TYP_ID,NAME) values ('SQLORACLE','Oracle Datenbank Prüfung');
Insert into GDI_SENSOR_TYP (SENSOR_TYP_ID,NAME) values ('SQLPG','PostgreSQL Datenbank Prüfung');
Insert into GDI_SENSOR_TYP (SENSOR_TYP_ID,NAME) values ('DBPBASIC','Serverseitige DB-Prüfung');
Insert into GDI_SENSOR_TYP (SENSOR_TYP_ID,NAME) values ('ARCGIS','ArcGIS Server Prüfung');
Insert into GDI_SENSOR_TYP (SENSOR_TYP_ID,NAME) values ('OAFBASIC','OAF Prüfung (OGC API-Features)');
commit;

-----------------------
--- GDI_MESSAGE_TYP ---
-----------------------
Insert into GDI_MESSAGE_TYP (MESSAGE_TYP_ID,NAME) values (1,'E-Mail');
Insert into GDI_MESSAGE_TYP (MESSAGE_TYP_ID,NAME) values (4,'SNMPv1 Trap');
Insert into GDI_MESSAGE_TYP (MESSAGE_TYP_ID,NAME) values (5,'SNMPv2c Notify');
commit;

----------------------
--- GDI_RESULTCODE ---
----------------------
Insert into GDI_RESULTCODE (RESULT_ID,NAME,DESCRIPTION) values (0,'Ergebnis undefiniert',null);
Insert into GDI_RESULTCODE (RESULT_ID,NAME,DESCRIPTION) values (1,'Prüfung erfolgreich','Die Prüfung ist ohne Fehlermeldung abgeschlossen worden');
Insert into GDI_RESULTCODE (RESULT_ID,NAME,DESCRIPTION) values (2,'Zeitüberschreitung','Eine Zeitüberschreitung ist während der Prüfung aufgetreten (z.B. durch Netzwerkprobleme oder Lastprobleme)');
Insert into GDI_RESULTCODE (RESULT_ID,NAME,DESCRIPTION) values (3,'Fehler beim Verbindungsaufbau','Es konnte während der Prüfung eine Verbindung nicht hergestellt werden (z.B. durch Routing- oder DNS-Problem)');
Insert into GDI_RESULTCODE (RESULT_ID,NAME,DESCRIPTION) values (4,'Prüfung fehlgeschlagen','Während der Prüfung ist ein Fehler aufgetreten, der nicht weiter Spezifiziert wurde');
Insert into GDI_RESULTCODE (RESULT_ID,NAME,DESCRIPTION) values (5,'Rückgabe fehlerhaft','Das Prüfungsziel hat auf eine Anfrage mit falschen bzw. nicht erwarteten Daten geantwortet');
Insert into GDI_RESULTCODE (RESULT_ID,NAME,DESCRIPTION) values (6,'Konfiguration fehlerhaft','Eine Prüfung ist nicht möglich da die Konfiguration fehlerhaft bzw. nicht ausreichend ist');
Insert into GDI_RESULTCODE (RESULT_ID,NAME,DESCRIPTION) values (7,'Sensor nicht erreicht','Ein Sensor konnte nicht erreicht werden (z.B. bei Netzwerkproblemen)');
Insert into GDI_RESULTCODE (RESULT_ID,NAME,DESCRIPTION) values (8,'Nachrichtentransport fehlgeschlagen','Der Sensor konnte keine Nachrichten zurückgeben (z.B. bei Netzwerkproblemen)');
Insert into GDI_RESULTCODE (RESULT_ID,NAME,DESCRIPTION) values (9,'Collection ist leer','Collection enthält keine Feature');
commit;

-------------------
--- GDI_MONITOR ---
-------------------
-- TRICKY: '1' is true (boolean) on Postgres and 1 (numeric) on Oracle
-- NOTE: The NAME and HOST has to be updated to reflect the installed hosts name and IPv4 address
Insert into GDI_MONITOR (DAEMON_ID,NAME,HOST,STATUS) values (1,'XXX','1.1.1.1','1');

-------------------
--- GDI_ACTIONS ---
-------------------
INSERT INTO gdi_actions(id,name) VALUES (1,'alle Werktage aktivieren');
INSERT INTO gdi_actions(id,name) VALUES (2,'alle Wochentage aktivieren');
INSERT INTO gdi_actions(id,name) VALUES (3,'von 6:00 Uhr bis 17:00 aktivieren');
INSERT INTO gdi_actions(id,name) VALUES (4,'von 5:00 Uhr bis 21:00 aktivieren');
INSERT INTO gdi_actions(id,name) VALUES (5,'alle Werktage deaktivieren');
commit;

INSERT INTO dee_db_version (name, version, "DATE") VALUES ('MonSuite_insert', '@@project.version@@', to_char (sysdate, 'YYYY-MM-DD'));
commit;
