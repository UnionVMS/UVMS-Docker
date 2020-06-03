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
package eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.*;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ListPagination;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.*;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

public class PollRestIT extends AbstractRest {

	@Test
	public void getRunningProgramPollsTest() {
		Response response = getWebTarget()
				.path("asset/rest/poll/running")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.get();

		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

		List<PollDto> pollDtos = response.readEntity(new GenericType<List<PollDto>>() {});
		assertNotNull(pollDtos);
		assertFalse(pollDtos.isEmpty());
	}

	@Test
	public void createPollTest() {
		AssetDTO testAsset = AssetTestHelper.createTestAsset();
		CreatePollResultDto resultDto = MobileTerminalTestHelper.createPoll_Helper(testAsset, PollType.MANUAL_POLL);
		assertNotNull(resultDto);
		assertEquals(1, resultDto.getSentPolls().size());
	}

	@Test
	public void createConfigurationPollTest() {
		AssetDTO dto = AssetTestHelper.createBasicAsset();
		AssetDTO asset = AssetTestHelper.createAsset(dto);
		MobileTerminalDto mt = MobileTerminalTestHelper.createMobileTerminal();
		CreatePollResultDto pollResultDto = MobileTerminalTestHelper.createConfigPollWithMT_Helper(asset, mt);
		assertNotNull(pollResultDto);
		assertEquals(1, pollResultDto.getSentPolls().size());
	}

	@Test
	public void createAndGetConfigurationPollWithCompositeCriteriaTest() {
		AssetDTO dto = AssetTestHelper.createBasicAsset();
		AssetDTO asset = AssetTestHelper.createAsset(dto);
		MobileTerminalDto mt = MobileTerminalTestHelper.createMobileTerminal();
		CreatePollResultDto pollResultDto = MobileTerminalTestHelper.createConfigPollWithMT_Helper(asset, mt);
		assertNotNull(pollResultDto);
		assertEquals(1, pollResultDto.getSentPolls().size());

		PollListQuery pollListQuery = new PollListQuery();
		ListPagination pagination = new ListPagination();
		pagination.setListSize(100);
		pagination.setPage(1);
		pollListQuery.setPagination(pagination);

		PollSearchCriteria pollSearchCriteria = new PollSearchCriteria();
		pollSearchCriteria.setIsDynamic(true);
		ListCriteria listCriteria = new ListCriteria();
		listCriteria.setKey(SearchKey.POLL_TYPE);
		listCriteria.setValue("CONFIGURATION_POLL");
		pollSearchCriteria.getCriterias().add(listCriteria);

		listCriteria = new ListCriteria();
		listCriteria.setKey(SearchKey.CONNECT_ID);
		listCriteria.setValue(asset.getId().toString());
		pollSearchCriteria.getCriterias().add(listCriteria);

		listCriteria = new ListCriteria();
		listCriteria.setKey(SearchKey.POLL_ID);
		listCriteria.setValue(pollResultDto.getSentPolls().get(0));
		pollSearchCriteria.getCriterias().add(listCriteria);

		pollListQuery.setPollSearchCriteria(pollSearchCriteria);

		PollChannelListDto response = getWebTarget()
				.path("asset/rest/poll/list")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.post(Entity.json(pollListQuery), PollChannelListDto.class);

		assertNotNull(response.getPollableChannels());
	}

	@Test
	public void startProgramPollTest() {
		AssetDTO testAsset = AssetTestHelper.createTestAsset();
		CreatePollResultDto resultDto = MobileTerminalTestHelper.createPoll_Helper(testAsset, PollType.PROGRAM_POLL);
		List<String> unsentPolls = resultDto.getUnsentPolls();
		String uid = unsentPolls.get(0);

		Response response = getWebTarget()
				.path("asset/rest/poll")
				.path(uid)
                .path("start")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.put(Entity.json(""));

		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		PollDto pollDto = response.readEntity(PollDto.class);
		assertNotNull(pollDto);
		assertEquals(10, pollDto.getValues().size());
	}

