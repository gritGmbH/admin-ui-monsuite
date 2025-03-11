/*-
 * #%L
 * xGDM-MonSuite GUI API
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
package de.grit.monsuite.api.data;

import java.io.Serializable;

import javax.persistence.Entity;

@Entity
public class Status implements Serializable {

    private static final long serialVersionUID = -5487174074311583725L;

    private long id;

    private String title;

    // state of the API
    private boolean healthy;

    private String msg;

    private String hint;

    private boolean database;

    // with jobid
    private boolean state;

    private boolean active;

    private String lastResult;

    private String resultDetails;

    private String msgTime;

    private long duration;

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public void setHealthy( boolean healthy ) {
        this.healthy = healthy;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg( String msg ) {
        this.msg = msg;
    }

    public String getHint() {
        return hint;
    }

    public void setHint( String hint ) {
        this.hint = hint;
    }

    public boolean getDatabase() {
        return database;
    }

    public void setDatabase( boolean database ) {
        this.database = database;
    }

    public boolean getState() {
        return state;
    }

    public void setState( String state ) {
        // TODO Mapping
        // this.state = state;
        this.state = "res_ok".equals( state );
    }

    public boolean getActive() {
        return active;
    }

    public void setActive( String timeframe ) {
        this.active = "intf".equals( timeframe );
    }

    public String getLastResult() {
        return lastResult;
    }

    public void setLastResult( String lastResult ) {
        this.lastResult = lastResult;
    }

    public String getResultDetails() {
        return resultDetails;
    }

    public void setResultDetails( String resultDetails ) {
        this.resultDetails = resultDetails;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime( String msgTime ) {
        this.msgTime = msgTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration( long duration ) {
        this.duration = duration;
    }

}
