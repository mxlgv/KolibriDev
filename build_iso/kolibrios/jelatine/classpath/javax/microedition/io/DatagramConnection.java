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
import java.io.InterruptedIOException;

/**
 * This interface represents the methods implemented by a datagram connection
 */
public interface DatagramConnection extends Connection {

    /**
     * Gets the maximum length of a datagram
     * @return The datagram's maximum length
     * @throws IOException If an error occurs
     */
    public int getMaximumLength() throws IOException;

    /**
     * Gets the nominal length of a datagram
     * @return The datagram's nominal length
     * @throws IOException If an error occurs
     */
    public int getNominalLength() throws IOException;

    /**
     * Sends a single datagram
     * @param datagram The datagram to be sent
     * @throws IOException If an error occurs
     * @throws InterruptedIOException If a timeout or interrupt occurs
     * before completion
     */
    public void send(Datagram datagram) throws IOException, InterruptedIOException;

    /**
     * Receive a single datagram
     * @param datagram The datagram where the new data will be stored
     * @throws IOException If an error occurs
     * @throws InterruptedIOException If a timeout or interrupt occurs
     * before completion
     */
    public void receive(Datagram datagram)
        throws IOException, InterruptedIOException;

    /**
     * Create a new datagram
     * @param size The size of the data buffer
     * @return A newly created datagram object
     * @throws IOException If an error occurs
     * @throws IllegalArgumentException if the buffer size is negative or
     * larger than the maximum size
     */
    public Datagram newDatagram(int size)
        throws IOException, IllegalArgumentException;

    /**
     * Create a new datagram with the specified address
     * @param size The size of the data buffer
     * @param address The target address
     * @return A newly create datagram object
     * @throws IOException If an error occurs
     * @throws IllegalArgumentException if the buffer size is negative or
     * larger than the maximum size
     */
    public Datagram newDatagram(int size, String address)
        throws IOException, IllegalArgumentException;

    /**
     * Create a new datagram object with the specified data
     * @param buffer The data buffer
     * @param size The size of the data buffer
     * @return A newly created datagram object
     * @throws IOException If an error occurs
     * @throws IllegalArgumentException if the buffer size is negative or
     * larger than the maximum size
     */
    public Datagram newDatagram(byte[] buffer, int size)
        throws IOException, IllegalArgumentException;

    /**
     * Create a new datagram object with the specified size and address
     * @param buffer The data buffer
     * @param size The size of the data buffer
     * @param address The target address
     * @return A newly create datagram object
     * @throws IOException If an error occurs
     * @throws IllegalArgumentException if the buffer size is negative or
     * larger than the maximum size
     */
    public Datagram newDatagram(byte[] buffer, int size, String address)
        throws IOException, IllegalArgumentException;

}

