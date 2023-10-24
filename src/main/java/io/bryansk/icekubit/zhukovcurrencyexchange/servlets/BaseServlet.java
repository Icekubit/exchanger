package io.bryansk.icekubit.zhukovcurrencyexchange.servlets;


import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public abstract class BaseServlet extends HttpServlet {
    protected void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.getWriter().write(getErrorJson(message));
    }

    protected void sendSuccess(HttpServletResponse resp, String json) throws IOException {
        resp.setContentType("application/json");
        resp.getWriter().write(json);
    }

    protected String getErrorJson(String message) {
        return "{\"message\": \"" + message + "\"}";
    }
}