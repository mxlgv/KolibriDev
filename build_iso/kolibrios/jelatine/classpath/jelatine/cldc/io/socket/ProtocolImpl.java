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

package jelatine.cldc.io.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connection;
import javax.microedition.io.StreamConnection;

import jelatine.cldc.io.Protocol;
import jelatine.cldc.io.URL;

/** Basic protocol implementation */

public class ProtocolImpl implements Protocol, StreamConnection {

    private String host; /**< Host name or IP address */
    private int port; /**< Port of this connection */
    private int socketId; /**< Native socket id */

    /**
     * Opens the native socket.
     *
     * @param host The host name or IP address
     * @param timeouts A flag to indicate that the caller wants timeout exceptions
     * @return The socket native id
     */
    private native int open(String host, int port, boolean timeouts);

    private native int read(int id);

    private native int readBuf(int id, byte[] buffer, int offset, int length);

    private native int write(int id, int b);

    private native int writeBuf(int id, byte[] buffer, int offset, int length);

    private native int close(int id);

    public Connection open(URL url, int mode, boolean timeouts) throws IllegalArgumentException, IOException {
        String target = url.getTarget();
        int portStartIndex = target.indexOf(':');

        if (!target.startsWith("//") || portStartIndex == -1) {
            throw new IllegalArgumentException("Malformed URL");
        }

        host = target.substring(2, portStartIndex);

        try {
            port = Integer.parseInt(target.substring(portStartIndex + 1));
        } catch(NumberFormatException e) {
            throw new IllegalArgumentException("Malformed URL: bad port");
        }

        socketId = open(host, port, timeouts);
        return this;
    }

    public DataInputStream openDataInputStream() throws IOException {
        return new DataInputStream(new SocketInputStream());
    }

    public InputStream openInputStream() throws IOException {
        return new SocketInputStream();
    }

    public DataOutputStream openDataOutputStream() throws IOException {
        return new DataOutputStream(new SocketOutputStream());
    }

    public OutputStream openOutputStream() throws IOException {
        return new SocketOutputStream();
    }

    public void close() throws IOException {
       close(socketId);
    }

    /** java.io.InputStream wrapper for sockets */

    class SocketInputStream extends InputStream {

        /** Reads the next byte from the input stream
         * @return The next byte in the input stream or -1 if no more data can
         * be read
         * @throws IOException if an error occurs */

        public int read() throws IOException {
            return ProtocolImpl.this.read(socketId);
        }

        /** Read the next \a b.length bytes of data from the input stream and
         * store them in \a b
         * @param b A byte array
         * @return The number the bytes read or -1 if at the end of stream
         * @throws IOException if an error occurs */

        public int read(byte[] b) throws IOException {
            return read(b, 0, b.length);
        }

        /**
         * This method read bytes len bytes from the stream and store them in
         * the buffer supplied by \a b
         * @param b The array into which the bytes read should be stored
         * @param off The offset into the array to start storing bytes
         * @param len The requested number of bytes to read
         * @return The actual number of bytes read, or -1 if end of stream
         * @throws IOException If an error occurs.
         */

        public int read(byte[] b, int off, int len) throws IOException {
            return ProtocolImpl.this.readBuf(socketId, b, off, len);
        }

    }

    /** java.io.OutputStream wrapper for socekts */

    class SocketOutputStream extends OutputStream {

        public void write(int b) throws IOException {
            ProtocolImpl.this.write(socketId, b);
        }

        public void write(byte[] b) throws IOException {
            write(b, 0, b.length);
        }

        public void write(byte[] b, int off, int len) throws IOException {
            ProtocolImpl.this.writeBuf(socketId, b, off, len);
        }

    }

}
