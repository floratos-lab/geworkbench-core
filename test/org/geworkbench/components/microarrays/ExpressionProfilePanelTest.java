package org.geworkbench.components.microarrays;

/*
 * The geworkbench project
 * 
 * Copyright (c) 2006 Columbia University 
 *
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geworkbench.BaseTestCase;
import org.geworkbench.bison.datastructure.biocollections.microarrays.CSMicroarraySet;
import org.geworkbench.bison.datastructure.biocollections.views.CSMicroarraySetView;
import org.geworkbench.events.MicroarraySetViewEvent;

/**
 * @author keshav
 * @version $Id: ExpressionProfilePanelTest.java,v 1.1 2006-02-14 23:19:46 keshav Exp $
 */
public class ExpressionProfilePanelTest extends BaseTestCase {

    protected final Log log = LogFactory.getLog( getClass() );

    private ExpressionProfilePanel expp = null;

    /**
     * Makes it evident that if there is a problem with the invocation ExpressionProfilePanel, the test will show this.
     * 
     * @throws Exception
     */
    protected void setUp() throws Exception {

        expp = new ExpressionProfilePanel();

    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {

        expp = null;
    }

    /**
     * Tests to make sure the widgets are loaded as expected.
     */
    public void testJbInit() {

        assertNotNull( expp.getGraphPanel() );
        assertNotNull( expp.getGraph() );
        assertNotNull( expp.getChart() );

    }

    /**
     * 
     * 
     */
    @SuppressWarnings("unchecked")
    public void testFireModelEventChangedWithMaSetView() {

        expp.setMaSetView( new CSMicroarraySetView( new CSMicroarraySet() ) );
        expp.fireModelChangedEvent( new MicroarraySetViewEvent( new Object() ) );

        log.debug( "Panel has " + expp.getGraphPanel().getComponentCount() + " components." );

        assertEquals( expp.getGraphPanel().getComponentCount(), 0 );

    }

    /**
     * 
     * 
     */
    @SuppressWarnings("unchecked")
    public void testFireModelEventChangedWithoutMaSetView() {

        expp.fireModelChangedEvent( new MicroarraySetViewEvent( new Object() ) );

        log.debug( "Panel has " + expp.getGraphPanel().getComponentCount() + " component." );

        assertEquals( expp.getGraphPanel().getComponentCount(), 1 );

    }

}
