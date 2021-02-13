/* Double.java -- object wrapper for double
   Copyright (C) 1998, 1999, 2000, 2001, 2002, 2003, 2005
   Free Software Foundation, Inc.

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

package java.lang;

/**
 * Instances of class <code>Double</code> represent primitive
 * <code>double</code> values.
 *
 * Additionally, this class provides various helper functions and variables
 * related to doubles.
 *
 * @author Paul Fisher
 * @author Andrew Haley (aph@cygnus.com)
 * @author Eric Blake (ebb9@email.byu.edu)
 */
public final class Double
{
    /**
     * The maximum positive value a <code>double</code> may represent
     * is 1.7976931348623157e+308.
     */
    public static final double MAX_VALUE = 1.7976931348623157e+308;

    /**
     * The minimum positive value a <code>double</code> may represent
     * is 5e-324.
     */
    public static final double MIN_VALUE = 5e-324;

    /**
     * The value of a double representation -1.0/0.0, negative
     * infinity.
     */
    public static final double NEGATIVE_INFINITY = -1.0 / 0.0;

    /**
     * The value of a double representing 1.0/0.0, positive infinity.
     */
    public static final double POSITIVE_INFINITY = 1.0 / 0.0;

    /**
     * All IEEE 754 values of NaN have the same value in Java.
     */
    public static final double NaN = 0.0 / 0.0;

    /**
     * The immutable value of this Double.
     *
     * @serial the wrapped double
     */
    private final double value;

    /**
     * Create a <code>Double</code> from the primitive <code>double</code>
     * specified.
     *
     * @param value the <code>double</code> argument
     */
    public Double(double value)
    {
        this.value = value;
    }

    /**
     * Convert the <code>double</code> to a <code>String</code>.
     * Floating-point string representation is fairly complex: here is a
     * rundown of the possible values.  "<code>[-]</code>" indicates that a
     * negative sign will be printed if the value (or exponent) is negative.
     * "<code>&lt;number&gt;</code>" means a string of digits ('0' to '9').
     * "<code>&lt;digit&gt;</code>" means a single digit ('0' to '9').<br>
     *
     * <table border=1>
     * <tr><th>Value of Double</th><th>String Representation</th></tr>
     * <tr><td>[+-] 0</td> <td><code>[-]0.0</code></td></tr>
     * <tr><td>Between [+-] 10<sup>-3</sup> and 10<sup>7</sup>, exclusive</td>
     *     <td><code>[-]number.number</code></td></tr>
     * <tr><td>Other numeric value</td>
     *     <td><code>[-]&lt;digit&gt;.&lt;number&gt;
     *          E[-]&lt;number&gt;</code></td></tr>
     * <tr><td>[+-] infinity</td> <td><code>[-]Infinity</code></td></tr>
     * <tr><td>NaN</td> <td><code>NaN</code></td></tr>
     * </table>
     *
     * Yes, negative zero <em>is</em> a possible value.  Note that there is
     * <em>always</em> a <code>.</code> and at least one digit printed after
     * it: even if the number is 3, it will be printed as <code>3.0</code>.
     * After the ".", all digits will be printed except trailing zeros. The
     * result is rounded to the shortest decimal number which will parse back
     * to the same double.
     *
     * @param d the <code>double</code> to convert
     * @return the <code>String</code> representing the <code>double</code>
     */
    public native static String toString(double d);

   /**
     * Create a new <code>Double</code> object using the <code>String</code>.
     *
     * @param s the <code>String</code> to convert
     * @return the new <code>Double</code>
     * @throws NumberFormatException if <code>s</code> cannot be parsed as a
     *         <code>double</code>
     * @throws NullPointerException if <code>s</code> is null.
     * @see #parseDouble(String)
     */
    public static Double valueOf(String s)
        throws NumberFormatException, NullPointerException
    {
        return new Double(parseDouble(s));
    }

