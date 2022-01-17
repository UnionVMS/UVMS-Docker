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
package eu.europa.ec.fisheries.uvms.docker.validation.system;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import org.junit.Test;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;

public class MetricsIT extends AbstractRest {

    private static final String INMARSAT_INCOMING = "inmarsat_incoming";
    private static final String FLUX_INCOMING = "flux_incoming";
    private static final String NAF_INCOMING = "naf_incoming";
    private static final String AIS_INCOMING = "ais_incoming";
    private static final String REST_OUTGOING = "rest_outgoing";

    @Test
    public void inmarsatIncoming() throws Exception {
        try (MessageHelper messageHelper = new MessageHelper()) {
            messageHelper.sendMessage("UVMSInmarsatMessages", "test");
            TimeUnit.SECONDS.sleep(1);
        }
        assertThat(getMetricValue(INMARSAT_INCOMING), is(notNullValue()));
    }

    @Test
    public void fluxIncoming() throws URISyntaxException, IOException, InterruptedException {
        assertThat(getMetricValue(FLUX_INCOMING), is(notNullValue()));
    }

    @Test
    public void nafIncoming() throws URISyntaxException, IOException, InterruptedException {
        assertThat(getMetricValue(NAF_INCOMING), is(notNullValue()));
    }

    @Test
    public void aisIncoming() throws URISyntaxException, IOException, InterruptedException {
        assertThat(getMetricValue(AIS_INCOMING), is(notNullValue()));
    }

    @Test
    public void restOutgoing() throws URISyntaxException, IOException, InterruptedException {
        assertThat(getMetricValue(REST_OUTGOING), is(notNullValue()));
    }

    private String getMetricValue(String metricName) throws URISyntaxException, IOException, InterruptedException {
        Map<String, Map<String, String>> metrics = ClientBuilder.newClient()
            .target("http://" + getHost() + ":29990/metrics")
            .request(MediaType.APPLICATION_JSON)
            .get(new GenericType<Map<String, Map<String, String>>>() {});
        return metrics.get("application")
                .get(metricName);
    }
}
