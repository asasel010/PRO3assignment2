import database.AnimalEntity;
import database.ProductEntity;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class ProductEntityTest {
    @Test
    public void shouldReturnAnimals() {
        ProductEntity productEntity = new ProductEntity();

        int productId = 1;
        ArrayList<Integer> animalIds =  productEntity.readAnimalsInProduct(productId);
        assert(animalIds.size() == 2);
        assert(animalIds.get(0) == 1123);
        assert(animalIds.get(1) == 1244);
    }
}
