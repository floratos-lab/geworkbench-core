package org.geworkbench.analysis;

import java.util.Random;

import org.geworkbench.util.FishersExactTest;

public class FishersExactTestTest {

    public static void main(String[] args) throws Exception {

        int a=1, b=15, c=2, d=306;
        System.out.println(a+" "+b+" "+c+" "+d+" "+FishersExactTest.getRightSideOneTailedP(a, b, c, d)); // 0.1414
        a=15; b=1; c=306; d=2;
        System.out.println(a+" "+b+" "+c+" "+d+" "+FishersExactTest.getRightSideOneTailedP(a, b, c, d)); // 0.9933
        a=2; b=10; c=10; d=2;
        System.out.println(a+" "+b+" "+c+" "+d+" "+FishersExactTest.getRightSideOneTailedP(a, b, c, d));
        a=10; b=2; c=2; d=10;
        System.out.println(a+" "+b+" "+c+" "+d+" "+FishersExactTest.getRightSideOneTailedP(a, b, c, d));

        for(int i=0; i<100; i++)
        	test();
    }
    
    private static void test() {
    	Random random = new Random();
    	int a = random.nextInt(100);
    	int b = random.nextInt(100);
    	int c = random.nextInt(100);
    	int d = random.nextInt(100);
        System.out.println(a+" "+b+" "+c+" "+d+" "+FishersExactTest.getRightSideOneTailedP(a, b, c, d));
    }

}
