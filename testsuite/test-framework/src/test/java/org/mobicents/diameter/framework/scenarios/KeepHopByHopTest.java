package org.mobicents.diameter.framework.scenarios;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.mobicents.diameter.framework.TestingFramework;

public class KeepHopByHopTest
{

  @Test
  public void runTest() throws FileNotFoundException
  {
    TestingFramework tF = new TestingFramework();
    
    tF.executeTest( this.getClass().getClassLoader().getResourceAsStream( "scenarios/keep-hopbyhop-test.xml" ) );
  }
  
}