    /**
     * Parse the specified <code>String</code> as a <code>double</code>. The
     * extended BNF grammar is as follows:<br>
     * <pre>
     * <em>DecodableString</em>:
     *      ( [ <code>-</code> | <code>+</code> ] <code>NaN</code> )
     *    | ( [ <code>-</code> | <code>+</code> ] <code>Infinity</code> )
     *    | ( [ <code>-</code> | <code>+</code> ] <em>FloatingPoint</em>
     *              [ <code>f</code> | <code>F</code> | <code>d</code>
     *                | <code>D</code>] )
     * <em>FloatingPoint</em>:
     *      ( { <em>Digit</em> }+ [ <code>.</code> { <em>Digit</em> } ]
     *              [ <em>Exponent</em> ] )
     *    | ( <code>.</code> { <em>Digit</em> }+ [ <em>Exponent</em> ] )
     * <em>Exponent</em>:
     *      ( ( <code>e</code> | <code>E</code> )
     *              [ <code>-</code> | <code>+</code> ] { <em>Digit</em> }+ )
     * <em>Digit</em>: <em><code>'0'</code> through <code>'9'</code></em>
     * </pre>
     *
     * <p>NaN and infinity are special cases, to allow parsing of the output
     * of toString.  Otherwise, the result is determined by calculating
     * <em>n * 10<sup>exponent</sup></em> to infinite precision, then rounding
     * to the nearest double. Remember that many numbers cannot be precisely
     * represented in floating point. In case of overflow, infinity is used,
     * and in case of underflow, signed zero is used. Unlike Integer.parseInt,
     * this does not accept Unicode digits outside the ASCII range.
     *
     * <p>If an unexpected character is found in the <code>String</code>, a
     * <code>NumberFormatException</code> will be thrown.  Leading and trailing
     * 'whitespace' is ignored via <code>String.trim()</code>, but spaces
     * internal to the actual number are not allowed.
     *
     * @param str the <code>String</code> to convert
     * @return the <code>double</code> value of <code>s</code>
     * @throws NumberFormatException if <code>s</code> cannot be parsed as a
     *         <code>double</code>
     * @throws NullPointerException if <code>s</code> is null
     * @see #MIN_VALUE
     * @see #MAX_VALUE
     * @see #POSITIVE_INFINITY
     * @see #NEGATIVE_INFINITY
     */
    public static double parseDouble(String str)
        throws NumberFormatException, NullPointerException
    {
        String tmp = str.trim(); // Trim all whitespace & control characters
        long res, sign = 0;
        int i = 0, length = tmp.length();

        if (length == 0) {
            throw new NumberFormatException();
        }

        char c = tmp.charAt(0);

        // Detect the sign
        if (c == '-') {
            sign = 0x8000000000000000L;
            i++;
        } else if (c == '+') {
            i++;
        }

        tmp = tmp.substring(i);
        i = 0;

        if (tmp.equals("NaN")) {
            res = 0x7ff8000000000000L;
        } else if (tmp.equals("Infinity")) {
            res = 0x7ff0000000000000L;
        } else {
            length = tmp.length();

            if (length == 0) {
                throw new NumberFormatException();
            }

            // Parse the integral part (if present)
            double value = 0.0;

            while ((i < length) && Character.isDigit(tmp.charAt(i))) {
                value = (value * 10.0)
                        + (double) Character.digit(tmp.charAt(i), 10);
                i++;
            }

            // Skip the dot (if present)
            if ((i < length) && tmp.charAt(i) == '.') {
                i++;
            }

            // Parse the fractional part (if present)
            double div = 10.0;

            while ((i < length) && Character.isDigit(tmp.charAt(i))) {
                value += ((double) Character.digit(tmp.charAt(i), 10)) / div;
                div *= 10.0;
                i++;
            }

            // Parse the exponent
            int exponent = 0;
            boolean exp_pos = true;

            if ((i < length) && (Character.toLowerCase(tmp.charAt(i)) == 'e')) {
                i++;

                if (i < length) {
                    c = tmp.charAt(i);

                    if (c == '-') {
                        i++;
                        exp_pos = false;
                    } else if (c == '+') {
                        i++;
                    }
                }

                int j = i;

                while ((i < length) && Character.isDigit(tmp.charAt(i))) {
                    exponent = (exponent * 10)
                               + Character.digit(tmp.charAt(i), 10);
                    i++;
                }

                if (i == j) {
                    // After the e/E character there must be an exponent
                    throw new NumberFormatException();
                }

                if (!exp_pos) {
                    exponent = -exponent;
                }
            }

            // Parse the float/double specifier (if present)
            if (i < length) {
                c = Character.toLowerCase(tmp.charAt(i));
                i++;

                if (c != 'f' && c != 'd') {
                    throw new NumberFormatException();
                }
            }

            // Ensure that there are no more characters
            if (i != length) {
                throw new NumberFormatException();
            }

            // Encode the actual number
            value *= Math.pow(10.0, exponent);
            res = Double.doubleToLongBits(value);
        }

        return Double.longBitsToDouble(res | sign);
     }

    /**
     * Return <code>true</code> if the <code>double</code> has the same
     * value as <code>NaN</code>, otherwise return <code>false</code>.
     *
     * @param v the <code>double</code> to compare
     * @return whether the argument is <code>NaN</code>.
     */
    public static boolean isNaN(double v)
    {
        // This works since NaN != NaN is the only reflexive inequality
        // comparison which returns true.
        return v != v;
    }

    /**
     * Return <code>true</code> if the <code>double</code> has a value
     * equal to either <code>NEGATIVE_INFINITY</code> or
     * <code>POSITIVE_INFINITY</code>, otherwise return <code>false</code>.
     *
     * @param v the <code>double</code> to compare
     * @return whether the argument is (-/+) infinity.
     */
    public static boolean isInfinite(double v)
    {
        return v == POSITIVE_INFINITY || v == NEGATIVE_INFINITY;
    }

