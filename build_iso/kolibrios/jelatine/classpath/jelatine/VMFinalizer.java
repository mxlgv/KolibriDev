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

package jelatine;

import java.lang.Runnable;

/**
 * Internal VM class used for finalizing other objects.
 * This class uses a number of tricks to achieve its purpose. For example all
 * objects which need to be finalized are cast to VMFinalizer even though they
 * do not inherit from the class. Since this is done only for invoking the
 * finalize() method which is available in every class the call works
 */
public final class VMFinalizer implements Runnable
{
    /**
     * Gets the next object in the finalization queue
     * @returns An object or null if the queue is empty
     */
    public native static VMFinalizer getNextObject();

    /**
     * Implements the java.lang.Runnable.run() method
     */
    public void run()
    {
        VMFinalizer o;

        while (true) {
            // The finalizer thread never quits by itself, when execution stops
            // it is terminated abruptly
            try
            {
                // The next call will block until a new finalizable object is
                // available
                o = VMFinalizer.getNextObject();
                o.finalize();
            }
            catch (Exception e)
            {
                ; // A finalizer threw an exception, we ignore it
            }
        }
    }

    /** Make java.lang.Object.finalize() public so we can call it */
    public void finalize()
    {
        ; /* HACK: this method is empty as it will not be really called */
    }
}
