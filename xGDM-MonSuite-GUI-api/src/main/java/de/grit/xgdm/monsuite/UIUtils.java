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
package de.grit.xgdm.monsuite;

import static de.grit.vaadin.common.Messages.get;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.grit.vaadin.common.UIUtilsCommon;

public class UIUtils extends UIUtilsCommon {

    public static Double asDouble( Object value, Double defaultValue ) {
        if ( value == null )
            return defaultValue;

        if ( value instanceof Number )
            return new Double( ( (Number) value ).doubleValue() );

        Double res = defaultValue;
        try {
            String s = value.toString();
            if ( s != null )
                res = new Double( Double.parseDouble( s ) );
        } catch ( Exception nfe ) {
            // ign
        }

        return res;
    }

    public static Integer asInteger( Object value, Integer defaultValue ) {
        if ( value == null )
            return defaultValue;

        if ( value instanceof Number )
            return new Integer( ( (Number) value ).intValue() );

        Integer res = defaultValue;
        try {
            String s = value.toString();
            if ( s != null )
                res = new Integer( Integer.parseInt( s ) );
        } catch ( Exception nfe ) {
            // ign
        }

        return res;
    }

    public static int asInt( Object value, int defaultValue ) {
        if ( value == null )
            return defaultValue;

        if ( value instanceof Number )
            return ( (Number) value ).intValue();

        int res = defaultValue;
        try {
            String s = value.toString();
            if ( s != null )
                res = Integer.parseInt( s );
        } catch ( Exception nfe ) {
            // ign
        }

        return res;
    }

    public static String downloadNotice( String base, boolean button ) {
        boolean forceEmbedded = "embedded".equalsIgnoreCase( getProp( "download.mode", null ) );
        String res = "";
        if ( base != null )
            res = base;

        if ( forceEmbedded )
            return res;

        if ( button )
            return res + get( "txt.popup.descr" );
        else
            return res + get( "txt.popup.label" );
    }

    /**
     * Hole aktuellen / ausgewaehlten Monitor/Daemon zum Bearbeiten
     * 
     * @return configured daemon id or 1 as default value
     */
    public static int getDaemonId() {
        return asInt( getProp( "daemon.default-id", null ), 1 );
    }

    /**
     * Hole Getter fuer uebergebenes Attribut
     * 
     * @param bean
     *            Klasse in der die Methode sein soll
     * @param field
     *            Attribut nach dem gesucht wird
     * @return getter of given field attribute; null if the method don't exist
     */
    public static Method getGetterMethod( Class<?> bean, String field ) {
        String getter = "get" + field.substring( 0, 1 ).toUpperCase() + field.substring( 1 );
        try {
            return bean.getMethod( getter );
        } catch ( NoSuchMethodException e ) {
            return null;
        }
    }

    /**
     * Hole Setter fuer uebergebenes Attribut
     * 
     * @param bean
     *            Klasse in der die Methode sein soll
     * @param field
     *            Attribut nach dem gesucht wird
     * @return setter of given field attribute; null if the method don't exist
     */
    public static Method getSetterMethod( Class<?> bean, Field field ) {
        String fieldName = field.getName();
        String setter = "set" + fieldName.substring( 0, 1 ).toUpperCase() + fieldName.substring( 1 );
        try {
            return bean.getMethod( setter, ( field.getType() == Boolean.class ? boolean.class : field.getType() ) );
        } catch ( NoSuchMethodException e ) {
            return null;
        }
    }
}
