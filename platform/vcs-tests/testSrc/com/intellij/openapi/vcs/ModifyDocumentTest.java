/*
 * Copyright 2000-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.openapi.vcs;

import com.intellij.openapi.diff.DiffManager;
import com.intellij.openapi.editor.markup.MarkupEditorFilterFactory;
import com.intellij.openapi.vcs.ex.Range;

import java.util.Arrays;
import java.util.BitSet;


/**
 * author: lesya
 */
public class ModifyDocumentTest extends BaseLineStatusTrackerTestCase {

  public void testInit() {
    DiffManager instance = DiffManager.getInstance();
    assertNotNull(instance);
    MarkupEditorFilterFactory.createNotFilter(instance.getDiffEditorFilter());
    MarkupEditorFilterFactory.createIsNotDiffFilter();
  }

  public void testSimpleInsert() {
    createDocument("1234\n2345\n3456");
    insertString(2, "a");
    compareRanges();
  }

  public void testUndo() {
    createDocument("1234\n2345\n3456");
    insertString(7, "a");
    compareRanges();
    deleteString(7, 8);
    compareRanges();
  }

  public void testLineEndBeforeModification() {
    createDocument("1234\n2345\n3456");
    insertString(6, "a");
    compareRanges();
    insertString(5, "\n");
    compareRanges();
  }

  public void testLineEndBeforeModification2() {
    createDocument("1234\n2345\n3456");
    insertString(6, "a");
    compareRanges();
    insertString(4, "\n");
    compareRanges();
  }

  public void testInsertDoubleEnterAtEnd() {
    createDocument("1");
    insertString(1, "\n");
    compareRanges();
    insertString(2, "\n");
    compareRanges();
  }

  public void testSimpleInsertAndWholeReplace() {
    createDocument("1234\n2345\n3456");
    insertString(2, "a");
    compareRanges();
    replaceString(0, myDocument.getTextLength(), " ");
    compareRanges();
  }

  public void testSimpleInsert2() {
    createDocument("1\n2\n3\n4\n5");
    insertString(4, "a");
    compareRanges();
  }

  public void testSimpleInsertToEmpty() {
    createDocument("");
    insertString(0, "a");
    compareRanges();
  }

  public void testDoubleSimpleInsert() {
    createDocument("1234\n2345\n3456");
    insertString(2, "a");
    compareRanges();
    insertString(2, "a");
    compareRanges();
  }

  public void testInsertEnter() {
    createDocument("1234\n2345\n3456");
    insertString(2, "\n");
    compareRanges();
  }

  public void testSimpleInsertAndEnterToEmpty() {
    createDocument("");
    insertString(0, "a");
    compareRanges();
    insertString(1, "\n");
    compareRanges();
  }

  public void testInsertEnterAtEnter() {
    createDocument("1234\n2345\n3456");
    insertString(4, "\n");
    compareRanges();
  }

  public void testInsertEnterAtEnterAndSimpleInsert() {
    createDocument("1234\n2345\n3456");
    insertString(4, "\n");
    compareRanges();
    insertString(5, "a");
    compareRanges();
  }

  public void testInsertDoubleEnterAtEnters() {
    createDocument("1234\n2345\n3456");
    insertString(4, "\n");
    compareRanges();
    insertString(10, "\n");
    compareRanges();

  }

  public void testInsertEnterAndSpaceAfterEnter() {
    createDocument("12345\n12345\n12345");
    insertString(5, "\n ");
    compareRanges();
  }

  public void testInsertEnterAndDeleteEnter1() {
    createDocument("12345\n12345\n12345");
    insertString(5, "\n");
    compareRanges();
    deleteString(5, 6);
    compareRanges();
  }

  public void testInsertEnterAndDeleteEnter2() {
    createDocument("12345\n12345\n12345");
    insertString(5, "\n");
    compareRanges();
    deleteString(6, 7);
    compareRanges();
  }

  public void testSimpleDelete() {
    createDocument("1234\n2345\n3456");
    deleteString(2, 3);
    compareRanges();
  }

  public void testDeleteLine() {
    createDocument("1234\n2345\n3456");
    deleteString(0, 5);
    compareRanges();
  }

  public void testDoubleDelete() {
    createDocument("1234\n2345\n3456");
    deleteString(2, 3);
    deleteString(2, 3);
    compareRanges();
  }

  public void testDeleteEnter() {
    createDocument("12345\n23456\n34567");
    deleteString(5, 6);
    compareRanges();
  }

  public void testDeleteDoubleEnter() {
    createDocument("12345\n\n23456\n34567");
    deleteString(5, 6);
    compareRanges();
  }

  //
  public void testDoubleInsertToClass() {
    createDocument("class A{\n\n}");
    insertString(9, "a");
    compareRanges();
    insertString(10, "a");
    compareRanges();
  }

