package eu.europa.ec.fisheries.uvms.docker.validation.common;

import eu.europa.ec.fisheries.schema.audit.search.v1.AuditLogListQuery;
import eu.europa.ec.fisheries.schema.audit.search.v1.ListPagination;
import eu.europa.ec.fisheries.schema.audit.source.v1.GetAuditLogListByQueryResponse;
import eu.europa.ec.fisheries.schema.audit.v1.AuditLogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class AuditHelper extends AbstractHelper {

	private static final Logger log = LoggerFactory.getLogger(AuditHelper.class.getSimpleName());
	
	public static List<AuditLogType> getAuditLogs(AuditLogListQuery auditLogListQuery)  {

		Response response = getWebTarget()
				.path("audit/rest/audit/list")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.post(Entity.json(auditLogListQuery));

		Map objectMap = response.readEntity(Map.class);

		try {
			String valueAsString = writeValueAsString(objectMap.get("data"));
			GetAuditLogListByQueryResponse dataValue =
					OBJECT_MAPPER.readValue(valueAsString, GetAuditLogListByQueryResponse.class);
			return dataValue.getAuditLog();
		} catch (IOException e) {
			log.error("Error occurred while retrieving Audit List", e);
			return null;
		}
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
