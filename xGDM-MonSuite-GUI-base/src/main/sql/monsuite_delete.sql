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
DROP TABLE gdi_monitor CASCADE;
DROP TABLE gdi_sensor_typ CASCADE;
DROP TABLE gdi_person CASCADE;
DROP TABLE gdi_message_typ CASCADE;
DROP TABLE gdi_resultcode CASCADE;
DROP TABLE gdi_sensor CASCADE;
DROP TABLE gdi_check_task CASCADE;
DROP TABLE gdi_message CASCADE;
DROP TABLE gdi_log_sensor CASCADE;
DROP TABLE gdi_log_controller CASCADE;
DROP TABLE gdi_log_monitor CASCADE;

DROP SEQUENCE gdi_check_task_seq CASCADE;
DROP SEQUENCE gdi_message_seq CASCADE;
DROP SEQUENCE gdi_monitor_seq CASCADE;
DROP SEQUENCE gdi_person_seq CASCADE;
DROP SEQUENCE gdi_sensor_seq CASCADE;
