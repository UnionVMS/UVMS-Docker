package org.hibernate.spatial.dialect.postgis;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.spatial.GeolatteGeometryType;
import org.hibernate.spatial.GeometryType;
import org.hibernate.spatial.JTSGeometryType;

public class Postgis43WrapperDialect extends PostgisDialect {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates an instance
	 */
	public Postgis43WrapperDialect() {
		super();
		registerTypesAndFunctions();
	}

	@Override
	public void contributeTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
		super.contributeTypes(
				typeContributions,
				serviceRegistry
		);

		typeContributions.contributeType( new GeolatteGeometryType( PGGeometryTypeDescriptor.INSTANCE ) );
		typeContributions.contributeType( new JTSGeometryType( PGGeometryTypeDescriptor.INSTANCE ) );
		typeContributions.contributeType( new GeometryType( PGGeometryTypeDescriptor.INSTANCE ) );		
	}

}
