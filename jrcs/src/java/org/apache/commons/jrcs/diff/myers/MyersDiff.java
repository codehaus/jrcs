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

package org.apache.commons.jrcs.diff.myers;

import org.apache.commons.jrcs.diff.*;

/**
 * A clean-room implementation of
 * <a href="http://www.cs.arizona.edu/people/gene/">
 * Eugene Myers</a> differencing algorithm.
 * <p>
 * See the paper at
 * <a href="http://www.cs.arizona.edu/people/gene/PAPERS/diff.ps">
 * Myers' site</a>
 *
 * @version $Revision$ $Date$
 * @author <a href="mailto:juanco@suigeneris.org">Juanco Anez</a>
 * @see Delta
 * @see Revision
 * @see Diff
 */
public class MyersDiff
    implements DiffAlgorithm
{
    public MyersDiff()
    {
    }

    public Revision diff(Object[] orig, Object[] rev)
        throws DifferentiationFailedException
    {
        PathNode path = buildPath(orig, rev);
        return buildRevision(path, orig, rev);
    }

    /**
     * Computes the minimum diffpath that expresses de differences
     * between the original and revised sequences, according
     * to Gene Myers differencing algorithm.
     *
     * @param orig The original sequence.
     * @param rev The revised sequence.
     * @return A minimum {@link PathNode Path} accross the differences graph.
     */
    public PathNode buildPath(Object[] orig, Object[] rev)
    {
        int n = orig.length;
        int m = rev.length;

        int max = n + m + 1;
        PathNode diagonal[] = new PathNode[1 + 2 * max];
        int middle = (diagonal.length + 1) / 2;

        PathNode node = null;

        int d = 0;
        diagonal[middle + 1] = new PathNode(0, -1);
        outer:for (d = 0; d < max; d++)
        {
            for (int k = -d; k <= d; k += 2)
            {
                PathNode prev = null;
                int kmiddle = middle + k;
                int kplus = kmiddle + 1;
                int kminus = kmiddle - 1;

                int i;
                if ( (k == -d) ||
                    (k != d && diagonal[kminus].i < diagonal[kplus].i))
                {
                    i = diagonal[kplus].i;
                    prev = diagonal[kplus];
                }
                else
                {
                    i = diagonal[kminus].i + 1;
                    prev = diagonal[kminus];
                }
                if (prev.i < 0 || prev.j < 0)
                    prev = null;

                int j = i - k;
                while (i < n && j < m && orig[i].equals(rev[j]))
                {
                    i++;
                    j++;
                }
                diagonal[kmiddle] = new PathNode(i, j, prev);
                if (i >= n && j >= m)
                {
                    node = diagonal[kmiddle];
                    break outer;
                }
            }
        }
        return node;
    }

    /**
     * Constructs a {@link Revision} from a difference path.
     *
     * @param path The path.
     * @param orig The original sequence.
     * @param rev The revised sequence.
     * @return A {@link Revision} script corresponding to the path.
     * @throws DifferentiationFailedException
     */
    public Revision buildRevision(PathNode path, Object[] orig, Object[] rev)
    {
        if (path == null)
            throw new IllegalArgumentException("path is null");
        if (orig == null)
            throw new IllegalArgumentException("original sequence is null");
        if (path == null)
            throw new IllegalArgumentException("revised sequence is null");

        Revision revision = new Revision();
        while (path != null && path.prev != null)
        {
            int i = path.i;
            int j = path.j;
            while (i > 0 && j > 0 && orig[i - 1].equals(rev[j - 1]))
            { // reverse snake
                i--;
                j--;
            }
            if (i < 0 && j < 0)
                break;

            int ia;
            int ja;
            do
            {
                path = path.prev;
                ia = path.i;
                ja = path.j;
            }
            while (path.prev != null
                   && (ia == 0 || ja == 0
                       || !orig[ia - 1].equals(rev[ja - 1]))
                   ); // while no snake

            Delta delta = Delta.newDelta(new Chunk(orig, ia, i - ia),
                                         new Chunk(rev, ja, j - ja));
            revision.insertDelta(delta);
        }
        return revision;
    }

}