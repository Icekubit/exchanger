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
        String pathInfo = req.getPathInfo();
        System.out.println(pathInfo);
        if (pathInfo.equals("/") || pathInfo == null) {
            sendError(resp,400, "Код валюты отсутствует в адресе");
        } else {
            try {
                String code = pathInfo.substring(1);
                String json = currenciesService.getCurrencyByCode(code);
                sendSuccess(resp, json);
            } catch (CurrencyNotFoundException e) {
                sendError(resp, 404, "Валюта не найдена");
            }
        }

    }

}
