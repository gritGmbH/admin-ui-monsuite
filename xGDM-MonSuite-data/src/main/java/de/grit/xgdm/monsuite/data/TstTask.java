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
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import io.ebean.annotation.CreatedTimestamp;
import io.ebean.annotation.UpdatedTimestamp;

@Entity
@Table(name="TST_TASK")
public class TstTask implements Serializable {
    
    private static final long serialVersionUID = 4432178954965398070L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TST_TASK_SEQ_gen")
    @SequenceGenerator(name = "TST_TASK_SEQ_gen", sequenceName = "TST_TASK_SEQ")
    @Column(name="TASK_ID")
    private Long id;
    
    @Column( name="NAME", length=500)
    private String name;
    
    @Column( name="DATE_START")
    private Date dateStart;
    
    @Column( name="DATE_END")
    private Date dateEnd;
    
    @Column( name="TIME_START", length=5)
    private String timeStart;
    
    @Column( name="TIME_END", length=5)
    private String timeEnd;
    
    @Column( name="TIMEOUT")
    private Integer timeout;
    
    @Column( name="PAUSE")
    private Integer pause;
    
    @Column( name="CHECKDAY", length=7)
    private String checkday;
    
    //private List<TstTaskConfig> config;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "task")
    @MapKey(name = "name")
    private Map<String, TstTaskConfig> config = new HashMap<String, TstTaskConfig>();
    
    @CreatedTimestamp
    @Column( name="create_at")
    private Timestamp created;
    
    @UpdatedTimestamp
    @Column( name="update_at")
    private Timestamp updated;
    
    @Version
    @Column( name="VERSION" )
    private long version;

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

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart( Date dateStart ) {
        this.dateStart = dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd( Date dateEnd ) {
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

    public String getCheckday() {
        return checkday;
    }

    public void setCheckday( String checkday ) {
        this.checkday = checkday;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated( Timestamp created ) {
        this.created = created;
    }

    public Timestamp getUpdated() {
        return updated;
    }

    public void setUpdated( Timestamp updated ) {
        this.updated = updated;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion( long version ) {
        this.version = version;
    }

    public Map<String, TstTaskConfig> getConfig() {
        return config;
    }

    public void setConfig( Map<String, TstTaskConfig> config ) {
        this.config = config;
    }
}
