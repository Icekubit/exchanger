package io.bryansk.icekubit.zhukovcurrencyexchange.dao;


import io.bryansk.icekubit.zhukovcurrencyexchange.exceptions.CurrencyAlreadyExistException;
import io.bryansk.icekubit.zhukovcurrencyexchange.model.Currency;
import io.bryansk.icekubit.zhukovcurrencyexchange.services.CurrenciesService;
import org.sqlite.SQLiteException;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CurrencyDao {

//    private static final String URL = "jdbc:sqlite:C:\\Users\\User\\IdeaProjects\\ZhukovCurrencyExchange\\mydb.db";
    private static final String URL = "jdbc:sqlite::resource:mydb.db";

    private static CurrencyDao currencyDao;


    private CurrencyDao(){}

    public static CurrencyDao getCurrencyDao() {
        if (currencyDao == null) {
            currencyDao = new CurrencyDao();
        }
        return currencyDao;
    }

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Currency> getAllCurrencies() {
        List<Currency> allCurrencies = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL)){
            Statement statement = connection.createStatement();
            String SQL = "SELECT * FROM Currencies";
            ResultSet resultSet = statement.executeQuery(SQL);

            while (resultSet.next()) {
                Currency currency = new Currency();
                currency.setId(resultSet.getInt("ID"));
                currency.setCode(resultSet.getString("Code"));
                currency.setName(resultSet.getString("FullName"));
                currency.setSign(resultSet.getString("Sign"));
                allCurrencies.add(currency);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return allCurrencies;
    }
    public Currency getCurrencyById(int id) {
        Currency currency = new Currency();
        try (Connection connection = DriverManager.getConnection(URL)){
            String SQL = "SELECT * FROM Currencies WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            resultSet.next();


            currency.setId(resultSet.getInt("ID"));
            currency.setCode(resultSet.getString("Code"));
            currency.setName(resultSet.getString("FullName"));
            currency.setSign(resultSet.getString("Sign"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return currency;
    }

    public Currency getCurrencyByCode(String code) {
        Currency currency = null;
        try (Connection connection = DriverManager.getConnection(URL)){
            String SQL = "SELECT * FROM Currencies WHERE Code = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, code);
            ResultSet resultSet = preparedStatement.executeQuery();

            resultSet.next();

            // проверяем нашёл ли селект что-нибудь

            if (resultSet.getInt("ID") != 0) {

                // если нашёл, то создаём нью карренси, если не нашёл то швыряем налл

                currency = new Currency();
                currency.setId(resultSet.getInt("ID"));
                currency.setCode(resultSet.getString("Code"));
                currency.setName(resultSet.getString("FullName"));
                currency.setSign(resultSet.getString("Sign"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return currency;
    }

    public void save(Currency currency) {

        try (Connection connection = DriverManager.getConnection(URL)){
            String SQL = "INSERT INTO Currencies(Code, FullName, Sign) VALUES(?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getName());
            preparedStatement.setString(3, currency.getSign());
            preparedStatement.executeUpdate();
        } catch (SQLiteException e) {
            throw new CurrencyAlreadyExistException(e);
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {
        CurrencyDao currencyDao1 = CurrencyDao.getCurrencyDao();
        System.out.println(currencyDao1.getAllCurrencies());
    }
}
