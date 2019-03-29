/*
 * Copyright (C) 2017 geoagdt.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package uk.ac.leeds.ccg.andyt.projects.saric.core;

import java.io.Serializable;
import uk.ac.leeds.ccg.andyt.projects.saric.io.SARIC_Files;

/**
 *
 * @author geoagdt
 */
public abstract class SARIC_Object implements Serializable {

    public transient SARIC_Environment se;

    // For convenience
    public transient SARIC_Files Files;
    public transient int logID;

    /**
     * {@link #logID} defaulted to 0.
     * @param e 
     */
    public SARIC_Object(SARIC_Environment e) {
        this(e, 0);
    }

    /**
     * 
     * @param e
     * @param i The logID.
     */
    public SARIC_Object(SARIC_Environment e, int i) {
        this.se = e;
        Files = e.Files;
        this.logID = i;
    }
}
