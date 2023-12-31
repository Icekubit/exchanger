package io.bryansk.icekubit.zhukovcurrencyexchange.servlets;

import io.bryansk.icekubit.zhukovcurrencyexchange.model.Currency;
import io.bryansk.icekubit.zhukovcurrencyexchange.services.CurrenciesService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;



public class CurrencyServlet extends BaseServlet {

    private CurrenciesService currenciesService = CurrenciesService.getCurrenciesService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        System.out.println(pathInfo);
        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(resp,400, "Код валюты отсутствует в адресе");
        } else {
            String code = pathInfo.replaceFirst("/", "");
            Optional<Currency> optional = currenciesService.getCurrencyByCode(code);
            if (optional.isEmpty()) {
                sendError(resp, 404, "Валюта не найдена");
            } else {
                sendSuccess(resp, optional.get());
            }
        }

    }

}