    /**
     * Convert the <code>double</code> value of this <code>Double</code>
     * to a <code>String</code>.  This method calls
     * <code>Double.toString(double)</code> to do its dirty work.
     *
     * @return the <code>String</code> representation
     * @see #toString(double)
     */
    public String toString()
    {
        return toString(value);
    }

    /**
     * Return <code>true</code> if the value of this <code>Double</code>
     * is the same as <code>NaN</code>, otherwise return <code>false</code>.
     *
     * @return whether this <code>Double</code> is <code>NaN</code>
     */
    public boolean isNaN()
    {
        return isNaN(value);
    }

    /**
     * Return <code>true</code> if the value of this <code>Double</code>
     * is the same as <code>NEGATIVE_INFINITY</code> or
     * <code>POSITIVE_INFINITY</code>, otherwise return <code>false</code>.
     *
     * @return whether this <code>Double</code> is (-/+) infinity
     */
    public boolean isInfinite()
    {
        return isInfinite(value);
    }

    /**
     * Return the value of this <code>Double</code> as a <code>byte</code>.
     *
     * @return the byte value
     */
    public byte byteValue()
    {
        return (byte) value;
    }

    /**
     * Return the value of this <code>Double</code> as a <code>short</code>.
     *
     * @return the short value
     */
    public short shortValue()
    {
        return (short) value;
    }

    /**
     * Return the value of this <code>Double</code> as an <code>int</code>.
     *
     * @return the int value
     */
    public int intValue()
    {
        return (int) value;
    }

    /**
     * Return the value of this <code>Double</code> as a <code>long</code>.
     *
     * @return the long value
     */
    public long longValue()
    {
        return (long) value;
    }

    /**
     * Return the value of this <code>Double</code> as a <code>float</code>.
     *
     * @return the float value
     */
    public float floatValue()
    {
        return (float) value;
    }

    /**
     * Return the value of this <code>Double</code>.
     *
     * @return the double value
     */
    public double doubleValue()
    {
        return value;
    }

    /**
     * Return a hashcode representing this Object. <code>Double</code>'s hash
     * code is calculated by:<br>
     * <code>long v = Double.doubleToLongBits(doubleValue());<br>
     *    int hash = (int)(v^(v&gt;&gt;32))</code>.
     *
     * @return this Object's hash code
     * @see #doubleToLongBits(double)
     */
    public int hashCode()
    {
        long v = doubleToLongBits(value);

        return (int) (v ^ (v >>> 32));
    }

    /**
     * Returns <code>true</code> if <code>obj</code> is an instance of
     * <code>Double</code> and represents the same double value. Unlike comparing
     * two doubles with <code>==</code>, this treats two instances of
     * <code>Double.NaN</code> as equal, but treats <code>0.0</code> and
     * <code>-0.0</code> as unequal.
     *
     * <p>Note that <code>d1.equals(d2)</code> is identical to
     * <code>doubleToLongBits(d1.doubleValue()) ==
     *    doubleToLongBits(d2.doubleValue())</code>.
     *
     * @param obj the object to compare
     * @return whether the objects are semantically equal
     */
    public boolean equals(Object obj)
    {
        if (! (obj instanceof Double))
            return false;

        double d = ((Double) obj).value;

        // Avoid call to native method. However, some implementations, like gcj,
        // are better off using floatToIntBits(value) == floatToIntBits(f).
        // Check common case first, then check NaN and 0.
        if (value == d)
            return (value != 0) || (1 / value == 1 / d);

        return isNaN(value) && isNaN(d);
    }

    /**
     * Convert the double to the IEEE 754 floating-point "double format" bit
     * layout. Bit 63 (the most significant) is the sign bit, bits 62-52
     * (masked by 0x7ff0000000000000L) represent the exponent, and bits 51-0
     * (masked by 0x000fffffffffffffL) are the mantissa. This function
     * collapses all versions of NaN to 0x7ff8000000000000L. The result of this
     * function can be used as the argument to
     * <code>Double.longBitsToDouble(long)</code> to obtain the original
     * <code>double</code> value.
     *
     * @param value the <code>double</code> to convert
     * @return the bits of the <code>double</code>
     * @see #longBitsToDouble(long)
     */
    public native static long doubleToLongBits(double value);

    /**
     * Convert the argument in IEEE 754 floating-point "double format" bit
     * layout to the corresponding float. Bit 63 (the most significant) is the
     * sign bit, bits 62-52 (masked by 0x7ff0000000000000L) represent the
     * exponent, and bits 51-0 (masked by 0x000fffffffffffffL) are the mantissa.
     * This function leaves NaN alone, so that you can recover the bit pattern
     * with <code>Double.doubleToRawLongBits(double)</code>.
     *
     * @param bits the bits to convert
     * @return the <code>double</code> represented by the bits
     */
    public native static double longBitsToDouble(long bits);
}
