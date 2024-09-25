package com.example.jenkinsspring.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CorsFilter implements Filter {

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;

    // Разрешаем все домены (*), можно ограничить конкретными доменами
    response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");  // Замените на нужный домен или оставьте "*", если не требуется куки
    response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
    response.setHeader("Access-Control-Max-Age", "3600"); // Кэширование preflight-запросов
    response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");


    // Обрабатываем preflight OPTIONS-запросы
    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
      response.setStatus(HttpServletResponse.SC_OK); // Возвращаем 200 OK для preflight-запросов
      return;
    }

    chain.doFilter(req, res);
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}

  @Override
  public void destroy() {}
}
