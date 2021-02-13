/* Class.java -- Representation of a Java class.
   Copyright (C) 1998, 1999, 2000, 2002, 2003, 2004, 2005
   Free Software Foundation

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

import java.io.InputStream;
import jelatine.VMResourceStream;

/**
 * A Class represents a Java type.  There will never be multiple Class
 * objects with identical names and ClassLoaders. Primitive types, array
 * types, and void also have a Class object.
 *
 * <p>Arrays with identical type and number of dimensions share the same class.
 * The array class ClassLoader is the same as the ClassLoader of the element
 * type of the array (which can be null to indicate the bootstrap classloader).
 * The name of an array class is <code>[&lt;signature format&gt;;</code>.
 * <p> For example,
 * String[]'s class is <code>[Ljava.lang.String;</code>. boolean, byte,
 * short, char, int, long, float and double have the "type name" of
 * Z,B,S,C,I,J,F,D for the purposes of array classes.  If it's a
 * multidimensioned array, the same principle applies:
 * <code>int[][][]</code> == <code>[[[I</code>.
 *
 * <p>There is no public constructor - Class objects are obtained only through
 * the virtual machine, as defined in ClassLoaders.
 *
 * @author John Keiser
 * @author Eric Blake (ebb9@email.byu.edu)
 * @author Tom Tromey (tromey@cygnus.com)
 */
public final class Class
{
    /* This class is a bootstrap class, its internal layout is managed by
     * the Jelatine VM */
    private String name;
    private int id;
    private byte is_array;
    private byte is_interface;

    /**
     * Class is non-instantiable from Java code; only the VM can create
     * instances of this class.
     */
    Class()
    {
    }

    /**
     * Return the human-readable form of this Object.  For an object, this
     * is either "interface " or "class " followed by <code>getName()</code>,
     * for primitive types and void it is just <code>getName()</code>.
     *
     * @return the human-readable form of this Object
     */
    public String toString()
    {
        return (isInterface() ? "interface " : "class ") + getName();
    }

    /**
     * Use the classloader of the current class to load, link, and initialize
     * a class. This is equivalent to your code calling
     * <code>Class.forName(name, true, getClass().getClassLoader())</code>.
     *
     * @param name the name of the class to find
     * @return the Class object representing the class
     * @throws ClassNotFoundException if the class was not found by the
     *         classloader
     * @throws LinkageError if linking the class fails
     * @throws ExceptionInInitializerError if the class loads, but an exception
     *         occurs during initialization
     */
    public native static Class forName(String name)
        throws ClassNotFoundException;

    /**
     * Get a new instance of this class by calling the no-argument constructor.
     * The class is initialized if it has not been already. A security check
     * may be performed, with <code>checkMemberAccess(this, Member.PUBLIC)</code>
     * as well as <code>checkPackageAccess</code> both having to succeed.
     *
     * @return a new instance of this class
     * @throws InstantiationException if there is not a no-arg constructor
     *         for this class, including interfaces, abstract classes, arrays,
     *         primitive types, and void; or if an exception occurred during
     *         the constructor
     * @throws IllegalAccessException if you are not allowed to access the
     *         no-arg constructor because of scoping reasons
     */
    public native Object newInstance() throws InstantiationException,
        IllegalAccessException;

    /**
     * Discover whether an Object is an instance of this Class.  Think of it
     * as almost like <code>o instanceof (this class)</code>.
     *
     * @param o the Object to check
     * @return whether o is an instance of this class
     */
    public native boolean isInstance(Object o);

    /**
     * Discover whether an instance of the Class parameter would be an
     * instance of this Class as well.  Think of doing
     * <code>isInstance(c.newInstance())</code> or even
     * <code>c.newInstance() instanceof (this class)</code>. While this
     * checks widening conversions for objects, it must be exact for primitive
     * types.
     *
     * @param c the class to check
     * @return whether an instance of c would be an instance of this class
     *         as well
     * @throws NullPointerException if c is null
     * @since 1.1
     */
    public native boolean isAssignableFrom(Class c)
        throws NullPointerException;

    /**
     * Check whether this class is an interface or not.  Array types are not
     * interfaces.
     *
     * @return whether this class is an interface or not
     */
    public boolean isInterface()
    {
        if (is_interface == 1)
            return true;
        else
            return false;
    }

    /**
     * Return whether this class is an array type.
     *
     * @return whether this class is an array type
     * @since 1.1
     */
    public boolean isArray()
    {
        if (is_array == 1)
            return true;
        else
            return false;
    }

    /**
     * Get the name of this class, separated by dots for package separators.
     * If the class represents a primitive type, or void, then the
     * name of the type as it appears in the Java programming language
     * is returned.  For instance, <code>Byte.TYPE.getName()</code>
     * returns "byte".
     *
     * Arrays are specially encoded as shown on this table.
     * <pre>
     * array type          [<em>element type</em>
     *                     (note that the element type is encoded per
     *                      this table)
     * boolean             Z
     * byte                B
     * char                C
     * short               S
     * int                 I
     * long                J
     * float               F
     * double              D
     * void                V
     * class or interface, alone: &lt;dotted name&gt;
     * class or interface, as element type: L&lt;dotted name&gt;;
     * </pre>
     *
     * @return the name of this class
     */
    public String getName()
    {
        if (name == null) {
            name = getInternalName().replace('/', '.');
        }

        return name;
    }

    /**
     * Get the name of this class in the classfile internal format.
     *
     * @return the name of this class in the classfile internal format
     */
    private native String getInternalName();

    /**
     * Finds a resource with a given name in the application's JAR file and
     * returns it as an InputStream
     *
     * @param name The name of the resource in either relative or absolute
     * format
     * @return An InputStream used for accessing the resource or null if none
     * was found
     */

    public InputStream getResourceAsStream(String name)
    {
        StringBuffer path = new StringBuffer(name);

        if (path.charAt(0) == '/') {
            /* Absolute path, remove the leading slash */
            path.deleteCharAt(0);
        } else {
            /* Relative path, prepend the directory where the class is found */
            StringBuffer dir = new StringBuffer(getName());
            int last_slash = 0;

            for (int i = 0; i < dir.length(); i++) {
                if (dir.charAt(i) == '.') {
                    dir.setCharAt(i, '/');
                    last_slash = i;
                }
            }

            if (last_slash != 0) {
                dir.delete(last_slash + 1, dir.length());
                path.insert(0, dir);
            }
        }

        VMResourceStream rs = new VMResourceStream(path.toString());

        if (rs.open()) {
            return rs;
        } else {
            return null;
        }
    }
}
