package io.bryansk.icekubit.zhukovcurrencyexchange.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.bryansk.icekubit.zhukovcurrencyexchange.exceptions.CurrencyAlreadyExistException;
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
        String json = currenciesService.getAllCurrencies();
        sendSuccess(resp, json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");

        if (name == null || code == null || sign == null)
            sendError(resp, 400, "Отсутствует нужное поле формы");
        else try {
            String json = currenciesService.save(new Currency(code, name, sign));
            sendSuccess(resp, json);
        } catch (CurrencyAlreadyExistException e) {
            sendError(resp, 409, "Валюта с таким кодом уже существует");
        }

    }
}
