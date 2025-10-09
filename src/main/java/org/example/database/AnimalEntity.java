package org.example.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AnimalEntity implements AnimalDAO {

    @Override
    public ArrayList<Integer> readProductsWithAnimal(int id) {
        try(Connection connection = DatabaseConnection.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM animal INNER JOIN slaughter_house.animal_part ap on animal.id = ap.animal_id " +
                            "inner join slaughter_house.product_part pp on ap.id = pp.animal_part_id " +
                            "inner join slaughter_house.product p on p.id = pp.product_id " +
                            "WHERE animal.id = ?");
            statement.setInt(1, id);

            ResultSet rs = statement.executeQuery();
            ArrayList<Integer> ids = new ArrayList<>();
            while(rs.next()) {
                ids.add(rs.getInt("p.id"));

            }
            return ids;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
        }
        return null;
    }


}
