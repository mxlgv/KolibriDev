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

import java.io.OutputStream;
import java.lang.String;

/**
 * Internal VM class used for handling System's standard error and output
 * streams
 */
public class VMOutputStream extends OutputStream
{
    private static final int STDERR = 0;
    private static final int STDOUT = 1;

    private int type;

    /**
     * Creates a new stream of the specified type
     * @param type Stream type
     */
    public VMOutputStream(String type)
    {
        if (type.equals("stderr"))
            this.type = STDERR;
        else if (type.equals("stdout"))
            this.type = STDOUT;
    }

    /**
     * This method writes a single byte to the output stream.  The byte written
     * is the low eight bits of the <code>int</code> passed and a argument.
     * <p>
     * Subclasses must provide an implementation of this abstract method
     *
     * @param b The byte to be written to the output stream, passed as
     *          the low eight bits of an <code>int</code>
     *
     * @throws IOException If an error occurs
     */
    public void write(int b)
    {
        switch (type)
        {
            case STDERR:
                write_to_stderr((byte) b);
                break;
            case STDOUT:
                write_to_stdout((byte) b);
                break;
            default:
                ;
        }
    }

    private static native void write_to_stderr(byte b);
    private static native void write_to_stdout(byte b);
}
