/*-
 * #%L
 * xGDM-MonSuite (Data Binding)
 * %%
 * Copyright (C) 2022 - 2025 grit GmbH
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package de.grit.xgdm.monsuite.data;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import de.grit.vaadin.common.EbeanUtilsCommon;
import io.ebean.annotation.Sql;

@Entity
@Sql
public class SensorStatus implements Serializable {

    private static final long serialVersionUID = 6264431690510535933L;

    // private static final Logger LOG = LoggerFactory.getLogger( SensorStatus.class );

    public static final String TIME_OUT_OF_TIMEFRAME = "outtf";

    public static final String TIME_IN_TIMEFRAME = "intf";

    public static final String RES_TIME_WARN = "time_warn";

    public static final String RES_TIME_ERROR = "time_err";

    public static final String RES_RESULTCODE_OK = "res_ok";

    public static final String RES_RESULTCODE_ERROR = "res_err";

    public static final String RES_UNKNOWN = "res_unknown";

    public static final String RES_TRANSPARENT = "trans";

    private static String sql_sensorstatus = null;

    private Integer taskId;

    private Integer resultId;

    private LocalDateTime term;

    private Long duration;

    private String details;

    private String host;

    private String resultName;

    private String checkTaskName;

    private Date startDate;

    private Date endDate;

    private String startTime;

    private String endTime;

    private Integer timeout;

    private Integer pause;

    private boolean checkday0;

    private boolean checkday1;

    private boolean checkday2;

    private boolean checkday3;

    private boolean checkday4;

    private boolean checkday5;

    private boolean checkday6;

    private String sensorName;

    private String sensorTypId;

    private String sensorTypName;

    private String monitorName;

    private boolean monitorStatus;

    private LocalDateTime sysdate;

    @Transient
    private boolean _changed = true;

    @Transient
    private String _timeframe;

    @Transient
    private String _status;

    /*
     * ToString
     */
    public String toString() {
        StringBuilder sb = new StringBuilder( 200 );
        sb.append( "(taskId=" ).append( taskId );
        sb.append( ",resultId=" ).append( resultId );
        sb.append( ",term=" ).append( term );
        sb.append( ",duration=" ).append( duration );
        sb.append( ",details=" ).append( details );
        sb.append( ",host=" ).append( host );
        sb.append( ",resultName=" ).append( resultName );
        sb.append( ",checkTaskName=" ).append( checkTaskName );
        sb.append( ",startDate=" ).append( startDate );
        sb.append( ",endDate=" ).append( endDate );
        sb.append( ",startTime=" ).append( startTime );
        sb.append( ",endTime=" ).append( endTime );
        sb.append( ",timeout=" ).append( timeout );
        sb.append( ",pause=" ).append( pause );
        sb.append( ",checkday_0=" ).append( checkday0 );
        sb.append( ",checkday_1=" ).append( checkday1 );
        sb.append( ",checkday_2=" ).append( checkday2 );
        sb.append( ",checkday_3=" ).append( checkday3 );
        sb.append( ",checkday_4=" ).append( checkday4 );
        sb.append( ",checkday_5=" ).append( checkday5 );
        sb.append( ",checkday_6=" ).append( checkday6 );
        sb.append( ",sensorName=" ).append( sensorName );
        sb.append( ",sensorTypId=" ).append( sensorTypId );
        sb.append( ",sensorTypName=" ).append( sensorTypName );
        sb.append( ",monitorName=" ).append( monitorName );
        sb.append( ",monitorStatus=" ).append( monitorStatus );
        sb.append( ")" );
        return sb.toString();
    }

    /**
     * DOCUMENT_ME
     *
     * @param str_time
     *            DOCUMENT_ME
     *
     * @return DOCUMENT_ME
     */
    private int gritTimeToMinutes( String str_time ) {
        int res = 0;

        // Leere Strings --> 0
        if ( str_time == null || str_time.equals( "" ) ) {
            return res;
        }

        int pos = str_time.indexOf( ":" );

        if ( pos >= 0 ) {
            int std = Integer.parseInt( str_time.substring( 0, pos ) );
            int min = Integer.parseInt( str_time.substring( pos + 1 ) );
            res = ( std * 60 ) + min;
        } else {
            res = Integer.parseInt( str_time );
        }

        return res;
    }

    private LocalDateTime gritODateToDateTime( Date odate ) {
        LocalDateTime res = null;

        if ( odate != null ) {
            res = odate.toInstant().atZone( ZoneId.systemDefault() ).toLocalDateTime();
        }

        return res;
    }

    // private long gritODateToLong( LocalDateTime odate ) {
    // long res = 0;
    //
    // if ( odate != null ) {
    // try {
    // // res = odate.getTime();
    // res = odate.atZone( ZoneId.systemDefault() ).toInstant().toEpochMilli();
    // // .dateValue().getTime();
    // } catch ( Exception ex1 ) {
    // res = -1;
    // }
    // }
    //
    // return res;
    // }

    public String getTimeframe() {
        if ( _changed ) {
            _calculate();
        }

        return _timeframe;
    }

    public String getStatus() {
        if ( _changed ) {
            _calculate();
        }

        return _status;
    }

    private void _calculate() {
        this._timeframe = _getTimeframe();

        if ( TIME_OUT_OF_TIMEFRAME.equals( this._timeframe ) ) {
            this._status = RES_TRANSPARENT;
        } else {
            this._status = _getStatus();
        }

        _changed = false;
    }

    public String _getTimeframe() {
        // return (String)getAttributeInternal(TIMEFRAME);

        LocalDateTime ldtStart = gritODateToDateTime( getStartDate() );
        LocalDateTime ldtEnd = gritODateToDateTime( getEndDate() );
        // LocalDateTime ldtNow = gritODateToDateTime( getSysdate() );
        LocalDateTime ldtNow = getSysdate();

        boolean chk_sun = getCheckday0();
        boolean chk_mon = getCheckday1();
        boolean chk_tue = getCheckday2();
        boolean chk_wed = getCheckday3();
        boolean chk_thu = getCheckday4();
        boolean chk_fri = getCheckday5();
        boolean chk_sat = getCheckday6();

        int stime = gritTimeToMinutes( getStartTime() );
        int etime = gritTimeToMinutes( getEndTime() );

        // System.err.println("DeBuG - zeitraum = " + sdate + " - " + edate);
        // System.err.println("DeBuG - zeitfenster = " + stime + " - " + etime);
        // System.err.println("DeBuG - current date = " + nowdt);
        // System.err.println("DeBuG - messsage term = " + mterm);

        // Hacked
        if ( getMonitorStatus() != true ) {
            return TIME_OUT_OF_TIMEFRAME;
        }

        if ( ldtNow == null ) {
            return TIME_OUT_OF_TIMEFRAME;
        }

        if ( ldtStart != null && ldtNow.toLocalDate().isBefore( ldtStart.toLocalDate() ) ) {
            return TIME_OUT_OF_TIMEFRAME;
        }

        if ( ldtEnd != null && ldtNow.toLocalDate().isAfter( ldtEnd.toLocalDate() ) ) {
            return TIME_OUT_OF_TIMEFRAME;
        }

        int now_min = ldtNow.getHour() * 60 + ldtNow.getMinute();

        if ( now_min < stime ) {
            // Vor Startzeit
            // System.err.println("DeBuG - Vor Startzeit");
            return TIME_OUT_OF_TIMEFRAME;
        }

        if ( ( now_min > etime ) && ( etime != 0 ) ) {
            // Nach Endzeit, wenn Zeit angegeben
            // System.err.println("DeBuG - nach Endzeit, wenn Zeit angegeben");
            return TIME_OUT_OF_TIMEFRAME;
        }

        // Wochentagspruefung
        boolean weekday_ok = false;

        if ( ldtNow.getDayOfWeek() == DayOfWeek.SUNDAY ) {
            weekday_ok = chk_sun;
        } else if ( ldtNow.getDayOfWeek() == DayOfWeek.MONDAY ) {
            weekday_ok = chk_mon;
        } else if ( ldtNow.getDayOfWeek() == DayOfWeek.TUESDAY ) {
            weekday_ok = chk_tue;
        } else if ( ldtNow.getDayOfWeek() == DayOfWeek.WEDNESDAY ) {
            weekday_ok = chk_wed;
        } else if ( ldtNow.getDayOfWeek() == DayOfWeek.THURSDAY ) {
            weekday_ok = chk_thu;
        } else if ( ldtNow.getDayOfWeek() == DayOfWeek.FRIDAY ) {
            weekday_ok = chk_fri;
        } else if ( ldtNow.getDayOfWeek() == DayOfWeek.SATURDAY ) {
            weekday_ok = chk_sat;
        }

        if ( !weekday_ok ) {
            // log.debug("taskInTimeFrame - current weekday not set");
            return TIME_OUT_OF_TIMEFRAME;
        }

        return TIME_IN_TIMEFRAME;
    }

    /**
     *
     * Gets the attribute value for the calculated attribute Status
     */
    public String _getStatus() {
        // return (String)getAttributeInternal(STATUS);

        long rc = getResultId().longValue();
        // long time = getTimeout().longValue();
        // long pause = getPause().longValue();

        String tHost = getHost();
        String tResultName = getResultName();

        if ( tHost != null && tResultName != null && /* mterm == 0 && */tHost.equals( "-" )
             && tResultName.equals( "-" ) ) {
            // kein Meldung eingegangen
            return RES_UNKNOWN;
        }

        // double f = Double.parseDouble( UIUtilsCommon.getProp( "sensorstatus.factor", "0.5" ) );
        // Auswertung
        // double tdiff = getTerm().until( getSysdate(), ChronoUnit.MILLIS );
        // double wtime = ( ( 1 * ( pause + time ) ) + ( time * f ) ) * 1000;
        // double etime = ( ( 2 * ( pause + time ) ) + ( time * f ) ) * 1000;

        // System.err.println("DeBuG TI="+getTaskId()+" - Diff "+tdiff+" WT "+wtime+" ET "+etime);

        if ( rc != 1 ) {
            // Resultcode nicht ok
            return RES_RESULTCODE_ERROR;
            // tricky: calculation is inaccurate, daemon seams to start the next job to late
            // } else if ( tdiff > etime ) {
            // // Letzte Meldung zu alt ==> fehler
            // // last - sys > 2 x (pause + timeout) + (timeout * 0.5)
            // LOG.warn( "Job viel zu lange nicht gelaufen." );
            // LOG.warn( "tdiff: {}", tdiff );
            // LOG.warn( "etime: {}", etime );
            // return RES_TIME_ERROR;
            // } else if ( tdiff > wtime ) {
            // // Letzte Meldung zu alt ==> warnung
            // // last - sys > (pause + timeout) + (timeout * 0.5)
            // LOG.warn( "Job zu lange nicht gelaufen." );
            // LOG.warn( "tdiff: {}", tdiff );
            // LOG.warn( "wtime: {}", wtime );
            // return RES_TIME_WARN;
        } else {
            return RES_RESULTCODE_OK;
        }

        // return "<"+nowdt+"-"+mterm+"-"+rc+">";
    }

    /*
     * Generated
     */

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime( String startTime ) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime( String endTime ) {
        this.endTime = endTime;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId( Integer taskId ) {
        this.taskId = taskId;
    }

    public Integer getResultId() {
        return resultId;
    }

    public void setResultId( Integer resultId ) {
        this.resultId = resultId;
    }

    public LocalDateTime getTerm() {
        return term;
    }

    public void setTerm( LocalDateTime term ) {
        this.term = term;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration( Long duration ) {
        this.duration = duration;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails( String details ) {
        this.details = details;
    }

    public String getHost() {
        return host;
    }

    public void setHost( String host ) {
        this.host = host;
    }

    public String getResultName() {
        return resultName;
    }

    public void setResultName( String resultName ) {
        this.resultName = resultName;
    }

    public String getCheckTaskName() {
        return checkTaskName;
    }

    public void setCheckTaskName( String checkTaskName ) {
        this.checkTaskName = checkTaskName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate( Date startDate ) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate( Date endDate ) {
        this.endDate = endDate;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout( Integer timeout ) {
        this.timeout = timeout;
    }

    public Integer getPause() {
        return pause;
    }

    public void setPause( Integer pause ) {
        this.pause = pause;
    }

    public boolean getCheckday0() {
        return checkday0;
    }

    public void setCheckday0( boolean checkday0 ) {
        this.checkday0 = checkday0;
    }

    public boolean getCheckday1() {
        return checkday1;
    }

    public void setCheckday1( boolean checkday1 ) {
        this.checkday1 = checkday1;
    }

    public boolean getCheckday2() {
        return checkday2;
    }

    public void setCheckday2( boolean checkday2 ) {
        this.checkday2 = checkday2;
    }

    public boolean getCheckday3() {
        return checkday3;
    }

    public void setCheckday3( boolean checkday3 ) {
        this.checkday3 = checkday3;
    }

    public boolean getCheckday4() {
        return checkday4;
    }

    public void setCheckday4( boolean checkday4 ) {
        this.checkday4 = checkday4;
    }

    public boolean getCheckday5() {
        return checkday5;
    }

    public void setCheckday5( boolean checkday5 ) {
        this.checkday5 = checkday5;
    }

    public boolean getCheckday6() {
        return checkday6;
    }

    public void setCheckday6( boolean checkday6 ) {
        this.checkday6 = checkday6;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName( String sensorName ) {
        this.sensorName = sensorName;
    }

    public String getSensorTypId() {
        return sensorTypId;
    }

    public void setSensorTypId( String sensorTypId ) {
        this.sensorTypId = sensorTypId;
    }

    public String getSensorTypName() {
        return sensorTypName;
    }

    public void setSensorTypName( String sensorTypName ) {
        this.sensorTypName = sensorTypName;
    }

    public String getMonitorName() {
        return monitorName;
    }

    public void setMonitorName( String monitorName ) {
        this.monitorName = monitorName;
    }

    public boolean getMonitorStatus() {
        return monitorStatus;
    }

    public void setMonitorStatus( boolean monitorStatus ) {
        this.monitorStatus = monitorStatus;
    }

    public LocalDateTime getSysdate() {
        return sysdate;
    }

    public void setSysdate( LocalDateTime sysdate ) {
        this.sysdate = sysdate;
    }

    public static String getSqlSensorstatus() {
        if ( sql_sensorstatus == null ) {
            sql_sensorstatus = EbeanUtilsCommon.getSQLProp( "sql.static.sensorstatus", "SELECT 1 FROM DUAL" );
        }
        return sql_sensorstatus;
    }

}
