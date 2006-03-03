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
 */
public abstract class BaseTestCase extends TestCase {

	protected abstract void setUp() throws Exception;

	protected abstract void tearDown() throws Exception;

}
