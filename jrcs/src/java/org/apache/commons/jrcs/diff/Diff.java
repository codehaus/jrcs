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


/**
 * Implements a differencing engine that works on arrays of {@link Object Object}.
 *
 * <p>Within this library, the word <i>text</i> means a unit of information
 * subject to version control.
 *
 * <p>Text is represented as <code>Object[]</code> because
 * the diff engine is capable of handling more than plain ascci. In fact,
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

    public static final String NL = System.getProperty("line.separator");
    public static final String RCS_EOL = "\n";


    static final int NOT_FOUND_i = -2;

    static final int NOT_FOUND_j = -1;

    static final int EOS = Integer.MAX_VALUE;

    Object[] orig;

    public Diff(Object[] o)
    {
        if (o == null)
        {
            throw new IllegalArgumentException();
        }
        orig = o;
    }

    public static Revision diff(Object[] orig, Object[] rev)
            throws DifferentiationFailedException
    {
        if (orig == null || rev == null)
        {
            throw new IllegalArgumentException();
        }

        return new Diff(orig).diff(rev);
    }

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

    protected int scan(int[] ndx, int i, int target)
    {
        while (ndx[i] < target)
        {
            i++;
        }
        return i;
    }

    public Revision diff(Object[] rev)
        throws DifferentiationFailedException
    {
        Map eqs = buildEqSet(orig, rev);
        int[] indx = buildIndex(eqs, orig, NOT_FOUND_i);
        int[] jndx = buildIndex(eqs, rev, NOT_FOUND_j);
        eqs = null; // let gc know we're done with this

        Revision deltas = new Revision(); //!!! new Revision()
        int i = 0;
        int j = 0;

        // skip matching
        for (; indx[i] != EOS && indx[i] == jndx[j]; i++, j++)
        {/* void */
        }

        while (indx[i] != jndx[j])
        { // only equal if both == EOS
            // they are different
            int ia = i;
            int ja = j;

            // size of this delta
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
                //System.err.println("************* reversing "+ i);
            }

            deltas.addDelta(Delta.newDelta(new Chunk(orig, ia, i - ia),
                    new Chunk(rev, ja, j - ja)));
            // skip matching
            for (; indx[i] != EOS && indx[i] == jndx[j]; i++, j++)
            {/* void */
            }
        }
        try
        {
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


    protected Map buildEqSet(Object[] orig, Object[] rev)
    {
        Set items = new HashSet(Arrays.asList(orig));
        items.retainAll(Arrays.asList(rev));

        Map eqs = new HashMap();
        for (int i = 0; i < orig.length; i++)
        {
            if (items.contains(orig[i]))
            {
                eqs.put(orig[i], new Integer(i));
                items.remove(orig[i]);
            }
        }
        return eqs;
    }

    protected int[] buildIndex(Map eqs, Object[] seq, int NF)
    {
        int[] result = new int[seq.length + 1];
        for (int i = 0; i < seq.length; i++)
        {
            Integer value = (Integer) eqs.get(seq[i]);
            if (value == null || value.intValue() < 0)
            {
                result[i] = NF;
            }
            else
            {
                result[i] = value.intValue();
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



    public static Object[] randomEdit(Object[] text)
    {
        return randomEdit(text, text.length);
    }


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


    public static Object[] shuffle(Object[] text)
    {
        return shuffle(text, text.length);
    }


    public static Object[] shuffle(Object[] text, long seed)
    {
        List result = new ArrayList(Arrays.asList(text));
        Collections.shuffle(result);
        return result.toArray();
    }
}



