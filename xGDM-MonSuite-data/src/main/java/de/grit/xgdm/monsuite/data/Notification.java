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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Benachrichtigungszuordnung
 * <p>
 * Datenbank Primaerschluessel wird aus den drei Spalten gebildet; wird so in Ebean nicht dargestellt
 * 
 * @author gerling
 *
 */
@Entity
@Table(name = "GDI_MESSAGE")
public class Notification implements Serializable {

    private static final long serialVersionUID = 2498354412957468866L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Notification_gen")
    @SequenceGenerator(name = "Notification_gen", sequenceName = "GDI_MESSAGE_SEQ")
    @Column(name = "ID")
    private Long id;

    @ManyToOne(optional = true)
    @JoinColumn(name = "TASK_ID")
    private CheckTask task;

    @ManyToOne(optional = true)
    @JoinColumn(name = "PERSON_ID")
    private Person person;

    @Column(name = "MESSAGE_TYP_ID")
    private Integer type;

    /**
     * Custom hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash( ( getPerson() != null ? getPerson().getId() : null ), //
                             ( getTask() != null ? getTask().getId() : null ), //
                             type );
    }

    /**
     * Custom equals
     */
    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( !( obj instanceof Notification ) ) {
            return false;
        }
        Notification other = (Notification) obj;

        if ( Objects.equals( getPerson() != null ? getPerson().getId() : null,
                             other.getPerson() != null ? other.getPerson().getId() : null )
             && Objects.equals( getTask() != null ? getTask().getId() : null,
                                other.getTask() != null ? other.getTask().getId() : null )
             && Objects.equals( type, other.type ) )
            return true;
        else
            return false;
    }

    public CheckTask getTask() {
        return task;
    }

    public void setTask( CheckTask task ) {
        this.task = task;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson( Person person ) {
        this.person = person;
    }

    public Integer getType() {
        return type;
    }

    public void setType( Integer type ) {
        this.type = type;
    }
}
