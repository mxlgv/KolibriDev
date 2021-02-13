/* OutputStreamWriter.java -- Writer that converts chars to bytes
   Copyright (C) 1998, 1999, 2000, 2001, 2003, 2005  Free Software Foundation, Inc.

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

package java.io;

/**
 * This class writes characters to an output stream that is byte oriented
 * It converts the chars that are written to bytes using an encoding layer,
 * which is specific to a particular encoding standard.  The desired
 * encoding can either be specified by name, or if no encoding is specified,
 * the system default encoding will be used.  The system default encoding
 * name is determined from the system property <code>file.encoding</code>.
 * The only encodings that are guaranteed to be available are "8859_1"
 * (the Latin-1 character set) and "UTF8".  Unfortunately, Java does not
 * provide a mechanism for listing the encodings that are supported in
 * a given implementation.
 * <p>
 * Here is a list of standard encoding names that may be available:
 * <p>
 * <ul>
 * <li>8859_1 (ISO-8859-1/Latin-1)
 * <li>ASCII (7-bit ASCII)
 * <li>More Later
 * </ul>
 *
 * @author Aaron M. Renn (arenn@urbanophile.com)
 * @author Per Bothner (bothner@cygnus.com)
 */
public class OutputStreamWriter extends Writer
{
    /**
     * The output stream.
     */
    private OutputStream out;

    /**
     * This method initializes a new instance of <code>OutputStreamWriter</code>
     * to write to the specified stream using a caller supplied character
     * encoding scheme.  Note that due to a deficiency in the Java language
     * design, there is no way to determine which encodings are supported.
     *
     * @param out The <code>OutputStream</code> to write to
     * @param encoding The name of the encoding scheme to use for character to
     * byte translation
     *
     * @throws UnsupportedEncodingException If the named encoding is
     * not available.
     */
    public OutputStreamWriter(OutputStream out, String encoding)
        throws UnsupportedEncodingException
    {
        if (!encoding.equals("ISO8859_1")
            && !encoding.equals("US_ASCII")
            && !encoding.equals("UTF-8"))
        {
            throw new UnsupportedEncodingException();
        }

        this.out = out;
    }

    /**
     * This method initializes a new instance of <code>OutputStreamWriter</code>
     * to write to the specified stream using the default encoding.
     *
     * @param out The <code>OutputStream</code> to write to
     */
    public OutputStreamWriter(OutputStream out)
    {
        this.out = out;
    }

    /**
     * This method writes a single character to the output stream.
     *
     * @param ch The char to write, passed as an int.
     *
     * @throws IOException If an error occurs
     */
    public void write(int ch) throws IOException
    {
        write(new char[] { (char) ch }, 0, 1);
    }

    /**
     * This method writes <code>count</code> characters from the specified
     * array to the output stream starting at position <code>offset</code>
     * into the array.
     *
     * @param buf The array of character to write from
     * @param offset The offset into the array to start writing chars from
     * @param count The number of chars to write.
     *
     * @throws IOException If an error occurs
     */
    public void write(char[] buf, int offset, int count) throws IOException
    {
        if (buf == null)
            throw new IOException();

        byte[] b = new byte[count];

        for (int i = 0; i < count; i++)
            b[i] = (byte) ((buf[offset + i] <= 0xFF) ? buf[offset + i] : '?');

        out.write(b);
    }

    /**
     * This method writes <code>count</code> bytes from the specified
     * <code>String</code> starting at position <code>offset</code> into the
     * <code>String</code>.
     *
     * @param str The <code>String</code> to write chars from
     * @param offset The position in the <code>String</code> to start
     * writing chars from
     * @param count The number of chars to write
     *
     * @throws IOException If an error occurs
     */
    public void write(String str, int offset, int count) throws IOException
    {
        if (str == null)
           throw new IOException();

        write(str.toCharArray(), offset, count);
    }

    /**
     * This method flushes any buffered bytes to the underlying output sink.
     *
     * @throws IOException If an error occurs
     */
    public void flush() throws IOException
    {
        if (out != null)
        {
            out.flush();
        }
    }

    /**
     * This method closes this stream, and the underlying
     * <code>OutputStream</code>
     *
     * @throws IOException If an error occurs
     */
    public void close() throws IOException
    {
        synchronized (lock)
        {
            if (out == null)
                return;

            flush();
            out.close();
            out = null;
        }
    }
} // class OutputStreamWriter

