/* Hashtable.java -- a class providing a basic hashtable data structure,
   mapping Object --> Object
   Copyright (C) 1998, 1999, 2000, 2001, 2002, 2004, 2005  Free Software Foundation, Inc.

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

package java.util;

// NOTE: This implementation is very similar to that of HashMap. If you fix
// a bug in here, chances are you should make a similar change to the HashMap
// code.

/**
 * A class which implements a hashtable data structure.
 * <p>
 *
 * This implementation of Hashtable uses a hash-bucket approach. That is:
 * linear probing and rehashing is avoided; instead, each hashed value maps
 * to a simple linked-list which, in the best case, only has one node.
 * Assuming a large enough table, low enough load factor, and / or well
 * implemented hashCode() methods, Hashtable should provide O(1)
 * insertion, deletion, and searching of keys.  Hashtable is O(n) in
 * the worst case for all of these (if all keys hash to the same bucket).
 * <p>
 *
 * This is a JDK-1.2 compliant implementation of Hashtable.  As such, it
 * belongs, partially, to the Collections framework (in that it implements
 * Map).  For backwards compatibility, it inherits from the obsolete and
 * utterly useless Dictionary class.
 * <p>
 *
 * Being a hybrid of old and new, Hashtable has methods which provide redundant
 * capability, but with subtle and even crucial differences.
 * For example, one can iterate over various aspects of a Hashtable with
 * either an Iterator (which is the JDK-1.2 way of doing things) or with an
 * Enumeration.  The latter can end up in an undefined state if the Hashtable
 * changes while the Enumeration is open.
 * <p>
 *
 * Unlike HashMap, Hashtable does not accept `null' as a key value. Also,
 * all accesses are synchronized: in a single thread environment, this is
 * expensive, but in a multi-thread environment, this saves you the effort
 * of extra synchronization. However, the old-style enumerators are not
 * synchronized, because they can lead to unspecified behavior even if
 * they were synchronized. You have been warned.
 * <p>
 *
 * The iterators are <i>fail-fast</i>, meaning that any structural
 * modification, except for <code>remove()</code> called on the iterator
 * itself, cause the iterator to throw a
 * <code>ConcurrentModificationException</code> rather than exhibit
 * non-deterministic behavior.
 *
 * @author Jon Zeppieri
 * @author Warren Levy
 * @author Bryce McKinlay
 * @author Eric Blake (ebb9@email.byu.edu)
 */
public class Hashtable
{
    // WARNING: Hashtable is a CORE class in the bootstrap cycle. See the
    // comments in vm/reference/java/lang/Runtime for implications of this fact.

    /** Default number of buckets. This is the value the JDK 1.3 uses. Some
     * early documentation specified this value as 101. That is incorrect.
     */
    private static final int DEFAULT_CAPACITY = 11;

    // Package visible for use by nested classes.
    /** Enumerator for KEYS */
    static final int KEYS = 0;
    /** Enumerator for VALUES */
    static final int VALUES = 1;
    /** Enumerator for ENTRIES */
    static final int ENTRIES = 2;

    /**
     * The rounded product of the capacity and the load factor; when the number
     * of elements exceeds the threshold, the Hashtable calls
     * <code>rehash()</code>.
     */
    private int threshold;

    /**
     * Load factor of this Hashtable:  used in computing the threshold.
     */
    private static final float loadFactor = 0.75f;

    /**
     * Array containing the actual key-value mappings.
     */
    // Package visible for use by nested classes.
    HashEntry[] buckets;

    /**
     * The size of this Hashtable:  denotes the number of key-value pairs.
     */
    // Package visible for use by nested classes.
    int size;

    /**
     * Class to represent an entry in the hash table. Holds a single key-value
     * pair. A Hashtable Entry is identical to a HashMap Entry, except that
     * `null' is not allowed for keys and values.
     */
    private static final class HashEntry
    {
        Object key;
        Object value;
    
        /** The next entry in the linked list. */
        HashEntry next;

        /**
         * Simple constructor.
         * @param key the key, already guaranteed non-null
         * @param value the value, already guaranteed non-null
         */
        HashEntry(Object key, Object value)
        {
            this.key = key;
            this.value = value;
        }

        /**
         * Resets the value.
         * @param newVal the new value
         * @return the prior value
         * @throws NullPointerException if <code>newVal</code> is null
         */
        public Object setValue(Object newVal)
        {
            Object prev_value;
        
            if (newVal == null)
                throw new NullPointerException();
                
            prev_value = value;
            value = newVal;
            
            return prev_value;
        }
    }
    
