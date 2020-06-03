package eu.europa.ec.fisheries.uvms.docker.validation.common;

import eu.europa.ec.fisheries.schema.audit.search.v1.AuditLogListQuery;
import eu.europa.ec.fisheries.schema.audit.search.v1.ListPagination;
import eu.europa.ec.fisheries.schema.audit.source.v1.GetAuditLogListByQueryResponse;
import eu.europa.ec.fisheries.schema.audit.v1.AuditLogType;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.math.BigInteger;
import java.util.List;

public class AuditHelper extends AbstractHelper {

	public static List<AuditLogType> getAuditLogs(AuditLogListQuery auditLogListQuery)  {

		GetAuditLogListByQueryResponse response = getWebTarget()
				.path("audit/rest/audit/list")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.post(Entity.json(auditLogListQuery), GetAuditLogListByQueryResponse.class);

	    return response.getAuditLog();
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
