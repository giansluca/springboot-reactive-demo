package org.gmdev.reactivedemo.config;

import org.springframework.http.server.RequestPath;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.reactive.function.server.support.ServerRequestWrapper;

import java.net.URI;

public class CaseInsensitiveRequestPredicate implements RequestPredicate {

    private final RequestPredicate target;

    public CaseInsensitiveRequestPredicate(RequestPredicate target) {
        this.target = target;
    }

    @Override
    public boolean test(ServerRequest request) {
        return this.target.test(new LowerCaseUriServerRequestWrapper(request));
    }

    @Override
    public String toString() {
        return this.target.toString();
    }

    private static class LowerCaseUriServerRequestWrapper extends ServerRequestWrapper {

        LowerCaseUriServerRequestWrapper(ServerRequest delegate) {
            super(delegate);
        }

        @Override
        public URI uri() {
            return URI.create(super.uri().toString().toLowerCase());
        }

        @Override
        public String path() {
            return uri().getRawPath();
        }

        @Override
        public RequestPath requestPath() {
            return RequestPath.parse(path(), null);
        }
    }


}


