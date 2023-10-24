package io.bryansk.icekubit.zhukovcurrencyexchange.servlets;

import io.bryansk.icekubit.zhukovcurrencyexchange.services.ExchangeRatesService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import java.io.IOException;


public class ExchangeRatesServlet extends BaseServlet {

    ExchangeRatesService exchangeRatesService = ExchangeRatesService.getExchangeRatesService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ExchangeRatesService exchangeRatesService = ExchangeRatesService.getExchangeRatesService();

        resp.setContentType("application/json");
        String json = exchangeRatesService.getAllExchangeRates();

        sendSuccess(resp, json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rateString = req.getParameter("rate");


        if (baseCurrencyCode == null || targetCurrencyCode == null || rateString == null) {
//            resp.setContentType("text/plain");
//            resp.setStatus(400);
//            resp.getWriter().write("Отсутствует нужное поле формы");
            sendError(resp,400, "Отсутствует нужное поле формы");
        }
//        else if (exchangeRatesService.isExchangeRateExist(baseCurrencyCode, targetCurrencyCode))
//            sendError(resp,409, "Валютная пара с таким кодом уже существует");
        else {
            double rate = Double.parseDouble(rateString);
            String json = exchangeRatesService.save(baseCurrencyCode, targetCurrencyCode, rate);
            sendSuccess(resp, json);
        }
    }
}
