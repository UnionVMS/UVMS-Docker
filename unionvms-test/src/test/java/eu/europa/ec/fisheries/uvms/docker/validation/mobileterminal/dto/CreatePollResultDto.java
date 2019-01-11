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
package eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto;

import java.util.ArrayList;
import java.util.List;

public class CreatePollResultDto {

	private boolean unsentPoll;
	private List<String> sentPolls = new ArrayList<>();
	private List<String> unsentPolls = new ArrayList<>();
	
	public CreatePollResultDto() {
	}

	public List<String> getSentPolls() {
		return sentPolls;
	}

	public void setSentPolls(List<String> sentPolls) {
		this.sentPolls = sentPolls;
	}

	public List<String> getUnsentPolls() {
		return unsentPolls;
	}

	public void setUnsentPolls(List<String> unsentPolls) {
		this.unsentPolls = unsentPolls;
	}

	public boolean isUnsentPoll() {
		return unsentPoll;
	}

	public void setUnsentPoll(boolean unsentPoll) {
		this.unsentPoll = unsentPoll;
	}	
}
