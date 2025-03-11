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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.Binder;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.grit.vaadin.GritTheme;
import de.grit.vaadin.common.FontAwesome;
import de.grit.vaadin.common.Messages;
import de.grit.vaadin.common.annotation.MenuButton;
import de.grit.vaadin.common.annotation.Navigation;
import de.grit.vaadin.common.components.FilteredView;
import de.grit.vaadin.common.components.GritView;
import de.grit.vaadin.common.interfaces.IRefreshable;
import de.grit.xgdm.monsuite.UIUtils;
import de.grit.xgdm.monsuite.data.Person;
import io.ebean.Ebean;
import io.ebean.Query;

@Navigation(path = "messages", titleKey = "txt.root.tab.message", menu = @MenuButton(icon = FontAwesome.ENVELOPE, sort = 500))
public class MessagePage extends GritView implements Button.ClickListener, IRefreshable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger( MessagePage.class );

    private static final String DATE_FMT_STRING = get( "txt.date.format" );

    private static final DateFormat DATE_FMT = new SimpleDateFormat( DATE_FMT_STRING );

    private final Button btnNew = UIUtils.getButton( null, get( "txt.button.add" ), this, "edit_add",
                                                     GritTheme.BUTTON_LINK );

    private final Button btnEdit = UIUtils.getButton( null, get( "txt.button.edit" ), this, "color_line",
                                                      GritTheme.BUTTON_LINK );

    private final Button btnDelete = UIUtils.getButton( null, get( "txt.button.remove" ), this, "edit_remove",
                                                        GritTheme.BUTTON_LINK );

    private final Button btnRefresh = UIUtils.getButton( null, get( "txt.button.refresh" ), this, "reload",
                                                         GritTheme.BUTTON_LINK );

    private VerticalLayout mainLay;

    private Label refreshed = new Label();

    private FilteredView<Person> mainTable = new FilteredView<>( Person.class, "message.fields" );

    public MessagePage() {
        super();
        buildMainLayout();
        setCompositionRoot( mainLay );
    }

    private void buildMainLayout() {
        mainLay = UIUtils.getDefaultRoot( null, btnNew, btnEdit, btnDelete, btnRefresh );

        mainTable.addHeaderFilterRow( Messages.getSplittedArray( "message.fields_names", "", "," ) );

        mainTable.addSelectionListener( lsnr -> {
            changeButtons( lsnr.getAllSelectedItems().size() );
        } );

        btnEdit.setEnabled( false );
        btnDelete.setEnabled( false );

        mainLay.addComponent( mainTable );
        mainLay.setExpandRatio( mainTable, 1.0f );

        mainLay.addComponent( refreshed );

        UIUtils.setFullWidth( mainTable );

    }

    public void changeButtons( int size ) {
        btnEdit.setEnabled( size == 1 );
        btnDelete.setEnabled( size > 0 );
    }

    public void persistDelete( Object obj ) {
        Ebean.delete( obj );
        refresh();
    }

    public void persistSave( Object obj ) {
        Ebean.save( obj );
        refresh();
    }

    @Override
    public void refresh() {
        Query<Person> qry = Ebean.find( Person.class );
        mainTable.update( qry.where().findList() );

        refreshed.setValue( get( "txt.refreshed.at", DATE_FMT.format( new Date() ) ) );
    }

    @Override
    public void buttonClick( ClickEvent event ) {
        try {
            if ( event.getSource() == btnRefresh ) {
                refresh();
            } else if ( event.getSource() == btnNew || event.getSource() == btnEdit ) {
                Person p;
                if ( event.getSource() == btnEdit )
                    p = mainTable.getSelectionModel().getFirstSelectedItem().orElse( null );
                else
                    p = new Person();
                showEditor( p );
            } else if ( event.getSource() == btnDelete ) {
                Person selected = mainTable.getSelectionModel().getFirstSelectedItem().orElse( null );
                if ( selected != null ) {
                    ConfirmDialog.show( UI.getCurrent(), get( "txt.confirm.delete" ),
                                        selected.getId() + " " + selected.getName(), get( "txt.button.yes" ),
                                        get( "txt.button.no" ), dialog -> {
                                            if ( dialog.isConfirmed() ) {
                                                persistDelete( selected );
                                                mainTable.deselectAll();
                                            }
                                        } );
                }
            }
        } catch ( Exception ex ) {
            UIUtils.showNotification( "Unerwarteter Fehler", ex.getMessage(), Type.WARNING_MESSAGE );
        }
    }

    private void showEditor( Person p ) {
        Binder<Person> binder = new Binder<>( Person.class );

        TextField name = new TextField( get( "message.fields.name" ) );
        binder.forField( name ) //
              .asRequired( get( "txt.error.validate.required" ) ) //
              .bind( Person::getName, Person::setName );

        TextField mail = new TextField( get( "message.fields.mail" ) );
        binder.forField( mail ) //
              .asRequired( get( "txt.error.validate.required" ) ) //
              .bind( Person::getMail, Person::setMail );

        TextField snmp = new TextField( get( "message.fields.snmp" ) );
        binder.forField( snmp ) //
              .bind( Person::getSnmp, Person::setSnmp );
        snmp.setDescription( get( "message.fields.snmp_tip" ) ); //$NON-NLS-1$

        binder.readBean( p );

        UIUtils.setFullWidth( name, mail, snmp );

        Window wnd = UIUtils.buildInputWindow( get( "txt.caption.message-editor" ), binder ) //
                            .withOkButtonCaption( get( "txt.button.save-and-close" ) ) //
                            .withResultOk( ( src, bnd ) -> {
                                try {
                                    if ( bnd.writeBeanIfValid( p ) ) {
                                        persistSave( p );
                                        src.close();
                                    } else {
                                        UIUtils.showNotification( get( "txt.error.val" ), Type.WARNING_MESSAGE );
                                    }
                                } catch ( Exception ex ) {
                                    LOG.error( "Fehler beim speichern: {}", ex.getLocalizedMessage() );
                                    LOG.trace( "", ex );
                                    UIUtils.showNotification( get( "txt.error.general-form" ), //$NON-NLS-1$
                                                              Type.WARNING_MESSAGE );
                                }
                            } ) //
                            .withCancelButtonCaption( get( "txt.button.close" ) ) //
                            .withLayout( new FormLayout() ) //
                            .withComponents( name, mail, snmp ) //
                            .withEmptyNotAllowed() //
                            .build();

        wnd.center();
        wnd.setWidth( 55.0f, Unit.PERCENTAGE );
        wnd.setHeight( 200.0f, Unit.PIXELS );
        UIUtils.setFullWidth( wnd.getContent() );
        UI.getCurrent().addWindow( wnd );
    }

}
