
import cache.LRUCache;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LRUCacheTest {

    @Test
    void shouldPutAndGetValue() {
        LRUCache<String, Integer> cache = new LRUCache<>(2);

        cache.put("A", 1);

        assertEquals(1, cache.get("A"));
        assertEquals(1, cache.size());
    }
    @Test
    void shouldRemoveLeastRecentlyUsedEntry(){
        LRUCache<String, Integer> cache = new LRUCache<>(2);

        cache.put("A", 1);
        cache.put("B", 2);

        cache.get("A");
        cache.put("C", 3);

        assertTrue(cache.containsKey("A"));
        assertTrue(cache.containsKey("C"));
        assertFalse(cache.containsKey("B"));
        assertEquals(2, cache.size());
    }




}
