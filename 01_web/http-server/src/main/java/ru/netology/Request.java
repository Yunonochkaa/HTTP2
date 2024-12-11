package ru.netology;

import org.apache.http.client.utils.URLEncodedUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


public class Request {
    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final String body;
    private final Map<String, List<String>> queryParams;

    public Request(String method, String path, Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
        this.queryParams = parseQueryParams(path);
    }

    private Map<String, List<String>> parseQueryParams(String path) {
        String[] parts = path.split("\\?");
        if (parts.length < 2) {
            return new HashMap<>();
        }
        return (Map<String, List<String>>) URLEncodedUtils.parse(parts[1], StandardCharsets.UTF_8);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path.split("\\?")[0];
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public String getQueryParam(String name) {
        List<String> values = queryParams.get(name);
        return (values != null && !values.isEmpty()) ? values.get(0) : null;
    }

    public Map<String, List<String>> getQueryParams() {
        return queryParams;
    }
}
