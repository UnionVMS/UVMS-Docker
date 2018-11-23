package eu.europa.ec.fisheries.uvms.docker.validation.movement;


/*import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Map;

public class AuthorizationHeaderWebTarget implements WebTarget {

    private WebTarget base;

    private String token;

    public AuthorizationHeaderWebTarget(WebTarget base, String token) {
        this.base = base;
        this.token = token;
    }

    // Inject that cookie whenever someone requests a Builder (like EventSource does):
    public Invocation.Builder request() {
        return base.request().header(HttpHeaders.AUTHORIZATION, token);
    }

    public Invocation.Builder request(String... paramArrayOfString) {
        return base.request(paramArrayOfString).header(HttpHeaders.AUTHORIZATION, token);
    }

    public Invocation.Builder request(MediaType... paramArrayOfMediaType) {
        return base.request(paramArrayOfMediaType).header(HttpHeaders.AUTHORIZATION, token);
    }

    public Configuration getConfiguration() {
        return base.getConfiguration();
    }

    //All other methods from WebTarget are delegated as-is:

    public URI getUri() {
        return base.getUri();
    }

    public UriBuilder getUriBuilder() {
        return base.getUriBuilder();
    }

    public WebTarget path(String paramString) {
        return base.path(paramString);
    }

    public WebTarget matrixParam(String paramString, Object... paramArrayOfObject) {
        return base.matrixParam(paramString, paramArrayOfObject);
    }

    public WebTarget property(String paramString, Object paramObject) {
        return base.property(paramString, paramObject);
    }

    public WebTarget queryParam(String paramString, Object... paramArrayOfObject) {
        return base.queryParam(paramString, paramArrayOfObject);
    }

    public WebTarget register(Class<?> paramClass, Class<?>... paramArrayOfClass) {
        return base.register(paramClass, paramArrayOfClass);
    }

    public WebTarget register(Class<?> paramClass, int paramInt) {
        return base.register(paramClass, paramInt);
    }

    public WebTarget register(Class<?> paramClass, Map<Class<?>, Integer> paramMap) {
        return base.register(paramClass, paramMap);
    }

    public WebTarget register(Class<?> paramClass) {
        return base.register(paramClass);
    }

    public WebTarget register(Object paramObject, Class<?>... paramArrayOfClass) {
        return base.register(paramObject, paramArrayOfClass);
    }

    public WebTarget register(Object paramObject, int paramInt) {
        return base.register(paramObject, paramInt);
    }

    public WebTarget register(Object paramObject, Map<Class<?>, Integer> paramMap) {
        return base.register(paramObject, paramMap);
    }

    public WebTarget register(Object paramObject) {
        return base.register(paramObject);
    }

    public WebTarget resolveTemplate(String paramString, Object paramObject) {
        return base.resolveTemplate(paramString, paramObject);
    }

    public WebTarget resolveTemplate(String paramString, Object paramObject, boolean paramBoolean) {
        return base.resolveTemplate(paramString, paramObject, paramBoolean);
    }

    public WebTarget resolveTemplateFromEncoded(String paramString, Object paramObject) {
        return base.resolveTemplateFromEncoded(paramString, paramObject);
    }

    public WebTarget resolveTemplates(Map<String, Object> paramMap) {
        return base.resolveTemplates(paramMap);
    }

    public WebTarget resolveTemplates(Map<String, Object> paramMap, boolean paramBoolean) {
        return base.resolveTemplates(paramMap, paramBoolean);
    }

    public WebTarget resolveTemplatesFromEncoded(Map<String, Object> paramMap) {
        return base.resolveTemplatesFromEncoded(paramMap);
    }

}*/
import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ClientWebTarget;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.*;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;

public class AuthorizationHeaderWebTarget implements ResteasyWebTarget {

    private ClientWebTarget base;

    private String token;

    public AuthorizationHeaderWebTarget(ClientWebTarget base, String token) {
        this.base = base;
        this.token = token;
    }

    // Inject that header whenever someone requests a Builder (like EventSource does):
    public Invocation.Builder request() {
        return base.request().header(HttpHeaders.AUTHORIZATION, token);
    }

    public Invocation.Builder request(String... paramArrayOfString) {
        return base.request(paramArrayOfString).header(HttpHeaders.AUTHORIZATION, token);
    }

    public Invocation.Builder request(MediaType... paramArrayOfMediaType) {
        return base.request(paramArrayOfMediaType).header(HttpHeaders.AUTHORIZATION, token);
    }

