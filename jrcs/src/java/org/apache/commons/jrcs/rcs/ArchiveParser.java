/* Generated By:JavaCC: Do not edit this line. ArchiveParser.java */
package org.apache.commons.jrcs.rcs;

import java.util.Map;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Parses an RCS/CVS style version control archive into an Archive.
 * This class is NOT thread safe.
 * 
 * @author <a href="mailto:juanco@suigeneris.org">Juanco Anez</a>
 * @version $Id$
 * @see Archive
 */
class ArchiveParser implements ArchiveParserConstants {

  static final String ident = "RCS ArchiveParser Parser $version$:";

  public static void main(String args[]) {
    ArchiveParser parser;
    if (args.length == 0)
    {
      System.out.println(ident + "  Reading from standard input . . .");
      parser = new ArchiveParser(System.in);
    }
    else if (args.length == 1)
    {
      System.out.println(ident + "  Reading from file " + args[0] + " . . .");
      try
      {
        parser = new ArchiveParser(new FileInputStream(args[0]));
      }
      catch (java.io.FileNotFoundException e)
      {
        System.out.println(ident + "  File " + args[0] + " not found.");
        return;
      }
    }
    else
    {
      System.out.println(ident+"  Usage is one of:");
      System.out.println("         java ArchiveParser < inputfile");
      System.out.println("OR");
      System.out.println("         java ArchiveParser inputfile");
      return;
    }
    parser.parse();
  }

  public static void load(Archive arc, InputStream input) throws ParseException
  {
      ArchiveParser parser = new ArchiveParser(input);
      parser.archive(arc);
  }

  public static void load(Archive arc, String fname) throws FileNotFoundException, ParseException
  {
    load(arc, new FileInputStream(fname) );
  }

  public void parse()
  {
    try
    {
      archive(null);
      System.out.println("RCS ArchiveParser Parser version 1.1:  RCS ArchiveParser parsed successfully.");
    }
    catch (ParseException e)
    {
      System.out.println("RCS ArchiveParser Parser version 1.1:  Encountered errors during parse.");
    }
  }

/**
* PARSER STARTS HERE
*/
  final public void archive(Archive arc) throws ParseException {
    admin(arc);
    label_1:
    while (true) {
      if (jj_2_1(3)) {
        ;
      } else {
        break label_1;
      }
      delta(arc);
    }
    desc(arc);
    label_2:
    while (true) {
      if (jj_2_2(3)) {
        ;
      } else {
        break label_2;
      }
      text(arc);
    }
    jj_consume_token(0);
  }

  final public void admin(Archive arc) throws ParseException {
    head(arc);
    if (jj_2_3(3)) {
      branch(arc);
    } else {
      ;
    }
    access(arc);
    symbols(arc);
    locks(arc);
    optionals(arc);
  }

