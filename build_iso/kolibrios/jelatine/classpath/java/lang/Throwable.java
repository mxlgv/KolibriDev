/* java.lang.Throwable -- Root class for all Exceptions and Errors
   Copyright (C) 1998, 1999, 2002, 2004, 2005  Free Software Foundation, Inc.

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

/**
 * Throwable is the superclass of all exceptions that can be raised.
 *
 * <p>There are two special cases: Error and RuntimeException:
 * these two classes (and their subclasses) are considered unchecked
 * exceptions, and are either frequent enough or catastrophic enough that you
 * do not need to declare them in <code>throws</code> clauses.  Everything
 * else is a checked exception, and is ususally a subclass of
 * Exception; these exceptions have to be handled or declared.
 *
 * <p>Instances of this class are usually created with knowledge of the
 * execution context, so that you can get a stack trace of the problem spot
 * in the code.  Also, since JDK 1.4, Throwables participate in "exception
 * chaining."  This means that one exception can be caused by another, and
 * preserve the information of the original.
 *
 * <p>One reason this is useful is to wrap exceptions to conform to an
 * interface.  For example, it would be bad design to require all levels
 * of a program interface to be aware of the low-level exceptions thrown
 * at one level of abstraction. Another example is wrapping a checked
 * exception in an unchecked one, to communicate that failure occured
 * while still obeying the method throws clause of a superclass.
 *
 * <p>A cause is assigned in one of two ways; but can only be assigned once
 * in the lifetime of the Throwable.  There are new constructors added to
 * several classes in the exception hierarchy that directly initialize the
 * cause, or you can use the <code>initCause</code> method. This second
 * method is especially useful if the superclass has not been retrofitted
 * with new constructors:<br>
 * <pre>
 * try
 *   {
 *     lowLevelOp();
 *   }
 * catch (LowLevelException lle)
 *   {
 *     throw (HighLevelException) new HighLevelException().initCause(lle);
 *   }
 * </pre>
 * Notice the cast in the above example; without it, your method would need
 * a throws clase that declared Throwable, defeating the purpose of chainig
 * your exceptions.
 *
 * <p>By convention, exception classes have two constructors: one with no
 * arguments, and one that takes a String for a detail message.  Further,
 * classes which are likely to be used in an exception chain also provide
 * a constructor that takes a Throwable, with or without a detail message
 * string.
 *
 * <p>Another 1.4 feature is the StackTrace, a means of reflection that
 * allows the program to inspect the context of the exception, and which is
 * serialized, so that remote procedure calls can correctly pass exceptions.
 *
 * @author Brian Jones
 * @author John Keiser
 * @author Mark Wielaard
 * @author Tom Tromey
 * @author Eric Blake (ebb9@email.byu.edu)
 */
public class Throwable
{
    /**
     * The detail message.
     */
    private final String detailMessage;

    /**
     * Instantiate this Throwable with an empty message. The cause remains
     * uninitialized.  fillInStackTrace() will be called to set
     * up the stack trace.
     */
    public Throwable()
    {
        this((String) null);
    }

    /**
     * Instantiate this Throwable with the given message. The cause remains
     * uninitialized.  fillInStackTrace() will be called to set
     * up the stack trace.
     *
     * @param message the message to associate with the Throwable
     */
    public Throwable(String message)
    {
        detailMessage = message;
    }

    /**
     * Get the message associated with this Throwable.
     *
     * @return the error message associated with this Throwable, may be null
     */
    public String getMessage()
    {
        return detailMessage;
    }

    /**
     * Get a human-readable representation of this Throwable. The detail message
     * is retrieved by getLocalizedMessage().  Then, with a null detail
     * message, this string is simply the object's class name; otherwise
     * the string is <code>getClass().getName() + ": " + message</code>.
     *
     * @return a human-readable String represting this Throwable
     */
    public String toString()
    {
        String msg = getMessage();
        return getClass().getName() + (msg == null ? "" : ": " + msg);
    }

    /**
     * Print a stack trace to the standard error stream. This stream is the
     * current contents of <code>System.err</code>. The first line of output
     * is the result of toString(), and the remaining lines represent
     * the data created by fillInStackTrace(). While the format is
     * unspecified, this implementation uses the suggested format, demonstrated
     * by this example:<br>
     * <pre>
     * public class Junk
     * {
     *   public static void main(String args[])
     *   {
     *     try
     *       {
     *         a();
     *       }
     *     catch(HighLevelException e)
     *       {
     *         e.printStackTrace();
     *       }
     *   }
     *   static void a() throws HighLevelException
     *   {
     *     try
     *       {
     *         b();
     *       }
     *     catch(MidLevelException e)
     *       {
     *         throw new HighLevelException(e);
     *       }
     *   }
     *   static void b() throws MidLevelException
     *   {
     *     c();
     *   }
     *   static void c() throws MidLevelException
     *   {
     *     try
     *       {
     *         d();
     *       }
     *     catch(LowLevelException e)
     *       {
     *         throw new MidLevelException(e);
     *       }
     *   }
     *   static void d() throws LowLevelException
     *   {
     *     e();
     *   }
     *   static void e() throws LowLevelException
     *   {
     *     throw new LowLevelException();
     *   }
     * }
     * class HighLevelException extends Exception
     * {
     *   HighLevelException(Throwable cause) { super(cause); }
     * }
     * class MidLevelException extends Exception
     * {
     *   MidLevelException(Throwable cause)  { super(cause); }
     * }
     * class LowLevelException extends Exception
     * {
     * }
     * </pre>
     * <p>
     * <pre>
     *  HighLevelException: MidLevelException: LowLevelException
     *          at Junk.a(Junk.java:13)
     *          at Junk.main(Junk.java:4)
     *  Caused by: MidLevelException: LowLevelException
     *          at Junk.c(Junk.java:23)
     *          at Junk.b(Junk.java:17)
     *          at Junk.a(Junk.java:11)
     *          ... 1 more
     *  Caused by: LowLevelException
     *          at Junk.e(Junk.java:30)
     *          at Junk.d(Junk.java:27)
     *          at Junk.c(Junk.java:21)
     *          ... 3 more
     * </pre>
     */
    public native void printStackTrace();
}
