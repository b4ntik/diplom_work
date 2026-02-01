package ru.skypro.homework.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Enumeration;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        System.out.println("\n=== НАЧАЛО ЗАПРОСА ===");
        System.out.println("URL: " + request.getRequestURL());
        System.out.println("Method: " + request.getMethod());
        System.out.println("Content-Type: " + request.getContentType());
        System.out.println("Content-Length: " + request.getContentLength());

        // Логируем заголовки
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            System.out.println("Header " + headerName + ": " + request.getHeader(headerName));
        }

        // Логируем параметры
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            System.out.println("Parameter " + paramName + ": " + request.getParameter(paramName));
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        System.out.println("=== КОНЕЦ ЗАПРОСА ===");
        System.out.println("Status: " + response.getStatus());
        System.out.println("Content-Type: " + response.getContentType());
        System.out.println("\n");
    }
}
