/* StringBuffer.java -- Growable strings
   Copyright (C) 1998, 1999, 2000, 2001, 2002, 2003, 2004, 2005
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

import java.lang.System;

/**
 * <code>StringBuffer</code> represents a changeable <code>String</code>.
 * It provides the operations required to modify the
 * <code>StringBuffer</code>, including insert, replace, delete, append,
 * and reverse. It is thread-safe; meaning that all modifications to a buffer
 * are in synchronized methods.
 *
 * <p><code>StringBuffer</code>s are variable-length in nature, so even if
 * you initialize them to a certain size, they can still grow larger than
 * that. <em>Capacity</em> indicates the number of characters the
 * <code>StringBuffer</code> can have in it before it has to grow (growing
 * the char array is an expensive operation involving <code>new</code>).
 *
 * <p>Incidentally, compilers often implement the String operator "+"
 * by using a <code>StringBuffer</code> operation:<br>
 * <code>a + b</code><br>
 * is the same as<br>
 * <code>new StringBuffer().append(a).append(b).toString()</code>.
 *
 * <p>Classpath's StringBuffer is capable of sharing memory with Strings for
 * efficiency.  This will help when a StringBuffer is converted to a String
 * and the StringBuffer is not changed after that (quite common when performing
 * string concatenation).
 *
 * @author Paul Fisher
 * @author John Keiser
 * @author Tom Tromey
 * @author Eric Blake (ebb9@email.byu.edu)
 * @see String
 * @since 1.0
 */
public final class StringBuffer
{
    /**
     * Index of next available character (and thus the size of the current
     * string contents).  Note that this has permissions set this way so that
     * String can get the value.
     */
    int count;

    /**
     * The buffer.  Note that this has permissions set this way so that String
     * can get the value.
     */
    char[] value;

    /**
     * True if the buffer is shared with another object (StringBuffer or
     * String); this means the buffer must be copied before writing to it again.
     * Note that this has permissions set this way so that String can get the
     * value.
     */
    boolean shared;

    /**
     * The default capacity of a buffer.
     */
    private static final int DEFAULT_CAPACITY = 16;

