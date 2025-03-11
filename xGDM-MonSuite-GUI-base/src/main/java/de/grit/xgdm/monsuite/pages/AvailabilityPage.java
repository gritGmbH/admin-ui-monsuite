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
import static de.grit.vaadin.common.Messages.getRes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.Resource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.ImageRenderer;

import de.grit.vaadin.GritTheme;
import de.grit.vaadin.common.FontAwesome;
import de.grit.vaadin.common.Messages;
import de.grit.vaadin.common.annotation.MenuButton;
import de.grit.vaadin.common.annotation.Navigation;
import de.grit.vaadin.common.components.FilteredView;
import de.grit.vaadin.common.components.GritView;
import de.grit.vaadin.common.interfaces.IRefreshable;
import de.grit.xgdm.monsuite.UIUtils;
import de.grit.xgdm.monsuite.data.SensorStatus;
import io.ebean.Ebean;
import io.ebean.Query;
import io.ebean.RawSql;
import io.ebean.RawSqlBuilder;

@Navigation(path = "availability", titleKey = "txt.root.tab.availability", defaultView = true, menu = @MenuButton(icon = FontAwesome.FLASH, sort = 100))
public class AvailabilityPage extends GritView implements Button.ClickListener, IRefreshable {

    private static final long serialVersionUID = 6197948279049566083L;

    private static final Logger LOG = LoggerFactory.getLogger( AvailabilityPage.class );

    private static final String DATE_FMT_STRING = get( "txt.date.format" );

    private static final DateFormat DATE_FMT = new SimpleDateFormat( DATE_FMT_STRING );

    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern( DATE_FMT_STRING );

    private final Button btnRefresh = UIUtils.getButton( null, get( "txt.button.refresh" ), this, "reload",
                                                         GritTheme.BUTTON_LINK );

    private VerticalLayout mainLay;

    private transient Timer autoRefreshTimer = new Timer();

    private transient TimerTask autoRefreshTask = null;

    private DateFormat DATE_ONLY_FMT = new SimpleDateFormat( get( "txt.date_only.format" ) );

    private FilteredView<SensorStatus> mainTable;

    private ComboBox<Integer> refreshSelect = new ComboBox<>();

    private Label refreshed = new Label();

    private Label details = new Label( "", ContentMode.HTML );

    public AvailabilityPage() {
        super();
        buildMainLayout();
        setCompositionRoot( mainLay );
    }

