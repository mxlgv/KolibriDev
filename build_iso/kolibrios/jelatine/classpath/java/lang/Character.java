/* java.lang.Character -- Wrapper class for char, and Unicode subsets
   Copyright (C) 1998, 1999, 2001, 2002, 2005 Free Software Foundation, Inc.

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
 * Wrapper class for the primitive char data type.  In addition, this class
 * allows one to retrieve property information and perform transformations
 * on the 57,707 defined characters in the Unicode Standard, Version 3.0.0.
 * java.lang.Character is designed to be very dynamic, and as such, it
 * retrieves information on the Unicode character set from a separate
 * database, gnu.java.lang.CharData, which can be easily upgraded.
 *
 * <p>For predicates, boundaries are used to describe
 * the set of characters for which the method will return true.
 * This syntax uses fairly normal regular expression notation.
 * See 5.13 of the Unicode Standard, Version 3.0, for the
 * boundary specification.
 *
 * <p>See <a href="http://www.unicode.org">http://www.unicode.org</a>
 * for more information on the Unicode Standard.
 *
 * @author Tom Tromey (tromey@cygnus.com)
 * @author Paul N. Fisher
 * @author Jochen Hoenicke
 * @author Eric Blake (ebb9@email.byu.edu)
 */
public final class Character
{
    /**
     * Smallest value allowed for radix arguments in Java. This value is 2.
     */
    public static final int MIN_RADIX = 2;

    /**
     * Largest value allowed for radix arguments in Java. This value is 36.
     */
    public static final int MAX_RADIX = 36;

    /**
     * The minimum value the char data type can hold.
     * This value is <code>'\\u0000'</code>.
     */
    public static final char MIN_VALUE = '\u0000';

    /**
     * The maximum value the char data type can hold.
     * This value is <code>'\\uFFFF'</code>.
     */
    public static final char MAX_VALUE = '\uFFFF';

    /**
     * The immutable value of this Character.
     */
    private final char value;

    /**
     * Wraps up a character.
     *
     * @param value the character to wrap
     */
    public Character(char value)
    {
        this.value = value;
    }

    /**
     * Returns the character which has been wrapped by this class.
     *
     * @return the character wrapped
     */
    public char charValue()
    {
        return value;
    }

    /**
     * Returns the numerical value (unsigned) of the wrapped character.
     * Range of returned values: 0x0000-0xFFFF.
     *
     * @return the value of the wrapped character
     */
    public int hashCode()
    {
        return value;
    }

    /**
     * Determines if an object is equal to this object. This is only true for
     * another Character object wrapping the same value.
     *
     * @param o object to compare
     * @return true if o is a Character with the same value
     */
    public boolean equals(Object o)
    {
        return o instanceof Character && value == ((Character) o).value;
    }

    /**
     * Converts the wrapped character into a String.
     *
     * @return a String containing one character -- the wrapped character
     *         of this instance
     */
    public String toString()
    {
        // Package constructor avoids an array copy.
        return new String(new char[] { value }, 0, 1, true);
    }

    /**
     * Determines if a character is a Unicode lowercase letter. For example,
     * <code>'a'</code> is lowercase.
     * <br>
     * lowercase = [Ll]
     *
     * @param ch character to test
     * @return true if ch is a Unicode lowercase letter, else false
     */
    public static boolean isLowerCase(char ch)
    {
        if ((ch >= 'a') && (ch <= 'z'))
            return true;
        else if ((ch >= 0x00DF) && (ch <= 0x00FF) && (ch != 0x00F7))
            return true;
        else
            return false;
    }

    /**
     * Determines if a character is a Unicode uppercase letter. For example,
     * <code>'A'</code> is uppercase.
     * <br>
     * uppercase = [Lu]
     *
     * @param ch character to test
     * @return true if ch is a Unicode uppercase letter, else false
     */
    public static boolean isUpperCase(char ch)
    {
        if ((ch >= 'A') && (ch <= 'Z'))
            return true;
        else if ((ch >= 0x00C0) && (ch <= 0x00DE) && (ch != 0x00D7))
            return true;
        else
            return false;
    }

    /**
     * Determines if a character is a Unicode decimal digit. For example,
     * <code>'0'</code> is a digit.
     * <br>
     * Unicode decimal digit = [Nd]
     *
     * @param ch character to test
     * @return true if ch is a Unicode decimal digit, else false
     */
    public static boolean isDigit(char ch)
    {
        if ((ch >= '0') && (ch <= '9'))
            return true;
        else
            return false;
    }

    /**
     * Converts a Unicode character into its lowercase equivalent mapping.
     * If a mapping does not exist, then the character passed is returned.
     * Note that isLowerCase(toLowerCase(ch)) does not always return true.
     *
     * @param ch character to convert to lowercase
     * @return lowercase mapping of ch, or ch if lowercase mapping does
     *         not exist
     */
    public static char toLowerCase(char ch)
    {
        if ((ch >= 'A') && (ch <= 'Z'))
            return (char) ((ch - 'A') + 'a');
        else if ((ch >= 0x00C0) && (ch <= 0x00DE) && (ch != 0x00D7))
            return (char) ((ch - 0x00C0) + 0x00DF);
        else
            return ch;
    }

    /**
     * Converts a Unicode character into its uppercase equivalent mapping.
     * If a mapping does not exist, then the character passed is returned.
     * Note that isUpperCase(toUpperCase(ch)) does not always return true.
     *
     * @param ch character to convert to uppercase
     * @return uppercase mapping of ch, or ch if uppercase mapping does
     *         not exist
     */
    public static char toUpperCase(char ch)
    {
        if ((ch >= 'a') && (ch <= 'z'))
            return (char) ((ch - 'a') + 'A');
        else if ((ch >= 0x00DF) && (ch <= 0x00FF) && (ch != 0x00F7))
            return (char) ((ch - 0x00DF) + 0x00C0);
        else
            return ch;
    }

    /**
     * Converts a character into a digit of the specified radix. If the radix
     * exceeds MIN_RADIX or MAX_RADIX, or if the result of getNumericValue(ch)
     * exceeds the radix, or if ch is not a decimal digit or in the case
     * insensitive set of 'a'-'z', the result is -1.
     * <br>
     * character argument boundary = [Nd]|U+0041-U+005A|U+0061-U+007A
     *    |U+FF21-U+FF3A|U+FF41-U+FF5A
     *
     * @param ch character to convert into a digit
     * @param radix radix in which ch is a digit
     * @return digit which ch represents in radix, or -1 not a valid digit
     */
    public static int digit(char ch, int radix)
    {
        int value = 0;

        if (radix >= Character.MIN_RADIX && radix <= Character.MAX_RADIX)
        {
            if ((ch >= '0') && (ch <= '9'))
                value = ch - '0';
            else if (isUpperCase(ch) || isLowerCase(ch))
                value = (ch & 0x1F) + 9;
            else
                return -1;
        }
        else
            return -1;

        if (value < radix)
            return value;
        else
            return -1;
    }
} // class Character
