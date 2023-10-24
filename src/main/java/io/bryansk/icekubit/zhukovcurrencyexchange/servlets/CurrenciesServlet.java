package io.bryansk.icekubit.zhukovcurrencyexchange.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.bryansk.icekubit.zhukovcurrencyexchange.exceptions.CurrencyNotFoundException;
import io.bryansk.icekubit.zhukovcurrencyexchange.model.Currency;
import io.bryansk.icekubit.zhukovcurrencyexchange.services.CurrenciesService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CurrenciesServlet extends BaseServlet {

    private CurrenciesService currenciesService = CurrenciesService.getCurrenciesService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        String URI = req.getRequestURI();
//        Currency currency = null;



//        if (URI.startsWith("/currencies/")) {
//            String code = URI.substring(12);
//            if (code.equals("")) {
//                resp.sendError(400, "Код валюты отсутствует в адресе");
//            } else {
//                currency = currenciesService.getCurrencyByCode(code);
//                if (currency.getCode() == null) {
//                    resp.sendError(404, "Валюта не найдена");
//                }
//            }
//            ObjectMapper objectMapper = new ObjectMapper();
//
//            String json = objectMapper.writeValueAsString(currency);
//
//
//            resp.getWriter().write(json);
//        } else {
        String json = currenciesService.getAllCurrencies();
        sendSuccess(resp, json);
//        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");

        if (name == null || code == null || sign == null)
            sendError(resp, 400, "Отсутствует нужное поле формы");
        else if (currenciesService.isCurrencyExist(code)) {
            sendError(resp, 409, "Валюта с таким кодом уже существует");
        } else {
            String json = currenciesService.save(new Currency(code, name, sign));
            sendSuccess(resp, json);
        }

    }
}
