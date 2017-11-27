package org.hibernate.spatial;

import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

public class GeometryType extends JTSGeometryType {

	private static final long serialVersionUID = 1L;

	public GeometryType(SqlTypeDescriptor sqlTypeDescriptor) {
		super(sqlTypeDescriptor);
	}

}
