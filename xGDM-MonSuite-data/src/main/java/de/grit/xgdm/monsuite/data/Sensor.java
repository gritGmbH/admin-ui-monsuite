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
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import io.ebean.Ebean;
import io.ebean.annotation.JsonIgnore;

@Entity
@Table(name = "GDI_SENSOR")
public class Sensor implements Serializable, Cloneable {

    private static final long serialVersionUID = -2046821126686841074L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GDI_SENSOR_seq_gen")
    @SequenceGenerator(name = "GDI_SENSOR_seq_gen", sequenceName = "GDI_SENSOR_SEQ")
    @Column(name = "SENSOR_ID")
    @JsonIgnore
    private Long id;

    @JsonIgnore
    private String name;

    @OneToOne(mappedBy = "sensor", fetch = FetchType.LAZY)
    private CheckTask checktask;

    @Column(name = "SENSOR_TYP_ID")
    private String type;

    @Column(name = "DAEMON_ID")
    @JsonIgnore
    private Integer daemonId;

    @Version
    @JsonIgnore
    private Long version;

    public SensorTyp getSensorTyp() {
        return Ebean.find( SensorTyp.class, type );
    }

    public Monitor getMonitor() {
        return Ebean.find( Monitor.class, daemonId );
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "Sensor [id=" );
        builder.append( id );
        builder.append( ", name=" );
        builder.append( name );
        builder.append( ", checktask=" );
        builder.append( checktask );
        builder.append( ", type=" );
        builder.append( type );
        builder.append( ", daemonId=" );
        builder.append( daemonId );
        builder.append( ", config=" );
        builder.append( config );
        builder.append( ", version=" );
        builder.append( version );
        builder.append( "]" );
        return builder.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash( getChecktask() != null ? getChecktask().getId() : null, // only id
                             getConfig(), daemonId, id, name, type );
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( !( obj instanceof Sensor ) ) {
            return false;
        }
        Sensor other = (Sensor) obj;

        if ( Objects.equals( config, other.config ) && // all other property
             Objects.equals( daemonId, other.daemonId ) && //
             Objects.equals( id, other.id ) && //
             Objects.equals( name, other.name ) && //
             Objects.equals( getChecktask() != null ? getChecktask().getId() : null,
                             other.getChecktask() != null ? other.getChecktask().getId() : null ) )
            return true;
        else
            return false;
    }

    public Sensor clone() {
        Sensor s = new Sensor();

        s.setName( this.getName() );
        s.setType( this.getType() );
        s.setDaemonId( this.getDaemonId() );
        s.setConfig( this.getConfig().clone() );

        return s;
    }

    public void update( Sensor update ) {
        if ( update.getName() != null )
            this.setName( update.getName() );
        if ( update.getType() != null )
            this.setType( update.getType() );
        if ( update.getDaemonId() != null )
            this.setDaemonId( update.getDaemonId() );
        if ( update.getConfig() != null )
            this.getConfig().update( update.getConfig() );
    }

    @Embedded
    private SensorConfig config;

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

    public String getType() {
        return type;
    }

    public void setType( String type ) {
        this.type = type;
    }

    public Integer getDaemonId() {
        return daemonId;
    }

    public void setDaemonId( int daemonId ) {
        this.daemonId = Integer.valueOf( daemonId );
    }

    public CheckTask getChecktask() {
        return checktask;
    }

    public void setChecktask( CheckTask checktask ) {
        this.checktask = checktask;
    }

    public SensorConfig getConfig() {
        return config;
    }

    public void setConfig( SensorConfig config ) {
        this.config = config;
    }

    public Long getVersion() {
        return version;
    }
}
