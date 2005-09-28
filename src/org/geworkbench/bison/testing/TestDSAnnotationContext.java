package org.geworkbench.bison.testing;

import org.geworkbench.bison.annotation.DSAnnotationContextManager;
import org.geworkbench.bison.annotation.DSAnnotationContext;
import org.geworkbench.bison.annotation.DSAnnotationType;
import org.geworkbench.bison.datastructure.biocollections.DSDataSet;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import junit.framework.TestCase;

/**
 * @author John Watkinson
 */
public abstract class TestDSAnnotationContext extends TestCase {

    //// Provide concrete implementations to this test case by implementing the methods below

    public abstract DSAnnotationContextManager getAnnotationContextManager();

    public abstract <Q> DSAnnotationType<Q> createAnnotationType(Object label, Class<Q> type);

    //// Test methods

    public void testAnnotations() {
        DSAnnotationContextManager manager = getAnnotationContextManager();
        // Create a dataset
        DSDataSet<DSMicroarray> dataSet = UtilsForTests.createTestExprMicroarraySet();
        DSMicroarray item1 = dataSet.get(0);
        DSMicroarray item2 = dataSet.get(1);
        DSMicroarray item3 = dataSet.get(2);
        // Use the default context
        DSAnnotationContext<DSMicroarray> context = manager.getDefaultContext(dataSet);
        assertNotNull(context);
        // Create an annotation
        DSAnnotationType<String> gender = createAnnotationType("Gender", String.class);
        // Ensure that the type didn't already exist
        assertTrue(context.addAnnotationType(gender));
        // Apply annotation
        context.annotateItem(item1, gender, "Female");
        context.annotateItem(item2, gender, "Male");
        context.annotateItem(item3, gender, "Female");
        // Check values
        assertEquals(context.getAnnotationForItem(item1, gender), "Female");
        assertEquals(context.getAnnotationForItem(item2, gender), "Male");
        assertEquals(context.getAnnotationForItem(item3, gender), "Female");
        // Create another annotation
        DSAnnotationType<Double> bloodPressure = createAnnotationType("Blood Pressure", Double.class);
        // Ensure that the type didn't already exist
        assertTrue(context.addAnnotationType(bloodPressure));
        // Ensure that the type already exists if we add it again
        assertFalse(context.addAnnotationType(bloodPressure));
        // Make sure there are exact two annotations: gender and blood pressure
        assertEquals(context.getNumberOfAnnotationTypes(), 2);
        assertSame(context.getAnnotationType(0), gender);
        assertSame(context.getAnnotationType(1), bloodPressure);
        // Apply annotations
        context.annotateItem(item1, bloodPressure, 85.0);
        context.annotateItem(item2, bloodPressure, 102.0);
        context.annotateItem(item3, bloodPressure, 96.0);
        // Check values
        assertEquals(context.getAnnotationForItem(item1, bloodPressure), 85.0);
        assertEquals(context.getAnnotationForItem(item2, bloodPressure), 102.0);
        assertEquals(context.getAnnotationForItem(item3, bloodPressure), 96.0);
    }
}
