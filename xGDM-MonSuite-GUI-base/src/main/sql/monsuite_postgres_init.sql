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
--------------------------------------------------------
--  Table GDI_MONITOR
--------------------------------------------------------
CREATE TABLE "gdi_monitor" (
	"daemon_id" INTEGER PRIMARY KEY,
	"name" VARCHAR(100) NOT NULL UNIQUE,
	"host" VARCHAR(15) NOT NULL UNIQUE,
	"status" BOOLEAN NOT NULL
);

--------------------------------------------------------
--  Table GDI_SENSOR_TYP
--------------------------------------------------------
CREATE TABLE "gdi_sensor_typ" (
	"sensor_typ_id" VARCHAR(10) PRIMARY KEY,
	"name" VARCHAR(100) NOT NULL UNIQUE
);

--------------------------------------------------------
--  Table GDI_PERSON
--------------------------------------------------------
CREATE TABLE "gdi_person" (
	"person_id" INTEGER PRIMARY KEY,
	"name" VARCHAR(200) NOT NULL UNIQUE,
	"mail" VARCHAR(1000),
	"fax" VARCHAR(1000),
	"sms" VARCHAR(1000),
	"snmp" VARCHAR(400)
);

--------------------------------------------------------
--  Table GDI_MESSAGE_TYP
--------------------------------------------------------
CREATE TABLE "gdi_message_typ" (
	"message_typ_id" INTEGER PRIMARY KEY,
	"name" VARCHAR(50) NOT NULL
);

--------------------------------------------------------
--  Table GDI_RESULTCODE
--------------------------------------------------------
CREATE TABLE "gdi_resultcode" (
	"result_id" INTEGER PRIMARY KEY,
	"name" VARCHAR(100) NOT NULL UNIQUE,
	"description" VARCHAR(1000)
);

--------------------------------------------------------
--  Table GDI_SENSOR
--------------------------------------------------------
CREATE TABLE "gdi_sensor" (
	"sensor_id" INTEGER PRIMARY KEY,
	"name" VARCHAR(200),
	"sensor_typ_id" VARCHAR(10) NOT NULL,
	"daemon_id" INTEGER NOT NULL,
	"url" VARCHAR(500),
	"layer_avail" VARCHAR(500),
	"version_string" VARCHAR(100),
	"layer_imgcheck" VARCHAR(500),
	"http_user" VARCHAR(100),
	"http_pass" VARCHAR(100),
	"ims_pipe" VARCHAR(100),
	"ims_host" VARCHAR(100),
	"ims_action" SMALLINT,
	"app_user" VARCHAR(100),
	"app_pass" VARCHAR(100),
	"dbp_database" VARCHAR(200),
	"dbp_action" SMALLINT,
	"feature_cap" VARCHAR(1500),
	"feature_get" VARCHAR(1500),
	"proxy" VARCHAR(250),
	"srs" VARCHAR(250),
	"bbox" VARCHAR(500),
	"styles_imgcheck" VARCHAR(500),
	"format_imgcheck" VARCHAR(500),
	"regexp_req" VARCHAR(500),
	"regexp_not" VARCHAR(500),
	"valid_code" VARCHAR(250),
	"image_size" VARCHAR(150),
	"image_dir" VARCHAR(150),
	"sql_cmd" VARCHAR(250),
	"map_name_id" INTEGER,
	"scale" INTEGER,
	"rotation" INTEGER,
	"center_pos" VARCHAR(250),
	"string_prop" VARCHAR(1000),
	"res_dpi" INTEGER,
	"fmt_name" VARCHAR(50),
	"map_mxd" VARCHAR(250),
	"version" INTEGER DEFAULT 0,
	FOREIGN KEY ("daemon_id") REFERENCES "gdi_monitor" ("daemon_id"),
	FOREIGN KEY ("sensor_typ_id") REFERENCES "gdi_sensor_typ" ("sensor_typ_id")
);

--------------------------------------------------------
--  Table GDI_CHECK_TASK
--------------------------------------------------------
CREATE TABLE "gdi_check_task" (
	"task_id" INTEGER PRIMARY KEY,
	"sensor_id" INTEGER NOT NULL UNIQUE,
	"name" VARCHAR(200),
	"start_date" DATE,
	"end_date" DATE,
	"start_time" VARCHAR(5),
	"end_time" VARCHAR(5),
	"timeout" SMALLINT,
	"pause" SMALLINT,
	"version" INTEGER DEFAULT 0,
	"checkday_0" BOOLEAN DEFAULT false NOT NULL,
	"checkday_1" BOOLEAN DEFAULT false NOT NULL,
	"checkday_2" BOOLEAN DEFAULT false NOT NULL,
	"checkday_3" BOOLEAN DEFAULT false NOT NULL,
	"checkday_4" BOOLEAN DEFAULT false NOT NULL,
	"checkday_5" BOOLEAN DEFAULT false NOT NULL,
	"checkday_6" BOOLEAN DEFAULT false NOT NULL,
	FOREIGN KEY ("sensor_id") REFERENCES "gdi_sensor" ("sensor_id")
);

