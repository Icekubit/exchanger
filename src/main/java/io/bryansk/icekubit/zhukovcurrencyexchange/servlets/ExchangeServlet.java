package io.bryansk.icekubit.zhukovcurrencyexchange.servlets;


import io.bryansk.icekubit.zhukovcurrencyexchange.exceptions.ExchangeRateNotFoundException;
import io.bryansk.icekubit.zhukovcurrencyexchange.services.ExchangeRatesService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ExchangeServlet extends BaseServlet {
    private ExchangeRatesService exchangeRatesService = ExchangeRatesService.getExchangeRatesService();


//    @Override
//    public void init() throws ServletException {
//        super.init();
//        exchangeRatesService = ExchangeRatesService.getExchangeRatesService();
//    }
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Извлечение параметров из URL
        String baseCurrencyCode = req.getParameter("from");
        String targetCurrencyCode = req.getParameter("to");
        String amountStr = req.getParameter("amount");


        double amount = 0.0;



        if (baseCurrencyCode == null || targetCurrencyCode == null || amountStr == null) {
            sendError(resp, 400, "Отсутствует параметр запроса");
        } else {
            try {
                amount = Double.parseDouble(amountStr);
                String json = exchangeRatesService.exchange(baseCurrencyCode, targetCurrencyCode, amount);
                sendSuccess(resp, json);
            } catch (NumberFormatException e) {

//                resp.sendError(400, "Неправильный формат параметра amount");


                sendError(resp,400, "Неправильный формат параметра amount");

            } catch (ExchangeRateNotFoundException e) {
//                resp.sendError(404, "Валютная пара отсутствует в базе данных");

                sendError(resp, 404, "Валютная пара отсутствует в базе данных");
            }
        }


        // Здесь вы можете выполнить логику обработки запроса, используя извлеченные параметры
        // Например, выполнить конвертацию валют и отправить результат в ответ

//        ExchangeRatesService exchangeRatesService = ExchangeRatesService.getExchangeRatesService();
//        resp.setContentType("application/json");
//        String json = exchangeRatesService.exchange(baseCurrencyCode, targetCurrencyCode, amount);
//        resp.getWriter().write(json);
    }
}
