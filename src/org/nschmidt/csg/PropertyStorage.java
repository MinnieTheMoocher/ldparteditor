/**
 * PropertyStorage.java
 *
 * Copyright 2014-2014 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY Michael Hoffer <info@michaelhoffer.de> "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Michael Hoffer <info@michaelhoffer.de> OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of Michael Hoffer <info@michaelhoffer.de>.
 */

package org.nschmidt.csg;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple property storage.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class PropertyStorage {

    private final Map<String, Object> map = new HashMap<String, Object>();

    /**
     * Constructor. Creates a new property storage.
     */
    public PropertyStorage() {
    }

    public PropertyStorage(PropertyStorage clone) {
        map.putAll(new HashMap<String, Object>(clone.map));
    }

    /**
     * Sets a property. Existing properties are overwritten.
     *
     * @param key
     *            key
     * @param property
     *            property
     */
    public void set(String key, Object property) {
        map.put(key, property);
    }

    /**
     * Returns a property.
     *
     * @param <T>
     *            property type
     * @param key
     *            key
     * @return
     * @return the property; an empty {@link java.util.Optional} will be
     *         returned if the property does not exist or the type does not
     *         match
     */
    public <T> Object getValue(String key) {
        return map.get(key);
    }

    public <T> Object getFirstValue() {
        if (!map.keySet().isEmpty()) {
            return map.get(map.keySet().iterator().next());
        }
        return null;
    }

    /**
     * Deletes the requested property if present. Does nothing otherwise.
     *
     * @param key
     *            key
     */
    public void delete(String key) {
        map.remove(key);
    }

    /**
     * Indicates whether this storage contains the requested property.
     *
     * @param key
     *            key
     * @return {@code true} if this storage contains the requested property;
     *         {@code false}
     */
    public boolean contains(String key) {
        return map.containsKey(key);
    }
}