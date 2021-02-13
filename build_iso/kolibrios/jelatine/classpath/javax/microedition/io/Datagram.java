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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.IllegalArgumentException;

/**
 * This class represents the interface of a datagram packet
 */
public interface Datagram extends DataInput, DataOutput {

    /**
     * Get the datagram's address
     * @return a string holding the address or null if it was not set
     */
    public String getAddress();

    /**
     * Gets the data held by the data buffer
     * @return a byte array holding the data buffer
     */
    public byte[] getData();

    /**
     * Gets the length of the datagram
     * @return the datagram's length
     */
    public int getLength();

    /**
     * Gets the offset of the datagram
     * @return the datagram's offset
     */
    public int getOffset();

    /**
     * Sets the datagram address
     * @param address the new address in URL form
     * @throws IllegalArgumentException if the address is invalid
     * @throws IOException if an error occurs
     */
    public void setAddress(String address)
        throws IllegalArgumentException, IOException;

    /**
     * Sets the datagram address from another datagram
     * @param datagram source datagram for the address
     * @throws IllegalArgumentException if the address is invalid
     */
    public void setAddress(Datagram datagram) throws IllegalArgumentException;

    /**
     * Sets the length of the datagram
     * @param new_length the new length
     * @throws IllegalArgumentException if the new length exceeds the buffer
     */
    public void setLength(int new_length) throws IllegalArgumentException;

    /**
     * Sets the buffer, offset and length of the datagram
     * @param new_buffer the new data buffer
     * @param new_offset the new offset into the data buffer
     * @param new_length the new length of the data in the buffer
     * @throws IllegalArgumentException if the new length exceeds the buffer
     * or if the new_buffer parameter is invalid
     */
    public void setData(byte[] new_buffer, int new_offset, int new_length)
        throws IllegalArgumentException;

    /**
     * Resets the access pointer, the offset and the length
     */
    public void reset();
}