	@Test
	public void stopProgramPollTest() {
		AssetDTO testAsset = AssetTestHelper.createTestAsset();
		CreatePollResultDto resultDto = MobileTerminalTestHelper.createPoll_Helper(testAsset, PollType.PROGRAM_POLL);
		List<String> unsentPolls = resultDto.getUnsentPolls();
		String uid = unsentPolls.get(0);

		Response started = getWebTarget()
				.path("asset/rest/poll")
				.path(uid)
                .path("start")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.put(Entity.json(""));

		assertEquals(Response.Status.OK.getStatusCode(), started.getStatus());
		PollDto startedPollDto = started.readEntity(PollDto.class);
		assertNotNull(startedPollDto);
		assertEquals(10, startedPollDto.getValues().size());

		Response stopped = getWebTarget()
				.path("asset/rest/poll")
				.path(uid)
                .path("stop")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.put(Entity.json(""));

		assertEquals(Response.Status.OK.getStatusCode(), stopped.getStatus());
		PollDto stoppedPollDto = stopped.readEntity(PollDto.class);
		assertNotNull(stoppedPollDto);
		assertEquals(10, stoppedPollDto.getValues().size());

		List<PollValue> pollValues = stoppedPollDto.getValues();

		Optional<String> pollKeyOptional = pollValues
				.stream()
				.filter(pollValue -> pollValue.getKey().equals(PollKey.PROGRAM_RUNNING))
				.map(PollValue::getValue)
				.findFirst();

		pollKeyOptional.ifPresent(value -> assertTrue(value.equalsIgnoreCase("false")));
	}

	@Test
	public void inactivateProgramPollTest() {
		AssetDTO testAsset = AssetTestHelper.createTestAsset();
		CreatePollResultDto resultDto = MobileTerminalTestHelper.createPoll_Helper(testAsset, PollType.PROGRAM_POLL);
		List<String> unsentPolls = resultDto.getUnsentPolls();
		String uid = unsentPolls.get(0);

		Response started = getWebTarget()
				.path("asset/rest/poll")
				.path(uid)
                .path("start")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.put(Entity.json(""));

		assertEquals(Response.Status.OK.getStatusCode(), started.getStatus());
		PollDto startedPollDto = started.readEntity(PollDto.class);
		assertNotNull(startedPollDto);
		assertEquals(10, startedPollDto.getValues().size());

		Response inactivated = getWebTarget()
				.path("asset/rest/poll")
				.path(uid)
                .path("archive")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.put(Entity.json(""));

		assertEquals(Response.Status.OK.getStatusCode(), inactivated.getStatus());
		PollDto inactivatedPollDto = inactivated.readEntity(PollDto.class);
		assertNotNull(inactivatedPollDto);
		assertEquals(10, inactivatedPollDto.getValues().size());

		List<PollValue> pollValues = inactivatedPollDto.getValues();

		Optional<String> pollKeyOptional = pollValues
				.stream()
				.filter(pollValue -> pollValue.getKey().equals(PollKey.PROGRAM_RUNNING))
				.map(PollValue::getValue)
				.findFirst();

		pollKeyOptional.ifPresent(value -> assertTrue(value.equalsIgnoreCase("false")));
	}

	@Test
	public void getPollBySearchCriteriaTest() {
		PollListQuery pollListQuery = new PollListQuery();
		ListPagination pagination = new ListPagination();
		pollListQuery.setPagination(pagination);
		pagination.setListSize(100);
		pagination.setPage(1);

		PollSearchCriteria pollSearchCriteria = new PollSearchCriteria();
		pollListQuery.setPollSearchCriteria(pollSearchCriteria);
		pollSearchCriteria.setIsDynamic(true);

		Response response = getWebTarget()
				.path("asset/rest/poll/list")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.post(Entity.json(pollListQuery));

		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	}

	@Test
	public void getPollableChannelsTest() {
		PollableQuery pollableQuery = new PollableQuery();
		ListPagination listPagination = new ListPagination();
		listPagination.setListSize(100);
		listPagination.setPage(1);
		pollableQuery.setPagination(listPagination);
		pollableQuery.getConnectIdList().add("00000000-0000-0000-0000-000000000001");

		Response response = getWebTarget()
				.path("asset/rest/poll/getPollable")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.post(Entity.json(pollableQuery));

		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	}
}
