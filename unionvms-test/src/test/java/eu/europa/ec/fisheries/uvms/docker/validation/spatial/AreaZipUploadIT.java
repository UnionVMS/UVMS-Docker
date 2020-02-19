package eu.europa.ec.fisheries.uvms.docker.validation.spatial;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.spatial.dto.upload.AreaUploadMapping;
import eu.europa.ec.fisheries.uvms.docker.validation.spatial.dto.upload.AreaUploadMappingProperty;
import eu.europa.ec.fisheries.uvms.docker.validation.spatial.dto.upload.AreaUploadMetadata;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

public class AreaZipUploadIT extends AbstractRest {

    @Test
    public void uploadNewFaoDefinitionsTest() throws IOException {
        byte[] bytes = getClass().getClassLoader().getResourceAsStream("FAO_AREAS.zip").readAllBytes();

        MultipartFormDataOutput mdo = new MultipartFormDataOutput();
        mdo.addFormData("areaType", "FAO", MediaType.TEXT_PLAIN_TYPE );
        mdo.addFormData("uploadedFile", bytes, MediaType.APPLICATION_OCTET_STREAM_TYPE );

        String stringResponse = getWebTarget()
                .path("spatialSwe/rest")
                .path("files")
                .path("metadata")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.entity(mdo, MediaType.MULTIPART_FORM_DATA_TYPE), String.class);

        ObjectMapper om = new ObjectMapper();
        AreaUploadMetadata response = om.readValue(stringResponse, AreaUploadMetadata.class);       //for some reason the client always uses jsonb despite us saying that it should use jackson, and we use a jackson-specific annotation.

        AreaUploadMapping mapping = new AreaUploadMapping();
        mapping.setAdditionalProperty("ref", response.getAdditionalProperties().get("ref"));

        mapping.getMapping().add(createAreaUploadMappingProperty("name", "NAME_EN"));
        mapping.getMapping().add(createAreaUploadMappingProperty("code", "F_CODE"));
        mapping.getMapping().add(createAreaUploadMappingProperty("ocean", "OCEAN"));
        mapping.getMapping().add(createAreaUploadMappingProperty("subocean", "SUBOCEAN"));
        mapping.getMapping().add(createAreaUploadMappingProperty("fArea", "F_AREA"));
        mapping.getMapping().add(createAreaUploadMappingProperty("fSubarea", "F_SUBAREA"));
        mapping.getMapping().add(createAreaUploadMappingProperty("fDivision", "F_DIVISION"));
        mapping.getMapping().add(createAreaUploadMappingProperty("fSubdivis", "F_SUBDIVIS"));
        mapping.getMapping().add(createAreaUploadMappingProperty("fSubunit", "F_SUBUNIT"));
        mapping.getMapping().add(createAreaUploadMappingProperty("fLabel", "F_CODE"));

        String json = om.writeValueAsString(mapping);

        Response uploadResponse = getWebTarget()
                .path("spatialSwe/rest")
                .path("files")
                .path("upload")
                .path("4326")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(json) , Response.class);

        assertNotNull(uploadResponse);
        assertEquals(200, uploadResponse.getStatus());
    }

    private AreaUploadMappingProperty createAreaUploadMappingProperty(String source, String target){
        AreaUploadMappingProperty property = new AreaUploadMappingProperty(source, target);
        return property;
    }
}
