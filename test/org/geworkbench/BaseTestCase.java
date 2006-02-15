package org.geworkbench;

/*
 * The geworkbench project
 * 
 * Copyright (c) 2006 Columbia University 
 *
 */

import junit.framework.TestCase;

/**
 * @author keshav
 * @version $Id: BaseTestCase.java,v 1.1 2006-02-15 00:03:12 keshav Exp $
 */
public abstract class BaseTestCase extends TestCase {

     protected abstract void setUp() throws Exception;

    protected abstract void tearDown() throws Exception;

}
