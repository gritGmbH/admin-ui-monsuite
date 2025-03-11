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

import static de.grit.vaadin.common.Messages.get;
import static de.grit.vaadin.common.UIUtilsCommon.extendDateTimeFieldTwoDigitYear;

import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.grit.vaadin.GritTheme;
import de.grit.vaadin.common.FontAwesome;
import de.grit.vaadin.common.Pair;
import de.grit.vaadin.common.annotation.MenuButton;
import de.grit.vaadin.common.annotation.Navigation;
import de.grit.vaadin.common.interfaces.IRefreshable;
import de.grit.xgdm.monsuite.UIUtils;
import io.ebean.Ebean;
import io.ebean.SqlQuery;
import io.ebean.SqlRow;

@Navigation(path = "overview", titleKey = "txt.root.tab.reports", menu = @MenuButton(icon = FontAwesome.CLOCK_O, sort = 200))
public class ReportPage extends AbstractQueryPage implements Button.ClickListener, IRefreshable {

    private static final long serialVersionUID = 1579563637143648643L;

    private VerticalLayout mainLay;

    private Button btnExecute = UIUtils.getButton( null, get( "reportspage.execute" ), this, FontAwesome.PLAY );

    private Label resValue = new Label( "", ContentMode.HTML );

    private ComboBox<Pair<Integer, String>> cmdSelect;

    private DateTimeField filDateStart = new DateTimeField();

    private DateTimeField filDateEnd = new DateTimeField();

    public ReportPage() {
        super();
        buildMainLayout();
        setCompositionRoot( mainLay );

        String styles = UIUtils.getProp( "report.css", null );
        if ( styles == null ) {
            Page.getCurrent().getStyles().add( new ThemeResource( "report_page.css" ) );
        } else {
            Page.getCurrent().getStyles().add( styles );
        }
    }

    private void buildMainLayout() {
        mainLay = new VerticalLayout();
        mainLay.setMargin( true );
        mainLay.setSpacing( true );

        cmdSelect = new ComboBox<>();
        cmdSelect.setEmptySelectionAllowed( false );
        cmdSelect.setItemCaptionGenerator( Pair::getSecond );
        cmdSelect.setWidth( 30.0f, Unit.EM );

        LocalDateTime dStart, dEnd;
        dStart = LocalDateTime.now() //
                              .minusMonths( 1 ) //
                              .with( TemporalAdjusters.firstDayOfMonth() );
        dEnd = LocalDateTime.now()//
                            .minusMonths( 1 ) //
                            .with( TemporalAdjusters.lastDayOfMonth() );
        filDateStart.setValue( dStart );
        filDateStart.setResolution( DateTimeResolution.DAY );
        filDateStart.setDateFormat( "dd.MM.yyyy" );
        filDateStart.setParseErrorMessage( get( "txt.error.val.date" ) );
        extendDateTimeFieldTwoDigitYear( filDateStart );

        filDateEnd.setValue( dEnd );
        filDateEnd.setResolution( DateTimeResolution.DAY );
        filDateEnd.setDateFormat( "dd.MM.yyyy" );
        filDateEnd.setParseErrorMessage( get( "txt.error.val.date" ) );
        extendDateTimeFieldTwoDigitYear( filDateEnd );

        HorizontalLayout execBar = new HorizontalLayout();
        execBar.addStyleName( GritTheme.LAYOUT_HORIZONTAL_FIELD_ALIGNMENT );
        execBar.addStyleName( GritTheme.LAYOUT_HORIZONTAL_WRAPPING );
        execBar.setSpacing( true );
        execBar.addComponent( new Label( get( "reportspage.date-from" ) ) );
        execBar.addComponent( filDateStart );
        execBar.addComponent( new Label( get( "reportspage.date-until" ) ) );
        execBar.addComponent( filDateEnd );
        execBar.addComponent( new Label( get( "reportspage.report" ) + ":" ) );
        execBar.addComponent( cmdSelect );
        execBar.addComponent( btnExecute );

        mainLay.addComponent( execBar );

        mainLay.addComponent( resValue );
        // mainLay.setExpandRatio( resValue, 1.0f );

        mainLay.addComponent( refreshed );

        UIUtils.setFullWidth( resValue );
    }

    @Override
    public void refresh() {
        int i = 0;
        String lbl;
        List<Pair<Integer, String>> selectVals = new LinkedList<>();

        do {
            lbl = UIUtils.getSQLProp( "report." + i + ".lbl", null );

            if ( lbl == null )
                break;

            selectVals.add( new Pair<Integer, String>( i, //
                                                       ( lbl.trim().length() == 0 ) ? ( get( "reportspage.report" )
                                                                                        + " " + i )
                                                                                    : lbl ) //
            );

            i++;
        } while ( i >= 0 && i < 999 );

        cmdSelect.setItems( selectVals );
        cmdSelect.setSelectedItem( selectVals.get( 0 ) );
    }

    @Override
    public void buttonClick( ClickEvent event ) {
        if ( event.getButton() == btnExecute ) {
            Integer i = cmdSelect.getValue().getFirst();

            LocalDateTime ts = simpleDate( filDateStart.getValue(), false );
            LocalDateTime te = simpleDate( filDateEnd.getValue(), true );

            StringBuilder result = new StringBuilder();
            try {
                for ( String sql : UIUtils.getSQLPropNumberedArray( "report." + i + ".sql" ) ) {

                    SqlQuery qry = Ebean.createSqlQuery( sql );
                    qry = qry.setParameter( "timeStart", ts );
                    qry = qry.setParameter( "timeEnd", te );

                    String lkey, key;
                    for ( SqlRow row : qry.findList() ) {
                        for ( int sub = 0; sub >= 0 && sub < 100; sub++ ) {
                            if ( sub == 0 ) {
                                lkey = "lhtml";
                                key = "html";
                            } else {
                                lkey = "lhtml" + sub;
                                key = "html" + sub;
                            }

                            if ( row.containsKey( lkey ) ) {
                                Object clob = row.get( lkey );
                                appendCLOB( result, clob );
                            } else if ( row.containsKey( key ) ) {
                                result.append( row.getString( key ) );
                            } else {
                                // stop iteration
                                sub = -2;
                            }
                        }
                    }
                }
            } catch ( Exception ex ) {
                result.setLength( 0 );
                result.append( get( "txt.error.report" ) );
                StringWriter sw = new StringWriter();
                ex.printStackTrace( new PrintWriter( sw ) );
                result.append( sw.toString() );
            }

            resValue.setValue( result.toString() );

            refreshed.setValue( get( "txt.refreshed.at", DATE_FMT.format( new Date() ) ) );
        }

    }

    @SuppressWarnings("deprecation")
    private void appendCLOB( StringBuilder res, Object lob )
                            throws Exception {
        if ( lob != null && lob instanceof oracle.sql.CLOB ) {
            oracle.sql.CLOB clob = (oracle.sql.CLOB) lob;

            Reader rdr = clob.getCharacterStream();
            char[] cbuf = new char[512];
            int len;
            while ( ( len = rdr.read( cbuf ) ) > -1 ) {
                res.append( cbuf, 0, len );
            }
        }
    }
}
