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

package jelatine.cldc.io;

import java.util.Hashtable;

/**
 * This class stores URL format as described in RFC 2396. This takes the general
 * form:<br>
 * {scheme}:[{target}][{params}]<br>
 * where {scheme} is the name of a protocol such as http.<br>
 * The {target} is normally some kind of network address.<br>
 * Any {params} are formed as a series of equates of the form ";x=y". Example:
 * ";type=a".
 */

public class URL {

    String name; /**< The URL name */
    String scheme; /**< The URL scheme */
    String target; /**< The URL target */
    String params; /**< The URL parameters */

    /** Default constructor for an URL
     * @param name A string holding the URL */

    public URL(String name) throws IllegalArgumentException {
        this.name = name;

        int shemeEndIndex = name.indexOf(":");

        if (shemeEndIndex == -1) {
            throw new IllegalArgumentException("Invalid URL");
        }

        scheme = name.substring(0, shemeEndIndex);
        int paramsStartIndex = name.indexOf(";");

        if (paramsStartIndex == -1) {
            target = name.substring(shemeEndIndex + 1);
        } else {
            target = name.substring(shemeEndIndex + 1, paramsStartIndex);
            params = name.substring(paramsStartIndex);
        }
    }

    /** Parses the parameters held by \a params and returns them in an hash
     * table
     * @param params a string holding the URL parameters
     * @returns The parameters parsed in an hash-table */

    public Hashtable getParamsMap(String params) {
        Hashtable map = new Hashtable();

        int paramStartIndex = -1;

        while ((paramStartIndex = params.indexOf(';', paramStartIndex + 1))
               != -1)
        {
            int paramEndIndex = params.indexOf(';', paramStartIndex + 1);
            String keyValueString;

            if (paramEndIndex == -1) {
                keyValueString = params.substring(paramStartIndex + 1);
            } else {
                keyValueString = params.substring(paramStartIndex + 1,
                                                  paramEndIndex);
            }

            int equalIndex = keyValueString.indexOf('=');

            if (equalIndex == -1) {
                break;
            }

            String key = keyValueString.substring(0, equalIndex);
            String value = keyValueString.substring(equalIndex + 1);
            map.put(key, value);
        }

        return map;
    }

    /** Return the URL name
     * @returns The URL name as a string */

    public String getName() {
        return name;
    }

    /** Return the URL scheme
     * @returns The URL scheme as a string */

    public String getScheme() {
        return scheme;
    }

    /** Return the URL target
     * @returns The URL target as a string */

    public String getTarget() {
        return target;
    }

    /** Return the URL parameters
     * @returns The URL parameters as a string */

    public String getParams() {
        return params;
    }

}
