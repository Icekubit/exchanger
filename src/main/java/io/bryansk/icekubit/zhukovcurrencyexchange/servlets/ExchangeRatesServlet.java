package io.bryansk.icekubit.zhukovcurrencyexchange.servlets;

import io.bryansk.icekubit.zhukovcurrencyexchange.dto.ExchangeRateDto;
import io.bryansk.icekubit.zhukovcurrencyexchange.exceptions.CurrencyNotFoundException;
import io.bryansk.icekubit.zhukovcurrencyexchange.exceptions.ExchangeRateAlreadyExistException;
import io.bryansk.icekubit.zhukovcurrencyexchange.services.ExchangeRatesService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;


public class ExchangeRatesServlet extends BaseServlet {

    ExchangeRatesService exchangeRatesService = ExchangeRatesService.getExchangeRatesService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        List<ExchangeRateDto> allExchangeRates = exchangeRatesService.getAllExchangeRates();

        sendSuccess(resp, allExchangeRates);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rateString = req.getParameter("rate");


        if (baseCurrencyCode == null || targetCurrencyCode == null || rateString == null) {
            sendError(resp,400, "Отсутствует нужное поле формы");
        }
        else {
            try {
                BigDecimal rate = BigDecimal.valueOf(Double.parseDouble(rateString));
                ExchangeRateDto exchangeRateDto = exchangeRatesService.save(baseCurrencyCode, targetCurrencyCode, rate);
                sendSuccess(resp, exchangeRateDto);
            } catch (NumberFormatException e) {
                sendError(resp, 422, "Неправильный формат курса валют");
            } catch (CurrencyNotFoundException e) {
                sendError(resp, 422, "Валюты отсутствуют в базе данных");
            } catch (ExchangeRateAlreadyExistException e) {
                sendError(resp, 409, "Валютная пара с таким кодом уже существует");
            }
        }
    }
}
