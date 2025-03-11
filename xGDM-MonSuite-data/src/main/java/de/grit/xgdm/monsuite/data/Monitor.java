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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Monitor / Daemon
 * <p>
 * wird zurzeit noch nicht genutzt
 * 
 * @author <a href="mailto:reichhelm@grit.de">Stephan Reichhelm</a>
 */
@Entity
@Table(name = "GDI_MONITOR")
public class Monitor implements Serializable {
    private static final long serialVersionUID = -8803630021859034826L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GDI_MONITOR_seq_gen")
    @SequenceGenerator(name = "GDI_MONITOR_seq_gen", sequenceName = "GDI_MONITOR_SEQ")
    @Column(name = "DAEMON_ID", insertable = false, updatable = false, nullable = false)
    private Long id;

    @Column(name = "NAME", insertable = false, updatable = false, nullable = false)
    private String name;

    @Column(name = "HOST", insertable = false, updatable = false, nullable = false)
    private String host;

    @Column(name = "STATUS", insertable = false, updatable = false)
    private boolean enabled;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( "Monitor [id=" );
        builder.append( id );
        builder.append( ", name=" );
        builder.append( name );
        builder.append( ", host=" );
        builder.append( host );
        builder.append( ", enabled=" );
        builder.append( enabled );
        builder.append( "]" );
        return builder.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash( enabled, host, id, name );
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( !( obj instanceof Monitor ) ) {
            return false;
        }
        Monitor other = (Monitor) obj;
        if ( Objects.equals( enabled, other.enabled ) && // first check version for match
             Objects.equals( host, other.host ) && //
             Objects.equals( id, other.id ) && //
             Objects.equals( name, other.name ) //
        )
            return true;
        else
            return false;
    }

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

    public String getHost() {
        return host;
    }

    public void setHost( String host ) {
        this.host = host;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled( boolean enabled ) {
        this.enabled = enabled;
    }
}
