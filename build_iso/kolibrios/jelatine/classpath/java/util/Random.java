/* Random.java -- a pseudo-random number generator
   Copyright (C) 1998, 1999, 2000, 2001, 2002 Free Software Foundation, Inc.

This file is part of GNU Classpath.

GNU Classpath is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.

GNU Classpath is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with GNU Classpath; see the file COPYING.  If not, write to the
Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301 USA.

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version. */

package java.util;

/**
 * This class generates pseudorandom numbers.  It uses the same
 * algorithm as the original JDK-class, so that your programs behave
 * exactly the same way, if started with the same seed.
 *
 * The algorithm is described in <em>The Art of Computer Programming,
 * Volume 2</em> by Donald Knuth in Section 3.2.1.  It is a 48-bit seed,
 * linear congruential formula.
 *
 * If two instances of this class are created with the same seed and
 * the same calls to these classes are made, they behave exactly the
 * same way.  This should be even true for foreign implementations
 * (like this), so every port must use the same algorithm as described
 * here.
 *
 * If you want to implement your own pseudorandom algorithm, you
 * should extend this class and overload the <code>next()</code> and
 * <code>setSeed(long)</code> method.  In that case the above
 * paragraph doesn't apply to you.
 *
 * This class shouldn't be used for security sensitive purposes (like
 * generating passwords or encryption keys.  See <code>SecureRandom</code>
 * in package <code>java.security</code> for this purpose.
 *
 * For simple random doubles between 0.0 and 1.0, you may consider using
 * Math.random instead.
 *
 * @see Math#random()
 * @author Jochen Hoenicke
 * @author Eric Blake (ebb9@email.byu.edu)
 */
public class Random
{
    /**
     * The seed.  This is the number set by setSeed and which is used
     * in next.
     *
     * @see #next(int)
     */
    private long seed;

    /**
     * Creates a new pseudorandom number generator.  The seed is initialized
     * to the current time, as if by
     * <code>setSeed(System.currentTimeMillis());</code>.
     *
     * @see System#currentTimeMillis()
     */
    public Random()
    {
        this(System.currentTimeMillis());
    }

    /**
     * Creates a new pseudorandom number generator, starting with the
     * specified seed, using <code>setSeed(seed);</code>.
     *
     * @param seed the initial seed
     */
    public Random(long seed)
    {
        setSeed(seed);
    }

    /**
     * Sets the seed for this pseudorandom number generator.  As described
     * above, two instances of the same random class, starting with the
     * same seed, should produce the same results, if the same methods
     * are called.  The implementation for java.util.Random is:
     *
     * <pre>public synchronized void setSeed(long seed)
     * {
     *     this.seed = (seed ^ 0x5DEECE66DL) & ((1L &lt;&lt; 48) - 1);
     *     haveNextNextGaussian = false;
     * }</pre>
     *
     * @param seed the new seed
     */
    public synchronized void setSeed(long seed)
    {
        this.seed = (seed ^ 0x5DEECE66DL) & ((1L << 48) - 1);
    }

    /**
     * Generates the next pseudorandom number.  This returns
     * an int value whose <code>bits</code> low order bits are
     * independent chosen random bits (0 and 1 are equally likely).
     * The implementation for java.util.Random is:
     *
     * <pre>protected synchronized int next(int bits)
     * {
     *     seed = (seed * 0x5DEECE66DL + 0xBL) & ((1L &lt;&lt; 48) - 1);
     *     return (int) (seed &gt;&gt;&gt; (48 - bits));
     * }</pre>
     *
     * @param bits the number of random bits to generate, in the range 1..32
     * @return the next pseudorandom value
     */
    protected synchronized int next(int bits)
    {
        seed = (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
        return (int) (seed >>> (48 - bits));
    }

    /**
     * Generates the next pseudorandom number.  This returns
     * an int value whose 32 bits are independent chosen random bits
     * (0 and 1 are equally likely).  The implementation for
     * java.util.Random is:
     * 
     * <pre>public int nextInt()
     * {
     *     return next(32);
     * }</pre>
     *
     * @return the next pseudorandom value
     */
    public int nextInt()
    {
        return next(32);
    }

    /**
     * Generates the next pseudorandom number.  This returns
     * a value between 0(inclusive) and <code>n</code>(exclusive), and
     * each value has the same likelihodd (1/<code>n</code>).
     * (0 and 1 are equally likely).  The implementation for
     * java.util.Random is:
     * 
     * <pre>
     * public int nextInt(int n)
     * {
     *     if (n &lt;= 0)
     *         throw new IllegalArgumentException("n must be positive");
     *
     *     if ((n & -n) == n)  // i.e., n is a power of 2
     *         return (int)((n * (long) next(31)) &gt;&gt; 31);
     *
     *     int bits, val;
     *     do
     *     {
     *         bits = next(31);
     *         val = bits % n;
     *     }
     *     while(bits - val + (n-1) &lt; 0);
     *
     *     return val;
     * }</pre>
     *   
     * <p>This algorithm would return every value with exactly the same
     * probability, if the next()-method would be a perfect random number
     * generator.
     *
     * The loop at the bottom only accepts a value, if the random
     * number was between 0 and the highest number less then 1<<31,
     * which is divisible by n.  The probability for this is high for small
     * n, and the worst case is 1/2 (for n=(1<<30)+1).
     *
     * The special treatment for n = power of 2, selects the high bits of
     * the random number (the loop at the bottom would select the low order
     * bits).  This is done, because the low order bits of linear congruential
     * number generators (like the one used in this class) are known to be
     * ``less random'' than the high order bits.
     *
     * @param n the upper bound
     * @throws IllegalArgumentException if the given upper bound is negative
     * @return the next pseudorandom value
     */
    public int nextInt(int n)
    {
        if (n <= 0)
            throw new IllegalArgumentException("n must be positive");
    
        if ((n & -n) == n) // i.e., n is a power of 2
            return (int) ((n * (long) next(31)) >> 31);
    
        int bits, val;
        do
        {
            bits = next(31);
            val = bits % n;
        }
        while (bits - val + (n - 1) < 0);

        return val;
    }

    /**
     * Generates the next pseudorandom long number.  All bits of this
     * long are independently chosen and 0 and 1 have equal likelihood.
     * The implementation for java.util.Random is:
     *
     * <pre>public long nextLong()
     * {
     *     return ((long) next(32) &lt;&lt; 32) + next(32);
     * }</pre>
     *
     * @return the next pseudorandom value
     */
    public long nextLong()
    {
        return ((long) next(32) << 32) + next(32);
    }

    /**
     * Generates the next pseudorandom float uniformly distributed
     * between 0.0f (inclusive) and 1.0f (exclusive).  The
     * implementation is as follows.
     * 
     * <pre>public float nextFloat()
     * {
     *     return next(24) / ((float)(1 &lt;&lt; 24));
     * }</pre>
     *
     * @return the next pseudorandom float
     */
    public float nextFloat()
    {
        return next(24) / (float) (1 << 24);
    }

    /**
     * Generates the next pseudorandom double uniformly distributed
     * between 0.0 (inclusive) and 1.0 (exclusive).  The
     * implementation is as follows.
     *
     * <pre>public double nextDouble()
     * {
     *     return (((long) next(26) &lt;&lt; 27) + next(27)) / (double)(1L &lt;&lt; 53);
     * }</pre>
     *
     * @return the next pseudorandom double
     */
    public double nextDouble()
    {
        return (((long) next(26) << 27) + next(27)) / (double) (1L << 53);
    }
}
