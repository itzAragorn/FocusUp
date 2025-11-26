package com.example.focusup

import org.junit.Test
import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
    
    @Test
    fun subtraction_isCorrect() {
        assertEquals(2, 4 - 2)
    }
    
    @Test
    fun multiplication_isCorrect() {
        assertEquals(6, 2 * 3)
    }
    
    @Test
    fun division_isCorrect() {
        assertEquals(3, 9 / 3)
    }
    
    @Test
    fun string_concatenation_works() {
        val result = "Hello" + " " + "World"
        assertEquals("Hello World", result)
    }
    
    @Test
    fun list_operations_work() {
        val list = mutableListOf<String>()
        list.add("Task 1")
        list.add("Task 2")
        
        assertEquals(2, list.size)
        assertTrue(list.contains("Task 1"))
        assertFalse(list.contains("Task 3"))
    }
    
    @Test
    fun boolean_logic_works() {
        assertTrue(true && true)
        assertFalse(true && false)
        assertTrue(true || false)
        assertFalse(false && false)
    }
    
    @Test
    fun null_handling_works() {
        val nullableString: String? = null
        val nonNullString: String? = "Not null"
        
        assertNull(nullableString)
        assertNotNull(nonNullString)
        assertEquals("Not null", nonNullString)
    }
}