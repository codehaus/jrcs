package org.apache.commons.jrcs.diff;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Maven" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Maven", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.jrcs.util.ToString;
import java.util.Iterator;


/**
 * Implements a differencing engine that works on arrays of {@link Object Object}.
 *
 * <p>Within this library, the word <i>text</i> means a unit of information
 * subject to differencing.
 *
 * <p>Text is represented as <code>Object[]</code> because
 * the diff engine is capable of handling more than plain ASCII. In fact,
 * arrays of any type that implements
 * {@link java.lang.Object#hashCode hashCode()} and
 * {@link java.lang.Object#equals equals()}
 * correctly can be subject to differencing using this
 * library.</p>
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:juanco@suigeneris.org">Juanco Anez</a>
 * @see     Delta
 */

public class Diff
        extends ToString
{

    /**
     * The system line separator is used for standard kinds of output
     */
    public static final String NL = System.getProperty("line.separator");

    /**
     * For RCS kind of output a plain newline is required.
     */
    public static final String RCS_EOL = "\n";


    /**
     * Mark for non-matching items in source sequences.
     */
    static final int NOT_FOUND_i = -2;

    /**
     * Mark for non-matching items in target sequences. They have to
     * be different in source and target because they would match
     * otherwise.
     */
    static final int NOT_FOUND_j = -1;

    /**
     * Marker for End-Of-Sequence. It must be the same for both sequences.
     */
    static final int EOS = Integer.MAX_VALUE;


    /**
     * The source (original) sequence is stored here.
     */
    protected Object[] orig;

    /**
     * Constructs a differencer based on an original/source sequence.
     * @param o the original sequences.
     */
    public Diff(Object[] o)
    {
        if (o == null)
        {
            throw new IllegalArgumentException();
        }
        orig = o;
    }

    /**
     * Calculates the differences between two input sequences.
     * @param orig The original/source sequence.
     * @param rev The target/revised sequence.
     * @return The differences as a {@link Revision Revision} object.
     * @throws DifferentiationFailedException if the algorithm cannot
     * construct the revised sequence out of the original one using the generated
     * edit script.
     */
    public static Revision diff(Object[] orig, Object[] rev)
            throws DifferentiationFailedException
    {
        if (orig == null || rev == null)
        {
            throw new IllegalArgumentException();
        }

        return new Diff(orig).diff(rev);
    }

    /**
     * Compares two sequences.
     * @param orig The original sequence.
     * @param rev Revised sequences.
     * @return true if the sequences are identical, false otherwise.
     */
    public static boolean compare(Object[] orig, Object[] rev)
    {
        if (orig.length != rev.length)
        {
            return false;
        }
        else
        {
            for (int i = 0; i < orig.length; i++)
            {
                if (!orig[i].equals(rev[i]))
                {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Scans a sequence of indexes for a value.
     * @param ndx The sequence to scan.
     * @param i The starting index.
     * @param target The value to scan for.
     * @return The index in the sequence of a value less-than or equal to
     * the target value.
     */
    protected int scan(int[] ndx, int i, int target)
    {
        while (ndx[i] < target)
        {
            i++;
        }
        return i;
    }

    /**
     * Calculates the differences between the sequence given as original/source
     * and the given target/revised sequence.
     * <p>
     * This algorithm was designed by
     * <a href="juanca@suigeneris.org">Juancarlo Añez</a> way back when
     * there weren't any good implementations of diff in Java.
     *
     * @param rev The target/revised sequence.
     * @return The differences as a {@link Revision Revision} object.
     * @throws DifferentiationFailedException if the algorithm cannot
     * construct the revised sequence out of the original one using the generated
     * edit script.
     */
    public Revision diff(Object[] rev)
        throws DifferentiationFailedException
    {
        Map eqs = buildEqSet(orig, rev);
        int[] indx = buildIndex(eqs, orig, NOT_FOUND_i);
        int[] jndx = buildIndex(eqs, rev, NOT_FOUND_j);
        eqs = null; // let gc know we're done with this

        Revision deltas = new Revision();
        int i = 0;
        int j = 0;

        // skip matching
        for (; indx[i] != EOS && indx[i] == jndx[j]; i++, j++)
        {
            /* void */
        }

        while (indx[i] != jndx[j]) // only equal if both == EOS
        {
            // indexed items in each sequence are different
            int ia = i;
            int ja = j;

            // find the delta
            do
            {
                while (jndx[j] < 0 || jndx[j] < indx[i])
                {
                    j++;
                }
                while (indx[i] < 0 || indx[i] < jndx[j])
                {
                    i++;
                }
            }
            while (indx[i] != jndx[j]);

            // they are equal, reverse any exedent matches
            while (i > ia && j > ja && indx[i - 1] == jndx[j - 1])
            {
                --i;
                --j;
            }

            deltas.addDelta(Delta.newDelta(new Chunk(orig, ia, i - ia),
                    new Chunk(rev, ja, j - ja)));

            // skip matching
            for (; indx[i] != EOS && indx[i] == jndx[j]; i++, j++)
            {
                /* void */
            }
        }
        try
        {
            /** @todo This should be removed at some point as it is
             * just a verification and it has considerable impact
             * on algorithm execution time
             */

            System.out.println(deltas.toString());
            if (!compare(deltas.patch(orig), rev))
            {
                throw new DifferentiationFailedException();
            }
        }
        catch(PatchFailedException e)
        {
          throw new DifferentiationFailedException(e.getMessage());
        }
        return deltas;
    }

    /**
     * Builds the "equivalence set" for two input sequences.
     * <p>
     * The equivalence set is a map that contains sequence items
     * as keys, and a list of source-sequence indexes as values.
     * <p>
     * Only items that appear in both input sequences are mapped, and
     * the indexes in the map values correspond to the positions in the
     * original/source sequence where the items were found.
     *
     * @param orig The original/source sequence.
     * @param rev The target/revised sequence.
     * @return The equivalence set.
     */
    protected Map buildEqSet(Object[] orig, Object[] rev)
    {
        Set items = new HashSet(Arrays.asList(orig));
        items.retainAll(Arrays.asList(rev));

        Map eqs = new HashMap();
        for (int i = 0; i < orig.length; i++)
        {
            if (items.contains(orig[i]))
            {
                List matches = (List) eqs.get(orig[i]);
                if (matches == null)
                {
                    matches = new LinkedList();
                    eqs.put(orig[i], matches);
                }
                matches.add(new Integer(i));
            }
        }
        return eqs;
    }

    /**
     * Indexes a sequence according to a given equivalence set.
     * <p>
     * Items in the input sequene are searched for in the equivalence set.
     * For each item, the index with the less absolute distance to the item
     * number is chosen from the list in the eauivalence set.
     * <p>
     * Items which do not appear in the equivalence set get an index of
     * NF (not found).
     *
     * @param eqs The equivalence set.
     * @param seq The sequence to index.
     * @param NF The value to use for sequence items which do not appear
     * on the equivalence set.
     * @return The index.
     */
    protected int[] buildIndex(Map eqs, Object[] seq, int NF)
    {
        int[] result = new int[seq.length + 1];
        for (int i = 0; i < seq.length; i++)
        {
            List matches = (List) eqs.get(seq[i]);
            if (matches == null)
            {
                result[i] = NF;
            }
            else
            {
                Iterator match = matches.iterator();
                int j = ((Integer) match.next()).intValue();
                int distance = Math.abs(i - j);
                result[i] = j;
                while (match.hasNext())
                {
                    j = ((Integer) match.next()).intValue();;
                    int d = Math.abs(i - j);
                    if (d < distance)
                    {
                        distance = d;
                        result[i] = j;
                    }
                }
            }
        }
        result[seq.length] = EOS;
        return result;
    }


    /**
     * Converts an array of {@link Object Object} to a string
     * using {@link Diff#NL Diff.NL}
     * as the line separator.
     * @param o the array of objects.
     */
    public static String arrayToString(Object[] o)
    {
        return arrayToString(o, Diff.NL);
    }



    /**
     * Performs random edits on the input sequence. Useful for testing.
     * @param text The input sequence.
     * @return The sequence with random edits performed.
     */
    public static Object[] randomEdit(Object[] text)
    {
        return randomEdit(text, text.length);
    }


    /**
     * Performs random edits on the input sequence. Useful for testing.
     * @param text The input sequence.
     * @param seed A seed value for the randomizer.
     * @return The sequence with random edits performed.
     */
    public static Object[] randomEdit(Object[] text, long seed)
    {
        List result = new ArrayList(Arrays.asList(text));
        Random r = new Random(seed);
        int nops = r.nextInt(10);
        for (int i = 0; i < nops; i++)
        {
            boolean del = r.nextBoolean();
            int pos = r.nextInt(result.size() + 1);
            int len = Math.min(result.size() - pos, 1 + r.nextInt(4));
            if (del && result.size() > 0)
            { // delete
                result.subList(pos, pos + len).clear();
            }
            else
            {
                for (int k = 0; k < len; k++, pos++)
                {
                    result.add(pos, "[" + i + "] random edit[" + i + "][" + i + "]");
                }
            }
        }
        return result.toArray();
    }


    /**
     * Shuffles around the items in the input sequence.
     * @param text The input sequence.
     * @return The shuffled sequence.
     */
    public static Object[] shuffle(Object[] text)
    {
        return shuffle(text, text.length);
    }


    /**
     * Shuffles around the items in the input sequence.
     * @param text The input sequence.
     * @param seed A seed value for randomizing the suffle.
     * @return The shuffled sequence.
     */
    public static Object[] shuffle(Object[] text, long seed)
    {
        List result = new ArrayList(Arrays.asList(text));
        Collections.shuffle(result);
        return result.toArray();
    }
}



