package org.apache.commons.jrcs.rcs;

import junit.framework.*;
import org.apache.commons.jrcs.rcs.*;
import org.apache.commons.jrcs.diff.*;
import org.apache.commons.jrcs.util.*;

public class ChangeDeltaTest extends TestCase
{
    private Archive archive = null;

    String[] v1 = new String[]
    {
        "1",
        "2",
        "3",
        "4",
    };
    Object[] v2 = new String[]
    {
        "a0",
        "1",
        // deleted two lines
        // added three lines
        "a1",
        "a2",
        "a3",
        "4"
    };

    public ChangeDeltaTest(String name)
    {
        super (name);
    }

    protected void setUp()
        throws Exception
    {
        archive = new Archive(v1, "original");
        super.setUp();
    }

    protected void tearDown()
        throws Exception
    {
        archive = null;
        super.tearDown();
    }

    public static Test suite()
    {
        return new TestSuite(ArchiveTest.class);
    }

    public void testChangeDelta()
        throws Exception
    {
        archive.addRevision(v2, "applied change delta");
        archive.addRevision(v1, "back to original");

        String[] rcsFile = (String[]) Diff.stringToArray(archive.toString());
        for(int i = 0; i < rcsFile.length && i < expectedFile.length; i++)
        {
            if (! rcsFile[i].startsWith("date"))
                assertEquals("line " + i, expectedFile[i], rcsFile[i]);
        }
        assertEquals("file size", expectedFile.length, rcsFile.length);
    }

    public void testFileSave()
       throws Exception
   {
      this.testChangeDelta();
      String filePath =System.getProperty("user.home") + java.io.File.separator + "jrcs_test.rcs";
      archive.save(filePath);

      Archive newArc = new Archive(filePath);
      new java.io.File(filePath).delete();

      String[] rcsFile = (String[]) Diff.stringToArray(newArc.toString());
      for(int i = 0; i < rcsFile.length && i < expectedFile.length; i++)
      {
          System.err.println(i + " " +rcsFile[i]);
          if (! rcsFile[i].startsWith("date"))
              assertEquals("line " + i, expectedFile[i], rcsFile[i]);
      }
      assertEquals("file size", expectedFile.length, rcsFile.length);

      assertEquals(archive.toString(), newArc.toString());
   }


    String[] expectedFile = {
            "head\t1.3;",          // 0
            "access;",             // 1
            "symbols;",            // 2
            "locks; strict;",      // 3
            "comment\t@# @;",      // 4
            "",                    // 5
            "",                    // 6
            "1.3",                 // 7
            "date\t2002.09.28.12.55.36;\tauthor juanca;\tstate Exp;",
            "branches;",           // 9
            "next\t1.2;",          //10
            "",                    //11
            "1.2",                 //12
            "date\t2002.09.28.12.53.53;\tauthor juanca;\tstate Exp;",
            "branches;",           //14
            "next\t1.1;",          //15
            "",                    //16
            "1.1",                 //17
            "date\t2002.09.28.12.52.55;\tauthor juanca;\tstate Exp;",
            "branches;",           //19
            "next\t;",             //20
            "",                    //21
            "",                    //22
            "desc",                //23
            "@@",                  //24
            "",                    //25
            "",                    //26
            "1.3",                 //27
            "log",                 //28
            "@back to original",   //29
            "@",                   //30
            "text",                //31
            "@1",                  //32
            "2",                   //33
            "3",                   //34
            "4",                   //35
            "@",                   //36
            "",                    //37
            "",                    //38
            "1.2",                 //39
            "log",                 //40
            "@applied change delta",  //41
            "@",                   //42
            "text",                //43
            "@a0 1",               //44
            "a0",                  //45
            "d2 2",                //46
            "a3 3",                //47
            "a1",                  //48
            "a2",                  //49
            "a3",                  //50
            "@",                   //51
            "",                    //52
            "",                    //53
            "1.1",                 //54
            "log",                 //55
            "@original",           //56
            "@",                   //57
            "text",
            "@d1 1",
            "d3 3",
            "a5 2",
            "2",
            "3",
            "@"
    };
}
