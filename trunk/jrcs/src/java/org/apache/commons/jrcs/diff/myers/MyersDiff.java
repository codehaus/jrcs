/*
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
 *
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
 * http://www.cs.arizona.edu/people/gene/PAPERS/diff.ps</a>
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
    /**
     * Constructs an instance of the Myers differencing algorithm.
     */
    public MyersDiff()
    {
    }

    /**
     * {@inheritDoc}
     */
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
     * @throws DifferentiationFailedException if a diff path could not be found.
     */
    public static PathNode buildPath(Object[] orig, Object[] rev)
        throws DifferentiationFailedException
    {
        if (orig == null)
            throw new IllegalArgumentException("original sequence is null");
        if (rev == null)
            throw new IllegalArgumentException("revised sequence is null");

        // these are local constants
        final int N = orig.length;
        final int M = rev.length;

        final int MAX = N + M + 1;
        final int size = 1 + 2 * MAX;
        final int middle = (size + 1) / 2;
        final PathNode diagonal[] = new PathNode[size];

        PathNode path = null;

        diagonal[middle + 1] = new PathNode(0, -1);
        for (int d = 0; d < MAX; d++)
        {
            for (int k = -d; k <= d; k += 2)
            {
                int kmiddle = middle + k;
                int kplus = kmiddle + 1;
                int kminus = kmiddle - 1;
                PathNode prev = null;

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
                {
                    prev = null; // discard artificial nodes used for bootstrapping
                }

                int j = i - k;

                // orig and rev are zero-based
                // but the algorithm is one-based
                // that's why there's no +1 when indexing the sequences
                while (i < N && j < M && orig[i].equals(rev[j]))
                {
                    i++;
                    j++;
                }

                diagonal[kmiddle] = new PathNode(i, j, prev);

                if (i >= N && j >= M)
                {
                    return diagonal[kmiddle];
                }
            }
        }
        // According to Myers, this cannot happen
        throw new DifferentiationFailedException("could not find a diff path");
    }

    /**
     * Constructs a {@link Revision} from a difference path.
     *
     * @param path The path.
     * @param orig The original sequence.
     * @param rev The revised sequence.
     * @return A {@link Revision} script corresponding to the path.
     * @throws DifferentiationFailedException if the {@link Revision} could
     *         not be built.
     */
    public static Revision buildRevision(PathNode path, Object[] orig, Object[] rev)
    {
        if (path == null)
            throw new IllegalArgumentException("path is null");
        if (orig == null)
            throw new IllegalArgumentException("original sequence is null");
        if (rev == null)
            throw new IllegalArgumentException("revised sequence is null");

        Revision revision = new Revision();
        while (path != null && path.prev != null)
        {
            int i = path.i;
            int j = path.j;
            while (i > 0 && j > 0
                   && i > path.prev.i && j > path.prev.j // donnot follow a snake past a previous node
                   && orig[i - 1].equals(rev[j - 1])) // check that it is indeed a snake
            {
                // reverse snake
                i--;
                j--;
            }

            int ianchor;
            int janchor;
            do
            {
                path = path.prev;
                ianchor = path.i;
                janchor = path.j;
            }
            while (path.prev != null
                   && (ianchor == path.prev.i || janchor == path.prev.j) // while no snake
                   );

            Delta delta = Delta.newDelta(new Chunk(orig, ianchor, i - ianchor),
                                         new Chunk(rev, janchor, j - janchor));
            revision.insertDelta(delta);
        }
        return revision;
    }

}