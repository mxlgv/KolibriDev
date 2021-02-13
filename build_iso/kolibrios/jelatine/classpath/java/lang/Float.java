/* Float.java -- object wrapper for float
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

import java.lang.Double;

/**
 * Instances of class <code>Float</code> represent primitive
 * <code>float</code> values.
 *
 * Additionally, this class provides various helper functions and variables
 * related to floats.
 *
 * @author Paul Fisher
 * @author Andrew Haley (aph@cygnus.com)
 * @author Eric Blake (ebb9@email.byu.edu)
 */
public final class Float
{
    /**
     * The maximum positive value a <code>double</code> may represent
     * is 3.4028235e+38f.
     */
    public static final float MAX_VALUE = 3.4028235e+38f;

    /**
     * The minimum positive value a <code>float</code> may represent
     * is 1.4e-45.
     */
    public static final float MIN_VALUE = 1.4e-45f;

    /**
     * The value of a float representation -1.0/0.0, negative infinity.
     */
    public static final float NEGATIVE_INFINITY = -1.0f / 0.0f;

    /**
     * The value of a float representation 1.0/0.0, positive infinity.
     */
    public static final float POSITIVE_INFINITY = 1.0f / 0.0f;

    /**
     * All IEEE 754 values of NaN have the same value in Java.
     */
    public static final float NaN = 0.0f / 0.0f;

    /**
     * The immutable value of this Float.
     *
     * @serial the wrapped float
     */
    private final float value;

    /**
     * Create a <code>Float</code> from the primitive <code>float</code>
     * specified.
     *
     * @param value the <code>float</code> argument
     */
    public Float(float value)
    {
        this.value = value;
    }

    /**
     * Create a <code>Float</code> from the primitive <code>double</code>
     * specified.
     *
     * @param value the <code>double</code> argument
     */
    public Float(double value)
    {
        this.value = (float) value;
    }

    /**
     * Convert the <code>float</code> to a <code>String</code>.
     * Floating-point string representation is fairly complex: here is a
     * rundown of the possible values.  "<code>[-]</code>" indicates that a
     * negative sign will be printed if the value (or exponent) is negative.
     * "<code>&lt;number&gt;</code>" means a string of digits ('0' to '9').
     * "<code>&lt;digit&gt;</code>" means a single digit ('0' to '9').<br>
     *
     * <table border=1>
     * <tr><th>Value of Float</th><th>String Representation</th></tr>
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
     * to the same float.
     *
     * @param f the <code>float</code> to convert
     * @return the <code>String</code> representing the <code>float</code>
     */
    public native static String toString(float f);

    /**
     * Creates a new <code>Float</code> object using the <code>String</code>.
     *
     * @param s the <code>String</code> to convert
     * @return the new <code>Float</code>
     * @throws NumberFormatException if <code>s</code> cannot be parsed as a
     *         <code>float</code>
     * @throws NullPointerException if <code>s</code> is null
     * @see #parseFloat(String)
     */
    public static Float valueOf(String s)
    {
        return new Float(parseFloat(s));
    }

    /**
     * Parse the specified <code>String</code> as a <code>float</code>. The
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
     * to the nearest float. Remember that many numbers cannot be precisely
     * represented in floating point. In case of overflow, infinity is used,
     * and in case of underflow, signed zero is used. Unlike Integer.parseInt,
     * this does not accept Unicode digits outside the ASCII range.
     *
     * <p>If an unexpected character is found in the <code>String</code>, a
     * <code>NumberFormatException</code> will be thrown.  Leading and trailing
     * 'whitespace' is ignored via <code>String.trim()</code>, but spaces
     * internal to the actual number are not allowed.
     *
     * <p>To parse numbers according to another format, consider using
     * java.text.NumberFormat.
     *
     * @param str the <code>String</code> to convert
     * @return the <code>float</code> value of <code>s</code>
     * @throws NumberFormatException if <code>s</code> cannot be parsed as a
     *         <code>float</code>
     * @throws NullPointerException if <code>s</code> is null
     * @see #MIN_VALUE
     * @see #MAX_VALUE
     * @see #POSITIVE_INFINITY
     * @see #NEGATIVE_INFINITY
     */
    public static float parseFloat(String str)
    {
        // XXX Rounding parseDouble() causes some errors greater than 1 ulp from
        // the infinitely precise decimal.
        return (float) Double.parseDouble(str);
    }

    /**
     * Return <code>true</code> if the <code>float</code> has the same
     * value as <code>NaN</code>, otherwise return <code>false</code>.
     *
     * @param v the <code>float</code> to compare
     * @return whether the argument is <code>NaN</code>
     */
    public static boolean isNaN(float v)
    {
        // This works since NaN != NaN is the only reflexive inequality
        // comparison which returns true.
        return v != v;
    }

