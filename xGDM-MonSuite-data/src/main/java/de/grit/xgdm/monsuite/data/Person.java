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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import de.grit.vaadin.common.interfaces.ObjectWithId;

@Entity
@Table(name = "GDI_PERSON")
public class Person implements ObjectWithId<Long>, Serializable {

    private static final long serialVersionUID = 2600244131621671935L;

    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GDI_PERSON_seq_gen")
    @SequenceGenerator(name = "GDI_PERSON_seq_gen", sequenceName = "GDI_PERSON_SEQ")
    @Column(name = "PERSON_ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "MAIL")
    private String mail;

    @Column(name = "SNMP")
    private String snmp;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "person")
    private List<Notification> notifications;

    public String displayString() {
        StringBuilder sb = new StringBuilder();
        boolean isMail = mail != null && mail.trim().length() > 0;
        boolean isSnmp = snmp != null && snmp.trim().length() > 0;

        sb.append( name );

        if ( isMail && isSnmp )
            sb.append( " (" ).append( mail ).append( " / SNMP: " ).append( snmp ).append( ")" );
        else if ( isMail )
            sb.append( " (" ).append( mail ).append( ")" );
        else if ( isSnmp )
            sb.append( " (SNMP: " ).append( snmp ).append( ")" );

        return sb.toString();
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

    public String getMail() {
        return mail;
    }

    public void setMail( String mail ) {
        this.mail = mail;
    }

    public String getSnmp() {
        return snmp;
    }

    public void setSnmp( String snmp ) {
        this.snmp = snmp;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications( List<Notification> notifications ) {
        this.notifications = notifications;
    }

}
