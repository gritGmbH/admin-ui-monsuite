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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;

import de.grit.monsuite.api.json.CheckTaskJsonReader;
import de.grit.vaadin.common.Pair;
import de.grit.vaadin.common.api.AbstractApiRequestHandler;
import de.grit.vaadin.common.data.JSONUtils;
import de.grit.xgdm.monsuite.UIUtils;
import de.grit.xgdm.monsuite.data.CheckTask;
import de.grit.xgdm.monsuite.data.Sensor;
import de.grit.xgdm.monsuite.data.SensorConfig;
import io.ebean.Ebean;
import io.ebean.ExpressionList;

public class CheckTaskEndpoint extends AbstractApiRequestHandler {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger( CheckTaskEndpoint.class );

    public static final String API_V1_BASE = "/v1/checktask";

    private static final String valid = "HTTPCHK,OAFBASIC,SQLORACLE,SQLPG,WFSBASIC,WMSBASIC";

    @SuppressWarnings({ "unchecked" })
    @Override
    public boolean handleApiRequest( VaadinSession session, VaadinRequest req, VaadinResponse res, String pathInfo )
                            throws IOException {
        String pathParam = null;
        Map<String, String[]> reqParams = req.getParameterMap();
        boolean getAllowed = false;
        boolean putAllowed = false;
        boolean deleteAllowed = false;

        int jobId = -1;

        if ( pathInfo.startsWith( API_V1_BASE ) ) {
            if ( pathInfo.length() > API_V1_BASE.length() )
                pathParam = pathInfo.substring( API_V1_BASE.length() + 1 );
        } else {
            return false;
        }

        CheckTask inputJSON = null;
        // read body if exists
        try {
            if ( !UIUtils.getProp( "api.secret", "" ).equals( req.getHeader( "token" ) ) ) {
                LOG.error( "send token: {}", req.getHeader( "token" ) );
                res.sendError( 401, "No authorization!" );
            }

            if ( req.getContentLength() > 0 ) {
                inputJSON = CheckTaskJsonReader.getCheckTask( req.getInputStream() );
            }

            LOG.debug( "Method: {}", req.getMethod() );
            LOG.debug( "path: {}", pathInfo );
            LOG.debug( "Type: {}", pathParam );

            // 3 Zustaende:
            // leer -> Get Methode/Reqeuest Params required
            // String -> Teil von Valid?
            // Integer -> JobId
            if ( ( pathParam == null || pathParam.isEmpty() ) && "GET".equalsIgnoreCase( req.getMethod() ) ) {

                Object taskType = reqParams.getOrDefault( "taskType", null );
                taskType = ( taskType != null ? ( (String[]) taskType )[0] : null );
                Object taskName = reqParams.getOrDefault( "taskName", null );
                taskName = ( taskName != null ? UIUtils.makelike( ( (String[]) taskName )[0] ) : null );
                // Object active = reqParams.getOrDefault( "active", null );
                // active = Boolean.getBoolean( active != null ? ( (String[]) active )[0] : null );

                return handleGetJobByParam( res, false, //
                                            new Pair<String, Object>( "name", (String) taskName ), //
                                            new Pair<String, Object>( "sensor.type", taskType )// , //
                // new Pair<String, Object>( "sensor.type", active )
                );

            } else if ( Arrays.asList( valid.toUpperCase().split( "," ) ).contains( pathParam.toUpperCase() ) //
                        && inputJSON != null //
                        && "POST".equalsIgnoreCase( req.getMethod() ) ) {
                try {
                    if ( "OAFBASIC".equalsIgnoreCase( pathParam ) ) {
                        SensorConfig sc = inputJSON.getSensor().getConfig();

                        String cap = sc.getFeatureCap();
                        String get = sc.getFeatureGet();
                        String lay = sc.getLayerAvail();
                        if ( cap != null && ( get == null || get.isEmpty() ) && ( lay == null || lay.isEmpty() ) || //
                             get != null && ( cap == null || cap.isEmpty() ) && ( lay == null || lay.isEmpty() ) || //
                             lay != null && ( cap == null || cap.isEmpty() ) && ( get == null || get.isEmpty() ) ) {
                            LOG.debug( "OAFBASIC, only one required param exist" );
                        } else {
                            LOG.error( "to much required parameters" );
                            throw new IllegalArgumentException( "OAF darf nur entweder featureCap, featureGet oder layerAvail als Parameter haben, nicht mehrere davon." );
                        }
                    }

                    inputJSON.getSensor().setName( "* API GENERATED SENSOR *" );
                    inputJSON.getSensor().setType( pathParam.toUpperCase() );
                    inputJSON.getSensor().setDaemonId( UIUtils.getDaemonId() );

                    handlePostCreation( inputJSON );

                    res.setContentType( "application/json; charset=utf-8" );
                    res.getWriter().write( "{ jobid: " + inputJSON.getId() + "}" );

                    return true;
                } catch ( IllegalArgumentException e ) {
                    LOG.error( "not all required parameters were sent" );
                    res.sendError( 412, "The passed attributes are invalid" );
                    return true;
                } catch ( Exception e ) {
                    LOG.error( "Error with handling post: {}", e.getLocalizedMessage() );
                    res.sendError( 412, "Error with handling post" );
                    return true;
                }
            } else {
                try {
                    jobId = Integer.parseInt( pathParam );
                    getAllowed = true;
                    putAllowed = true;
                    deleteAllowed = true;
                } catch ( NumberFormatException nfe ) {
                    LOG.info( "{} is not parsable to integer.", pathParam );
                    res.sendError( 406, "The path could not be used and will be rejected." );
                    return true;
                }
            }

            if ( getAllowed && "GET".equalsIgnoreCase( req.getMethod() ) ) {
                // return given Job

                return handleGetJobByParam( res, true, new Pair<String, Object>( "id", jobId ) );
            } else if ( putAllowed && inputJSON != null && "PUT".equalsIgnoreCase( req.getMethod() ) ) {
                // update given Job
                CheckTask existing = Ebean.find( CheckTask.class, jobId );
                if ( existing != null ) {
                    existing.update( inputJSON );
                    Ebean.save( existing );
                } else
                    res.sendError( 404, "the given job does not exist." );
                res.setContentType( "application/json; charset=utf-8" );
                return true;
            } else if ( deleteAllowed && "DELETE".equalsIgnoreCase( req.getMethod() ) ) {
                // delete given Job
                int delete = Ebean.delete( CheckTask.class, jobId );
                LOG.debug( "result of delete: {}", delete );
                if ( delete == 0 ) {
                    // id not exising
                    res.sendError( 404, "Sent JobId does not exist" );
                }
                res.setContentType( "application/json; charset=utf-8" );
                return true;
            } else {
                res.sendError( 405, pathParam + " is not supported. Request rejected." );
            }

        } catch ( IllegalArgumentException iae ) {
            LOG.error( "Illegal Argument in JSON: {}", iae.getLocalizedMessage() );
            res.sendError( 412, iae.getLocalizedMessage() );
        } catch ( Exception e ) {
            LOG.error( "Error with request: {}", e.getLocalizedMessage() );
            res.sendError( 500, "Error with request: " + e.getLocalizedMessage() );
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    private boolean handleGetJobByParam( VaadinResponse res, boolean singleData, Pair<String, Object>... filters )
                            throws IOException {
        res.setContentType( "application/json; charset=utf-8" );
        res.setStatus( 200 );
        ExpressionList<CheckTask> exp = Ebean.find( CheckTask.class ).fetch( "sensor" ).where();

        LOG.debug( "Filterlaenge: {}", filters.length );

        for ( Pair<String, Object> f : filters ) {
            if ( f.second != null ) {
                if ( f.second instanceof String[] ) {
                    exp.ieq( f.first, ( (String[]) f.second )[0] );
                } else if ( f.second instanceof String ) {
                    exp.ilike( f.first, (String) f.second );
                } else {
                    exp.eq( f.first, f.second );
                }
            }
        }

        List<CheckTask> result = exp.findList();
        LOG.debug( "resultlist: {}", result.size() );

        String json = Ebean.json().toJson( result );

        LOG.debug( "result: {}", json );

        JSONUtils.writeList( res.getWriter(), result, null, "api", singleData );

        return true;
    }

    private boolean handlePostCreation( CheckTask ct )
                            throws Exception {
        // validate the object
        String type = ct.getSensor().getType();
        String[] required = UIUtils.getProp( "api." + type + ".required", "" ).split( "," );
        if ( !required[0].isEmpty() ) {
            for ( String param : required ) {
                try {
                    if ( getValue( ct, param ) == null ) {
                        throw new IllegalArgumentException( param + " is a required parameter" );
                    }
                } catch ( Exception e ) {
                    LOG.debug( "error with {}", param );
                    LOG.error( "", e );
                    throw e;
                }
            }

        }

        Ebean.save( ct );
        // Ebean.save( ct.getSensor() );
        // Ebean.save( ct.getSensor().getConfig() );

        LOG.info( "generate new Job with ID  {}", ct.getId() );

        return true;
    }

    private Object getValue( CheckTask ct, String param )
                            throws IllegalAccessException,
                            IllegalArgumentException,
                            InvocationTargetException {
        Method m = null;
        Object bean = ct;

        m = UIUtils.getGetterMethod( CheckTask.class, param );
        if ( m == null ) {
            m = UIUtils.getGetterMethod( Sensor.class, param );
            bean = ct.getSensor();
        }
        if ( m == null ) {
            m = UIUtils.getGetterMethod( SensorConfig.class, param );
            bean = ct.getSensor().getConfig();
        }

        return m != null ? m.invoke( bean ) : null;
    }
}
