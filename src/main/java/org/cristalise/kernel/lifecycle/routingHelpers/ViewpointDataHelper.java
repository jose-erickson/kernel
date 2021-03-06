/**
 * This file is part of the CRISTAL-iSE kernel.
 * Copyright (c) 2001-2015 The CRISTAL Consortium. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 3 of the License, or (at
 * your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; with out even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 *
 * http://www.fsf.org/licensing/licenses/lgpl.html
 */
package org.cristalise.kernel.lifecycle.routingHelpers;

import javax.xml.xpath.XPathExpressionException;

import org.cristalise.kernel.common.InvalidDataException;
import org.cristalise.kernel.common.ObjectNotFoundException;
import org.cristalise.kernel.common.PersistencyException;
import org.cristalise.kernel.lookup.ItemPath;
import org.cristalise.kernel.persistency.ClusterStorage;
import org.cristalise.kernel.persistency.outcome.Outcome;
import org.cristalise.kernel.persistency.outcome.Viewpoint;
import org.cristalise.kernel.process.Gateway;
import org.cristalise.kernel.utils.Logger;


/**
 * Implements the  DataHelper to get Outcome data using Viewpoint and XPath.
 */
public class ViewpointDataHelper implements DataHelper {

    /**
     * dataPath syntax is used for search : viewpoint:/xpath/to/field
     */
    @Override
    public String get(ItemPath itemPath, String actContext, String dataPath, Object locker)
            throws InvalidDataException, PersistencyException, ObjectNotFoundException
    {
        String[] paths = dataPath.split(":");

        if (paths.length != 2) throw new InvalidDataException("Invalid path: '"+dataPath+"' should have only one colon (:)");

        String viewpoint = paths[0];
        String xpath = paths[1];

        // Leading dot now no longer necessary - remove if present
        if (viewpoint.startsWith("./")) {
            Logger.warning("Removing leading dot on viewpoint data helper path at "+actContext+" in "+
                            itemPath.getUUID().toString() + ". Definition should be migrated.");
            viewpoint = viewpoint.substring(2);
        }

        // load Viewpoint and Outcome
        Viewpoint view  = (Viewpoint) Gateway.getStorage().get(itemPath, ClusterStorage.VIEWPOINT+"/"+viewpoint, locker);
        Outcome outcome = (Outcome)   view.getOutcome(locker);

        // apply the XPath to its outcome
       	try {
       	    return outcome.getFieldByXPath(xpath);
       	}
       	catch (XPathExpressionException e) {
       	    throw new InvalidDataException("Invalid XPath: "+xpath);
       	}
    }
}
