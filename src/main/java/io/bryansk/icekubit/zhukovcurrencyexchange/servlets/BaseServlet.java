package io.bryansk.icekubit.zhukovcurrencyexchange.servlets;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.bryansk.icekubit.zhukovcurrencyexchange.dto.ExchangeRateDto;
import io.bryansk.icekubit.zhukovcurrencyexchange.dto.ExchangeResponseDto;
import io.bryansk.icekubit.zhukovcurrencyexchange.model.Currency;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public abstract class BaseServlet extends HttpServlet {
    protected void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.getWriter().write(getErrorJson(message));
    }

    protected void sendSuccess(HttpServletResponse resp, Object obj) throws IOException {
        resp.setContentType("application/json");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(obj);
        resp.getWriter().write(json);
    }

    protected String getErrorJson(String message) {
        return "{\"message\": \"" + message + "\"}";
    }
}