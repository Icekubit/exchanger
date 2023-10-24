package io.bryansk.icekubit.zhukovcurrencyexchange.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.bryansk.icekubit.zhukovcurrencyexchange.dao.CurrencyDao;
import io.bryansk.icekubit.zhukovcurrencyexchange.dto.ErrorMessageDto;
import io.bryansk.icekubit.zhukovcurrencyexchange.exceptions.CurrencyNotFoundException;
import io.bryansk.icekubit.zhukovcurrencyexchange.model.Currency;

import java.util.List;

public class CurrenciesService {

    private static CurrenciesService currenciesService;
    private CurrencyDao currencyDao;

    private CurrenciesService(){
        currencyDao = CurrencyDao.getCurrencyDao();
    }

    public static CurrenciesService getCurrenciesService() {
        if (currenciesService == null) {
            currenciesService = new CurrenciesService();
        }
        return currenciesService;
    }

    public Currency getCurrencyById(int id) {
        return currencyDao.getCurrencyById(id);
    }

    public String getAllCurrencies() {
        List<Currency> listOfCurrencies = currencyDao.getAllCurrencies();
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "";
        try {
            json = objectMapper.writeValueAsString(listOfCurrencies);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }

    public String getCurrencyByCode(String code) {
        Currency currency = currencyDao.getCurrencyByCode(code);
        if (currency == null)
            throw new CurrencyNotFoundException();
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "";
        try {
            json = objectMapper.writeValueAsString(currency);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }

    public String save(Currency currency) {
        currencyDao.save(currency);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // вытаскиваем из бд только, что сохранённую валюту, потому как нам нужно знать её айдишник
        // чтобы швырнуться нормальным джейсоном в ответ
        Currency addedCurrency = currencyDao.getCurrencyByCode(currency.getCode());

        ObjectMapper objectMapper = new ObjectMapper();
        String json = "";
        try {
            json = objectMapper.writeValueAsString(addedCurrency);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;

    }

    public boolean isCurrencyExist(String code) {
        return currencyDao.getCurrencyByCode(code) != null;
    }
}