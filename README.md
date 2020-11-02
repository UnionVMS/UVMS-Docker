See https://focusfish.atlassian.net/wiki/spaces/UVMS/pages/11403266/Docker+Installation for documentation.

Build
=====

Make sure you have built the code using `-Ppublish-sql`.

```shell
mvn clean install -Duvms.docker.version=1.6.0-MARE-SNAPSHOT
```

Execute
=======

You need to specify the following environment variables:

- `ECAS_BASE_URL`: The EU Login configuration `eu.cec.digit.ecas.client.filter.ecasBaseUrl`, read about it in `ecas-config.properties`
- `ECAS_SERVICE_URL`: The EU Login configuration `edu.yale.its.tp.cas.client.filter.serviceUrl`, read about it in `ecas-config.properties`
- `ECAS_CERTIFICATE_ESCAPED`: The EU Login configuration `eu.cec.digit.ecas.client.filter.trustedCertificates`, read about it in `ecas-config.properties`;
  if you have a `.cer` certificate (e.g. obtained using `keytool  -export -v -rfc -alias myServerAlias -file myServerCert.cer -keystore myServerKeystore.jks`)
  you can convert it using the following command:

        sed -n -e '/^-----BEGIN/,/^-----END/{/^-----BEGIN/d;/^-----END/!{s/=/\\=/g;H;x;s/\n//;x};/^-----END/{x;p}}' myServerCert.cer
