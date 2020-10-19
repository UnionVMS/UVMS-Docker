/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.docker.validation.user;

import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.user.dto.EndPoint;
import eu.europa.ec.fisheries.uvms.docker.validation.user.dto.Organisation;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class OrganisationRestIT extends AbstractRest {

    @Test
    public void createOrganisationTest() {
        Organisation organisation = UserHelper.getBasicOrganisation();
        Organisation createdOrganisation = UserHelper.createOrganisation(organisation);
        assertThat(createdOrganisation.getName(), is(organisation.getName()));
    }
    
    @Test
    public void createEndpointTest() {
        Organisation organisation = UserHelper.getBasicOrganisation();
        Organisation createdOrganisation = UserHelper.createOrganisation(organisation);
        assertThat(createdOrganisation.getName(), is(organisation.getName()));
        
        EndPoint endpoint = new EndPoint();
        String endpointName = "Test";
        endpoint.setName(endpointName);
        endpoint.setURI("TestURI");
        endpoint.setStatus("E");
        endpoint.setOrganisationName(organisation.getName());
        EndPoint createdEndpoint = UserHelper.createEndpoint(endpoint);
        
        assertThat(createdEndpoint.getName(), is(endpoint.getName()));
        
        Organisation fetchedOrganisation = UserHelper.getOrganisation(createdOrganisation.getOrganisationId());
        assertThat(fetchedOrganisation.getEndpoints().size(), is(1));
        assertThat(fetchedOrganisation.getEndpoints().get(0).getName(), is(endpointName));
    }
}