    private void buildMainLayout() {
        final ProgressBar refreshProgress = new ProgressBar();
        refreshProgress.setIndeterminate( true );
        // refreshProgress.setEnabled( false );
        refreshProgress.setVisible( false );

        mainLay = UIUtils.getDefaultRoot( null, btnRefresh, refreshSelect, refreshProgress );

        final Map<String, Resource> iconCache = new HashMap<String, Resource>();
        // vailability.field
        mainTable = new FilteredView<SensorStatus>( SensorStatus.class, "availability.field" ) {

            private static final long serialVersionUID = 4578266081840183508L;

            @Override
            protected void generatedColumns() {

                this.addColumn( status -> {
                    String key = status.getTimeframe();
                    Resource res = iconCache.get( key );
                    if ( res == null ) {
                        res = getRes( "status", key );
                        if ( res != null )
                            iconCache.put( key, res );
                    }
                    return res;
                } ) //
                    .setRenderer( new ImageRenderer<>() ) //
                    .setId( "_timeframe" );

                this.addColumn( status -> {
                    String key = status.getStatus();
                    LOG.debug( "Status key: {}", key );
                    Resource res = iconCache.get( key );
                    if ( res == null ) {
                        res = getRes( "status", key );
                        if ( res != null )
                            iconCache.put( key, res );
                    }
                    LOG.debug( "Result Mimetype: {}", res.getMIMEType() );
                    return res;
                } ) //
                    .setRenderer( new ImageRenderer<>() ) //
                    .setId( "_status" );

            }
        };

        mainTable.addHeaderFilterRow( Messages.getSplittedArray( "availability.field_search", "", "," ) );
        mainTable.addSelectionListener( lsnr -> {
            SensorStatus s = mainTable.getSelectionModel().getFirstSelectedItem().orElse( null );
            if ( s != null )
                details.setValue( get( "availability.selecttext", "", //
                                       // s.getTerm() != null ? DATE_FMT.format( s.getTerm() ) : "", //
                                       s.getTerm() != null ? s.getTerm().format( DATE_TIME_FMT ) : "", //
                                       s.getResultName() != null ? s.getResultName() : "", //
                                       s.getDuration() != null ? s.getDuration() : "", //
                                       s.getDetails() != null ? s.getDetails() : "", //
                                       s.getCheckTaskName() != null ? s.getCheckTaskName() : "", //
                                       s.getSensorTypName() != null ? s.getSensorTypName() : "", //
                                       s.getStartTime() != null ? s.getStartTime() : "", //
                                       s.getEndTime() != null ? s.getEndTime() : "", //
                                       s.getPause() != null ? s.getPause() : "", //
                                       s.getTimeout() != null ? s.getTimeout() : "", //
                                       s.getStartDate() != null ? DATE_ONLY_FMT.format( s.getStartDate() ) : "", //
                                       s.getEndDate() != null ? DATE_ONLY_FMT.format( s.getEndDate() ) : "" //
                ) );
            else
                details.setValue( "" );

        } );

        refreshSelect.setEmptySelectionAllowed( false );
        refreshSelect.setItems( new Integer[] { 0, 15, 30, 60, 300 } );
        refreshSelect.setItemCaptionGenerator( item -> get( "availability.refresh." + item ) );
        refreshSelect.setSelectedItem( 0 );

        refreshSelect.setDescription( get( "availability.refresh.select" ) );
        refreshSelect.addSelectionListener( lsnr -> {
            int val = lsnr.getSelectedItem().orElse( null ).intValue();
            try {
                if ( val > 0 ) {
                    if ( autoRefreshTask != null ) {
                        try {
                            autoRefreshTask.cancel();
                        } catch ( Exception ign ) {

                        }
                    }

                    getUI().setPollInterval( val * 1000 );

                    autoRefreshTimer.purge();
                    autoRefreshTask = new TimerTask() {
                        @Override
                        public void run() {
                            getUI().access( () -> {
                                LOG.debug( "Refresh from timer" );
                                refresh();
                            } );
                        }
                    };
                    autoRefreshTimer.schedule( autoRefreshTask, val * 1000L, val * 1000L );
                    refreshProgress.setVisible( true );
                    refresh();

                } else {
                    refreshProgress.setVisible( false );
                    getUI().setPollInterval( -1 );
                    if ( autoRefreshTask != null ) {
                        try {
                            autoRefreshTask.cancel();
                        } catch ( Exception ign ) {
                        }
                    }
                    autoRefreshTimer.purge();
                }
            } catch ( Exception ex ) {
                LOG.error( "Fehler beim Neuladen: {}", ex.getLocalizedMessage() );
                LOG.trace( "", ex );
            }
        } );

        mainLay.addComponent( mainTable );
        mainLay.setExpandRatio( mainTable, 1.0f );

        mainLay.addComponent( details );

        mainLay.addComponent( refreshed );

        UIUtils.setFullWidth( mainTable );
    }

    @Override
    public void refresh() {
        RawSql rawSql = RawSqlBuilder.parse( SensorStatus.getSqlSensorstatus() ).columnMappingIgnore( "SORT_ID" ).create();

        Query<SensorStatus> query = Ebean.find( SensorStatus.class );
        query.setRawSql( rawSql );

        mainTable.update( query.findList() );

        LOG.debug( "refreshed at: {}", DATE_FMT.format( new Date() ) );

        refreshed.setValue( get( "txt.refreshed.at", DATE_FMT.format( new Date() ) ) );
    }

    @Override
    public void buttonClick( ClickEvent event ) {
        if ( event.getSource() == btnRefresh ) {
            refresh();
        }
    }

}
