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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="TST_TASK_CONFIG")
public class TstTaskConfig implements Serializable {
    //, Map.Entry<String, TstTaskConfig> {
    //
    private static final long serialVersionUID = -3683954622296434947L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TST_TASK_CONFIG_gen")
    @SequenceGenerator(name = "TST_TASK_CONFIG_gen", sequenceName = "TST_TASK_SEQ")
    @Column(name = "TASK_CONFIG_ID")
    private Long id;
    
    @ManyToOne()
    @JoinColumn(name = "TASK_ID")
    private TstTask task;
    
    @Column(name="key", length=255)
    private String name;
    
    @Column(name="val", length=2000)
    private String val;

    public TstTaskConfig() {
        
    }
                         
    public TstTaskConfig( TstTask task, String name, String val) {
        setTask( task );
        setName( name );
        setVal( val );
    }

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public TstTask getTask() {
        return task;
    }

    public void setTask( TstTask task ) {
        this.task = task;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getVal() {
        return val;
    }

    public void setVal( String val ) {
        this.val = val;
    }

    
//    //TRICKY Work around ebean bug
//    @Override
//    public String getKey() {
//        return getName();
//    }
//
//    //TRICKY Work around ebean bug
//    @Override
//    public TstTaskConfig getValue() {
//        return this;
//    }
//
//    //TRICKY Work around ebean bug
//    @Override
//    public TstTaskConfig setValue( TstTaskConfig value ) {
//        if ( this != value )
//            throw new IllegalArgumentException();
//            
//        return this;
//    }
}
