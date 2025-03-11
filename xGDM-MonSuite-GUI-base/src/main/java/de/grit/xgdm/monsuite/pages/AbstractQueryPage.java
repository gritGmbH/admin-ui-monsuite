/*-
 * #%L
 * xGDM-MonSuite GUI (Base)
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
package de.grit.xgdm.monsuite.pages;
//

import static de.grit.vaadin.common.Messages.get;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Calendar;

import com.vaadin.ui.Label;

import de.grit.vaadin.common.components.GritView;

public class AbstractQueryPage extends GritView {

    private static final long serialVersionUID = 1992364313934897029L;

    protected static DateFormat DATE_FMT = new SimpleDateFormat( get( "txt.date.format" ) );

    protected Label refreshed = new Label();

    public AbstractQueryPage() {
        super();
    }

    protected long simpleDate( Date obj, boolean endOfDay ) {
        Calendar cal = Calendar.getInstance();
        cal.setTime( obj );
        if ( endOfDay ) {
            cal.set( Calendar.HOUR_OF_DAY, 23 );
            cal.set( Calendar.MINUTE, 59 );
            cal.set( Calendar.SECOND, 59 );
            cal.set( Calendar.MILLISECOND, 999 );
        } else {
            cal.set( Calendar.HOUR_OF_DAY, 0 );
            cal.set( Calendar.MINUTE, 0 );
            cal.set( Calendar.SECOND, 0 );
            cal.set( Calendar.MILLISECOND, 0 );
        }

        return cal.getTimeInMillis();
    }

    protected LocalDateTime simpleDate( LocalDateTime ldt, boolean endOfDay ) {
        if ( endOfDay ) {
            ldt = ldt.withHour( 23 );
            ldt = ldt.withMinute( 59 );
            ldt = ldt.withSecond( 59 );
            ldt = ldt.with( ChronoField.MILLI_OF_SECOND, 999 );
        } else {
            ldt = ldt.withHour( 0 );
            ldt = ldt.withMinute( 0 );
            ldt = ldt.withSecond( 0 );
            ldt = ldt.with( ChronoField.MILLI_OF_SECOND, 0 );
        }
        return ldt;
    }
}
