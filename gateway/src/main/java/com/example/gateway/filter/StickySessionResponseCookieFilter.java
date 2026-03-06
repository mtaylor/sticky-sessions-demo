package com.example.gateway.filter;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class StickySessionResponseCookieFilter implements GlobalFilter, Ordered {

    private static final String COOKIE_NAME = "sc-lb-instance-id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        exchange.getResponse().beforeCommit(() -> {
            String instanceId = getInstanceId(exchange);
            if (instanceId != null) {
                exchange.getResponse().addCookie(
                        ResponseCookie.from(COOKIE_NAME, instanceId).path("/").maxAge(172800).build());
            }
            return Mono.empty();
        });
        return chain.filter(exchange);
    }

    private String getInstanceId(ServerWebExchange exchange) {
        Response<ServiceInstance> r = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_LOADBALANCER_RESPONSE_ATTR);
        if (r != null && r.hasServer()) {
            String id = r.getServer().getInstanceId();
            if (id != null && !id.isBlank()) return id;
        }
        URI uri = (URI) exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        return uri != null && uri.getHost() != null ? uri.getHost() + ":" + uri.getPort() : null;
    }

    @Override
    public int getOrder() {
        return 10150;
    }
}
