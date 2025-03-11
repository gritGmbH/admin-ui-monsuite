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

import java.util.Map;

import io.ebean.Ebean;

import de.grit.xgdm.monsuite.data.TstTask;
import de.grit.xgdm.monsuite.data.TstTaskConfig;

public class TstTaskTest {
    public static void main( String[] args ) {
        try {
            // new TstTaskTest().run0();
            //new TstTaskTest().run1();
            new TstTaskTest().run2();
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }

    public void run0()
                            throws Exception {

        TstTask task = new TstTask();

        task.setName( "Test Task Nr 1" );

        // if ( task.getConfig() == null )
        // task.setConfig( new LinkedList<TstTaskConfig>() );

        // task.getConfig().add( new TstTaskConfig( task, "wms.version", "1.1.1" ) );

        Ebean.save( task );

    }

    public void run2()
                            throws Exception {
        TstTask task = Ebean.find( TstTask.class, 21 );

        // if ( task.getConfig() == null )
        // task.setConfig( new LinkedList<TstTaskConfig>() );
        System.out.println("old");
        for ( Map.Entry<String, TstTaskConfig> elem : task.getConfig().entrySet() ) {
            System.out.println( elem.getKey() + " / " + elem.getValue().getName() + " = " + elem.getValue().getVal() );
        }

        TstTaskConfig ttcfg = new TstTaskConfig();
        ttcfg.setName( "wms.style" );
        ttcfg.setVal( "test2" );
        //
        ttcfg.setTask( task );
        TstTaskConfig old = task.getConfig().put( "wms.style", ttcfg );
        
        System.out.println("new (old" + old + ")");
        for ( Map.Entry<String, TstTaskConfig> elem : task.getConfig().entrySet() ) {
            System.out.println( elem.getKey() + " / " + elem.getValue().getName() + " = " + elem.getValue().getVal() );
        }
        
        if ( old != null ) {
            System.out.println( "delete old" );
            Ebean.delete( old );
        }
        Ebean.save( task );
    }
    
    public void run1()
                            throws Exception {
        TstTask task = Ebean.find( TstTask.class, 21 );

        // if ( task.getConfig() == null )
        // task.setConfig( new LinkedList<TstTaskConfig>() );
        System.out.println("old");
        for ( Map.Entry<String, TstTaskConfig> elem : task.getConfig().entrySet() ) {
            System.out.println( elem.getKey() + " / " + elem.getValue().getName() + " = " + elem.getValue().getVal() );
        }

        TstTaskConfig old = task.getConfig().put( "wms.style", new TstTaskConfig( task, "wms.style", "global" ) );
        System.out.println("new (old" + old + ")");
        for ( Map.Entry<String, TstTaskConfig> elem : task.getConfig().entrySet() ) {
            System.out.println( elem.getKey() + " / " + elem.getValue().getName() + " = " + elem.getValue().getVal() );
        }
        
        if ( old != null ) {
            System.out.println( "delete old" );
            Ebean.delete( old );
        }
        Ebean.save( task );
    }


}
