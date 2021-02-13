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

package javax.microedition.io;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * This interface an input stream's connection methods
 */
public interface InputConnection extends Connection {

    /**
     * Return a new, opened input stream for a connection
     * @return An input stream object
     * @throws IOException If any I/O error occurs
     */
    public InputStream openInputStream() throws IOException;

    /**
     * Return a new, opened data input stream for a connection
     * @return An input stream object
     * @throws IOException  If an error occurs
     */
    public DataInputStream openDataInputStream() throws IOException;
}

