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
package de.grit.xgdm.monsuite;

import static de.grit.vaadin.common.EbeanUtilsCommon.getProp;
import static de.grit.vaadin.common.Messages.get;
import static de.grit.vaadin.common.Messages.getRes;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.dialogs.DefaultConfirmDialogFactory;

import com.vaadin.annotations.Theme;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;

import de.grit.vaadin.GritTheme;
import de.grit.vaadin.common.UserData;
import de.grit.vaadin.common.components.AbstractGritUI;
import de.grit.vaadin.common.pages.DatabaseLoginComponent;
import de.grit.vaadin.common.pages.StaticLoginComponent;
import io.ebean.Ebean;
import io.ebean.SqlRow;

@Theme(GritTheme.THEME_NAME)
public class MonitoringSuiteApplication extends AbstractGritUI {

    private static final long serialVersionUID = -420480327663996023L;

    private static final Logger LOG = LoggerFactory.getLogger( MonitoringSuiteApplication.class );

    public MonitoringSuiteApplication() {
        LOG.info( "Started MonSuite Application" );

        boolean staticLogin = "true".equalsIgnoreCase( getProp( "login.static", "false" ) );

        if ( staticLogin ) {
            login = new StaticLoginComponent( this );
        } else {
            login = new DatabaseLoginComponent( this );
        }
        initGeneric();
        loadGritViews( getNavigator() );
    }

    @Override
    protected void init( VaadinRequest request ) {
        if ( !VaadinService.getCurrent().getDeploymentConfiguration().isProductionMode() ) {
            // Running in development Mode
            if ( "true".equalsIgnoreCase( getProp( "dev.disablesec", "false" ) ) ) {
                disableSecurity = true;
            }
        }

        ConfirmDialog.setFactory( new DefaultConfirmDialogFactory() {
            private static final long serialVersionUID = -5029243503269197618L;

            @Override
            public ConfirmDialog create( String caption, String message, String okCaption, String cancelCaption,
                                         String notOkCaption ) {
                ConfirmDialog dlg = super.create( caption, message, okCaption, cancelCaption, notOkCaption );
                dlg.addStyleName( GritTheme.WINDOW_CUSTOM );
                return dlg;
            };
        } );

        userData = new UserData();
        userDataChanged();

        String extraCss = getProp( "environment.extracss", null ); // $NON-NLS-1$
        if ( extraCss != null ) {
            LOG.debug( "Using extra CSS: {}", extraCss );
            Page.getCurrent().getStyles().add( extraCss );
        } else {
            LOG.debug( "No extra CSS defined" );
        }

    }

    @Override
    protected String getVersionAsText() {
        return UIUtils.getPomVersion( "de.grit", "xgdm-monsuite-gui-base" );
    }

    @Override
    public String getRevisionAsText() {
        String v = UIUtils.getPomVersion( getProp( "config.group", "de.grit.config" ),
                                          getProp( "config.artifact", "does_not_exist" ), null );

        return v != null ? "(c" + v + ")" : "";
    }

    @Override
    public String getTitle() {
        return getProp( "overwrite_text.app.title", get( "app.title" ) );
    }

    @Override
    protected String getText( String key, Object... args ) {
        return get( key, args );
    }

    @Override
    protected Resource getAppIcon() {
        return getRes( "48x48", "monitor" );
    }

    @Override
    public void userDataChanged() {
        super.userDataChanged();
        String configuredPages = ",,";
        List<SqlRow> admin = null;
        if ( !isAdministrator() && userData.getUsername() != null )
            admin = Ebean.createSqlQuery( UIUtils.getSQLProp( "login.adminright", null ) ) //
                         .setParameter( "username", userData.getUsername() ) //
                         .findList();

        if ( admin != null && admin.size() == 0 )
            configuredPages = getProp( "login.noadmin.tabs", "status,overview,details,help" );

        clearMenuButtons();

        for ( AbstractGritUI.MenuItem mi : getAvailableMenuItems() ) {
            // Add all MenuButtons that are not hidden and configured
            if ( !mi.path.startsWith( "_" ) && //
                 ( ",,".equals( configuredPages ) || configuredPages.indexOf( mi.path.toLowerCase() ) > -1 ) ) {
                addMenuButton( mi.path, mi.title, mi.icon );
            }
        }
    }

    public boolean isAdministrator() {
        if ( disableSecurity ) {
            LOG.debug( "*******************************************************************************" );
            LOG.debug( "*******************************************************************************" );
            LOG.debug( "***                                                                         ***" );
            LOG.debug( "***  S E C U R I T Y   D I S A B L E D  -  D E V E L O P M E N T   O N L Y  ***" );
            LOG.debug( "***                                                                         ***" );
            LOG.debug( "*******************************************************************************" );
            LOG.debug( "*******************************************************************************" );
            return true;
        }

        if ( getUserData() == null ) {
            return false;
        }

        String admin = getProp( "login.adminusername", "SEC_ADMIN" );
        return ( admin != null && admin.equalsIgnoreCase( this.getUserData().getUsername() ) );
    }

}
