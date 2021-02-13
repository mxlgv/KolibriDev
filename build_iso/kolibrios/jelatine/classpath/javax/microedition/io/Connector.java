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
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jelatine.cldc.io.Protocol;
import jelatine.cldc.io.URL;

/**
 * This class is a factory for creating Connection objects
 */
public class Connector
{
    /**
     * READ access mode
     */
    public final static int READ  = 1;

    /**
     * WRITE access mode
     */
    public final static int WRITE = 2;

    /**
     * READ_WRITE access mode
     */
    public final static int READ_WRITE = 3;
    
    private final static String PROTOCOL_PATH_KEY = "javax.microedition.io.Connector.protocolpath";
    
    private final static String DEFAULT_PROTOCOL_PATH = "jelatine.cldc.io";

    /**
     * Non-public constructor
     */
    private Connector()
    {
    }

    /**
     * Create a connection object and open it
     * @param name A String holding an URL
     * @return A newly created Connection object
     * @throws IllegalArgumentException If the parameter is invalid
     * @throws ConnectionNotFoundException If the passed name cannot be found or
     * the protocol is unsupported
     * @throws IOException If any I/O error happens
     * @throws SecurityException If access to the requested protocol handler is
     * not allowed
     */
    public static Connection open(String name) throws IllegalArgumentException,
        ConnectionNotFoundException, IOException, SecurityException
    {
        Connection c = open(name, READ_WRITE);

        return c;
    }

    /**
     * Create a connection object and open it with the requested access mode
     * @param name A String holding an URL
     * @param mode The access mode
     * @return A newly created Connection object
     * @throws IllegalArgumentException If the parameter is invalid
     * @throws ConnectionNotFoundException If the passed name cannot be found or
     * the protocol is unsupported
     * @throws IOException If any I/O error happens
     * @throws SecurityException If access to the requested protocol handler is
     * not allowed
     */
    public static Connection open(String name, int mode)
        throws IllegalArgumentException, ConnectionNotFoundException,
            IOException, SecurityException
    {
        return open(name, mode, false);
    }

    /**
     * Create a connection object and open it with the requested access mode and
     * an explicit flag asking (or not) for timeout exceptions
     * @param name A String holding an URL
     * @param mode The access mode
     * @param timeouts True if timeout-related exceptions must be thrown
     * @return A newly created Connection object
     * @throws IllegalArgumentException If the parameter is invalid
     * @throws ConnectionNotFoundException If the passed name cannot be found or
     * the protocol is unsupported
     * @throws IOException If any I/O error happens
     * @throws SecurityException If access to the requested protocol handler is
     * not allowed
     */
    public static Connection open(String name, int mode, boolean timeouts)
        throws IllegalArgumentException, ConnectionNotFoundException,
            IOException, SecurityException
    {
        if ((name == null) || (mode < 1 || mode > 3))
            throw new IllegalArgumentException();

        // Parse URL
        URL url = new URL(name);
        
        // Get the protocol implementation class
        String protocolPath = System.getProperty(PROTOCOL_PATH_KEY);
        if (protocolPath == null) {
            protocolPath = DEFAULT_PROTOCOL_PATH;
        }
        
        String protocolClassName = protocolPath + "." + url.getScheme() + ".ProtocolImpl";
        Protocol protocolImpl = null;
        try {
            Class protocolClass = Class.forName(protocolClassName);
            protocolImpl = (Protocol)protocolClass.newInstance();
        } catch (Exception e) {
            throw new ConnectionNotFoundException("Unknown Protocol: " + url.getScheme());
        }
        
        // Now really open the connection
        Connection connection = protocolImpl.open(url, mode, timeouts);

        return connection;
    }

    /**
     * Creates a new connection input stream and open it
     * @param name A String holding an URL
     * @return A newly created DataInputStream object
     * @throws IllegalArgumentException If the parameter is invalid
     * @throws ConnectionNotFoundException If the passed name cannot be found or
     * the protocol is unsupported
     * @throws IOException If any I/O error happens
     * @throws SecurityException If access to the requested protocol handler is
     * not allowed
     */
    public static DataInputStream openDataInputStream(String name)
        throws IllegalArgumentException, ConnectionNotFoundException,
            IOException, SecurityException
    {
        InputConnection connection = (InputConnection)
            Connector.open(name, Connector.READ);

        DataInputStream stream;

        try
        {
            stream = connection.openDataInputStream();
            return stream;
        }
        finally
        {
            connection.close();
        }
    }

    /**
     * Creates a new connection output stream and open it
     * @param name A String holding an URL
     * @return A newly created DataOutputStream object
     * @throws IllegalArgumentException If the parameter is invalid
     * @throws ConnectionNotFoundException If the passed name cannot be found or
     * the protocol is unsupported
     * @throws IOException If any I/O error happens
     * @throws SecurityException If access to the requested protocol handler is
     * not allowed
     */
    public static DataOutputStream openDataOutputStream(String name)
        throws IllegalArgumentException, ConnectionNotFoundException,
            IOException, SecurityException
    {
        OutputConnection connection = (OutputConnection)
            Connector.open(name, Connector.WRITE);

        DataOutputStream stream;

        try
        {
            stream = connection.openDataOutputStream();
            return stream;
        }
        finally
        {
            connection.close();
        }
    }

    /**
     * Creates a new connection input stream and open it
     * @param name A String holding an URL
     * @return A newly created InputStream object
     * @throws IllegalArgumentException If the parameter is invalid
     * @throws ConnectionNotFoundException If the passed name cannot be found or
     * the protocol is unsupported
     * @throws IOException If any I/O error happens
     * @throws SecurityException If access to the requested protocol handler is
     * not allowed
     */
    public static InputStream openInputStream(String name)
        throws IllegalArgumentException, ConnectionNotFoundException,
            IOException, SecurityException
    {
        return openDataInputStream(name);
    }

    /**
     * Creates a new connection output stream and open it
     * @param name A String holding an URL
     * @return A newly created OutputStream object
     * @throws IllegalArgumentException If the parameter is invalid
     * @throws ConnectionNotFoundException If the passed name cannot be found or
     * the protocol is unsupported
     * @throws IOException If any I/O error happens
     * @throws SecurityException If access to the requested protocol handler is
     * not allowed
     */
    public static OutputStream openOutputStream(String name)
        throws IllegalArgumentException, ConnectionNotFoundException,
            IOException, SecurityException
    {
	    return openDataOutputStream(name);
    }
    
}