    public Configuration getConfiguration() {
        return base.getConfiguration();
    }

    //All other methods from WebTarget are delegated as-is:

    public URI getUri() {
        return base.getUri();
    }

    public UriBuilder getUriBuilder() {
        return base.getUriBuilder();
    }

    public ResteasyWebTarget path(String paramString) {
        return base.path(paramString);
    }

    public ResteasyWebTarget matrixParam(String paramString, Object... paramArrayOfObject) {
        return base.matrixParam(paramString, paramArrayOfObject);
    }

    public ResteasyWebTarget property(String paramString, Object paramObject) {
        return base.property(paramString, paramObject);
    }

    public ResteasyWebTarget queryParam(String paramString, Object... paramArrayOfObject) {
        return base.queryParam(paramString, paramArrayOfObject);
    }

    public ResteasyWebTarget register(Class<?> paramClass, Class<?>... paramArrayOfClass) {
        return base.register(paramClass, paramArrayOfClass);
    }

    public ResteasyWebTarget register(Class<?> paramClass, int paramInt) {
        return base.register(paramClass, paramInt);
    }

    public ResteasyWebTarget register(Class<?> paramClass, Map<Class<?>, Integer> paramMap) {
        return base.register(paramClass, paramMap);
    }

    public ResteasyWebTarget register(Class<?> paramClass) {
        return base.register(paramClass);
    }

    public ResteasyWebTarget register(Object paramObject, Class<?>... paramArrayOfClass) {
        return base.register(paramObject, paramArrayOfClass);
    }

    public ResteasyWebTarget register(Object paramObject, int paramInt) {
        return base.register(paramObject, paramInt);
    }

    public ResteasyWebTarget register(Object paramObject, Map<Class<?>, Integer> paramMap) {
        return base.register(paramObject, paramMap);
    }

    public ResteasyWebTarget register(Object paramObject) {
        return base.register(paramObject);
    }

    public ResteasyWebTarget resolveTemplate(String paramString, Object paramObject) {
        return base.resolveTemplate(paramString, paramObject);
    }

    public ResteasyWebTarget resolveTemplate(String paramString, Object paramObject, boolean paramBoolean) {
        return base.resolveTemplate(paramString, paramObject, paramBoolean);
    }

    public ResteasyWebTarget resolveTemplateFromEncoded(String paramString, Object paramObject) {
        return base.resolveTemplateFromEncoded(paramString, paramObject);
    }

    public ResteasyWebTarget resolveTemplates(Map<String, Object> paramMap) {
        return base.resolveTemplates(paramMap);
    }

    public ResteasyWebTarget resolveTemplates(Map<String, Object> paramMap, boolean paramBoolean) {
        return base.resolveTemplates(paramMap, paramBoolean);
    }

    public ResteasyWebTarget resolveTemplatesFromEncoded(Map<String, Object> paramMap) {
        return base.resolveTemplatesFromEncoded(paramMap);
    }

    @Override
    public ResteasyClient getResteasyClient() {
        return base.getResteasyClient();
    }

    @Override
    public <T> T proxy(Class<T> proxyInterface) {
        return base.proxy(proxyInterface);
    }

    @Override
    public <T> ProxyBuilder<T> proxyBuilder(Class<T> proxyInterface) {
        return base.proxyBuilder(proxyInterface);
    }

    @Override
    public ResteasyWebTarget queryParams(MultivaluedMap<String, Object> parameters) throws IllegalArgumentException, NullPointerException {
        return base.queryParams(parameters);
    }

    @Override
    public ResteasyWebTarget queryParamNoTemplate(String name, Object... values) throws NullPointerException {
        return base.queryParamNoTemplate(name, values);
    }

    @Override
    public ResteasyWebTarget queryParamsNoTemplate(MultivaluedMap<String, Object> parameters) throws IllegalArgumentException, NullPointerException {
        return base.queryParamsNoTemplate(parameters);
    }

    @Override
    public ResteasyWebTarget path(Class<?> resource) throws IllegalArgumentException {
        return base.path(resource);
    }

    @Override
    public ResteasyWebTarget path(Method method) throws IllegalArgumentException {
        return base.path(method);
    }

    @Override
    public ResteasyWebTarget clone() {
        return base.clone();
    }

    @Override
    public ResteasyWebTarget setChunked(boolean chunked) {
        return base.setChunked(chunked);
    }
}