  public void testInsertSymbolAndEnterToClass() {
    createDocument("class A{\n\n}");
    insertString(9, "a");
    compareRanges();
    insertString(10, "\n");
    compareRanges();
  }

  public void testMultiLineReplace2() {
    createDocument("012a\n012b\n012c");
    replaceString(4, 9, "\nx\ny\nz");
    compareRanges();
  }

  public void testChangedLines1() {
    createDocument("class A{\nx\na\nb\nc\n}", "class A{\n1\nx\n2\n}");
    compareRanges();
  }

  public void testMultiLineReplace1() {
    createDocument("012a\n012b\n012c\n012d\n012e");
    replaceString(5, 6, "a");
    compareRanges();
    replaceString(4, 14, "\nx");
    compareRanges();
  }

  public void testInsertAndModify() {
    createDocument("a\nb\nc\nd");
    insertString(3, "\n");
    compareRanges();
    insertString(4, "\n");
    compareRanges();
    insertString(6, " ");
    compareRanges();
  }

  public void testRangesShouldMerge() {
    createDocument("1\n2\n3\n4");
    insertString(1, "1");
    compareRanges();
    insertString(6, "3");
    compareRanges();
    insertString(3, "2");
    compareRanges();
  }

  public void testShiftRangesAfterChange() {
    createDocument("1\n2\n3\n4");
    insertString(7, "4");
    compareRanges();
    insertString(0, "\n");
    compareRanges();
    insertString(0, "\n");
    compareRanges();
    insertString(0, "\n");
    compareRanges();
  }

  public void testInsertBeforeChange() {
    createDocument("   11\n   3 \n   44\n   55\n   6\n   7\n   88\n   ", "   1\n   2\n   3 \n   4\n   5\n   6\n   7\n   8\n   ");
    insertString(9, "3");
    compareRanges();
    assertTextContentIs("   11\n   33 \n   44\n   55\n   6\n   7\n   88\n   ");
    insertString(9, "aaa\nbbbbbbbb\ncccc\ndddd");
    compareRanges();
  }


  public void testUndoDeletion() {
    createDocument("1\n2\n3\n4\n5\n6\n7\n");
    deleteString(4, 6);
    assertTextContentIs("1\n2\n4\n5\n6\n7\n");
    compareRanges();
    insertString(4, "3\n");
    compareRanges();
  }

  public void testUndoDeletion2() {
    createDocument("1\n2\n3\n4\n5\n6\n7\n");
    deleteString(3, 5);
    assertTextContentIs("1\n2\n4\n5\n6\n7\n");
    compareRanges();
    insertString(4, "\n3");
    compareRanges();
  }

  public void testSRC17123() {
    createDocument("package package;\n" + "\n" + "public class Class3 {\n" + "    public int i1;\n" + "    public int i2;\n" +
                   "    public int i3;\n" + "    public int i4;\n" + "\n" + "    public static void main(String[] args) {\n" + "\n" +
                   "    }\n" + "}");
    deleteString(39, 58);
    compareRanges();
    assertTextContentIs("package package;\n" + "\n" + "public class Class3 {\n" + "    public int i2;\n" + "    public int i3;\n" +
                        "    public int i4;\n" + "\n" + "    public static void main(String[] args) {\n" + "\n" + "    }\n" + "}");

    deleteString(39, myDocument.getTextLength());
    compareRanges();
    deleteString(myDocument.getTextLength() - 1, myDocument.getTextLength());
  }

  public void testUnexpetedDeletedRange() {
    createDocument("    public class\n    bbb\n");
    insertString(17, "    \n");
    assertTextContentIs("    public class\n    \n    bbb\n");
    compareRanges();
    deleteString(17, 21);
    assertTextContentIs("    public class\n\n    bbb\n");
    compareRanges();
    insertString(18, "    \n");
    assertTextContentIs("    public class\n\n    \n    bbb\n");
    compareRanges();
    deleteString(18, 22);
    assertTextContentIs("    public class\n\n\n    bbb\n");
    compareRanges();
    deleteString(4, 10);
    assertTextContentIs("     class\n\n\n    bbb\n");
    compareRanges();
    insertString(4, "p");
    assertTextContentIs("    p class\n\n\n    bbb\n");
    compareRanges();
    insertString(5, "r");
    assertTextContentIs("    pr class\n\n\n    bbb\n");
    compareRanges();
    insertString(6, "i");
    assertTextContentIs("    pri class\n\n\n    bbb\n");
    compareRanges();
  }

  public void testSrc29814() {
    String text = "111\n" + "222\n" + "333\n";

    createDocument(text);
    deleteString(0, text.length());
    compareRanges();
    assertTextContentIs("");
    insertString(0, "222\n");
    compareRanges();
    deleteString(0, 4);
    compareRanges();
    insertString(0, text);
    compareRanges();
  }

