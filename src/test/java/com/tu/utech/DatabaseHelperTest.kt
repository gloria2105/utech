package com.tu.utech

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class DatabaseHelperTest {

    @Mock
    private lateinit var mockDbHelper: DatabaseHelper

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this) // Inicializa los mocks
    }

    @Test
    fun testInsertUser() {

        val name = "John Doe"
        val email = "john@example.com"
        val order = "Order123"


        doNothing().`when`(mockDbHelper).insertUser(name, email, order)


        mockDbHelper.insertUser(name, email, order)


        verify(mockDbHelper).insertUser(name, email, order)
    }
}
