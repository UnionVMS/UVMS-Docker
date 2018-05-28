package eu.europa.ec.fisheries.uvms.docker.validation.common;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;

import com.fasterxml.jackson.core.JsonProcessingException;

import eu.europa.ec.fisheries.schema.audit.search.v1.AuditLogListQuery;
import eu.europa.ec.fisheries.schema.audit.search.v1.ListPagination;
import eu.europa.ec.fisheries.schema.audit.source.v1.GetAuditLogListByQueryResponse;
import eu.europa.ec.fisheries.schema.audit.v1.AuditLogType;

public class AuditHelper extends AbstractHelper {
	
	public static List<AuditLogType> getAuditLogs(AuditLogListQuery auditLogListQuery) throws ClientProtocolException, JsonProcessingException, IOException {
		final HttpResponse response = Request.Post(getBaseUrl() + "audit/rest/audit/list")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(auditLogListQuery).getBytes()).execute().returnResponse();

		GetAuditLogListByQueryResponse auditLogList = checkSuccessResponseReturnObject(response, GetAuditLogListByQueryResponse.class);
		return auditLogList.getAuditLog();
	}
	
	public static AuditLogListQuery getBasicAuditLogListQuery() {
		AuditLogListQuery auditLogListQuery = new AuditLogListQuery();
		ListPagination listPagination = new ListPagination();
		listPagination.setPage(BigInteger.valueOf(1));
		listPagination.setListSize(BigInteger.valueOf(500));
		auditLogListQuery.setPagination(listPagination);
		return auditLogListQuery;
	}
}
