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

import java.io.IOException;
import java.io.OutputStream;
import java.io.DataOutputStream;

/**
 * This interface defines an output connection's methods
 */
public interface OutputConnection extends Connection
{
    /**
     * Returns a new, opened output stream for the connection.
     * @return An OutputStream object
     * @throws IOException If an error occurs
     */
    public OutputStream openOutputStream() throws IOException;

    /**
     * Returns a new, opened data output stream for the connection.
     * @return A DataOutputStream object
     * @throws IOException If an error occurs
     */
    public DataOutputStream openDataOutputStream() throws IOException;
}

