package io.bryansk.icekubit.zhukovcurrencyexchange.servlets;


import io.bryansk.icekubit.zhukovcurrencyexchange.dto.ExchangeRateDto;
import io.bryansk.icekubit.zhukovcurrencyexchange.exceptions.ExchangeRateNotFoundException;
import io.bryansk.icekubit.zhukovcurrencyexchange.services.ExchangeRatesService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;

public class ExchangeRateServlet extends BaseServlet {

    ExchangeRatesService exchangeRatesService = ExchangeRatesService.getExchangeRatesService();


    // ОЧЕНЬ ХУЁВАЯ ПИРАМИДА ИЗ ИФЁЛСОВ
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(resp,400, "Коды валют отсутствуют в адресе");
        } else {
            String currencyPairCodes = pathInfo.substring(1);
            if (currencyPairCodes.length() != 6) {
                sendError(resp, 404, "Некорректные коды валют");
            } else {
                String baseCurrencyCode = currencyPairCodes.substring(0, 3);
                String targetCurrencyCode = currencyPairCodes.substring(3);
                try {
                    ExchangeRateDto exchangeRateDto
                            = exchangeRatesService.getExchangeRate(baseCurrencyCode, targetCurrencyCode);
                    sendSuccess(resp, exchangeRateDto);
                } catch (ExchangeRateNotFoundException e) {
                    sendError(resp, 404, "Обменный курс для пары не найден");
                }
            }
        }
    }



    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if (!method.equals("PATCH")) {
            super.service(req, resp);
            return;
        }
        this.doPatch(req, resp);
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        // вытаскиваем тело PATCH-запроса
        BufferedReader reader = req.getReader();
        String requestBody = "";
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody += line;
        }

        // извлекаем валютную пару из URI
        String URI = req.getRequestURI();
        String currencyPairCodes = URI.substring(14);
        String baseCurrencyCode = currencyPairCodes.substring(0,3);
        String targetCurrencyCode = currencyPairCodes.substring(3);

        // Теперь извлекаем rate из тела запроса говнопалочным способом
        int positionOfRateInRequestBody = requestBody.indexOf("rate=");
        String rateString = null;
        if (positionOfRateInRequestBody != -1) {
            if (requestBody.indexOf("&", positionOfRateInRequestBody+5) == -1) {
                rateString = requestBody.substring(positionOfRateInRequestBody + 5);
            } else {
                rateString = requestBody.substring(positionOfRateInRequestBody + 5, requestBody.indexOf("&", positionOfRateInRequestBody+5));
            }
        }

        if (rateString == null)
            sendError(resp,400, "Отсутствует нужное поле формы");
        else {
            try {
                BigDecimal rate = BigDecimal.valueOf(Double.parseDouble(rateString));
                ExchangeRateDto exchangeRateDto
                        = exchangeRatesService.update(baseCurrencyCode, targetCurrencyCode, rate);
                sendSuccess(resp, exchangeRateDto);
            } catch (NumberFormatException e) {
                sendError(resp, 400, "Неправильный формат параметра rate");
            } catch (ExchangeRateNotFoundException e) {
                sendError(resp, 404, URI);
            }
        }
    }



}
