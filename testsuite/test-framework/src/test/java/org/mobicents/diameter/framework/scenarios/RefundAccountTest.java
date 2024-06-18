package org.mobicents.diameter.framework.scenarios;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.mobicents.diameter.framework.TestingFramework;

public class RefundAccountTest
{

  @Test
  public void runTest() throws FileNotFoundException
  {
    TestingFramework tF = new TestingFramework();
    
    tF.executeTest( this.getClass().getClassLoader().getResourceAsStream( "scenarios/refund-account-test.xml" ) );
  }
  
}