    /**
     * Create a new StringBuffer with default capacity 16.
     */
    public StringBuffer()
    {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Create an empty <code>StringBuffer</code> with the specified initial
     * capacity.
     *
     * @param capacity the initial capacity
     * @throws NegativeArraySizeException if capacity is negative
     */
    public StringBuffer(int capacity)
    {
        value = new char[capacity];
    }

    /**
     * Create a new <code>StringBuffer</code> with the characters in the
     * specified <code>String</code>. Initial capacity will be the size of the
     * String plus 16.
     *
     * @param str the <code>String</code> to convert
     * @throws NullPointerException if str is null
     */
    public StringBuffer(String str)
    {
        // Unfortunately, because the size is 16 larger, we cannot share.
        count = str.count;
        value = new char[count + DEFAULT_CAPACITY];
        str.getChars(0, count, value, 0);
    }

    /**
     * Get the length of the <code>String</code> this <code>StringBuffer</code>
     * would create. Not to be confused with the <em>capacity</em> of the
     * <code>StringBuffer</code>.
     *
     * @return the length of this <code>StringBuffer</code>
     * @see #capacity()
     * @see #setLength(int)
     */
    public synchronized int length()
    {
        return count;
    }

    /**
     * Get the total number of characters this <code>StringBuffer</code> can
     * support before it must be grown.  Not to be confused with <em>length</em>.
     *
     * @return the capacity of this <code>StringBuffer</code>
     * @see #length()
     * @see #ensureCapacity(int)
     */
    public synchronized int capacity()
    {
        return value.length;
    }

    /**
     * Increase the capacity of this <code>StringBuffer</code>. This will
     * ensure that an expensive growing operation will not occur until
     * <code>minimumCapacity</code> is reached. The buffer is grown to the
     * larger of <code>minimumCapacity</code> and
     * <code>capacity() * 2 + 2</code>, if it is not already large enough.
     *
     * @param minimumCapacity the new capacity
     * @see #capacity()
     */
    public synchronized void ensureCapacity(int minimumCapacity)
    {
        ensureCapacity_unsynchronized(minimumCapacity);
    }

    /**
     * Set the length of this StringBuffer. If the new length is greater than
     * the current length, all the new characters are set to 0. If the new
     * length is less than the current length, the first <code>newLength</code>
     * characters of the old array will be preserved, and the remaining
     * characters are truncated.
     *
     * @param newLength the new length
     * @throws IndexOutOfBoundsException if the new length is negative
     *         (while unspecified, this is a StringIndexOutOfBoundsException)
     * @see #length()
     */
    public synchronized void setLength(int newLength)
    {
        if (newLength < 0)
          throw new StringIndexOutOfBoundsException();

        int valueLength = value.length;

        /* Always call ensureCapacity_unsynchronized in order to preserve
         * copy-on-write semantics.  */
        ensureCapacity_unsynchronized(newLength);

        if (newLength < valueLength)
        {
            /* If the StringBuffer's value just grew, then we know that
             * value is newly allocated and the region between count and
             * newLength is filled with '\0'.  */
            count = newLength;
        }
        else
        {
        	/* The StringBuffer's value doesn't need to grow.  However,
             * we should clear out any cruft that may exist.  */
            while (count < newLength)
                value[count++] = '\0';
        }
    }

   /**
     * Get the character at the specified index.
     *
     * @param index the index of the character to get, starting at 0
     * @return the character at the specified index
     * @throws IndexOutOfBoundsException if index is negative or &gt;= length()
     */
    public synchronized char charAt(int index)
    {
        if (index < 0 || index >= count)
            throw new StringIndexOutOfBoundsException();

        return value[index];
    }

    /**
     * Get the specified array of characters. <code>srcOffset - srcEnd</code>
     * characters will be copied into the array you pass in.
     *
     * @param srcOffset the index to start copying from (inclusive)
     * @param srcEnd the index to stop copying from (exclusive)
     * @param dst the array to copy into
     * @param dstOffset the index to start copying into
     * @throws NullPointerException if dst is null
     * @throws IndexOutOfBoundsException if any source or target indices are
     *         out of range (while unspecified, source problems cause a
     *         StringIndexOutOfBoundsException, and dest problems cause an
     *         ArrayIndexOutOfBoundsException)
     * @see System#arraycopy(Object, int, Object, int, int)
     */
    public synchronized void getChars(int srcOffset, int srcEnd,
        char[] dst, int dstOffset)
    {
        if (srcOffset < 0 || srcEnd > count || srcEnd < srcOffset)
            throw new StringIndexOutOfBoundsException();

        System.arraycopy(value, srcOffset, dst, dstOffset, srcEnd - srcOffset);
    }

    /**
     * Set the character at the specified index.
     *
     * @param index the index of the character to set starting at 0
     * @param ch the value to set that character to
     * @throws IndexOutOfBoundsException if index is negative or &gt;= length()
     *         (while unspecified, this is a StringIndexOutOfBoundsException)
     */
    public synchronized void setCharAt(int index, char ch)
    {
        if (index < 0 || index >= count)
            throw new StringIndexOutOfBoundsException();

        // Call ensureCapacity to enforce copy-on-write.
        ensureCapacity_unsynchronized(count);
        value[index] = ch;
    }

    /**
     * Append the <code>String</code> value of the argument to this
     * <code>StringBuffer</code>. Uses <code>String.valueOf()</code> to convert
     * to <code>String</code>.
     *
     * @param obj the <code>Object</code> to convert and append
     * @return this <code>StringBuffer</code>
     * @see String#valueOf(Object)
     * @see #append(String)
     */
    public StringBuffer append(Object obj)
    {
        return append(obj == null ? "null" : obj.toString());
    }

    /**
     * Append the <code>String</code> to this <code>StringBuffer</code>. If
     * str is null, the String "null" is appended.
     *
     * @param str the <code>String</code> to append
     * @return this <code>StringBuffer</code>
     */
    public synchronized StringBuffer append(String str)
    {
        if (str == null)
            str = "null";

        int len = str.count;
        ensureCapacity_unsynchronized(count + len);
        str.getChars(0, len, value, count);
        count += len;
        return this;
    }

    /**
     * Append the <code>StringBuffer</code> value of the argument to this
     * <code>StringBuffer</code>. This behaves the same as
     * <code>append((Object) stringBuffer)</code>, except it is more efficient.
     *
     * @param stringBuffer the <code>StringBuffer</code> to convert and append
     * @return this <code>StringBuffer</code>
     * @see #append(Object)
     * @since 1.4
     */
    public synchronized StringBuffer append(StringBuffer stringBuffer)
    {
        if (stringBuffer == null)
            return append("null");

        synchronized (stringBuffer)
        {
            int len = stringBuffer.count;
            ensureCapacity_unsynchronized(count + len);
            System.arraycopy(stringBuffer.value, 0, value, count, len);
            count += len;
        }

        return this;
    }

    /**
     * Append the <code>char</code> array to this <code>StringBuffer</code>.
     * This is similar (but more efficient) than
     * <code>append(new String(data))</code>, except in the case of null.
     *
     * @param data the <code>char[]</code> to append
     * @return this <code>StringBuffer</code>
     * @throws NullPointerException if <code>str</code> is <code>null</code>
     * @see #append(char[], int, int)
     */
    public StringBuffer append(char[] data)
    {
        return append(data, 0, data.length);
    }

    /**
     * Append part of the <code>char</code> array to this
     * <code>StringBuffer</code>. This is similar (but more efficient) than
     * <code>append(new String(data, offset, count))</code>, except in the case
     * of null.
     *
     * @param data the <code>char[]</code> to append
     * @param offset the start location in <code>str</code>
     * @param count the number of characters to get from <code>str</code>
     * @return this <code>StringBuffer</code>
     * @throws NullPointerException if <code>str</code> is <code>null</code>
     * @throws IndexOutOfBoundsException if offset or count is out of range
     *         (while unspecified, this is a StringIndexOutOfBoundsException)
     */
    public synchronized StringBuffer append(char[] data, int offset, int count)
    {
        if (offset < 0 || count < 0 || offset > data.length - count)
            throw new StringIndexOutOfBoundsException();

        ensureCapacity_unsynchronized(this.count + count);
        System.arraycopy(data, offset, value, this.count, count);
        this.count += count;
        return this;
    }

    /**
     * Append the <code>String</code> value of the argument to this
     * <code>StringBuffer</code>. Uses <code>String.valueOf()</code> to convert
     * to <code>String</code>.
     *
     * @param bool the <code>boolean</code> to convert and append
     * @return this <code>StringBuffer</code>
     * @see String#valueOf(boolean)
     */
    public StringBuffer append(boolean bool)
    {
        return append(bool ? "true" : "false");
    }

    /**
     * Append the <code>char</code> to this <code>StringBuffer</code>.
     *
     * @param ch the <code>char</code> to append
     * @return this <code>StringBuffer</code>
     */
    public synchronized StringBuffer append(char ch)
    {
        ensureCapacity_unsynchronized(count + 1);
        value[count++] = ch;
        return this;
    }

    /**
     * Append the <code>String</code> value of the argument to this
     * <code>StringBuffer</code>. Uses <code>String.valueOf()</code> to convert
     * to <code>String</code>.
     *
     * @param inum the <code>int</code> to convert and append
     * @return this <code>StringBuffer</code>
     * @see String#valueOf(int)
     */
    // This is native in libgcj, for efficiency.
    public StringBuffer append(int inum)
    {
        return append(String.valueOf(inum));
    }

    /**
     * Append the <code>String</code> value of the argument to this
     * <code>StringBuffer</code>. Uses <code>String.valueOf()</code> to convert
     * to <code>String</code>.
     *
     * @param lnum the <code>long</code> to convert and append
     * @return this <code>StringBuffer</code>
     * @see String#valueOf(long)
     */
    public StringBuffer append(long lnum)
    {
        return append(Long.toString(lnum, 10));
    }

    /**
     * Append the <code>String</code> value of the argument to this
     * <code>StringBuffer</code>. Uses <code>String.valueOf()</code> to convert
     * to <code>String</code>.
     *
     * @param fnum the <code>float</code> to convert and append
     * @return this <code>StringBuffer</code>
     * @see String#valueOf(float)
     */
    public StringBuffer append(float fnum)
    {
        return append(Float.toString(fnum));
    }

    /**
     * Append the <code>String</code> value of the argument to this
     * <code>StringBuffer</code>. Uses <code>String.valueOf()</code> to convert
     * to <code>String</code>.
     *
     * @param dnum the <code>double</code> to convert and append
     * @return this <code>StringBuffer</code>
     * @see String#valueOf(double)
     */
    public StringBuffer append(double dnum)
    {
        return append(Double.toString(dnum));
    }

    /**
     * Delete characters from this <code>StringBuffer</code>.
     * <code>delete(10, 12)</code> will delete 10 and 11, but not 12. It is
     * harmless for end to be larger than length().
     *
     * @param start the first character to delete
     * @param end the index after the last character to delete
     * @return this <code>StringBuffer</code>
     * @throws StringIndexOutOfBoundsException if start or end are out of bounds
     * @since 1.2
     */
    public synchronized StringBuffer delete(int start, int end)
    {
        if (start < 0 || start > count || start > end)
          throw new StringIndexOutOfBoundsException();

        if (end > count)
            end = count;

        // This will unshare if required.
        ensureCapacity_unsynchronized(count);

        if (count - end != 0)
            System.arraycopy(value, end, value, start, count - end);

        count -= end - start;
        return this;
    }

    /**
     * Delete a character from this <code>StringBuffer</code>.
     *
     * @param index the index of the character to delete
     * @return this <code>StringBuffer</code>
     * @throws StringIndexOutOfBoundsException if index is out of bounds
     * @since 1.2
     */
    public StringBuffer deleteCharAt(int index)
    {
        return delete(index, index + 1);
    }

    /**
     * Insert the <code>String</code> value of the argument into this
     * <code>StringBuffer</code>. Uses <code>String.valueOf()</code> to convert
     * to <code>String</code>.
     *
     * @param offset the place to insert in this buffer
     * @param obj the <code>Object</code> to convert and insert
     * @return this <code>StringBuffer</code>
     * @exception StringIndexOutOfBoundsException if offset is out of bounds
     * @see String#valueOf(Object)
     */
    public StringBuffer insert(int offset, Object obj)
    {
        return insert(offset, obj == null ? "null" : obj.toString());
    }

    /**
     * Insert the <code>String</code> argument into this
     * <code>StringBuffer</code>. If str is null, the String "null" is used
     * instead.
     *
     * @param offset the place to insert in this buffer
     * @param str the <code>String</code> to insert
     * @return this <code>StringBuffer</code>
     * @throws StringIndexOutOfBoundsException if offset is out of bounds
     */
    public synchronized StringBuffer insert(int offset, String str)
    {
        if (offset < 0 || offset > count)
            throw new StringIndexOutOfBoundsException();

        if (str == null)
            str = "null";

        int len = str.count;
        ensureCapacity_unsynchronized(count + len);
        System.arraycopy(value, offset, value, offset + len, count - offset);
        str.getChars(0, len, value, offset);
        count += len;
        return this;
    }

    /**
     * Insert the <code>char[]</code> argument into this
     * <code>StringBuffer</code>.
     *
     * @param offset the place to insert in this buffer
     * @param data the <code>char[]</code> to insert
     * @return this <code>StringBuffer</code>
     * @throws NullPointerException if <code>data</code> is <code>null</code>
     * @throws StringIndexOutOfBoundsException if offset is out of bounds
     */
    public StringBuffer insert(int offset, char[] data)
    {
        int i;

        for (i = offset; i < data.length; i++)
            value[i] = data[i - offset];

        return this;
    }

    /**
     * Insert the <code>String</code> value of the argument into this
     * <code>StringBuffer</code>. Uses <code>String.valueOf()</code> to convert
     * to <code>String</code>.
     *
     * @param offset the place to insert in this buffer
     * @param bool the <code>boolean</code> to convert and insert
     * @return this <code>StringBuffer</code>
     * @throws StringIndexOutOfBoundsException if offset is out of bounds
     * @see String#valueOf(boolean)
     */
    public StringBuffer insert(int offset, boolean bool)
    {
        return insert(offset, bool ? "true" : "false");
    }

    /**
     * Insert the <code>char</code> argument into this <code>StringBuffer</code>.
     *
     * @param offset the place to insert in this buffer
     * @param ch the <code>char</code> to insert
     * @return this <code>StringBuffer</code>
     * @throws StringIndexOutOfBoundsException if offset is out of bounds
     */
    public synchronized StringBuffer insert(int offset, char ch)
    {
        if (offset < 0 || offset > count)
            throw new StringIndexOutOfBoundsException();

        ensureCapacity_unsynchronized(count + 1);
        System.arraycopy(value, offset, value, offset + 1, count - offset);
        value[offset] = ch;
        count++;
        return this;
    }

    /**
     * Insert the <code>String</code> value of the argument into this
     * <code>StringBuffer</code>. Uses <code>String.valueOf()</code> to convert
     * to <code>String</code>.
     *
     * @param offset the place to insert in this buffer
     * @param inum the <code>int</code> to convert and insert
     * @return this <code>StringBuffer</code>
     * @throws StringIndexOutOfBoundsException if offset is out of bounds
     * @see String#valueOf(int)
     */
    public StringBuffer insert(int offset, int inum)
    {
        return insert(offset, String.valueOf(inum));
    }

    /**
     * Insert the <code>String</code> value of the argument into this
     * <code>StringBuffer</code>. Uses <code>String.valueOf()</code> to convert
     * to <code>String</code>.
     *
     * @param offset the place to insert in this buffer
     * @param lnum the <code>long</code> to convert and insert
     * @return this <code>StringBuffer</code>
     * @throws StringIndexOutOfBoundsException if offset is out of bounds
     * @see String#valueOf(long)
     */
    public StringBuffer insert(int offset, long lnum)
    {
        return insert(offset, Long.toString(lnum, 10));
    }

    /**
     * Insert the <code>String</code> value of the argument into this
     * <code>StringBuffer</code>. Uses <code>String.valueOf()</code> to convert
     * to <code>String</code>.
     *
     * @param offset the place to insert in this buffer
     * @param fnum the <code>float</code> to convert and insert
     * @return this <code>StringBuffer</code>
     * @throws StringIndexOutOfBoundsException if offset is out of bounds
     * @see String#valueOf(float)
     */
    public StringBuffer insert(int offset, float fnum)
    {
        return insert(offset, Float.toString(fnum));
    }

    /**
     * Insert the <code>String</code> value of the argument into this
     * <code>StringBuffer</code>. Uses <code>String.valueOf()</code> to convert
     * to <code>String</code>.
     *
     * @param offset the place to insert in this buffer
     * @param dnum the <code>double</code> to convert and insert
     * @return this <code>StringBuffer</code>
     * @throws StringIndexOutOfBoundsException if offset is out of bounds
     * @see String#valueOf(double)
     */
    public StringBuffer insert(int offset, double dnum)
    {
        return insert(offset, Double.toString(dnum));
    }

    /**
     * Reverse the characters in this StringBuffer. The same sequence of
     * characters exists, but in the reverse index ordering.
     *
     * @return this <code>StringBuffer</code>
     */
    public synchronized StringBuffer reverse()
    {
        // Call ensureCapacity to enforce copy-on-write.
        ensureCapacity_unsynchronized(count);

        for (int i = count >> 1, j = count - i; --i >= 0; ++j)
        {
            char c = value[i];
            value[i] = value[j];
            value[j] = c;
        }

        return this;
    }

    /**
     * Convert this <code>StringBuffer</code> to a <code>String</code>. The
     * String is composed of the characters currently in this StringBuffer. Note
     * that the result is a copy, and that future modifications to this buffer
     * do not affect the String.
     *
     * @return the characters in this StringBuffer
     */
    public String toString()
    {
        // The string will set this.shared = true.
        return new String(this);
    }

    /**
     * An unsynchronized version of ensureCapacity, used internally to avoid
     * the cost of a second lock on the same object. This also has the side
     * effect of duplicating the array, if it was shared (to form copy-on-write
     * semantics).
     *
     * @param minimumCapacity the minimum capacity
     * @see #ensureCapacity(int)
     */
    private void ensureCapacity_unsynchronized(int minimumCapacity)
    {
        if (shared || minimumCapacity > value.length)
        {
            // We don't want to make a larger vector when `shared' is
            // set.  If we do, then setLength becomes very inefficient
            // when repeatedly reusing a StringBuffer in a loop.
            int max = (minimumCapacity > value.length
                ? value.length * 2 + 2 : value.length);

            minimumCapacity = (minimumCapacity < max ? max : minimumCapacity);
            char[] nb = new char[minimumCapacity];
            System.arraycopy(value, 0, nb, 0, count);
            value = nb;
            shared = false;
        }
    }
}
