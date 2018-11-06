/*

Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
� European Union, 2017.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.uvms.docker.validation.user;

import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;

/**
 * For Json so it will be easier to get a descent json via ObjectMapper
 */
public class UserPwd extends AbstractRest {

	public String userName;
	public String password;

	public UserPwd(String userName, String password) {
		super();
		this.userName = userName;
		this.password = password;
	}

	public UserPwd() {
	}

}