  final public void optionals(Archive arc) throws ParseException {
    label_3:
    while (true) {
      if (jj_2_4(3)) {
        ;
      } else {
        break label_3;
      }
      if (jj_2_5(3)) {
        comment(arc);
      } else if (jj_2_6(3)) {
        expand(arc);
      } else if (jj_2_7(3)) {
        newPhrase(arc.phrases);
      } else {
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
  }

  final public void newPhrases(Map map) throws ParseException {
    label_4:
    while (true) {
      if (jj_2_8(3)) {
        ;
      } else {
        break label_4;
      }
      newPhrase(map);
    }
  }

  final public void head(Archive arc) throws ParseException {
    Version v;
    jj_consume_token(HEAD);
    if (jj_2_9(3)) {
      v = version();
                           arc.setHead(v);
    } else {
      ;
    }
    jj_consume_token(28);
  }

  final public void branch(Archive arc) throws ParseException {
  Version v;
    jj_consume_token(BRANCH);
    if (jj_2_10(3)) {
      v = version();
                             arc.setBranch(v);
    } else {
      ;
    }
    jj_consume_token(28);
  }

  final public void access(Archive arc) throws ParseException {
    String name;
    jj_consume_token(ACCESS);
    label_5:
    while (true) {
      if (jj_2_11(3)) {
        ;
      } else {
        break label_5;
      }
      name = id();
                           arc.addUser(name);
    }
    jj_consume_token(28);
  }

  final public void symbols(Archive arc) throws ParseException {
    String  s;
    Version v;
    jj_consume_token(SYMBOLS);
    label_6:
    while (true) {
      if (jj_2_12(3)) {
        ;
      } else {
        break label_6;
      }
      s = sym();
      jj_consume_token(29);
      v = version();
                                            arc.addSymbol(s, v);
    }
    jj_consume_token(28);
  }

  final public void locks(Archive arc) throws ParseException {
    String  name;
    Version v;
    jj_consume_token(LOCKS);
    label_7:
    while (true) {
      if (jj_2_13(3)) {
        ;
      } else {
        break label_7;
      }
      name = id();
      jj_consume_token(29);
      v = version();
                                            arc.addLock(name, v);
    }
    jj_consume_token(28);
    if (jj_2_14(3)) {
      jj_consume_token(STRICT);
      jj_consume_token(28);
               arc.setStrictLocking(true);
    } else {
      ;
    }
  }

  final public void comment(Archive arc) throws ParseException {
  String s;
    jj_consume_token(COMMENT);
    if (jj_2_15(3)) {
      s = string();
                            arc.setComment(s);
    } else {
      ;
    }
    jj_consume_token(28);
  }

  final public void expand(Archive arc) throws ParseException {
 String s;
    jj_consume_token(EXPAND);
    if (jj_2_16(3)) {
      s = string();
                           arc.setExpand(s);
    } else {
      ;
    }
    jj_consume_token(28);
  }

  final public void newPhrase(Map map) throws ParseException {
  String key;
  String value;
  StringBuffer values = new StringBuffer();
    key = id();
    label_8:
    while (true) {
      if (jj_2_17(3)) {
        ;
      } else {
        break label_8;
      }
      value = word();
                     values.append(" " + value);
    }
    jj_consume_token(28);
    if (map != null) map.put(key, values.toString());
  }

  final public String word() throws ParseException {
  String result;
    if (jj_2_18(2)) {
      result = pair();
    } else if (jj_2_19(3)) {
      result = simpleWord();
    } else {
      jj_consume_token(-1);
      throw new ParseException();
    }
    {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  final public String simpleWord() throws ParseException {
    String  result;
    Version v;
    if (jj_2_20(3)) {
      result = id();
    } else if (jj_2_21(3)) {
      v = version();
                 result = v.toString();
    } else if (jj_2_22(3)) {
      result = string();
    } else {
      jj_consume_token(-1);
      throw new ParseException();
    }
   {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  final public String pair() throws ParseException {
    String left;
    String right;
    left = simpleWord();
    jj_consume_token(29);
    right = simpleWord();
      {if (true) return left + ":" + right;}
    throw new Error("Missing return statement in function");
  }

  final public void desc(Archive arc) throws ParseException {
  String s;
    jj_consume_token(DESC);
    s = string();
                          arc.setDesc(s);
  }

  final public void delta(Archive arc) throws ParseException {
    Version   v;
    Node      node;
    int[]     d;
    String    s;
    v = version();
       node = arc.newNode(v);
    jj_consume_token(DATE);
    d = date();
                              node.setDate(d);
    jj_consume_token(28);
    jj_consume_token(AUTHOR);
    s = id();
                              node.setAuthor(s);
    jj_consume_token(28);
    jj_consume_token(STATE);
    if (jj_2_23(3)) {
      s = id();
                            node.setState(s);
    } else {
      ;
    }
    jj_consume_token(28);
    jj_consume_token(BRANCHES);
    label_9:
    while (true) {
      if (jj_2_24(3)) {
        ;
      } else {
        break label_9;
      }
      v = version();
                                 node.addBranch(arc.newBranchNode(v));
    }
    jj_consume_token(28);
    jj_consume_token(NEXT);
    if (jj_2_25(3)) {
      v = version();
                                 node.setRCSNext(arc.newNode(v));
    } else {
      ;
    }
    jj_consume_token(28);
    newPhrases(node.phrases);
  }

  final public void text(Archive arc) throws ParseException {
  Version v;
  Node node;
  String log;
  String txt;
    v = version();
      node = arc.getNode(v);
    jj_consume_token(LOG);
    log = string();
      node.setLog(log);
    newPhrases(node.phrases);
    jj_consume_token(TEXT);
    txt = string();
       node.setText(txt);
  }

  final public String id() throws ParseException {
                    Token t;
    t = jj_consume_token(ID);
                                             {if (true) return t.image;}
    throw new Error("Missing return statement in function");
  }

  final public String sym() throws ParseException {
  Token t;
    if (jj_2_26(3)) {
      t = jj_consume_token(SYM);
    } else if (jj_2_27(3)) {
      t = jj_consume_token(ID);
    } else {
      jj_consume_token(-1);
      throw new ParseException();
    }
    {if (true) return t.image;}
    throw new Error("Missing return statement in function");
  }

  final public Version version() throws ParseException {
  Version v;
  int   n, r;
    n = num();
    v = new Version(n);
    label_10:
    while (true) {
      if (jj_2_28(3)) {
        ;
      } else {
        break label_10;
      }
      jj_consume_token(30);
      n = num();
                    v.__addBranch(n);
    }
    {if (true) return v;}
    throw new Error("Missing return statement in function");
  }

  final public int[] date() throws ParseException {
  int[] n = new int[6];
    n[0] = num();
    jj_consume_token(30);
    n[1] = num();
    jj_consume_token(30);
    n[2] = num();
    jj_consume_token(30);
    n[3] = num();
    jj_consume_token(30);
    n[4] = num();
    jj_consume_token(30);
    n[5] = num();
   {if (true) return n;}
    throw new Error("Missing return statement in function");
  }

  final public int num() throws ParseException {
              Token t;
    t = jj_consume_token(NUM);
                                      {if (true) return Integer.parseInt(t.image);}
    throw new Error("Missing return statement in function");
  }

  final public String string() throws ParseException {
 Token t;
    t = jj_consume_token(STRING);
                 {if (true) return Archive.unquoteString(t.image);}
    throw new Error("Missing return statement in function");
  }

  final private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    return !jj_3_1();
  }

  final private boolean jj_2_2(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    return !jj_3_2();
  }

  final private boolean jj_2_3(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    return !jj_3_3();
  }

  final private boolean jj_2_4(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    return !jj_3_4();
  }

  final private boolean jj_2_5(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    return !jj_3_5();
  }

  final private boolean jj_2_6(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    return !jj_3_6();
  }

  final private boolean jj_2_7(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    return !jj_3_7();
  }

  final private boolean jj_2_8(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    return !jj_3_8();
  }

  final private boolean jj_2_9(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    return !jj_3_9();
  }

  final private boolean jj_2_10(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    return !jj_3_10();
  }

  final private boolean jj_2_11(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    return !jj_3_11();
  }

  final private boolean jj_2_12(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    return !jj_3_12();
  }

  final private boolean jj_2_13(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    return !jj_3_13();
  }

  final private boolean jj_2_14(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    return !jj_3_14();
  }

  final private boolean jj_2_15(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    return !jj_3_15();
  }

  final private boolean jj_2_16(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    return !jj_3_16();
  }

  final private boolean jj_2_17(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    return !jj_3_17();
  }

  final private boolean jj_2_18(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    return !jj_3_18();
  }

  final private boolean jj_2_19(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    return !jj_3_19();
  }

  final private boolean jj_2_20(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    return !jj_3_20();
  }

  final private boolean jj_2_21(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    return !jj_3_21();
  }

  final private boolean jj_2_22(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    return !jj_3_22();
  }

  final private boolean jj_2_23(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    return !jj_3_23();
  }

  final private boolean jj_2_24(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    return !jj_3_24();
  }

  final private boolean jj_2_25(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    return !jj_3_25();
  }

  final private boolean jj_2_26(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    return !jj_3_26();
  }

  final private boolean jj_2_27(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    return !jj_3_27();
  }

  final private boolean jj_2_28(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    return !jj_3_28();
  }

  final private boolean jj_3R_24() {
    if (jj_scan_token(NUM)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3R_11() {
    if (jj_3R_17()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    if (jj_scan_token(DATE)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    if (jj_3R_25()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_9() {
    if (jj_3R_17()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3R_13() {
    if (jj_scan_token(BRANCH)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_10()) jj_scanpos = xsp;
    else if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    if (jj_scan_token(28)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3R_22() {
    if (jj_3R_23()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    if (jj_scan_token(29)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    if (jj_3R_23()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3R_20() {
    if (jj_scan_token(STRING)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_8() {
    if (jj_3R_16()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_7() {
    if (jj_3R_16()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_6() {
    if (jj_3R_15()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_4() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_5()) {
    jj_scanpos = xsp;
    if (jj_3_6()) {
    jj_scanpos = xsp;
    if (jj_3_7()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    } else if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    } else if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_5() {
    if (jj_3R_14()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3R_18() {
    if (jj_scan_token(ID)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3R_25() {
    if (jj_3R_24()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_22() {
    if (jj_3R_20()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_21() {
    if (jj_3R_17()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_20() {
    if (jj_3R_18()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3R_23() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_20()) {
    jj_scanpos = xsp;
    if (jj_3_21()) {
    jj_scanpos = xsp;
    if (jj_3_22()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    } else if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    } else if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_3() {
    if (jj_3R_13()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_28() {
    if (jj_scan_token(30)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    if (jj_3R_24()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3R_17() {
    if (jj_3R_24()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_28()) { jj_scanpos = xsp; break; }
      if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    }
    return false;
  }

  final private boolean jj_3_2() {
    if (jj_3R_12()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_19() {
    if (jj_3R_23()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_1() {
    if (jj_3R_11()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_18() {
    if (jj_3R_22()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3R_21() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_18()) {
    jj_scanpos = xsp;
    if (jj_3_19()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    } else if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_27() {
    if (jj_scan_token(ID)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_26() {
    if (jj_scan_token(SYM)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3R_19() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_26()) {
    jj_scanpos = xsp;
    if (jj_3_27()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    } else if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_17() {
    if (jj_3R_21()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_16() {
    if (jj_3R_20()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3R_16() {
    if (jj_3R_18()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_17()) { jj_scanpos = xsp; break; }
      if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    }
    if (jj_scan_token(28)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_15() {
    if (jj_3R_20()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3R_15() {
    if (jj_scan_token(EXPAND)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_16()) jj_scanpos = xsp;
    else if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    if (jj_scan_token(28)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_13() {
    if (jj_3R_18()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    if (jj_scan_token(29)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    if (jj_3R_17()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3R_14() {
    if (jj_scan_token(COMMENT)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_15()) jj_scanpos = xsp;
    else if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    if (jj_scan_token(28)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_12() {
    if (jj_3R_19()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    if (jj_scan_token(29)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    if (jj_3R_17()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_25() {
    if (jj_3R_17()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_24() {
    if (jj_3R_17()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3R_12() {
    if (jj_3R_17()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    if (jj_scan_token(LOG)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    if (jj_3R_20()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_23() {
    if (jj_3R_18()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_14() {
    if (jj_scan_token(STRICT)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    if (jj_scan_token(28)) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_11() {
    if (jj_3R_18()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  final private boolean jj_3_10() {
    if (jj_3R_17()) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) return false;
    return false;
  }

  public ArchiveParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  public Token token, jj_nt;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  public boolean lookingAhead = false;
  private boolean jj_semLA;

  public ArchiveParser(java.io.InputStream stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new ArchiveParserTokenManager(jj_input_stream);
    token = new Token();
    token.next = jj_nt = token_source.getNextToken();
  }

  public void ReInit(java.io.InputStream stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    token.next = jj_nt = token_source.getNextToken();
  }

  public ArchiveParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new ArchiveParserTokenManager(jj_input_stream);
    token = new Token();
    token.next = jj_nt = token_source.getNextToken();
  }

  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    token.next = jj_nt = token_source.getNextToken();
  }

  public ArchiveParser(ArchiveParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    token.next = jj_nt = token_source.getNextToken();
  }

  public void ReInit(ArchiveParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    token.next = jj_nt = token_source.getNextToken();
  }

  final private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken = token;
    if ((token = jj_nt).next != null) jj_nt = jj_nt.next;
    else jj_nt = jj_nt.next = token_source.getNextToken();
    if (token.kind == kind) {
      return token;
    }
    jj_nt = token;
    token = oldToken;
    throw generateParseException();
  }

  final private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    return (jj_scanpos.kind != kind);
  }

  final public Token getNextToken() {
    if ((token = jj_nt).next != null) jj_nt = jj_nt.next;
    else jj_nt = jj_nt.next = token_source.getNextToken();
    return token;
  }

  final public Token getToken(int index) {
    Token t = lookingAhead ? jj_scanpos : token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  final public ParseException generateParseException() {
    Token errortok = token.next;
    int line = errortok.beginLine, column = errortok.beginColumn;
    String mess = (errortok.kind == 0) ? tokenImage[0] : errortok.image;
    return new ParseException("Parse error at line " + line + ", column " + column + ".  Encountered: " + mess);
  }

  final public void enable_tracing() {
  }

  final public void disable_tracing() {
  }

}
