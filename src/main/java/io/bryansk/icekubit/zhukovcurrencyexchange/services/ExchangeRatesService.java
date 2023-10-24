package io.bryansk.icekubit.zhukovcurrencyexchange.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.bryansk.icekubit.zhukovcurrencyexchange.dao.CurrencyDao;
import io.bryansk.icekubit.zhukovcurrencyexchange.dao.ExchangeRateDao;
import io.bryansk.icekubit.zhukovcurrencyexchange.dto.ErrorMessageDto;
import io.bryansk.icekubit.zhukovcurrencyexchange.dto.ExchangeRateDto;
import io.bryansk.icekubit.zhukovcurrencyexchange.dto.ExchangeResponseDto;
import io.bryansk.icekubit.zhukovcurrencyexchange.exceptions.ExchangeRateNotFoundException;
import io.bryansk.icekubit.zhukovcurrencyexchange.model.ExchangeRate;

import java.util.ArrayList;
import java.util.List;

public class ExchangeRatesService {

    private static ExchangeRatesService exchangeRatesService;
    private ExchangeRateDao exchangeRateDao;
    private CurrencyDao currencyDao;

    private ExchangeRatesService(){
        exchangeRateDao = ExchangeRateDao.getExchangeRateDao();
        currencyDao = CurrencyDao.getCurrencyDao();
    }

