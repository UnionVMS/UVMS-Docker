package eu.europa.ec.fisheries.uvms.docker.validation.external;

import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import eu.europa.ec.fisheries.uvms.docker.validation.reporting.ReportingRestIT;

public class TestRunner {

	public static void main(String[] args) {
        System.out.println("Running report tests!");
        JUnitCore engine = new JUnitCore();
        engine.addListener(new TextListener(System.out)); 
        engine.run(ReportingTestRunner.class);
    }
	
	@RunWith(Suite.class)
	@SuiteClasses({ ReportingRestIT.class })
	public class ReportingTestRunner {

	}
}
