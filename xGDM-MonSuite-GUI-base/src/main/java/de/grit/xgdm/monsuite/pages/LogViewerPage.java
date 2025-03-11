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
import static de.grit.vaadin.common.Messages.getSplittedArray;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.HasValue;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.LocalDateTimeRenderer;

import de.grit.vaadin.GritTheme;
import de.grit.vaadin.common.FontAwesome;
import de.grit.vaadin.common.Pair;
import de.grit.vaadin.common.annotation.MenuButton;
import de.grit.vaadin.common.annotation.Navigation;
import de.grit.vaadin.common.components.FilteredView;
import de.grit.vaadin.common.interfaces.IRefreshable;
import de.grit.vaadin.common.interfaces.MultiSearchObjectChangedListener;
import de.grit.xgdm.monsuite.UIUtils;
import de.grit.xgdm.monsuite.data.CheckTask;
import de.grit.xgdm.monsuite.data.LogEntry;
import io.ebean.Ebean;
import io.ebean.ExpressionList;
import io.ebean.RawSql;
import io.ebean.RawSqlBuilder;

@Navigation(path = "details", titleKey = "txt.root.tab.logviewer", menu = @MenuButton(icon = FontAwesome.SEARCH, sort = 300))
public class LogViewerPage extends AbstractQueryPage
                           implements Button.ClickListener, MultiSearchObjectChangedListener, IRefreshable {

    private static final long serialVersionUID = -4250350133052031700L;

    private static final Logger LOG = LoggerFactory.getLogger( LogViewerPage.class );

    private static final String SERCH_DATE_FROM = "from";

    private static final String SERCH_DATE_UNTIL = "until";

    private static final String days = "DAYS";

    private Button btnRefresh = UIUtils.getButton( null, get( "txt.button.refresh" ), this, "reload",
                                                   GritTheme.BUTTON_LINK );

    private Button btnDnlCsv = UIUtils.getButton( null, UIUtils.downloadNotice( get( "logviewer.txt.dlnotice" ), true ),
                                                  this, "down", GritTheme.BUTTON_LINK );

    private VerticalLayout mainLay;

    private ComboBox<CheckTask> searchChecktask = new ComboBox<>();

    private Label details = new Label( "", ContentMode.HTML );

    private final ComboBox<String> searchMode = new ComboBox<>();

    private final DateTimeField searchDate = new DateTimeField();

    private final ComboBox<Pair<Integer, String>> searchCount = new ComboBox<>();

    private static String LOG_DATE_FMT = get( "logviewer.term_format" );

    private FilteredView<LogEntry> mainTable = new FilteredView<LogEntry>( LogEntry.class, "logviewer.field" ) {

        private static final long serialVersionUID = -7480309640943966467L;

        @Override
        protected void generatedColumns() {
            this.addColumn( LogEntry::getTerm ) //
                .setRenderer( new LocalDateTimeRenderer( LOG_DATE_FMT ) ) //
                .setId( "_term" );
        }
    };

    public LogViewerPage() {
        super();
        buildMainLayout();
        setCompositionRoot( mainLay );
    }

    private void buildMainLayout() {

        for ( String col : getSplittedArray( "logviewer.field_addcollapsed", "", "," ) ) {
            if ( col != null && col.length() > 0 )
                mainTable.setColumnCollapsed( col, true );
        }

        mainTable.addHeaderFilterRow( "resultName", "details" );

        mainTable.addSelectionListener( item -> {
            // in multiselect mode, a Set of itemIds is returned,
            // in singleselect mode the itemId is returned directly
            // boolean enable = null != event.getProperty().getValue();
            LogEntry s = mainTable.getSelectionModel().getFirstSelectedItem().orElse( null );
            if ( s != null )
                // todo use correct text
                details.setValue( get( "logviewer.selecttext", //
                                       s.getTaskName() != null ? s.getTaskName() : "", //
                                       s.getTerm().format( DateTimeFormatter.ofPattern( get( "txt.date.format" ) ) ), //
                                       s.getResultName() != null ? s.getResultName() : "", //
                                       s.getDuration() != null ? s.getDuration() : "", //
                                       s.getDetails() != null ? s.getDetails() : "", //
                                       s.getTaskTypeName() != null ? s.getTaskTypeName() : "", //
                                       s.getHost() != null ? s.getHost() : ""

                ) );
            else
                details.setValue( "" );
        } );

        searchChecktask.setEmptySelectionAllowed( false );
        searchChecktask.setItemCaptionGenerator( item -> {
            if ( item.getName() == null )
                return get( "logviewer.txt.task.all" );
            else
                return item.getName();
        } );
        searchChecktask.setWidth( 300.0f, Unit.PIXELS );

        searchMode.setItems( SERCH_DATE_FROM, SERCH_DATE_UNTIL );
        searchMode.setItemCaptionGenerator( val -> get( "logviewer.txt." + val, "" + val ) );
        searchMode.setEmptySelectionAllowed( false );
        searchMode.setSelectedItem( SERCH_DATE_UNTIL );
        searchMode.setWidth( 75.0f, Unit.PIXELS );

        searchDate.setValue( LocalDateTime.now() );
        searchDate.setResolution( DateTimeResolution.DAY );
        searchDate.setDateFormat( "dd.MM.yyyy" );
        searchDate.setWidth( 115.0f, Unit.PIXELS );
        searchDate.setParseErrorMessage( get( "txt.error.val.date" ) );
        UIUtils.extendDateTimeFieldTwoDigitYear( searchDate );

        List<Pair<Integer, String>> vals = new LinkedList<Pair<Integer, String>>();
        vals.add( new Pair<Integer, String>( 100, "ROW" ) );
        vals.add( new Pair<Integer, String>( 500, "ROW" ) );
        vals.add( new Pair<Integer, String>( 1000, "ROW" ) );
        vals.add( new Pair<Integer, String>( 1, "DAYS" ) );
        vals.add( new Pair<Integer, String>( 2, "DAYS" ) );
        vals.add( new Pair<Integer, String>( 7, "DAYS" ) );

        searchCount.setItems( vals );
        searchCount.setItemCaptionGenerator( val -> {
            String key = val.first + "" + val.second.charAt( 0 );
            return get( "logviewer.txt.count." + key.toLowerCase(), val.first + " " + val.second );
        } );
        searchCount.setEmptySelectionAllowed( false );
        searchCount.setSelectedItem( vals.get( 0 ) );

        mainLay = UIUtils.getDefaultRoot( UIUtils.getSearchBarObject( get( "txt.search" ),
                                                                      new String[] { get( "logviewer.field.taskName" ),
                                                                                     null, null, null, null },
                                                                      new String[] { "task", "mode", "date", "count" },
                                                                      new HasValue<?>[] { searchChecktask, searchMode,
                                                                                          searchDate, searchCount },
                                                                      this, FontAwesome.PLAY ),
                                          btnDnlCsv );

        mainLay.addComponent( mainTable );
        mainLay.setExpandRatio( mainTable,1.0f );

        mainLay.addComponent( details );

        mainLay.addComponent( refreshed );

        mainTable.enableCSVExport( btnDnlCsv );

        UIUtils.setFullWidth( mainTable );       
    }

    public void refresh() {
        List<CheckTask> vals = new LinkedList<>();
        vals.add( new CheckTask() );
        Ebean.find( CheckTask.class ).order( "name" ).findList().forEach( item -> {
            vals.add( item );
        } );

        searchChecktask.setItems( vals );
        searchChecktask.setSelectedItem( vals.get( 0 ) );
    }

    public void buttonClick( ClickEvent event ) {
        if ( event.getSource() == btnRefresh ) {
            refresh();
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public void searchValueChanged( Map<String, Object> fieldNamesWithTerm ) {
        LOG.debug( "-=[ searchValueChanged ]=-" );

        String mode = (String) fieldNamesWithTerm.get( "mode" );
        Pair<Integer, String> refcount = (Pair<Integer, String>) fieldNamesWithTerm.get( "count" );

        LocalDateTime refDate = (LocalDateTime) fieldNamesWithTerm.get( "date" );
        CheckTask refTask = (CheckTask) fieldNamesWithTerm.get( "task" );

        RawSql rawSql = RawSqlBuilder.parse( UIUtils.getSQLProp( LogEntry.SQL_QUERY,
                                                                 get( LogEntry.SQL_QUERY ) ) ).create();
        ExpressionList<LogEntry> qry = Ebean.find( LogEntry.class ).setRawSql( rawSql ).where();
        if ( refTask != null && refTask.getId() != null ) {
            qry.eq( "taskId", refTask.getId() );
        }

        if ( days.equals( refcount.second ) ) {
            // date filter
            if ( SERCH_DATE_UNTIL.equals( mode ) ) {
                qry.le( "term", simpleDate( refDate, true ) );
                LocalDateTime ldt = simpleDate( refDate, false );
                ldt = ldt.minus( refcount.first - 1, ChronoUnit.DAYS );
                qry.ge( "term", ldt );
            } else {
                qry.ge( "term", simpleDate( refDate, false ) );
                LocalDateTime ldt = simpleDate( refDate, true );
                ldt = ldt.plus( refcount.first - 1, ChronoUnit.DAYS );
                qry.le( "term", ldt );
            }
        } else {
            // rowcount filter
            if ( SERCH_DATE_UNTIL.equals( mode ) ) {
                qry.le( "term", simpleDate( refDate, true ) );
            } else {
                qry.ge( "term", simpleDate( refDate, false ) );
            }
            qry.setMaxRows( refcount.first );
        }

        for ( Map.Entry<String, Object> entry : fieldNamesWithTerm.entrySet() ) {
            LOG.debug( "Key: " + entry.getKey() );
            LOG.debug( "Value: " + entry.getValue() );
        }
        LOG.debug( "-----" );

        if ( SERCH_DATE_UNTIL.equals( mode ) ) {
            mainTable.update( qry.order().desc( "term" ).findList() );
        } else {
            mainTable.update( qry.order().asc( "term" ).findList() );
        }

        refreshed.setValue( get( "txt.refreshed.at", DATE_FMT.format( new Date() ) ) );
    }

}
