
package org.apache.commons.jrcs;

import junit.framework.*;

public class AllTests extends TestCase {

  public AllTests(String s) {
    super(s);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTestSuite(org.apache.commons.jrcs.diff.DiffTest.class);
    suite.addTestSuite(org.apache.commons.jrcs.rcs.ArchiveTest.class);
    suite.addTestSuite(org.apache.commons.jrcs.rcs.ChangeDeltaTest.class);
    suite.addTestSuite(org.apache.commons.jrcs.rcs.KeywordsFormatTest.class);
    //suite.addTestSuite(org.apache.commons.jrcs.rcs.ParsingTest.class);
    return suite;
  }
}
