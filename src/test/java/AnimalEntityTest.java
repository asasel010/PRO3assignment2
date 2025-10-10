import database.AnimalEntity;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class AnimalEntityTest {
    @Test
    public void shouldReturnProducts() {
        AnimalEntity animalEntity = new AnimalEntity();

        int animalId = 1244;
        ArrayList<Integer> productIds =  animalEntity.readProductsWithAnimal(animalId);
        assert(productIds.size() == 2);
        assert(productIds.get(0) == 1);
        assert(productIds.get(1) == 2);
    }
}
