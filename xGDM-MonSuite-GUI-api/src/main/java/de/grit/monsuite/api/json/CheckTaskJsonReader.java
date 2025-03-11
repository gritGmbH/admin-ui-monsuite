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
package de.grit.monsuite.api.json;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

import de.grit.xgdm.monsuite.UIUtils;
import de.grit.xgdm.monsuite.data.CheckTask;
import de.grit.xgdm.monsuite.data.Sensor;
import de.grit.xgdm.monsuite.data.SensorConfig;

public class CheckTaskJsonReader {
    private final static Logger LOG = LoggerFactory.getLogger( CheckTaskJsonReader.class );

    /**
     * transforms the given InputStream to an instance of CheckTask
     * 
     * @param is
     *            body of the request as an InputStream
     * @return the CheckTask Object from request body
     */
    public static CheckTask getCheckTask( InputStream is ) {
        CheckTask ct = new CheckTask();
        Sensor s = new Sensor();
        SensorConfig sc = new SensorConfig();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "yyyy-MM-dd" );

        List<Field> fields = new ArrayList<>();
        fields.addAll( asList( CheckTask.class.getDeclaredFields() ) );
        fields.addAll( asList( SensorConfig.class.getDeclaredFields() ) );

        List<String> attributes = new ArrayList<>();
        attributes.addAll( fields.stream().map( Field::getName ).collect( toList() ) );

        String name = null;
        try {
            JsonParser jp = new JsonFactory().createParser( is );
            while ( jp.nextToken() != null ) {
                name = jp.getCurrentName();
                Object bean = null;

                String attribute;
                Field field;
                if ( name != null ) {
                    for ( int i = 0; i < attributes.size(); i++ ) {
                        attribute = attributes.get( i );
                        if ( name.equalsIgnoreCase( attribute ) ) {
                            field = fields.get( i );
                            Method m = UIUtils.getSetterMethod( CheckTask.class, field );
                            if ( m == null )
                                m = UIUtils.getSetterMethod( SensorConfig.class, field );
                            else
                                bean = ct;
                            if ( m != null && bean == null )
                                bean = sc;

                            jp.nextToken();
                            if ( field.getType() == String.class ) {
                                m.invoke( bean, jp.getValueAsString() );
                            } else if ( field.getType() == Integer.class
                                        || "int".equals( field.getType().getTypeName() ) ) {
                                m.invoke( bean, Integer.parseInt( jp.getValueAsString() ) );
                            } else if ( field.getType() == Boolean.class
                                        || "boolean".equals( field.getType().getTypeName() ) ) {
                                String boolString = jp.getValueAsString();
                                if ( "true".equals( boolString ) || "false".equals( boolString ) ) {
                                    m.invoke( bean, jp.getValueAsBoolean() );
                                } else {
                                    throw new IllegalArgumentException( "F\u00FCr boolsche Werte werden nur true und false akzeptiert." );
                                }
                            } else if ( field.getType() == Long.class
                                        || "long".equals( field.getType().getTypeName() ) ) {
                                m.invoke( bean, jp.getValueAsLong() );
                            } else if ( field.getType() == LocalDate.class ) {
                                Object dateObject = jp.getCurrentValue();
                                if ( dateObject instanceof Long ) {
                                    m.invoke( bean,
                                              Instant.ofEpochMilli( jp.getValueAsLong() ).atZone( ZoneId.systemDefault() ).toLocalDate() );
                                } else {// maybe String
                                    String dateString = jp.getValueAsString();
                                    try {
                                        m.invoke( bean,
                                                  Instant.ofEpochMilli( Long.parseLong( dateString ) ).atZone( ZoneId.systemDefault() ).toLocalDate() );
                                    } catch ( Exception e ) {
                                        m.invoke( bean, LocalDate.parse( dateString, formatter ) );
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
        } catch ( IllegalArgumentException iae ) {
            throw iae;
        } catch ( Exception e ) {
            LOG.error( "error while parsing {}: {}", name, e.getLocalizedMessage() );
            LOG.error( "", e );
            return null;
        }

        s.setConfig( sc );
        ct.setSensor( s );
        return ct;
    }
}
