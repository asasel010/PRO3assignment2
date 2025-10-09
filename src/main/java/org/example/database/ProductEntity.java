package org.example.database;

import java.sql.*;
import java.util.ArrayList;

public class ProductEntity implements ProductDAO {

    @Override
    public ArrayList<Integer> readAnimalsInProduct() {
        try(Connection connection = DatabaseConnection.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT *\n" +
                    "FROM product\n" +
                    "INNER JOIN slaughter_house.product_part pp on product.id = pp.product_id\n" +
                    "inner join slaughter_house.animal_part ap on ap.id = pp.animal_part_id\n" +
                    "INNER JOIN slaughter_house.animal a on a.id = ap.animal_id\n" +
                    "WHERE product_id = ?;\n");

            statement.setInt(1, 1);
            ResultSet rs = statement.executeQuery();
            ArrayList<Integer> animals = new ArrayList<>();
            while (rs.next()) {
                animals.add(rs.getInt("a.id"));
            }

            return animals;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
        }
        return null;
    }
}
