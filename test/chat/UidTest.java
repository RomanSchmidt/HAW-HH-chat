package chat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

class UidTest {

    @Test
    void testEquals() {
        Uid a = new Uid("a", 1);
        Uid b = new Uid("b", 2);
        Uid c = new Uid("a", 3);
        Uid a2 = new Uid("a", 1);
        ArrayList<Uid> list = new ArrayList<>();
        list.add(a);
        HashMap<Uid, String> map = new HashMap<>();
        map.put(a, "foo");

        Assertions.assertEquals(a, a);
        Assertions.assertNotEquals(a, b);
        Assertions.assertNotEquals(c, b);
        Assertions.assertEquals(a, a2);

        Assertions.assertTrue(list.contains(a));
        Assertions.assertFalse(list.contains(b));
        Assertions.assertTrue(list.contains(a2));

        Assertions.assertTrue(map.containsKey(a));
        Assertions.assertTrue(map.containsKey(a2));
        Assertions.assertFalse(map.containsKey(b));
    }
}