    /**
     * Return <code>true</code> if the <code>float</code> has a value
     * equal to either <code>NEGATIVE_INFINITY</code> or
     * <code>POSITIVE_INFINITY</code>, otherwise return <code>false</code>.
     *
     * @param v the <code>float</code> to compare
     * @return whether the argument is (-/+) infinity
     */
    public static boolean isInfinite(float v)
    {
        return v == POSITIVE_INFINITY || v == NEGATIVE_INFINITY;
    }

    /**
     * Return <code>true</code> if the value of this <code>Float</code>
     * is the same as <code>NaN</code>, otherwise return <code>false</code>.
     *
     * @return whether this <code>Float</code> is <code>NaN</code>
     */
    public boolean isNaN()
    {
        return isNaN(value);
    }

    /**
     * Return <code>true</code> if the value of this <code>Float</code>
     * is the same as <code>NEGATIVE_INFINITY</code> or
     * <code>POSITIVE_INFINITY</code>, otherwise return <code>false</code>.
     *
     * @return whether this <code>Float</code> is (-/+) infinity
     */
    public boolean isInfinite()
    {
        return isInfinite(value);
    }

    /**
     * Convert the <code>float</code> value of this <code>Float</code>
     * to a <code>String</code>.  This method calls
     * <code>Float.toString(float)</code> to do its dirty work.
     *
     * @return the <code>String</code> representation
     * @see #toString(float)
     */
    public String toString()
    {
        return toString(value);
    }

    /**
     * Return the value of this <code>Float</code> as a <code>byte</code>.
     *
     * @return the byte value
     */
    public byte byteValue()
    {
        return (byte) value;
    }

    /**
     * Return the value of this <code>Float</code> as a <code>short</code>.
     *
     * @return the short value
     * @since 1.1
     */
    public short shortValue()
    {
        return (short) value;
    }

    /**
     * Return the value of this <code>Integer</code> as an <code>int</code>.
     *
     * @return the int value
     */
    public int intValue()
    {
        return (int) value;
    }

    /**
     * Return the value of this <code>Integer</code> as a <code>long</code>.
     *
     * @return the long value
     */
    public long longValue()
    {
        return (long) value;
    }

    /**
     * Return the value of this <code>Float</code>.
     *
     * @return the float value
     */
    public float floatValue()
    {
        return value;
    }

    /**
     * Return the value of this <code>Float</code> as a <code>double</code>
     *
     * @return the double value
     */
    public double doubleValue()
    {
        return value;
    }

    /**
     * Return a hashcode representing this Object. <code>Float</code>'s hash
     * code is calculated by calling <code>floatToIntBits(floatValue())</code>.
     *
     * @return this Object's hash code
     * @see #floatToIntBits(float)
     */
    public int hashCode()
    {
        return floatToIntBits(value);
    }

    /**
     * Returns <code>true</code> if <code>obj</code> is an instance of
     * <code>Float</code> and represents the same float value. Unlike comparing
     * two floats with <code>==</code>, this treats two instances of
     * <code>Float.NaN</code> as equal, but treats <code>0.0</code> and
     * <code>-0.0</code> as unequal.
     *
     * <p>Note that <code>f1.equals(f2)</code> is identical to
     * <code>floatToIntBits(f1.floatValue()) ==
     *    floatToIntBits(f2.floatValue())</code>.
     *
     * @param obj the object to compare
     * @return whether the objects are semantically equal
     */
    public boolean equals(Object obj)
    {
        if (! (obj instanceof Float))
            return false;

        float f = ((Float) obj).value;

        // Avoid call to native method. However, some implementations, like gcj,
        // are better off using floatToIntBits(value) == floatToIntBits(f).
        // Check common case first, then check NaN and 0.
        if (value == f)
            return (value != 0) || (1 / value == 1 / f);

        return isNaN(value) && isNaN(f);
    }

    /**
     * Convert the float to the IEEE 754 floating-point "single format" bit
     * layout. Bit 31 (the most significant) is the sign bit, bits 30-23
     * (masked by 0x7f800000) represent the exponent, and bits 22-0
     * (masked by 0x007fffff) are the mantissa. This function collapses all
     * versions of NaN to 0x7fc00000. The result of this function can be used
     * as the argument to <code>Float.intBitsToFloat(int)</code> to obtain the
     * original <code>float</code> value.
     *
     * @param value the <code>float</code> to convert
     * @return the bits of the <code>float</code>
     * @see #intBitsToFloat(int)
     */
    public native static int floatToIntBits(float value);

    /**
     * Convert the argument in IEEE 754 floating-point "single format" bit
     * layout to the corresponding float. Bit 31 (the most significant) is the
     * sign bit, bits 30-23 (masked by 0x7f800000) represent the exponent, and
     * bits 22-0 (masked by 0x007fffff) are the mantissa. This function leaves
     * NaN alone, so that you can recover the bit pattern with
     * <code>Float.floatToRawIntBits(float)</code>.
     *
     * @param bits the bits to convert
     * @return the <code>float</code> represented by the bits
     */
    public native static float intBitsToFloat(int bits);
}
