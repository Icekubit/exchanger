package io.bryansk.icekubit.zhukovcurrencyexchange.servlets;


import io.bryansk.icekubit.zhukovcurrencyexchange.dto.ExchangeResponseDto;
import io.bryansk.icekubit.zhukovcurrencyexchange.exceptions.ExchangeRateNotFoundException;
import io.bryansk.icekubit.zhukovcurrencyexchange.services.ExchangeRatesService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

public class ExchangeServlet extends BaseServlet {
    private ExchangeRatesService exchangeRatesService = ExchangeRatesService.getExchangeRatesService();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("from");
        String targetCurrencyCode = req.getParameter("to");
        String amountStr = req.getParameter("amount");


        BigDecimal amount = new BigDecimal(0.0);



        if (baseCurrencyCode == null || targetCurrencyCode == null || amountStr == null) {
            sendError(resp, 400, "Отсутствует параметр запроса");
        } else {
            try {
                amount = BigDecimal.valueOf(Double.parseDouble(amountStr));
                ExchangeResponseDto exchangeResponseDto
                        = exchangeRatesService.exchange(baseCurrencyCode, targetCurrencyCode, amount);
                sendSuccess(resp, exchangeResponseDto);
            } catch (NumberFormatException e) {
                sendError(resp,400, "Неправильный формат параметра amount");

            } catch (ExchangeRateNotFoundException e) {
                sendError(resp, 404, "Валютная пара отсутствует в базе данных");
            }
        }
    }
}
