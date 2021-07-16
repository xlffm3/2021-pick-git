package com.woowacourse.pickgit.authentication.presentation.interceptor;

import com.woowacourse.pickgit.authentication.application.OAuthService;
import com.woowacourse.pickgit.authentication.infrastructure.AuthorizationExtractor;
import com.woowacourse.pickgit.exception.authentication.InvalidTokenException;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthenticationInterceptor implements HandlerInterceptor {

    private final OAuthService oAuthService;

    public AuthenticationInterceptor(
        OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @Override
    public boolean preHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler) throws Exception {
        if (isPreflightRequest(request)) {
            return true;
        }
        String authentication = AuthorizationExtractor.extract(request);
        if (isGuestRequestForReadingHomeFeed(request, authentication)) {
            return true;
        }
        if (!oAuthService.validateToken(authentication)) {
            throw new InvalidTokenException();
        }
        request.setAttribute("authentication", authentication);
        return true;
    }

    private boolean isGuestRequestForReadingHomeFeed(HttpServletRequest request, String authentication) {
        return "/api/posts".equals(request.getRequestURI())
            && request.getMethod().equalsIgnoreCase(HttpMethod.GET.toString())
            && Objects.isNull(authentication);
    }

    private boolean isPreflightRequest(HttpServletRequest request) {
        return isOptions(request)
            && hasAccessControlRequestHeaders(request)
            && hasAccessControlRequestMethod(request)
            && hasOrigin(request);
    }

    private boolean isOptions(HttpServletRequest request) {
        return request.getMethod().equalsIgnoreCase(HttpMethod.OPTIONS.toString());
    }

    private boolean hasAccessControlRequestHeaders(HttpServletRequest request) {
        return Objects.nonNull(request.getHeader("Access-Control-Request-Headers"));
    }

    private boolean hasAccessControlRequestMethod(HttpServletRequest request) {
        return Objects.nonNull(request.getHeader("Access-Control-Request-Method"));
    }

    private boolean hasOrigin(HttpServletRequest request) {
        return Objects.nonNull(request.getHeader("Origin"));
    }
}
