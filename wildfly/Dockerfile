FROM uvms/wildfly:8.2.0

WORKDIR $DEPLOYMENTS

# APP Module
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.config&a=config-module&v=LATEST&r=public&e=ear"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.asset&a=asset-module&v=LATEST&r=public&e=ear"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.audit&a=audit-module&v=LATEST&r=public&e=ear"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.exchange&a=exchange-module&v=LATEST&r=public&e=ear"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.mobileterminal&a=mobileterminal-module&v=LATEST&r=public&e=ear"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.movement&a=movement-module&v=LATEST&r=public&e=ear"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.rules&a=rules-module&v=LATEST&r=public&e=ear"

RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.spatial&a=deploy-spatial-postgres&v=LATEST&r=public&e=ear"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.reporting&a=deploy&v=LATEST&r=public&e=ear"

# Proxies
RUN curl -O -J "$NEXUS?g=se.havochvatten.vessel.proxy.cache&a=hav-vessel-proxy-cache-module&v=LATEST&r=public&e=ear"
#RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.proxy.mobileterminal&a=national-proxy&v=LATEST&r=public&e=ear"

# Plugins
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.plugins.inmarsat&a=twostage-module&v=LATEST&r=public&e=ear"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.plugins&a=naf-module&v=LATEST&r=public&e=ear"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.plugins&a=flux-module&v=LATEST&r=public&e=ear"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.plugins&a=sweagencyemail-module&v=LATEST&r=public&e=ear"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.plugins.iridium&a=siriusone-module&v=LATEST&r=public&e=ear"


WORKDIR $AUTO_DEPLOYMENTS

# DB Module
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.config&a=config-dbaccess-module&v=LATEST&r=public&e=ear"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.asset&a=asset-dbaccess-module&v=LATEST&r=public&e=ear"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.audit&a=audit-dbaccess-module&v=LATEST&r=public&e=ear"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.exchange&a=exchange-dbaccess-module&v=LATEST&r=public&e=ear"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.mobileterminal&a=mobileterminal-dbaccess-module&v=LATEST&r=public&e=ear"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.movement&a=movement-dbaccess-module&v=LATEST&r=public&e=ear&c=postgres"
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.rules&a=rules-dbaccess-module&v=LATEST&r=public&e=ear"

# User module
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms.user&a=user-module&v=LATEST&r=public&e=ear"

# Frontend
RUN curl -O -J "$NEXUS?g=eu.europa.ec.fisheries.uvms&a=unionvms-web&v=LATEST&r=public&e=war"

RUN ls -l $DEPLOYMENTS