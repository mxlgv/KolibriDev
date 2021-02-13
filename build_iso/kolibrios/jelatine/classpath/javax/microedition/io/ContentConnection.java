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

/**
 * This interface defines the methods of a stream connection used for passing
 * content.
 */
public interface ContentConnection extends StreamConnection
{
    /**
     * Returns the type of content provided by the connected resource.
     * @return the type of content provided by the connected resource or null
     * if the content type is unknown
     */
    public String getType();

    /**
     * Returns a description of the encoding used for the content provided by
     * the conncted resource
     * @return a String describing the encoding used for the content provided by
     * the conncted resource or null if it is unknown */
    public String getEncoding();

    /**
     * Returns the provided content's length
     * @return the provided content's length or -1 if it is unknown
     */
    public long getLength();
}
