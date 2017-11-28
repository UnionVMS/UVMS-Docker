package org.hibernate.spatial;

import org.hibernate.spatial.dialect.postgis.PGGeometryTypeDescriptor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;

import com.vividsolutions.jts.geom.Geometry;

public class GeometryType extends AbstractSingleColumnStandardBasicType<Geometry> implements Spatial {

	public static final GeometryType INSTANCE = new GeometryType();

	public GeometryType() {
		super( PGGeometryTypeDescriptor.INSTANCE, GeometryJavaTypeDescriptor.INSTANCE );
	}
	

	@Override
	public String[] getRegistrationKeys() {
		return new String[] {
				com.vividsolutions.jts.geom.Geometry.class.getCanonicalName(),
				com.vividsolutions.jts.geom.Point.class.getCanonicalName(),
				com.vividsolutions.jts.geom.Polygon.class.getCanonicalName(),
				com.vividsolutions.jts.geom.MultiPolygon.class.getCanonicalName(),
				com.vividsolutions.jts.geom.LineString.class.getCanonicalName(),
				com.vividsolutions.jts.geom.MultiLineString.class.getCanonicalName(),
				com.vividsolutions.jts.geom.MultiPoint.class.getCanonicalName(),
				com.vividsolutions.jts.geom.GeometryCollection.class.getCanonicalName(),
				"geometry"
		};
	}


	@Override
	public String getName() {
		return "geometry";
	}

}
