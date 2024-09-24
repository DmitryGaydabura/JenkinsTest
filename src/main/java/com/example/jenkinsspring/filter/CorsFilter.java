//package com.example.jenkinsspring.filter;
//
//import javax.servlet.*;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//public class CorsFilter implements Filter {
//
//  @Override
//  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
//    HttpServletResponse response = (HttpServletResponse) res;
//    response.setHeader("Access-Control-Allow-Origin", "*");
//    response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
//    response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
//    chain.doFilter(req, res);
//  }
//
//  @Override
//  public void init(FilterConfig filterConfig) {}
//
//  @Override
//  public void destroy() {}
//}
