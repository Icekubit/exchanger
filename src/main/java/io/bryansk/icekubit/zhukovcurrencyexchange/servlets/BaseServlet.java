package io.bryansk.icekubit.zhukovcurrencyexchange.servlets;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.bryansk.icekubit.zhukovcurrencyexchange.dto.ExchangeRateDto;
import io.bryansk.icekubit.zhukovcurrencyexchange.dto.ExchangeResponseDto;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

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

    protected void sendSuccess(HttpServletResponse resp, ExchangeRateDto exchangeRateDto) throws IOException {
        resp.setContentType("application/json");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "";
        try {
            json = objectMapper.writeValueAsString(exchangeRateDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        resp.getWriter().write(json);
    }

    protected void sendSuccess(HttpServletResponse resp, ExchangeResponseDto exchangeResponseDto) throws IOException {
        resp.setContentType("application/json");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "";
        try {
            json = objectMapper.writeValueAsString(exchangeResponseDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        resp.getWriter().write(json);
    }

    protected void sendSuccess(HttpServletResponse resp, List<ExchangeRateDto> allExchangeRatesDto) throws IOException {
        resp.setContentType("application/json");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "";
        try {
            json = objectMapper.writeValueAsString(allExchangeRatesDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        resp.getWriter().write(json);
    }

    protected String getErrorJson(String message) {
        return "{\"message\": \"" + message + "\"}";
    }
}