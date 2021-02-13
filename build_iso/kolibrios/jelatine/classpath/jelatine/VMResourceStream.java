/***************************************************************************
 *   Copyright © 2005-2011 by Gabriele Svelto                              *
 *   gabriele.svelto@gmail.com                                             *
 *                                                                         *
 *   This file is part of Jelatine.                                        *
 *                                                                         *
 *   Jelatine is free software: you can redistribute it and/or modify      *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation, either version 3 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   Jelatine is distributed in the hope that it will be useful,           *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with Jelatine.  If not, see <http://www.gnu.org/licenses/>.     *
 ***************************************************************************/

package jelatine;

import java.io.InputStream;
import java.lang.String;
import jelatine.VMPointer;

/**
 * Internal VM class used for handling System's standard error and output
 * streams
 */
public class VMResourceStream extends InputStream
{
    /** Internal field handled by the VM */
    private VMPointer handle;

    /** Name of the resource */
    private String resource;

    /** Available bytes */
    private int available;

    /**
     * Creates a new stream for the specified resource
     * @param resource Resource name
     */
    public VMResourceStream(String resource)
    {
        this.resource = resource;
        available = 0;
    }

    /**
     * Opens the resource stream
     * @return true if the resource held by the object is available, false
     * otherwise
     */
    public native boolean open();

    /**
     * Returns the number of bytes that can be read (or skipped over) from this
     * input stream without blocking by the next caller of a method for this
     * input stream. The next caller might be the same thread or another thread
     *
     * @return the number of bytes that can be read from this input stream
     * without blocking
     */
    public int available()
    {
        return available;
    }

    /**
     * This method reads an unsigned byte from the input stream and returns it
     * as an int in the range of 0-255.  This method also will return -1 if
     * the end of the stream has been reached.
     * <p>
     * This method will block until the byte can be read.
     *
     * @return The byte read or -1 if end of stream
     */
    public native int read();

    /**
     * Finalize method
     */
    protected native void finalize();
}