    /**
     * Enumeration view of this Hashtable, providing sequential access to its
     * elements; this implementation is parameterized to provide access either
     * to the keys or to the values in the Hashtable.
     *
     * <b>NOTE</b>: Enumeration is not safe if new elements are put in the table
     * as this could cause a rehash and we'd completely lose our place.  Even
     * without a rehash, it is undetermined if a new element added would
     * appear in the enumeration.  The spec says nothing about this, but
     * the "Java Class Libraries" book infers that modifications to the
     * hashtable during enumeration causes indeterminate results.  Don't do it!
     *
     * @author Jon Zeppieri
     */
    private final class Enumerator implements Enumeration
    {
        /**
         * The type of this Iterator: {@link #KEYS} or {@link #VALUES}.
         */
        final int type;
        /** The number of elements remaining to be returned by next(). */
        int count = size;
        /** Current index in the physical hash table. */
        int idx = buckets.length;
        /**
         * Entry which will be returned by the next nextElement() call. It is
         * set if we are iterating through a bucket with multiple entries, or null
         * if we must look in the next bucket.
         */
        HashEntry next;

        /**
         * Construct the enumeration.
         * @param type either {@link #KEYS} or {@link #VALUES}.
         */
        Enumerator(int type)
        {
            this.type = type;
        }

        /**
         * Checks whether more elements remain in the enumeration.
         * @return true if nextElement() will not fail.
         */
        public boolean hasMoreElements()
        {
            return count > 0;
        }

        /**
         * Returns the next element.
         * @return the next element
         * @throws NoSuchElementException if there is none.
         */
        public Object nextElement()
        {
            if (count == 0)
                throw new NoSuchElementException("Hashtable Enumerator");
      
            count--;
            HashEntry e = next;

            while (e == null)
                e = buckets[--idx];

            next = e.next;
            return type == VALUES ? e.value : e.key;
         }
    } // class Enumerator

    /**
     * Construct a new Hashtable with the default capacity (11) and the default
     * load factor (0.75).
     */
    public Hashtable()
    {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Construct a new Hashtable with a specific inital capacity and
     * default load factor of 0.75.
     *
     * @param initialCapacity the initial capacity of this Hashtable (&gt;= 0)
     * @throws IllegalArgumentException if (initialCapacity &lt; 0)
     */
    public Hashtable(int initialCapacity)
    {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity: "
                + initialCapacity);

        if (initialCapacity == 0)
            initialCapacity = 1;
            
        buckets = new HashEntry[initialCapacity];
        threshold = (int) (initialCapacity * loadFactor);
        size = 0;
    }

    /**
     * Returns the number of key-value mappings currently in this hashtable.
     * @return the size
     */
    public synchronized int size()
    {
        return size;
    }
    
    /**
     * Returns true if there are no key-value mappings currently in this table.
     * @return <code>size() == 0</code>
     */
    public synchronized boolean isEmpty()
    {
        return size == 0;
    }

    /**
     * Return an enumeration of the keys of this table. There's no point
     * in synchronizing this, as you have already been warned that the
     * enumeration is not specified to be thread-safe.
     *
     * @return the keys
     */
    public Enumeration keys()
    {
        return new Enumerator(KEYS);
    }

    /**
     * Return an enumeration of the values of this table. There's no point
     * in synchronizing this, as you have already been warned that the
     * enumeration is not specified to be thread-safe.
     *
     * @return the values
     */
    public Enumeration elements()
    {
        return new Enumerator(VALUES);
    }

    /**
     * Returns true if this Hashtable contains a value <code>o</code>,
     * such that <code>o.equals(value)</code>.  This is the same as
     * <code>containsValue()</code>, and is O(n).
     * <p>
     *
     * @param value the value to search for in this Hashtable
     * @return true if at least one key maps to the value
     * @throws NullPointerException if <code>value</code> is null
     */
    public synchronized boolean contains(Object value)
    {
        for (int i = buckets.length - 1; i >= 0; i--)
        {
            HashEntry e = buckets[i];
      
            while (e != null)
            {
                if (value.equals(e.value))
                    return true;
      
                e = e.next;
            }
        }

        // Must throw on null argument even if the table is empty
        if (value == null)
            throw new NullPointerException();
 
        return false;  
    }

    /**
     * Returns true if the supplied object <code>equals()</code> a key
     * in this Hashtable.
     *
     * @param key the key to search for in this Hashtable
     * @return true if the key is in the table
     * @throws NullPointerException if key is null
     */
    public synchronized boolean containsKey(Object key)
    {
        int idx = hash(key);
        HashEntry e = buckets[idx];
  
        while (e != null)
        {
            if (key.equals(e.key))
                return true;
            e = e.next;
         }
       
         return false;
    }

    /**
     * Return the value in this Hashtable associated with the supplied key,
     * or <code>null</code> if the key maps to nothing.
     *
     * @param key the key for which to fetch an associated value
     * @return what the key maps to, if present
     * @throws NullPointerException if key is null
     * @see #put(Object, Object)
     * @see #containsKey(Object)
     */
    public synchronized Object get(Object key)
    {
        int idx = hash(key);
        HashEntry e = buckets[idx];
  
        while (e != null)
        {
            if (key.equals(e.key))
                return e.value;
  
            e = e.next;
        }
      
        return null;
    }

