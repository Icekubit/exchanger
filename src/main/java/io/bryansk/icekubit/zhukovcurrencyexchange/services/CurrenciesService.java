package io.bryansk.icekubit.zhukovcurrencyexchange.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.bryansk.icekubit.zhukovcurrencyexchange.dao.CurrencyDao;
import io.bryansk.icekubit.zhukovcurrencyexchange.dto.ErrorMessageDto;
import io.bryansk.icekubit.zhukovcurrencyexchange.exceptions.CurrencyNotFoundException;
import io.bryansk.icekubit.zhukovcurrencyexchange.model.Currency;

import java.util.List;
import java.util.Optional;

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

    public List<Currency> getAllCurrencies() {
        List<Currency> listOfCurrencies = currencyDao.getAllCurrencies();
        return listOfCurrencies;
    }

    public Optional<Currency> getCurrencyByCode(String code) {
        return currencyDao.getCurrencyByCode(code);
    }

    public Currency getCurrencyById(int id) {
        return currencyDao.getCurrencyById(id);
    }

    public Currency save(Currency currency) {
        currencyDao.save(currency);
        Currency addedCurrency = currencyDao.getCurrencyByCode(currency.getCode()).get();
        return addedCurrency;

    }

    public boolean isCurrencyExist(String code) {
        return currencyDao.getCurrencyByCode(code) != null;
    }
}