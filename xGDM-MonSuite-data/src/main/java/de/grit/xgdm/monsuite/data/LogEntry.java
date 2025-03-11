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
import java.time.LocalDateTime;

import javax.persistence.Entity;

import io.ebean.annotation.Sql;

@Entity
@Sql
public class LogEntry implements Serializable {

    private static final long serialVersionUID = 8461022029218965788L;

    public static final String SQL_QUERY = "sql.static.logentry";

    private long id;

    private long taskId;

    private String taskName;

    private String taskType;

    private String taskTypeName;

    private LocalDateTime term;

    private int resultId;

    private String resultName;

    private String host;

    private Double duration;

    private String details;

    /* Auto generated */

    public LocalDateTime getTerm() {
        return term;
    }

    public void setTerm( LocalDateTime term ) {
        this.term = term;
    }

    public int getResultId() {
        return resultId;
    }

    public void setResultId( int resultId ) {
        this.resultId = resultId;
    }

    public String getHost() {
        return host;
    }

    public void setHost( String host ) {
        this.host = host;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration( Double duration ) {
        this.duration = duration;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails( String details ) {
        this.details = details;
    }

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId( long taskId ) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName( String taskName ) {
        this.taskName = taskName;
    }

    public String getResultName() {
        return resultName;
    }

    public void setResultName( String resultName ) {
        this.resultName = resultName;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType( String taskType ) {
        this.taskType = taskType;
    }

    public String getTaskTypeName() {
        return taskTypeName;
    }

    public void setTaskTypeName( String taskTypeName ) {
        this.taskTypeName = taskTypeName;
    }
}
