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
package de.grit.xgdm.monsuite.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.ebean.Ebean;

import de.grit.xgdm.monsuite.data.CheckTask;
import de.grit.xgdm.monsuite.data.Notification;

public class EbeanTests {

    private static final Logger LOG = LoggerFactory.getLogger( EbeanTests.class );

    @Ignore
    @Test
    public void testLasyLoad() {
        CheckTask ct = Ebean.find( CheckTask.class ).select( "name" ).where().eq( "id", 21 ).findOne();
        assertEquals( "id match", new Long( 21 ), ct.getId() );
        assertNotNull( "Name", ct.getName() );
        LOG.info( "Load rules" );
        List<Notification> n = ct.getNotifications();
        LOG.info( "Found #{} params", n.size() );
        assertTrue( n.size() > 0 );
    }
}
