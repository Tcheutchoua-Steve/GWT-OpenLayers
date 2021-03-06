/**
 *
 *   Copyright 2015 sourceforge.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.gwtopenmaps.openlayers.client.util;


/**
 * Base class for Objects that wrap an opaque javascript object (represented by a JSObject),
 * to provide (part of) the javascript object's functionality in GWT.
 *
 * @author Edwin Commandeur - Atlis EJS
 */
public class JSObjectWrapper
{

    private JSObject jsObject;

    protected JSObjectWrapper(JSObject jsObject)
    {
        setJSObject(jsObject);
    }

    public JSObject getJSObject()
    {
        return jsObject;
    }

    public void setJSObject(JSObject jsObject)
    {
        this.jsObject = jsObject;
    }

}