    public static ExchangeRatesService getExchangeRatesService() {
        if (exchangeRatesService == null) {
            exchangeRatesService = new ExchangeRatesService();
        }
        return exchangeRatesService;
    }
    public String getAllExchangeRates() {
        List<ExchangeRate> allExchangeRates = exchangeRateDao.getAllExchangeRates();
        List<ExchangeRateDto> allExchangeRatesDto = new ArrayList<>();
        for (ExchangeRate exchangeRate : allExchangeRates) {
//            ExchangeRateDto exchangeRateDto = new ExchangeRateDto();
//            exchangeRateDto.setId(exchangeRate.getId());
//            exchangeRateDto.setBaseCurrency(currencyDao.getCurrencyById(exchangeRate.getBaseCurrencyId()));
//            exchangeRateDto.setTargetCurrency(currencyDao.getCurrencyById(exchangeRate.getTargetCurrencyId()));
//            exchangeRateDto.setRate(exchangeRate.getRate());
//            allExchangeRatesDto.add(exchangeRateDto);
            allExchangeRatesDto.add(convertToExchangeRateDto(exchangeRate));
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "";

        try {
            json = objectMapper.writeValueAsString(allExchangeRatesDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }

//    public boolean isExchangeRateExist(String baseCurrencyCode, String targetCurrencyCode) {
//        return exchangeRateDao.isExchangeRateExist(baseCurrencyCode, targetCurrencyCode);
//    }

    public String save(String baseCurrencyCode, String targetCurrencyCode, double rate) {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setBaseCurrencyId(currencyDao.getCurrencyByCode(baseCurrencyCode).getId());
        exchangeRate.setTargetCurrencyId(currencyDao.getCurrencyByCode(targetCurrencyCode).getId());
        exchangeRate.setRate(rate);
        exchangeRateDao.save(exchangeRate);

//        ExchangeRateDto exchangeRateDto = new ExchangeRateDto();
//        exchangeRateDto.setId(exchangeRate.getId());
//        exchangeRateDto.setBaseCurrency(currencyDao.getCurrencyById(exchangeRate.getBaseCurrencyId()));
//        exchangeRateDto.setTargetCurrency(currencyDao.getCurrencyById(exchangeRate.getTargetCurrencyId()));
//        exchangeRateDto.setRate(exchangeRate.getRate());


        ObjectMapper objectMapper = new ObjectMapper();
        String json = "";
        try {
            json = objectMapper.writeValueAsString(convertToExchangeRateDto(exchangeRate));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }

    public String getExchangeRate(String baseCurrencyCode, String targetCurrencyCode) {
        ExchangeRate exchangeRate = exchangeRateDao.getExchangeRate(baseCurrencyCode, targetCurrencyCode);
        if (exchangeRate == null)
            throw new ExchangeRateNotFoundException();

//        ExchangeRateDto exchangeRateDto = new ExchangeRateDto();
//        exchangeRateDto.setId(exchangeRate.getId());
//        exchangeRateDto.setBaseCurrency(currencyDao.getCurrencyById(exchangeRate.getBaseCurrencyId()));
//        exchangeRateDto.setTargetCurrency(currencyDao.getCurrencyById(exchangeRate.getTargetCurrencyId()));
//        exchangeRateDto.setRate(exchangeRate.getRate());

        ObjectMapper objectMapper = new ObjectMapper();
        String json = "";
        try {
            json = objectMapper.writeValueAsString(convertToExchangeRateDto(exchangeRate));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }

    public String update(String baseCurrencyCode, String targetCurrencyCode, double rate) {
        ExchangeRate exchangeRate = exchangeRateDao.getExchangeRate(baseCurrencyCode, targetCurrencyCode);
        if (exchangeRate == null)
            throw new ExchangeRateNotFoundException();

        exchangeRateDao.update(baseCurrencyCode, targetCurrencyCode, rate);

        exchangeRate.setRate(rate);


//        ExchangeRateDto exchangeRateDto = new ExchangeRateDto();
//        exchangeRateDto.setId(exchangeRate.getId());
//        exchangeRateDto.setBaseCurrency(currencyDao.getCurrencyById(exchangeRate.getBaseCurrencyId()));
//        exchangeRateDto.setTargetCurrency(currencyDao.getCurrencyById(exchangeRate.getTargetCurrencyId()));
//        exchangeRateDto.setRate(exchangeRate.getRate());

        ObjectMapper objectMapper = new ObjectMapper();
        String json = "";
        try {
            json = objectMapper.writeValueAsString(convertToExchangeRateDto(exchangeRate));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }

    public String exchange(String baseCurrencyCode, String targetCurrencyCode, double amount) {
        ExchangeRate exchangeRate = getRate(baseCurrencyCode, targetCurrencyCode);
//        if (exchangeRate == null)
//            return null;

        ExchangeResponseDto exchangeResponseDto = new ExchangeResponseDto();
        exchangeResponseDto.setBaseCurrency(currencyDao.getCurrencyById(exchangeRate.getBaseCurrencyId()));
        exchangeResponseDto.setTargetCurrency(currencyDao.getCurrencyById(exchangeRate.getTargetCurrencyId()));
        exchangeResponseDto.setRate(exchangeRate.getRate());
        exchangeResponseDto.setAmount(amount);
        exchangeResponseDto.setConvertedAmount(exchangeRate.getRate() * amount);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = "";
        try {
            json = objectMapper.writeValueAsString(exchangeResponseDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;

    }

    private ExchangeRate getRate(String baseCurrencyCode, String targetCurrencyCode) {
        ExchangeRate exchangeRate = exchangeRateDao.getExchangeRate(baseCurrencyCode, targetCurrencyCode);

        // если не нашли прямой курс пробуем найти обратный
        if (exchangeRate == null) {
            ExchangeRate reverseExchangeRate = exchangeRateDao.getExchangeRate(targetCurrencyCode, baseCurrencyCode);
            if (reverseExchangeRate != null) {

                // если нашли обратный курс, то преобразуем его в прямой
                exchangeRate = new ExchangeRate();


                exchangeRate.setBaseCurrencyId(reverseExchangeRate.getTargetCurrencyId());
                exchangeRate.setTargetCurrencyId(reverseExchangeRate.getBaseCurrencyId());
                exchangeRate.setRate(1/reverseExchangeRate.getRate());
            }
        }

        // если так ничего и не нашли, то ищем курс доллара по отношению каждой из валют нашей валютной пары
        if (exchangeRate == null) {
            ExchangeRate exchangeRateUsdToBaseCurrency
                    = exchangeRateDao.getExchangeRate("USD", baseCurrencyCode);
            ExchangeRate exchangeRateUsdToTargetCurrency
                    = exchangeRateDao.getExchangeRate("USD", targetCurrencyCode);

            // если всё нашлось, то лепим новый курс из этих данных

            if (exchangeRateUsdToBaseCurrency != null && exchangeRateUsdToTargetCurrency != null) {
                exchangeRate = new ExchangeRate();
                exchangeRate.setBaseCurrencyId(exchangeRateUsdToBaseCurrency.getTargetCurrencyId());
                exchangeRate.setTargetCurrencyId(exchangeRateUsdToTargetCurrency.getTargetCurrencyId());
                exchangeRate.setRate(exchangeRateUsdToTargetCurrency.getRate()/exchangeRateUsdToBaseCurrency.getRate());
            }

        }


        if (exchangeRate == null) {

            // если дошли сюда, то значит валюты есть в БД, а курса для них никак не найти

            throw new ExchangeRateNotFoundException();
        }
        return exchangeRate;
    }

    private ExchangeRateDto convertToExchangeRateDto(ExchangeRate exchangeRate) {
        ExchangeRateDto exchangeRateDto = new ExchangeRateDto();
        exchangeRateDto.setId(exchangeRate.getId());
        exchangeRateDto.setBaseCurrency(currencyDao.getCurrencyById(exchangeRate.getBaseCurrencyId()));
        exchangeRateDto.setTargetCurrency(currencyDao.getCurrencyById(exchangeRate.getTargetCurrencyId()));
        exchangeRateDto.setRate(exchangeRate.getRate());
        return exchangeRateDto;
    }

    public boolean isExchangeRateExist(String baseCurrencyCode, String targetCurrencyCode) {
        return exchangeRateDao.getExchangeRate(baseCurrencyCode, targetCurrencyCode) != null;
    }
}