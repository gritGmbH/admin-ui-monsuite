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

import static java.lang.Boolean.valueOf;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import de.grit.vaadin.common.interfaces.ObjectWithId;
import io.ebean.annotation.JsonIgnore;

@Entity
@Table(name = "GDI_CHECK_TASK")
public class CheckTask implements ObjectWithId<Long>, Serializable, Cloneable {

    private static final long serialVersionUID = 1739378652842379343L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GDI_CHECK_seq_gen")
    @SequenceGenerator(name = "GDI_CHECK_seq_gen", sequenceName = "GDI_CHECK_TASK_SEQ")
    @Column(name = "TASK_ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "START_DATE")
    private LocalDate dateStart;

    @Column(name = "END_DATE")
    private LocalDate dateEnd;

    @Column(name = "START_TIME")
    private String timeStart;

    @Column(name = "END_TIME")
    private String timeEnd;

    @Column(name = "TIMEOUT")
    private Integer timeout;

    @Column(name = "PAUSE")
    private Integer pause;

    @Column(name = "CHECKDAY_0")
    private Boolean checkday0Sun;

    @Column(name = "CHECKDAY_1")
    private Boolean checkday1Mon;

    @Column(name = "CHECKDAY_2")
    private Boolean checkday2Tue;

    @Column(name = "CHECKDAY_3")
    private Boolean checkday3Wed;

    @Column(name = "CHECKDAY_4")
    private Boolean checkday4Thu;

    @Column(name = "CHECKDAY_5")
    private Boolean checkday5Fri;

    @Column(name = "CHECKDAY_6")
    private Boolean checkday6Sat;

    @Transient
    @JsonIgnore
    private String checkday;

    // TRICKY lazy loading makes problem in vaadin table -> fetch it in query
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "SENSOR_ID", nullable = false)
    private Sensor sensor;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "task")
    private List<Notification> notifications;

    @Version
    @JsonIgnore
    private Long version;

    private static int timeToMinutes( String str_time ) {
        int res = -1;

        if ( str_time == null || str_time.isEmpty() ) {
            return res;
        }

        int pos = str_time.indexOf( ":" );

        try {
            if ( pos >= 0 ) {
                int std = Integer.parseInt( str_time.substring( 0, pos ) );
                int min = Integer.parseInt( str_time.substring( pos + 1 ) );
                res = ( std * 60 ) + min;
            } else {
                res = Integer.parseInt( str_time );
            }
        } catch ( Exception ign ) {
            // ignored
        }

        return res;
    }

    public String checkDayToString() {
        StringBuilder sb = new StringBuilder();
        boolean[] chk = new boolean[] { isCheckday0Sun(), isCheckday1Mon(), isCheckday2Tue(), isCheckday3Wed(),
                                        isCheckday4Thu(), isCheckday5Fri(), isCheckday6Sat(), isCheckday0Sun() };
        String[] txts = new String[] { "So", "Mo", "Di", "Mi", "Do", "Fr", "Sa", "So" };
        if ( chk[1] && chk[2] && chk[3] && chk[4] && chk[5] && chk[6] && chk[0] ) {
            return "Mo - So";
        } else if ( chk[1] && chk[2] && chk[3] && chk[4] && chk[5] ) {
            if ( chk[6] ) {
                return "Mo - Sa";
            } else if ( chk[0] ) {
                return "Mo - Fr, So";
            } else {
                return "Mo - Fr";
            }
        }

        for ( int i = 1; i < chk.length; i++ ) {
            if ( chk[i] ) {
                if ( sb.length() > 0 )
                    sb.append( ", " );

                sb.append( txts[i] );
            }
        }

        return sb.toString();
    }

    /**
     * Checks if a checktask is valid (activated)
     * 
     * @return
     */
    public boolean isValid() {
        if ( !isCheckday0Sun() && !isCheckday1Mon() && !isCheckday2Tue() && !isCheckday3Wed() && !isCheckday4Thu()
             && !isCheckday5Fri() && !isCheckday6Sat() ) {
            // if no weekday is set, the job cannot run
            return false;
        }

        if ( getDateEnd() != null && getDateStart() != null ) {
            // if ( getDateEnd().getTime() < getDateStart().getTime() ) {
            if ( getDateEnd().isAfter( getDateStart() ) ) {
                // the job ends before it starts
                return false;
            }
        }

        if ( getTimeStart() != null && getTimeEnd() != null ) {
            int start = timeToMinutes( getTimeStart() );
            int end = timeToMinutes( getTimeEnd() );
            if ( start > end ) {
                return false;
            }
        }

        if ( getDateEnd() != null ) {
            // Date now = new Date();
            if ( LocalDateTime.now().isAfter( getDateEndBeforeMidnight() ) ) {
                // job ends in the past
                return false;
            }
        }

        return true;
    }

    public int getTimeStartMinutes() {
        return timeToMinutes( getTimeStart() );
    }

    public int getTimeEndMinutes() {
        return timeToMinutes( getTimeEnd() );
    }

    public LocalDateTime getDateStartAfterMidnight() {
        if ( getDateStart() == null )
            return null;

        return getDateStart().atStartOfDay();

        // Calendar cal = Calendar.getInstance();
        // cal.setTime( getDateStart() );
        // cal.set( Calendar.HOUR_OF_DAY, 0 );
        // cal.set( Calendar.MINUTE, 0 );
        // cal.set( Calendar.SECOND, 0 );
        // return cal.getTime();
    }

    public LocalDateTime getDateEndBeforeMidnight() {
        if ( getDateEnd() == null )
            return null;

        return getDateStart().atTime( 23, 59, 59 );

        // Calendar cal = Calendar.getInstance();
        // cal.setTime( getDateEnd() );
        // cal.set( Calendar.HOUR_OF_DAY, 23 );
        // cal.set( Calendar.MINUTE, 59 );
        // cal.set( Calendar.SECOND, 59 );
        // return cal.getTime();
    }

    @Override
    public int hashCode() {
        return Objects.hash( checkday0Sun, checkday1Mon, checkday2Tue, checkday3Wed, //
                             checkday4Thu, checkday5Fri, checkday6Sat, dateEnd, dateStart, //
                             id, name, notifications, pause, //
                             ( getSensor() != null ? getSensor().getId() : null ), //
                             timeEnd, timeStart, timeout );
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( !( obj instanceof CheckTask ) ) {
            return false;
        }
        CheckTask other = (CheckTask) obj;

        if ( Objects.equals( version, other.version ) && // first check version for match
             Objects.equals( checkday0Sun, other.checkday0Sun ) && // all other property by a-z
             Objects.equals( checkday1Mon, other.checkday1Mon ) && //
             Objects.equals( checkday2Tue, other.checkday2Tue ) && //
             Objects.equals( checkday3Wed, other.checkday3Wed ) && //
             Objects.equals( checkday4Thu, other.checkday4Thu ) && //
             Objects.equals( checkday5Fri, other.checkday5Fri ) && //
             Objects.equals( checkday6Sat, other.checkday6Sat ) && //
             Objects.equals( dateEnd, other.dateEnd ) && //
             Objects.equals( dateStart, other.dateStart ) && //
             Objects.equals( id, other.id ) && //
             Objects.equals( name, other.name ) && //
             Objects.equals( getNotifications(), other.getNotifications() ) && //
             Objects.equals( pause, other.pause ) && //
             Objects.equals( timeEnd, other.timeEnd ) && //
             Objects.equals( timeStart, other.timeStart ) && //
             Objects.equals( timeout, other.timeout ) && //
             Objects.equals( ( getSensor() != null ? getSensor().getId() : null ),
                             ( other.getSensor() != null ? other.getSensor().getId() : null ) ) )
            return true;
        else
            return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "CheckTask [id=" );
        builder.append( id );
        builder.append( ", name=" );
        builder.append( name );
        builder.append( ", dateStart=" );
        builder.append( dateStart );
        builder.append( ", dateEnd=" );
        builder.append( dateEnd );
        builder.append( ", timeStart=" );
        builder.append( timeStart );
        builder.append( ", timeEnd=" );
        builder.append( timeEnd );
        builder.append( ", timeout=" );
        builder.append( timeout );
        builder.append( ", pause=" );
        builder.append( pause );
        builder.append( ", checkday0Sun=" );
        builder.append( checkday0Sun );
        builder.append( ", checkday1Mon=" );
        builder.append( checkday1Mon );
        builder.append( ", checkday2Tue=" );
        builder.append( checkday2Tue );
        builder.append( ", checkday3Wed=" );
        builder.append( checkday3Wed );
        builder.append( ", checkday4Thu=" );
        builder.append( checkday4Thu );
        builder.append( ", checkday5Fri=" );
        builder.append( checkday5Fri );
        builder.append( ", checkday6Sat=" );
        builder.append( checkday6Sat );
        builder.append( ", sensor=" );
        builder.append( sensor );
        builder.append( ", notifications=" );
        builder.append( notifications );
        builder.append( ", version=" );
        builder.append( version );
        builder.append( "]" );
        return builder.toString();
    }

    @Override
    public CheckTask clone() {
        CheckTask ct = new CheckTask();

        ct.setCheckday0Sun( this.isCheckday0Sun() );
        ct.setCheckday1Mon( this.isCheckday1Mon() );
        ct.setCheckday2Tue( this.isCheckday2Tue() );
        ct.setCheckday3Wed( this.isCheckday3Wed() );
        ct.setCheckday4Thu( this.isCheckday4Thu() );
        ct.setCheckday5Fri( this.isCheckday5Fri() );
        ct.setCheckday6Sat( this.isCheckday6Sat() );

        ct.setDateStart( this.getDateStart() );
        ct.setDateEnd( this.getDateEnd() );

        ct.setName( this.getName() );
        ct.setNotifications( this.getNotifications() );
        ct.setPause( this.getPause() );

        ct.setSensor( this.getSensor().clone() );

        ct.setTimeStart( this.getTimeStart() );
        ct.setTimeEnd( this.getTimeEnd() );

        ct.setTimeout( this.getTimeout() );

        return ct;
    }

    public void update( CheckTask update ) {
        if ( update.getName() != null )
            this.setName( update.getName() );

        if ( update.isCheckday0Sun() != null )
            this.setCheckday0Sun( update.isCheckday0Sun() );
        if ( update.isCheckday1Mon() != null )
            this.setCheckday1Mon( update.isCheckday1Mon() );
        if ( update.isCheckday2Tue() != null )
            this.setCheckday2Tue( update.isCheckday2Tue() );
        if ( update.isCheckday3Wed() != null )
            this.setCheckday3Wed( update.isCheckday3Wed() );
        if ( update.isCheckday4Thu() != null )
            this.setCheckday4Thu( update.isCheckday4Thu() );
        if ( update.isCheckday5Fri() != null )
            this.setCheckday5Fri( update.isCheckday5Fri() );
        if ( update.isCheckday6Sat() != null )
            this.setCheckday6Sat( update.isCheckday6Sat() );

        if ( update.getDateStart() != null )
            this.setDateStart( update.getDateStart() );
        if ( update.getDateEnd() != null )
            this.setDateEnd( update.getDateEnd() );

        if ( update.getTimeStart() != null )
            this.setTimeStart( update.getTimeStart() );
        if ( update.getTimeEnd() != null )
            this.setTimeEnd( update.getTimeEnd() );

        if ( update.getNotifications() != null && update.getNotifications().size() > 0 )
            this.setNotifications( update.getNotifications() );

        if ( update.getPause() != null )
            this.setPause( update.getPause() );
        if ( update.getTimeout() != null )
            this.setTimeout( update.getTimeout() );

        if ( update.getSensor() != null )
            this.getSensor().update( update.getSensor() );
    }

    public void setCheckday( String checkday ) {
        this.checkday = checkday;
        checkday0Sun = checkday.toLowerCase().indexOf( "so" ) > -1;
        checkday1Mon = checkday.toLowerCase().indexOf( "mo" ) > -1;
        checkday2Tue = checkday.toLowerCase().indexOf( "di" ) > -1;
        checkday3Wed = checkday.toLowerCase().indexOf( "mi" ) > -1;
        checkday4Thu = checkday.toLowerCase().indexOf( "do" ) > -1;
        checkday5Fri = checkday.toLowerCase().indexOf( "fr" ) > -1;
        checkday6Sat = checkday.toLowerCase().indexOf( "sa" ) > -1;
    }

    /* Autogenerated */

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor( Sensor sensor ) {
        this.sensor = sensor;
    }

    public LocalDate getDateStart() {
        return dateStart;
    }

    public void setDateStart( LocalDate dateStart ) {
        this.dateStart = dateStart;
    }

    public LocalDate getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd( LocalDate dateEnd ) {
        this.dateEnd = dateEnd;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart( String timeStart ) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd( String timeEnd ) {
        this.timeEnd = timeEnd;
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

    public Boolean isCheckday0Sun() {
        return checkday0Sun;
    }

    public void setCheckday0Sun( boolean checkday0Sun ) {
        this.checkday0Sun = valueOf( checkday0Sun );
    }

    public Boolean isCheckday1Mon() {
        return checkday1Mon;
    }

    public void setCheckday1Mon( boolean checkday1Mon ) {
        this.checkday1Mon = valueOf( checkday1Mon );
    }

    public Boolean isCheckday2Tue() {
        return checkday2Tue;
    }

    public void setCheckday2Tue( boolean checkday2Tue ) {
        this.checkday2Tue = valueOf( checkday2Tue );
    }

    public Boolean isCheckday3Wed() {
        return checkday3Wed;
    }

    public void setCheckday3Wed( boolean checkday3Wed ) {
        this.checkday3Wed = valueOf( checkday3Wed );
    }

    public Boolean isCheckday4Thu() {
        return checkday4Thu;
    }

    public void setCheckday4Thu( boolean checkday4Thu ) {
        this.checkday4Thu = valueOf( checkday4Thu );
    }

    public Boolean isCheckday5Fri() {
        return checkday5Fri;
    }

    public void setCheckday5Fri( boolean checkday5Fri ) {
        this.checkday5Fri = valueOf( checkday5Fri );
    }

    public Boolean isCheckday6Sat() {
        return checkday6Sat;
    }

    public void setCheckday6Sat( boolean checkday6Sat ) {
        this.checkday6Sat = valueOf( checkday6Sat );
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications( List<Notification> notifications ) {
        this.notifications = notifications;
    }

    public Long getVersion() {
        return version;
    }

    public String getCheckday() {
        return checkday;
    }
}
