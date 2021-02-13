/***************************************************************************
 *   Copyright © 2005-2009 by Guillaume Legris                             *
 *   guillaume.legris@gmail.com                                            *
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

package jelatine.cldc.io;

import java.io.IOException;

import javax.microedition.io.Connection;

/** Basic socket interface */

public interface Protocol {

    /** Open a connection for the associated protocol
     * @param url The target URL
     * @param mode Mode
     * @param timeouts A flag to indicate that the caller wants timeout
     * exceptions */

    public Connection open(URL url, int mode, boolean timeouts)
                      throws IllegalArgumentException, IOException;

}