  public void testDeletingTwoMethods() {

    String part1 = "class Foo {\n" + "  public void method1() {\n" + "    // something\n" + "  }\n" + "\n";

    String part2 = "  public void method2() {\n" + "    // something\n" + "  }\n" + "\n" + "  public void method3() {\n" +
                   "    // something\n" + "  }\n";

    String part3 = "\n" + "  public void method4() {\n" + "    // something\n" + "  }\n" + "}";

    String text = part1 + part2 + part3;

    createDocument(text);
    deleteString(part1.length(), part1.length() + part2.length());
    assertTextContentIs(part1 + part3);
    assertEqualRanges(Arrays.asList(new Range(5, 5, 5, 12)), myTracker.getRanges());

    deleteString(part1.length(), part1.length() + 1);
    compareRanges();
  }

  public void testBug1() {
    createDocument("1\n2\n3\n4\n");
    deleteString(4, 6);
    compareRanges();
    insertString(3, "X");
    compareRanges();
  }

  public void testBug2() {
    createDocument("1\n2\n3\n4\n5\n6\n");
    deleteString(4, 6);
    compareRanges();
    deleteString(8, 10);
    compareRanges();
    insertString(4, "3\n8\n");
    compareRanges();
  }

  public void testBug3() {
    createDocument("\n\n00\n556\n");

    deleteString(3, 6);
    deleteString(1, 4);
    deleteString(0, 2);
    insertString(0, "\n\n32\n");
    deleteString(1, 4);
  }

  public void testBug4() {
    createDocument("\n5\n30\n5240\n32\n46\n\n\n\n51530\n\n");

    insertString(3, "40\n1\n2");
    deleteString(10, 25);
    deleteString(1, 5);
    insertString(9, "30\n\n23");
    deleteString(2, 11);
  }

  public void testBug5() {
    createDocument("\n");

    replaceString(0, 0, "\n\n6406");
    deleteString(1, 2);
    insertString(1, "\n11\n5");
    insertString(3, "130");
    replaceString(8, 8, "3");
    replaceString(9, 14, "4\n\n56\n21\n");
    replaceString(3, 17, " 60246");
    insertString(7, "01511");
    insertString(9, "2633\n33");
    deleteString(16, 17);
    deleteString(15, 19);
    deleteString(4, 15);
    replaceString(2, 3, "\n34\n\n310\n");
    deleteString(2, 3);
    deleteString(8, 10);
    insertString(1, "051");
  }

  public void testTrimSpaces1() {
    createDocument("a \nb \nc ");
    insertString(0, "x");
    stripTrailingSpaces();

    BitSet lines = new BitSet();
    lines.set(0);
    rollback(lines);

    stripTrailingSpaces();
    assertTextContentIs("a \nb \nc ");
  }

  public void testTrimSpaces2() {
    createDocument("a \nb \nc ");
    insertString(0, "x");
    stripTrailingSpaces();

    assertTextContentIs("xa\nb \nc ");
  }

  public void testTrimSpaces3() {
    createDocument("a \nb \nc ");
    insertString(6, "x");
    insertString(0, "x");
    stripTrailingSpaces();

    BitSet lines = new BitSet();
    lines.set(2);
    rollback(lines);

    stripTrailingSpaces();
    assertTextContentIs("xa\nb \nc ");
  }

  public void testInsertion1() {
    createDocument("X\nX\nX\n");
    insertString(0, "X\n");

    assertEqualRanges(Arrays.asList(new Range(0, 1, 0, 0)), myTracker.getRanges());
  }

  public void testInsertion2() {
    createDocument("X\nX\nX\n");
    insertString(2, "X\n");

    assertEqualRanges(Arrays.asList(new Range(1, 2, 1, 1)), myTracker.getRanges());
  }

  public void testInsertion3() {
    createDocument("X\nX\nX\n");
    insertString(4, "X\n");

    assertEqualRanges(Arrays.asList(new Range(2, 3, 2, 2)), myTracker.getRanges());
  }

  public void testInsertion4() {
    createDocument("X\nX\nX\n");
    insertString(6, "X\n");

    assertEqualRanges(Arrays.asList(new Range(3, 4, 3, 3)), myTracker.getRanges());
  }

  public void testInsertion5() {
    createDocument("Z\nX\nX\n");
    replaceString(0, 1, "Y");
    insertString(2, "X\n");

    assertEqualRanges(Arrays.asList(new Range(0, 2, 0, 1)), myTracker.getRanges());
  }

  public void testInsertion6() {
    createDocument("X\nX\nX\n");
    replaceString(0, 1, "Y");
    insertString(4, "X\n");

    assertEqualRanges(Arrays.asList(new Range(0, 1, 0, 1), new Range(2, 3, 2, 2)), myTracker.getRanges());
  }

  public void testInsertion7() {
    createDocument("X\nX\nX\n");
    replaceString(0, 1, "Y");
    insertString(6, "X\n");

    assertEqualRanges(Arrays.asList(new Range(0, 1, 0, 1), new Range(3, 4, 3, 3)), myTracker.getRanges());
  }
}
