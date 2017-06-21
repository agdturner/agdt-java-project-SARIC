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
package uk.ac.leeds.ccg.andyt.projects.saric.data.osm;

import java.net.PasswordAuthentication;

/**
 *
 * @author geoagdt
 */
public class Authenticator extends java.net.Authenticator {
	
	private final PasswordAuthentication pa;
	
	public Authenticator(String username, String password) {
		this.pa = new PasswordAuthentication(
                        username, 
                        password.toCharArray());
	}
	
	public Authenticator(PasswordAuthentication pa) {
		this.pa = pa;
	}

	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return pa;
	}
	
}
