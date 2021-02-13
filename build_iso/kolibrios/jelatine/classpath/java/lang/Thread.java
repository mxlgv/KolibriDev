/* Thread -- an independent thread of executable code
   Copyright (C) 1998, 1999, 2000, 2001, 2002, 2003, 2004
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

import jelatine.VMPointer;

/**
 * Thread represents a single thread of execution in the VM. When an
 * application VM starts up, it creates a non-daemon Thread which calls the
 * main() method of a particular class.  There may be other Threads running,
 * such as the garbage collection thread.
 *
 * <p>Threads have names to identify them.  These names are not necessarily
 * unique. Every Thread has a priority, as well, which tells the VM which
 * Threads should get more running time. New threads inherit the priority
 * and daemon status of the parent thread, by default.
 *
 * <p>There are two methods of creating a Thread: you may subclass Thread and
 * implement the <code>run()</code> method, at which point you may start the
 * Thread by calling its <code>start()</code> method, or you may implement
 * <code>Runnable</code> in the class you want to use and then call new
 * <code>Thread(your_obj).start()</code>.
 *
 * <p>The virtual machine runs until all non-daemon threads have died (either
 * by returning from the run() method as invoked by start(), or by throwing
 * an uncaught exception); or until <code>System.exit</code> is called with
 * adequate permissions.
 *
 * <p>It is unclear at what point a Thread should be added to a ThreadGroup,
 * and at what point it should be removed. Should it be inserted when it
 * starts, or when it is created?  Should it be removed when it is suspended
 * or interrupted?  The only thing that is clear is that the Thread should be
 * removed when it is stopped.
 *
 * @author Tom Tromey
 * @author John Keiser
 * @author Eric Blake (ebb9@email.byu.edu)
 * @see Runnable
 * @see Runtime#exit(int)
 * @see #run()
 * @see #start()
 */
public class Thread implements Runnable
{
    /** The minimum priority for a Thread. */
    public static final int MIN_PRIORITY = 1;

    /** The priority a Thread gets by default. */
    public static final int NORM_PRIORITY = 5;

    /** The maximum priority for a Thread. */
    public static final int MAX_PRIORITY = 10;

    /** The object to run(), null if this is the target. */
    final Runnable runnable;

    /** The thread name, non-null. */
    volatile String name;

    /** The internal thread representation */
    volatile VMPointer vmThread;

    /** The thread priority, 1 to 10. */
    volatile int priority;

    /** The next thread number to use. */
    private static int numAnonymousThreadsCreated = 0;


    /**
     * Allocates a new <code>Thread</code> object. This constructor has
     * the same effect as <code>Thread(null, null,</code>
     * <i>gname</i><code>)</code>, where <b><i>gname</i></b> is
     * a newly generated name. Automatically generated names are of the
     * form <code>"Thread-"+</code><i>n</i>, where <i>n</i> is an integer.
     * <p>
     * Threads created this way must have overridden their
     * <code>run()</code> method to actually do anything.  An example
     * illustrating this method being used follows:
     * <p><pre>
     *     import java.lang.*;
     *
     *     class plain01 implements Runnable {
     *         String name;
     *         plain01() {
     *             name = null;
     *         }
     *         plain01(String s) {
     *             name = s;
     *         }
     *         public void run() {
     *             if (name == null)
     *                 System.out.println("A new thread created");
     *             else
     *                 System.out.println("A new thread with name " + name +
     *                                    " created");
     *         }
     *     }
     *     class threadtest01 {
     *         public static void main(String args[] ) {
     *             int failed = 0 ;
     *
     *             <b>Thread t1 = new Thread();</b>
     *             if (t1 != null)
     *                 System.out.println("new Thread() succeed");
     *             else {
     *                 System.out.println("new Thread() failed");
     *                 failed++;
     *             }
     *         }
     *     }
     * </pre>
     *
     * @see     java.lang.Thread#Thread(java.lang.ThreadGroup,
     *          java.lang.Runnable, java.lang.String)
     */
    public Thread()
    {
        this((Runnable) null, createAnonymousThreadName());
    }

    /**
     * Allocates a new <code>Thread</code> object. This constructor has
     * the same effect as <code>Thread(null, target,</code>
     * <i>gname</i><code>)</code>, where <i>gname</i> is
     * a newly generated name. Automatically generated names are of the
     * form <code>"Thread-"+</code><i>n</i>, where <i>n</i> is an integer.
     *
     * @param target the object whose <code>run</code> method is called.
     */
    public Thread(Runnable target)
    {
        this(target, createAnonymousThreadName());
    }

