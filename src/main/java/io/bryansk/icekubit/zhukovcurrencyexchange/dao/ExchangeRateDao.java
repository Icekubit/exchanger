package io.bryansk.icekubit.zhukovcurrencyexchange.dao;

import io.bryansk.icekubit.zhukovcurrencyexchange.exceptions.CurrencyNotFoundException;
import io.bryansk.icekubit.zhukovcurrencyexchange.exceptions.ExchangeRateAlreadyExistException;
import io.bryansk.icekubit.zhukovcurrencyexchange.model.ExchangeRate;
import org.sqlite.SQLiteException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateDao {

    private static final String URL = "jdbc:sqlite::resource:mydb.db";

    private static ExchangeRateDao exchangeRateDao;


    private ExchangeRateDao(){
    }

    public static ExchangeRateDao getExchangeRateDao() {
        if (exchangeRateDao == null) {
            exchangeRateDao = new ExchangeRateDao();
        }
        return exchangeRateDao;
    }

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }



    public List<ExchangeRate> getAllExchangeRates() {
        List<ExchangeRate> allExchangeRates = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL)){
            Statement statement = connection.createStatement();
            String SQL = "SELECT * FROM ExchangeRates";
            ResultSet resultSet = statement.executeQuery(SQL);

            while (resultSet.next()) {
                ExchangeRate exchangeRate = new ExchangeRate();
                exchangeRate.setId(resultSet.getInt("ID"));
                exchangeRate.setBaseCurrencyId(resultSet.getInt("BaseCurrencyId"));
                exchangeRate.setTargetCurrencyId(resultSet.getInt("TargetCurrencyId"));
                exchangeRate.setRate(resultSet.getBigDecimal("Rate").setScale(6, RoundingMode.FLOOR));
                allExchangeRates.add(exchangeRate);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return allExchangeRates;
    }

    public ExchangeRate getExchangeRate(String baseCurrencyCode, String targetCurrencyCode) {
        ExchangeRate exchangeRate = null;

//        int baseCurrencyId = 0;
//        int targetCurrencyId = 0;

//        try {
//            baseCurrencyId = currencyDao.getCurrencyByCode(baseCurrencyCode).getId();
//            targetCurrencyId = currencyDao.getCurrencyByCode(targetCurrencyCode).getId();
//        } catch (NullPointerException e) {
//            throw new CurrencyPairNotFoundException();
//        }

        try (Connection connection = DriverManager.getConnection(URL)){
//            String SQL = "SELECT * FROM ExchangeRates WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?";
//            String SQL = """
//                SELECT * FROM ExchangeRates WHERE
//                    BaseCurrencyId = (SELECT ID FROM Currencies WHERE Code = ?)
//                        AND
//                    TargetCurrencyId = (SELECT ID FROM Currencies WHERE Code = ?)
//            """;
            String SQL = """
                    SELECT ExchangeRates.ID ID, BaseCurrencyId, TargetCurrencyId, Rate 
                    FROM ExchangeRates
                    JOIN Currencies baseCurrency ON ExchangeRates.BaseCurrencyId = baseCurrency.ID
                    JOIN Currencies targetCurrency ON ExchangeRates.TargetCurrencyId = targetCurrency.ID
                    WHERE baseCurrency.Code = ?
                    AND
                        targetCurrency.Code = ?
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, baseCurrencyCode);
            preparedStatement.setString(2, targetCurrencyCode);
            ResultSet resultSet = preparedStatement.executeQuery();

            resultSet.next();
            if (resultSet.getInt("id") != 0) {
                exchangeRate = new ExchangeRate();
                exchangeRate.setId(resultSet.getInt("ID"));
                exchangeRate.setBaseCurrencyId(resultSet.getInt("BaseCurrencyId"));
                exchangeRate.setTargetCurrencyId(resultSet.getInt("TargetCurrencyId"));
                exchangeRate.setRate(resultSet.getBigDecimal("Rate").setScale(6, RoundingMode.FLOOR));
                return exchangeRate;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return exchangeRate;
    }

    public void save(ExchangeRate exchangeRate) {
        try (Connection connection = DriverManager.getConnection(URL)){
            String SQL = "INSERT INTO ExchangeRates(BaseCurrencyId, TargetCurrencyId, Rate) VALUES(?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1, exchangeRate.getBaseCurrencyId());
            preparedStatement.setInt(2, exchangeRate.getTargetCurrencyId());
            preparedStatement.setBigDecimal(3, exchangeRate.getRate().setScale(6, RoundingMode.FLOOR));
            preparedStatement.executeUpdate();
        } catch (SQLiteException e1) {
            throw new ExchangeRateAlreadyExistException(e1);
        }
        catch (SQLException e2) {
            throw new RuntimeException(e2);
        }
    }

    public ExchangeRate save(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        try (Connection connection = DriverManager.getConnection(URL)){
//            String SQL = "INSERT INTO ExchangeRates(BaseCurrencyId, TargetCurrencyId, Rate) VALUES(?, ?, ?)";
            String SQL = """
                            INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate)
                            SELECT
                            (SELECT ID FROM Currencies WHERE Code = ?),
                            (SELECT ID FROM Currencies WHERE Code = ?),
                            ?
                            """;
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, baseCurrencyCode);
            preparedStatement.setString(2, targetCurrencyCode);
            preparedStatement.setBigDecimal(3, rate.setScale(6, RoundingMode.FLOOR));
            preparedStatement.executeUpdate();
        } catch (SQLiteException e1) {
            String message = e1.getMessage();
            if (message.contains("[SQLITE_CONSTRAINT_UNIQUE]"))
                throw new ExchangeRateAlreadyExistException(e1);
            if (message.contains("[SQLITE_CONSTRAINT_NOTNULL]"))
                throw new CurrencyNotFoundException();
        }
        catch (SQLException e2) {
            throw new RuntimeException(e2);
        }
        return this.getExchangeRate(baseCurrencyCode, targetCurrencyCode);
    }

    public static void main(String[] args) {
        ExchangeRateDao exchangeRateDao1 = ExchangeRateDao.getExchangeRateDao();
        exchangeRateDao1.save("URL", "RUB", BigDecimal.valueOf(1));
    }

    public void update(ExchangeRate exchangeRate) {
        try (Connection connection = DriverManager.getConnection(URL)){
            String SQL = "UPDATE ExchangeRates SET Rate = ? WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setBigDecimal(1, exchangeRate.getRate().setScale(6, RoundingMode.FLOOR));
            preparedStatement.setInt(2, exchangeRate.getBaseCurrencyId());
            preparedStatement.setInt(3, exchangeRate.getTargetCurrencyId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        try (Connection connection = DriverManager.getConnection(URL)) {
            String SQL = """
                UPDATE ExchangeRates
                SET Rate = ?
                WHERE 
                    BaseCurrencyId = (SELECT ID FROM Currencies WHERE Code = ?) 
                        AND 
                    TargetCurrencyId = (SELECT ID FROM Currencies WHERE Code = ?)
            """;
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setBigDecimal(1, rate.setScale(6, RoundingMode.FLOOR));
            preparedStatement.setString(2, baseCurrencyCode);
            preparedStatement.setString(3, targetCurrencyCode);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}