    /**
     * Increases the size of the Hashtable and rehashes all keys to new array
     * indices; this is called when the addition of a new value would cause
     * size() &gt; threshold. Note that the existing Entry objects are reused in
     * the new hash table.
     * <p>
     *
     * This is not specified, but the new size is twice the current size plus
     * one; this number is not always prime, unfortunately. This implementation
     * is not synchronized, as it is only invoked from synchronized methods.
     */
    protected void rehash()
    {
        HashEntry[] oldBuckets = buckets;

        int newcapacity = (buckets.length * 2) + 1;
        threshold = (int) (newcapacity * loadFactor);
        buckets = new HashEntry[newcapacity];

        for (int i = oldBuckets.length - 1; i >= 0; i--)
        {
            HashEntry e = oldBuckets[i];

            while (e != null)
            {
                int idx = hash(e.key);
                HashEntry dest = buckets[idx];

                if (dest != null)
                {
                    while (dest.next != null)
                        dest = dest.next;
    
                    dest.next = e;
                }
                else
                {
                    buckets[idx] = e;
                }

                HashEntry next = e.next;
                e.next = null;
                e = next;
            }
        }
    }

    /**
     * Puts the supplied value into the Map, mapped by the supplied key.
     * Neither parameter may be null.  The value may be retrieved by any
     * object which <code>equals()</code> this key.
     *
     * @param key the key used to locate the value
     * @param value the value to be stored in the table
     * @return the prior mapping of the key, or null if there was none
     * @throws NullPointerException if key or value is null
     * @see #get(Object)
     * @see Object#equals(Object)
     */
    public synchronized Object put(Object key, Object value)
    {
        int idx = hash(key);
        HashEntry e = buckets[idx];

        // Check if value is null since it is not permitted.
        if (value == null)
            throw new NullPointerException();

        while (e != null)
        {
            if (key.equals(e.key))
            {
                // Bypass e.setValue, since we already know value is non-null.
                Object r = e.value;
                e.value = value;
                return r;
            }
            else
            {
                e = e.next;
            }
        }

        if (++size > threshold)
        {
            rehash();
            // Need a new hash value to suit the bigger table.
            idx = hash(key);
        }

        e = new HashEntry(key, value);

        e.next = buckets[idx];
        buckets[idx] = e;

        return null;
    }

    /**
     * Removes from the table and returns the value which is mapped by the
     * supplied key. If the key maps to nothing, then the table remains
     * unchanged, and <code>null</code> is returned.
     *
     * @param key the key used to locate the value to remove
     * @return whatever the key mapped to, if present
     */
    public synchronized Object remove(Object key)
    {
       int idx = hash(key);
        HashEntry e = buckets[idx];
        HashEntry last = null;

        while (e != null)
        {
            if (key.equals(e.key))
            {
                if (last == null)
                    buckets[idx] = e.next;
                else
                    last.next = e.next;

                size--;

                return e.value;
            }
    
            last = e;
            e = e.next;
        }
    
        return null;
    }

    /**
     * Clears the hashtable so it has no keys.  This is O(1).
     */
    public synchronized void clear()
    {
        if (size > 0)
        {
            for (int i = 0; i < buckets.length; i++)
                buckets[i] = null;

            size = 0;
        }
    }

    /**
     * Converts this Hashtable to a String, surrounded by braces, and with
     * key/value pairs listed with an equals sign between, separated by a
     * comma and space. For example, <code>"{a=1, b=2}"</code>.<p>
     *
     * NOTE: if the <code>toString()</code> method of any key or value
     * throws an exception, this will fail for the same reason.
     *
     * @return the string representation
     */
    public synchronized String toString()
    {
        // Since we are already synchronized, and entrySet().iterator()
        // would repeatedly re-lock/release the monitor, we directly use the
        // unsynchronized HashIterator instead.
        Enumeration entries = new Enumerator(ENTRIES);
        StringBuffer r = new StringBuffer("{");

        for (int pos = size; pos > 0; pos--)
        {
            r.append(entries.nextElement());

            if (pos > 1)
                r.append(", ");
        }

        r.append("}");
        return r.toString();
    }

    /**
     * Helper method that returns an index in the buckets array for `key'
     * based on its hashCode().
     *
     * @param key the key
     * @return the bucket number
     * @throws NullPointerException if key is null
     */
    private int hash(Object key)
    {
        // Note: Inline Math.abs here, for less method overhead, and to avoid
        // a bootstrap dependency, since Math relies on native methods.
        int hash = key.hashCode() % buckets.length;
        return hash < 0 ? -hash : hash;
    }
} // class Hashtable
