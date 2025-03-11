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
package de.grit.monsuite.api;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;

import de.grit.monsuite.api.data.Status;
import de.grit.vaadin.common.api.AbstractApiRequestHandler;
import de.grit.vaadin.common.data.JSONUtils;
import de.grit.xgdm.monsuite.UIUtils;
import de.grit.xgdm.monsuite.data.Actions;
import de.grit.xgdm.monsuite.data.SensorStatus;
import io.ebean.Ebean;
import io.ebean.Query;
import io.ebean.RawSql;
import io.ebean.RawSqlBuilder;

public class StatusEndpoint extends AbstractApiRequestHandler {

    private static final long serialVersionUID = 8232182947337682621L;

    private static final Logger LOG = LoggerFactory.getLogger( StatusEndpoint.class );

    public static final String API_V1_BASE = "/v1/status";

    @Override
    public boolean handleApiRequest( VaadinSession session, VaadinRequest req, VaadinResponse res, String pathInfo )
                            throws IOException {
        if ( pathInfo.startsWith( API_V1_BASE ) && "GET".equals( req.getMethod() ) ) {
            try {
                if ( !UIUtils.getProp( "api.secret", "" ).equals( req.getHeader( "token" ) ) ) {
                    LOG.error( "send token: {}", req.getHeader( "token" ) );
                    res.sendError( 401, "No authorization!" );
                }

                Status s = new Status();
                if ( pathInfo.length() <= API_V1_BASE.length() + 1 ) {
                    LOG.debug( "get status of the API" );
                    // without jobId - state of the API - check database
                    s.setHealthy( true );
                    try {
                        Ebean.find( Actions.class ).findCount();
                        LOG.info( "Database available" );
                        s.setDatabase( true );
                    } catch ( Throwable e ) {
                        LOG.error( "Database not available" );
                        // no database available
                        s.setDatabase( false );
                    }
                    JSONUtils.writeList( res.getWriter(), Arrays.asList( s ), null, "api", true );
                } else {
                    int potId = -1;
                    boolean validRequest = false;

                    try {
                        potId = Integer.parseInt( pathInfo.substring( API_V1_BASE.length() + 1 ) );
                        LOG.debug( "get status of the given JOB ({})", potId );
                        validRequest = true;
                    } catch ( Exception e ) {
                        // Keine valide Übergabe
                        if ( pathInfo.startsWith( API_V1_BASE + "/checktasks" ) )
                            validRequest = true;
                    }

                    if ( validRequest ) {

                        RawSql rawSql = RawSqlBuilder.parse( SensorStatus.getSqlSensorstatus() ).columnMappingIgnore( "SORT_ID" ).create();

                        Query<SensorStatus> query = Ebean.find( SensorStatus.class );
                        List<SensorStatus> tmp = query.setRawSql( rawSql ).findList();

                        if ( potId >= 0 ) {
                            try {
                                int jobId = potId;
                                tmp = tmp.stream().filter( a -> {
                                    return jobId == a.getTaskId();
                                } ).collect( Collectors.toList() );

                            } catch ( NullPointerException e ) {
                                LOG.error( "ID {} nicht vorhanden", potId );
                                res.sendError( 404, potId + " not available" );
                                return true;
                            }
                        }
                        // else {
                        List<Status> jobStates = new ArrayList<>();
                        for ( SensorStatus seSt : tmp ) {
                            s = new Status();
                            if ( potId < 0 ) {
                                s.setId( seSt.getTaskId() );
                                s.setTitle( seSt.getCheckTaskName() );
                            }
                            s.setState( seSt.getStatus() );
                            s.setActive( seSt.getTimeframe() );
                            s.setLastResult( seSt.getResultName() );
                            s.setResultDetails( seSt.getDetails() );
                            s.setMsgTime( seSt.getTerm() != null ? seSt.getTerm().format( ISO_DATE_TIME ) : "" );
                            s.setDuration( seSt.getDuration() );
                            jobStates.add( s );
                        }

                        // JSONUtils.writeList( res.getWriter(), Arrays.asList( s ), null, "api", true );
                        res.setContentType( "application/json; charset=utf-8" );
                        JSONUtils.writeList( res.getWriter(), jobStates, null, "api", potId >= 0 );
                        // }
                    } else {
                        return false;
                    }
                }

                return true;
            } catch ( IndexOutOfBoundsException e ) {
                LOG.error( "IndexOutOfBounds: {}", e.getLocalizedMessage() );
                return true;
            } catch ( NumberFormatException nfe ) {
                LOG.error( "NumberFormatException: {}", nfe.getLocalizedMessage() );
                res.sendError( 400, pathInfo );
                return true;
            } catch ( Exception e ) {
                LOG.error( "Fehler im Status Endpunkt: {}", e.getLocalizedMessage() );
                return true;
            }
        }
        return false;
    }

}
