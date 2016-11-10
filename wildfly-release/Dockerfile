FROM uvms/wildfly:8.2.0

WORKDIR $DEPLOYMENTS

# DB Module
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.config&a=config-dbaccess-module&v=RELEASE&r=public&e=ear"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.asset&a=asset-dbaccess-module&v=RELEASE&r=public&e=ear"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.audit&a=audit-dbaccess-module&v=RELEASE&r=public&e=ear"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.exchange&a=exchange-dbaccess-module&v=RELEASE&r=public&e=ear"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.mobileterminal&a=mobileterminal-dbaccess-module&v=RELEASE&r=public&e=ear"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.movement&a=movement-dbaccess-module&v=RELEASE&r=public&e=ear&c=postgres"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.rules&a=rules-dbaccess-module&v=RELEASE&r=public&e=ear"

# APP Module
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.config&a=config-module&v=RELEASE&r=public&e=ear"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.asset&a=asset-module&v=RELEASE&r=public&e=ear"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.audit&a=audit-module&v=RELEASE&r=public&e=ear"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.exchange&a=exchange-module&v=RELEASE&r=public&e=ear"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.mobileterminal&a=mobileterminal-module&v=RELEASE&r=public&e=ear"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.movement&a=movement-module&v=RELEASE&r=public&e=ear"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.rules&a=rules-module&v=RELEASE&r=public&e=ear"

RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.spatial&a=deploy-spatial-postgres&v=RELEASE&r=public&e=ear"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.reporting&a=deploy&v=RELEASE&r=public&e=ear"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.user&a=user-module&v=RELEASE&r=public&e=ear"

# Proxies
RUN curl -O -J "$NEXUS?g=se.havochvatten.vessel.proxy.cache&a=hav-vessel-proxy-cache-module&v=RELEASE&r=public&e=ear"
#RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.proxy.mobileterminal&a=national-proxy&v=RELEASE&r=public&e=ear"

# Plugins
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.plugins.inmarsat&a=twostage-module&v=RELEASE&r=public&e=ear"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.plugins&a=naf-module&v=RELEASE&r=public&e=ear"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.plugins&a=flux-module&v=RELEASE&r=public&e=ear"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.plugins&a=sweagencyemail-module&v=RELEASE&r=public&e=ear"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.plugins.iridium&a=siriusone-module&v=RELEASE&r=public&e=ear"

# Frontend
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms&a=unionvms-web&v=RELEASE&r=public&e=war"

RUN ls -l $DEPLOYMENTS