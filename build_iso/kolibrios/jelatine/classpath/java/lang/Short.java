/* Short.java -- object wrapper for short
   Copyright (C) 1998, 2001, 2002, 2005  Free Software Foundation, Inc.

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
 * Instances of class <code>Short</code> represent primitive
 * <code>short</code> values.
 *
 * Additionally, this class provides various helper functions and variables
 * related to shorts.
 *
 * @author Paul Fisher
 * @author John Keiser
 * @author Eric Blake (ebb9@email.byu.edu)
 */
public final class Short
{
    /**
     * The minimum value a <code>short</code> can represent is -32768 (or
     * -2<sup>15</sup>).
     */
    public static final short MIN_VALUE = -32768;

    /**
     * The minimum value a <code>short</code> can represent is 32767 (or
     * 2<sup>15</sup>).
     */
    public static final short MAX_VALUE = 32767;

    /**
     * The immutable value of this Short.
     *
     */
    private final short value;

    /**
     * Create a <code>Short</code> object representing the value of the
     * <code>short</code> argument.
     *
     * @param value the value to use
     */
    public Short(short value)
    {
        this.value = value;
    }

    /**
     * Converts the specified <code>String</code> into a <code>short</code>.
     * This function assumes a radix of 10.
     *
     * @param s the <code>String</code> to convert
     * @return the <code>short</code> value of <code>s</code>
     * @throws NumberFormatException if <code>s</code> cannot be parsed as a
     *         <code>short</code>
     */
    public static short parseShort(String s) throws NumberFormatException
    {
        return parseShort(s, 10);
    }

    /**
     * Converts the specified <code>String</code> into a <code>short</code>
     * using the specified radix (base). The string must not be <code>null</code>
     * or empty. It may begin with an optional '-', which will negate the answer,
     * provided that there are also valid digits. Each digit is parsed as if by
     * <code>Character.digit(d, radix)</code>, and must be in the range
     * <code>0</code> to <code>radix - 1</code>. Finally, the result must be
     * within <code>MIN_VALUE</code> to <code>MAX_VALUE</code>, inclusive.
     * Unlike Double.parseDouble, you may not have a leading '+'.
     *
     * @param s the <code>String</code> to convert
     * @param radix the radix (base) to use in the conversion
     * @return the <code>String</code> argument converted to <code>short</code>
     * @throws NumberFormatException if <code>s</code> cannot be parsed as a
     *         <code>short</code>
     */
    public static short parseShort(String s, int radix)
        throws NumberFormatException
    {
        int i = Integer.parseInt(s, radix);
    
        if ((short) i != i)
            throw new NumberFormatException();
    
        return (short) i;
    }

    /**
     * Return the value of this <code>Short</code>.
     *
     * @return the short value
     */
    public short shortValue()
    {
        return value;
    }

    /**
     * Converts the <code>short</code> to a <code>String</code> and assumes
     * a radix of 10.
     *
     * @param s the <code>short</code> to convert to <code>String</code>
     * @return the <code>String</code> representation of the argument
     */
    public static String toString(short s)
    {
        return String.valueOf(s);
    }

    /**
     * Return a hashcode representing this Object. <code>Short</code>'s hash
     * code is simply its value.
     *
     * @return this Object's hash code
     */
    public int hashCode()
    {
        return value;
    }

    /**
     * Returns <code>true</code> if <code>obj</code> is an instance of
     * <code>Short</code> and represents the same short value.
     *
     * @param obj the object to compare
     * @return whether these Objects are semantically equal
     */
    public boolean equals(Object obj)
    {
        return obj instanceof Short && value == ((Short) obj).value;
    }
}
