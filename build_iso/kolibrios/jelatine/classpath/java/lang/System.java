/* System.java -- useful methods to interface with the system
   Copyright (C) 1998, 1999, 2000, 2001, 2002, 2003, 2004, 2005
   Free Software Foundation, Inc.

This file is part of GNU Classpath.

GNU Classpath is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.

GNU Classpath is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with GNU Classpath; see the file COPYING.  If not, write to the
Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301 USA.

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version. */

package java.lang;

import java.io.OutputStream;
import java.io.PrintStream;
import jelatine.VMOutputStream;

/**
 * System represents system-wide resources; things that represent the
 * general environment.  As such, all methods are static.
 *
 * @author John Keiser
 * @author Eric Blake (ebb9@email.byu.edu)
 */
public final class System
{
    /**
     * The standard output PrintStream.  This is assigned at startup and
     * starts its life perfectly valid.
     *
     * <p>This corresponds to the C stdout and C++ cout variables, which
     * typically output normal messages to the screen, but may be used to pipe
     * output to other processes or files.  That should all be transparent to
     * you, however.
     */
    public static final PrintStream out = System.makeStandardOutputStream();

    /**
     * The standard output PrintStream.  This is assigned at startup and
     * starts its life perfectly valid.
     *
     * <p>This corresponds to the C stderr and C++ cerr variables, which
     * typically output error messages to the screen, but may be used to pipe
     * output to other processes or files.  That should all be transparent to
     * you, however.
     */
    public static final PrintStream err = System.makeStandardErrorStream();

    /**
     * This class is uninstantiable.
     */
    private System()
    {
    }

    /**
     * Get the current time, measured in the number of milliseconds from the
     * beginning of Jan. 1, 1970. This is gathered from the system clock, with
     * any attendant incorrectness (it may be timezone dependent).
     *
     * @return the current time
     * @see java.util.Date
     */
    public native static long currentTimeMillis();

    /**
     * Copy one array onto another from <code>src[srcStart]</code> ...
     * <code>src[srcStart+len-1]</code> to <code>dest[destStart]</code> ...
     * <code>dest[destStart+len-1]</code>. First, the arguments are validated:
     * neither array may be null, they must be of compatible types, and the
     * start and length must fit within both arrays. Then the copying starts,
     * and proceeds through increasing slots.  If src and dest are the same
     * array, this will appear to copy the data to a temporary location first.
     * An ArrayStoreException in the middle of copying will leave earlier
     * elements copied, but later elements unchanged.
     *
     * @param src the array to copy elements from
     * @param srcStart the starting position in src
     * @param dest the array to copy elements to
     * @param destStart the starting position in dest
     * @param len the number of elements to copy
     * @throws NullPointerException if src or dest is null
     * @throws ArrayStoreException if src or dest is not an array, if they are
     *         not compatible array types, or if an incompatible runtime type
     *         is stored in dest
     * @throws IndexOutOfBoundsException if len is negative, or if the start or
     *         end copy position in either array is out of bounds
     */
    public native static void arraycopy(Object src, int srcStart, Object dest,
        int destStart, int len);

    /**
     * Get a hash code computed by the VM for the Object. This hash code will
     * be the same as Object's hashCode() method.  It is usually some
     * convolution of the pointer to the Object internal to the VM.  It
     * follows standard hash code rules, in that it will remain the same for a
     * given Object for the lifetime of that Object.
     *
     * @param o the Object to get the hash code for
     * @return the VM-dependent hash code for this Object
     * @since 1.1
     */
    public native static int identityHashCode(Object o);

    /**
     * Get a single system property by name. A security check may be performed,
     * <code>checkPropertyAccess(key)</code>.
     *
     * @param key the name of the system property to get
     * @return the property, or null if not found
     * @throws NullPointerException if key is null
     * @throws IllegalArgumentException if key is ""
     */
    public static String getProperty(String key)
    {
        if (key.equals(""))
            throw new IllegalArgumentException();
        else
        {
            if (key.equals("line.separator"))
                return "\n";
            else if (key.equals("microedition.platform"))
                return "jelatine";
            else if (key.equals("microedition.encoding"))
                return "ISO-8859-1";
            else if (key.equals("microedition.configuration"))
                return "CLDC-1.1";
            else if (key.equals("microedition.profiles"))
                return null; // To be added by the profile implementation
            else
                return null;
        }
    }

    /**
     * Terminate the Virtual Machine. This just calls
     * <code>Runtime.getRuntime().exit(status)</code>, and never returns.
     * Obviously, a security check is in order, <code>checkExit</code>.
     *
     * @param status the exit status; by convention non-zero is abnormal
     * @see Runtime#exit(int)
     */
    public static void exit(int status)
    {
        Runtime.getRuntime().exit(status);
    }

    /**
     * Calls the garbage collector. This is only a hint, and it is up to the
     * implementation what this hint suggests, but it usually causes a
     * best-effort attempt to reclaim unused memory from discarded objects.
     * This calls <code>Runtime.getRuntime().gc()</code>.
     *
     * @see Runtime#gc()
     */
    public static void gc()
    {
        Runtime.getRuntime().gc();
    }

    /**
     * Creates the standard error stream
     * @returns A reference to the standard error stream
     */
    private static PrintStream makeStandardErrorStream()
    {
        OutputStream stderr = new VMOutputStream("stderr");

        return new PrintStream(stderr);
    }

    /**
     * Creates the standard output stream
     * @returns A reference to the standard output stream
     */
    private static PrintStream makeStandardOutputStream()
    {
        OutputStream stdout = new VMOutputStream("stdout");

        return new PrintStream(stdout);
    }
} // class System