    /**
     * Allocates a new <code>Thread</code> object. This constructor has
     * the same effect as <code>Thread(null, null, name)</code>.
     *
     * @param name the name of the new thread.
     */
    public Thread(String name)
    {
        this(null, name);
    }

    /**
     * Allocates a new <code>Thread</code> object. This constructor has
     * the same effect as <code>Thread(null, target, name)</code>.
     *
     * @param target the Runnable object to execute
     * @param name the name for the Thread
     * @throws NullPointerException if name is null
     */
    public Thread(Runnable target, String name)
    {
        Thread current = currentThread();

        // Use toString hack to detect null.
        this.name = name.toString();
        this.runnable = target;

        priority = current.priority;

    }

    /**
     * Generate a name for an anonymous thread.
     */
    private static synchronized String createAnonymousThreadName()
    {
      return "Thread-" + ++numAnonymousThreadsCreated;
    }


    /**
     * Get the currently executing Thread. In the situation that the
     * currently running thread was created by native code and doesn't
     * have an associated Thread object yet, a new Thread object is
     * constructed and associated with the native thread.
     *
     * @return the currently executing Thread
     */
    public native static Thread currentThread();

    /**
     * Yield to another thread. The Thread will not lose any locks it holds
     * during this time. There are no guarantees which thread will be
     * next to run, and it could even be this one, but most VMs will choose
     * the highest priority thread that has been waiting longest.
     */
    public native static void yield();

    /**
     * Suspend the current Thread's execution for the specified amount of
     * time. The Thread will not lose any locks it has during this time. There
     * are no guarantees which thread will be next to run, but most VMs will
     * choose the highest priority thread that has been waiting longest.
     *
     * @param ms the number of milliseconds to sleep.
     * @throws InterruptedException if the Thread is (or was) interrupted;
     *         it's <i>interrupted status</i> will be cleared
     * @throws IllegalArgumentException if ms is negative
     * @see #interrupt()
     */
    public native static void sleep(long ms) throws InterruptedException;

    /**
     * Start this Thread, calling the run() method of the Runnable this Thread
     * was created with, or else the run() method of the Thread itself. This
     * is the only way to start a new thread; calling run by yourself will just
     * stay in the same thread. This method implicitly sets vmThread.
     *
     * @throws IllegalThreadStateException if the thread has already started
     * @see #run()
     */
    public synchronized native void start();

    /**
     * The method of Thread that will be run if there is no Runnable object
     * associated with the Thread. Thread's implementation does nothing at all.
     */
    public void run()
    {
        if (runnable != null)
            runnable.run();
    }

    /**
     * Interrupt this Thread.
     *
     * <p>If the thread is waiting because of wait(),
     * sleep(long), or join(), its <i>interrupt status</i>
     * will be cleared, and an InterruptedException will be thrown. Notice that
     * this case is only possible if an external thread called interrupt().
     *
     * <p>Otherwise, the interrupt status will be set.
     */
    public synchronized native void interrupt();

    /**
     * Determine whether this Thread is alive. A thread which is alive has
     * started and not yet died.
     *
     * @return whether this Thread is alive
     */
    public final synchronized boolean isAlive()
    {
        return vmThread != null;
    }

    /**
     * Set this Thread's priority. There may be a security check,
     * <code>checkAccess</code>, then the priority is set to the smaller of
     * priority and the ThreadGroup maximum priority.
     *
     * @param priority the new priority for this Thread
     * @throws IllegalArgumentException if priority exceeds MIN_PRIORITY or
     *         MAX_PRIORITY
     * @throws SecurityException if you cannot modify this Thread
     */
    public final synchronized void setPriority(int priority)
    {
        if (priority < MIN_PRIORITY || priority > MAX_PRIORITY)
            throw new IllegalArgumentException();

        this.priority = priority;
    }

    /**
     * Get this Thread's priority.
     *
     * @return the Thread's priority
     */
    public final synchronized int getPriority()
    {
        return priority;
    }

    /**
     * Get this Thread's name.
     *
     * @return this Thread's name
     */
    public final String getName()
    {
        return name;
    }

    /**
     * Returns the current number of active threads in the virtual machine
     *
     * @return the current number of active threads
     */
    public native static int activeCount();

    /**
     * Wait forever for the Thread in question to die.
     *
     * @throws InterruptedException if the Thread is interrupted; it's
     *         <i>interrupted status</i> will be cleared
     */
    public native final void join() throws InterruptedException;

    /**
     * Returns a string representation of this thread, including the
     * thread's name, priority, and thread group.
     *
     * @return a human-readable String representing this Thread
     */
    public String toString()
    {
        return ("Thread[" + name + "," + priority + "]");
    }
}
