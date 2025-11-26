package com.example.focusup.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.focusup.data.database.dao.UserDao
import com.example.focusup.data.database.entities.User
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class UserRepositoryTestSimple {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var mockUserDao: UserDao
    private lateinit var userRepository: UserRepository

    // Test data
    private val testUser = User(
        id = 1L,
        email = "test@example.com",
        password = "hashedpassword",
        name = "Test User",
        profileType = "STUDENT"
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Create mocks
        mockUserDao = mockk()
        
        // Create repository with mocked DAO
        userRepository = UserRepository(mockUserDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `loginUser with valid credentials should return user`() = runTest {
        // Given
        val email = testUser.email
        val password = "plainpassword"
        
        coEvery { mockUserDao.getUserByCredentials(email, password) } returns testUser
        
        // When
        val result = userRepository.loginUser(email, password)
        
        // Then
        assertEquals("Should return user for valid credentials", testUser, result)
        coVerify { mockUserDao.getUserByCredentials(email, password) }
    }

    @Test
    fun `loginUser with invalid credentials should return null`() = runTest {
        // Given
        val email = testUser.email
        val wrongPassword = "wrongpassword"
        
        coEvery { mockUserDao.getUserByCredentials(email, wrongPassword) } returns null
        
        // When
        val result = userRepository.loginUser(email, wrongPassword)
        
        // Then
        assertNull("Should return null for invalid credentials", result)
        coVerify { mockUserDao.getUserByCredentials(email, wrongPassword) }
    }

    @Test
    fun `registerUser with new email should return success`() = runTest {
        // Given
        val newUser = testUser.copy(email = "newuser@example.com")
        val expectedId = 1L
        
        coEvery { mockUserDao.getUserByEmail(newUser.email) } returns null
        coEvery { mockUserDao.insertUser(newUser) } returns expectedId
        
        // When
        val result = userRepository.registerUser(newUser)
        
        // Then
        assertTrue("Should return success result", result.isSuccess)
        assertEquals("Should return user ID", expectedId, result.getOrNull())
        coVerify { mockUserDao.getUserByEmail(newUser.email) }
        coVerify { mockUserDao.insertUser(newUser) }
    }

    @Test
    fun `registerUser with existing email should return failure`() = runTest {
        // Given
        coEvery { mockUserDao.getUserByEmail(testUser.email) } returns testUser
        
        // When
        val result = userRepository.registerUser(testUser)
        
        // Then
        assertTrue("Should return failure result", result.isFailure)
        assertTrue("Should contain email exists error", 
            result.exceptionOrNull()?.message?.contains("Email already exists") == true)
        coVerify { mockUserDao.getUserByEmail(testUser.email) }
        coVerify(exactly = 0) { mockUserDao.insertUser(any()) }
    }

    @Test
    fun `getUserById should return user when exists`() = runTest {
        // Given
        coEvery { mockUserDao.getUserById(testUser.id) } returns testUser
        
        // When
        val result = userRepository.getUserById(testUser.id)
        
        // Then
        assertEquals("Should return user from DAO", testUser, result)
        coVerify { mockUserDao.getUserById(testUser.id) }
    }

    @Test
    fun `getUserById with non-existent ID should return null`() = runTest {
        // Given
        val nonExistentId = 999L
        coEvery { mockUserDao.getUserById(nonExistentId) } returns null
        
        // When
        val result = userRepository.getUserById(nonExistentId)
        
        // Then
        assertNull("Should return null for non-existent user", result)
        coVerify { mockUserDao.getUserById(nonExistentId) }
    }

    @Test
    fun `updateUser should call DAO update`() = runTest {
        // Given
        val updatedUser = testUser.copy(name = "Updated Name")
        coEvery { mockUserDao.updateUser(updatedUser) } just Runs
        
        // When
        userRepository.updateUser(updatedUser)
        
        // Then
        coVerify { mockUserDao.updateUser(updatedUser) }
    }

    @Test
    fun `isEmailExists should return true for existing email`() = runTest {
        // Given
        val email = testUser.email
        coEvery { mockUserDao.isEmailExists(email) } returns 1
        
        // When
        val result = userRepository.isEmailExists(email)
        
        // Then
        assertTrue("Should return true for existing email", result)
        coVerify { mockUserDao.isEmailExists(email) }
    }

    @Test
    fun `isEmailExists should return false for non-existing email`() = runTest {
        // Given
        val email = "nonexistent@example.com"
        coEvery { mockUserDao.isEmailExists(email) } returns 0
        
        // When
        val result = userRepository.isEmailExists(email)
        
        // Then
        assertFalse("Should return false for non-existing email", result)
        coVerify { mockUserDao.isEmailExists(email) }
    }

    @Test
    fun `repository should handle DAO exceptions gracefully`() = runTest {
        // Given
        val exception = Exception("Database error")
        coEvery { mockUserDao.getUserById(any()) } throws exception
        
        // When & Then
        try {
            userRepository.getUserById(1L)
            fail("Should have thrown exception")
        } catch (e: Exception) {
            assertEquals("Should propagate DAO exception", exception, e)
        }
        
        coVerify { mockUserDao.getUserById(1L) }
    }

    @Test
    fun `registerUser should handle database exceptions`() = runTest {
        // Given
        val exception = Exception("Database connection error")
        coEvery { mockUserDao.getUserByEmail(any()) } throws exception
        
        // When
        val result = userRepository.registerUser(testUser)
        
        // Then
        assertTrue("Should return failure result", result.isFailure)
        assertEquals("Should propagate database exception", exception, result.exceptionOrNull())
        coVerify { mockUserDao.getUserByEmail(testUser.email) }
    }
}