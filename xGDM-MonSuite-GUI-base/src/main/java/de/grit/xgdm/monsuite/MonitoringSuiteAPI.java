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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.ServiceInitEvent;
import com.vaadin.server.VaadinServiceInitListener;

import de.grit.monsuite.api.CheckTaskEndpoint;
import de.grit.monsuite.api.StatusEndpoint;
import de.grit.vaadin.common.api.ApiDefaultCatchAll;

public class MonitoringSuiteAPI implements VaadinServiceInitListener {

    private static final long serialVersionUID = -6444474669339317903L;

    private static final Logger LOG = LoggerFactory.getLogger( MonitoringSuiteAPI.class );

    @Override
    public void serviceInit( ServiceInitEvent event ) {
        LOG.info( "Initializing API-Endpoints" );
        event.addRequestHandler( new ApiDefaultCatchAll() );
        event.addRequestHandler( new StatusEndpoint() );
        event.addRequestHandler( new CheckTaskEndpoint() );
    }

}
