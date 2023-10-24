package io.bryansk.icekubit.zhukovcurrencyexchange.servlets;

import io.bryansk.icekubit.zhukovcurrencyexchange.exceptions.CurrencyNotFoundException;
import io.bryansk.icekubit.zhukovcurrencyexchange.model.Currency;
import io.bryansk.icekubit.zhukovcurrencyexchange.services.CurrenciesService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class CurrencyServlet extends BaseServlet {

    private CurrenciesService currenciesService = CurrenciesService.getCurrenciesService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String URI = req.getRequestURI();
//        Currency currency = null;

//        if (URI.equals("/currency/") || URI.equals("/currency")) {
//            sendError(resp,400, "Код валюты отсутствует в адресе");
//        } else {
//            String code = URI.substring(10);
//            currency = currenciesService.getCurrencyByCode(code);
//            if (currency == null) {
//                sendError(resp,404, "Валюта не найдена");
//            } else {
//                ObjectMapper objectMapper = new ObjectMapper();
//                String json = objectMapper.writeValueAsString(currency);
//                resp.getWriter().write(json);
//            }
//        }
        if (URI.equals("/helloworld/currency/") || URI.equals("/helloworld/currency")) {
            sendError(resp,400, "Код валюты отсутствует в адресе");
        } else {
            try {
                String code = URI.substring(21);
                String json = currenciesService.getCurrencyByCode(code);
                sendSuccess(resp, json);
            } catch (CurrencyNotFoundException e) {
                sendError(resp, 404, "Валюта не найдена");
            }
        }

    }

}
