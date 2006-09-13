package org.geworkbench.util.sequences;

import junit.framework.TestCase;

public class SequenceAlignerTest extends TestCase {
    private SequenceAligner sequenceAligner = null;

    public SequenceAlignerTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        /**@todo verify the constructors*/
        sequenceAligner = new SequenceAligner(null);
    }

    public void testSequenceAligner() {
        // Currently a no-op
    }

    protected void tearDown() throws Exception {
        sequenceAligner = null;
        super.tearDown();
    }
}