--------------------------------------------------------
--  Table GDI_MESSAGE
--------------------------------------------------------
CREATE TABLE "gdi_message" (
	"person_id" INTEGER,
	"task_id" INTEGER,
	"message_typ_id" INTEGER,
	"id" INTEGER NOT NULL UNIQUE,
	PRIMARY KEY ("person_id", "task_id", "message_typ_id"),
	FOREIGN KEY ("task_id") REFERENCES "gdi_check_task" ("task_id") ON DELETE CASCADE,
	FOREIGN KEY ("message_typ_id") REFERENCES "gdi_message_typ" ("message_typ_id"),
	FOREIGN KEY ("person_id") REFERENCES "gdi_person" ("person_id") ON DELETE CASCADE
);

--------------------------------------------------------
--  Table GDI_LOG_SENSOR
--------------------------------------------------------
CREATE TABLE "gdi_log_sensor" (
	"log_id" SERIAL PRIMARY KEY, 
	"task_id" INTEGER NOT NULL, 
	"result_id" INTEGER NOT NULL, 
	"term" TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	"host" VARCHAR(15) NOT NULL, 
	"duration" INTEGER, 
	"details" VARCHAR(1000),
	FOREIGN KEY ("task_id") REFERENCES "gdi_check_task" ("task_id") ON DELETE CASCADE,
	FOREIGN KEY ("result_id") REFERENCES "gdi_resultcode" ("result_id")
);

CREATE INDEX "gdi_log_sensor_task_idx" ON "gdi_log_sensor" ("task_id");
CREATE INDEX "gdi_log_sensor_task_result_idx" ON "gdi_log_sensor" ("task_id", "result_id");

--------------------------------------------------------
--  Table GDI_LOG_CONTROLLER
--------------------------------------------------------
CREATE TABLE "gdi_log_controller" (
	"log_id" SERIAL PRIMARY KEY, 
	"result_id" INTEGER NOT NULL, 
	"term" TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	"host" VARCHAR(15) NOT NULL, 
	"duration" INTEGER, 
	"details" VARCHAR(1000),
	FOREIGN KEY ("result_id") REFERENCES "gdi_resultcode" ("result_id") 
);

CREATE INDEX "gdi_log_controller_result_idx" ON "gdi_log_controller" ("result_id");

--------------------------------------------------------
--  Table GDI_LOG_MONITOR
--------------------------------------------------------
CREATE TABLE "gdi_log_monitor" (
	"log_id" SERIAL PRIMARY KEY, 
	"result_id" INTEGER NOT NULL, 
	"term" TIMESTAMP WITHOUT TIME ZONE NOT NULL, 
	"host" VARCHAR(15) NOT NULL, 
	"duration" INTEGER, 
	"details" VARCHAR(1000),
	FOREIGN KEY ("result_id") REFERENCES "gdi_resultcode" ("result_id")
);

CREATE INDEX "gdi_log_monitor_result_idx" ON "gdi_log_monitor" ("result_id");

--------------------------------------------------------
--  Table GDI_ACTIONS
--------------------------------------------------------
CREATE TABLE gdi_actions
(
    id integer NOT NULL,
    name character varying(50),
    PRIMARY KEY (id)
);

--------------------------------------------------------
-- SEQUENCES
--------------------------------------------------------
CREATE SEQUENCE "gdi_check_task_seq" INCREMENT BY 1 MINVALUE 1 NO MAXVALUE START WITH 1 CACHE 20  NO CYCLE;
CREATE SEQUENCE "gdi_message_seq" INCREMENT BY 1 MINVALUE 1 NO MAXVALUE START WITH 1 NO CYCLE;
CREATE SEQUENCE "gdi_monitor_seq" INCREMENT BY 1 MINVALUE 1 NO MAXVALUE START WITH 1 CACHE 20  NO CYCLE;
CREATE SEQUENCE "gdi_person_seq" INCREMENT BY 1 MINVALUE 1 NO MAXVALUE START WITH 1 CACHE 20  NO CYCLE;
CREATE SEQUENCE "gdi_sensor_seq" INCREMENT BY 1 MINVALUE 1 NO MAXVALUE START WITH 1 CACHE 20  NO CYCLE;

INSERT INTO DEE_DB_VERSION (name, version, date) VALUES ('MonSuite_init', '@@project.version@@', current_date);
