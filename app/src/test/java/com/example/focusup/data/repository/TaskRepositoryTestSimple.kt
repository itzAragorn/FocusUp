package com.example.focusup.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.focusup.data.database.dao.TaskDao
import com.example.focusup.data.database.entities.Task
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class TaskRepositoryTestSimple {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var mockTaskDao: TaskDao
    private lateinit var taskRepository: TaskRepository

    // Test data
    private val testTask = Task(
        id = 1L,
        userId = 100L,
        name = "Test Task",
        description = "Test description",
        date = "2024-01-01",
        time = "10:00",
        isCompleted = false,
        priority = "HIGH"
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Create mocks
        mockTaskDao = mockk()
        
        // Create repository with mocked DAO
        taskRepository = TaskRepository(mockTaskDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `insertTask should call DAO insert and return task ID`() = runTest {
        // Given
        val expectedId = 1L
        coEvery { mockTaskDao.insertTask(testTask) } returns expectedId
        
        // When
        val result = taskRepository.insertTask(testTask)
        
        // Then
        assertEquals("Should return inserted task ID", expectedId, result)
        coVerify { mockTaskDao.insertTask(testTask) }
    }

    @Test
    fun `getTaskById should call DAO and return task`() = runTest {
        // Given
        coEvery { mockTaskDao.getTaskById(testTask.id) } returns testTask
        
        // When
        val result = taskRepository.getTaskById(testTask.id)
        
        // Then
        assertEquals("Should return task from DAO", testTask, result)
        coVerify { mockTaskDao.getTaskById(testTask.id) }
    }

    @Test
    fun `getTaskById with non-existent ID should return null`() = runTest {
        // Given
        val nonExistentId = 999L
        coEvery { mockTaskDao.getTaskById(nonExistentId) } returns null
        
        // When
        val result = taskRepository.getTaskById(nonExistentId)
        
        // Then
        assertNull("Should return null for non-existent task", result)
        coVerify { mockTaskDao.getTaskById(nonExistentId) }
    }

    @Test
    fun `getTasksByUser should return flow of tasks`() = runTest {
        // Given
        val userId = 100L
        val taskList = listOf(testTask, testTask.copy(id = 2L, name = "Another Task"))
        val taskFlow = flow { emit(taskList) }
        
        every { mockTaskDao.getTasksByUser(userId) } returns taskFlow
        
        // When
        val result = taskRepository.getTasksByUser(userId).toList()
        
        // Then
        assertEquals("Should return task list from flow", listOf(taskList), result)
        verify { mockTaskDao.getTasksByUser(userId) }
    }

    @Test
    fun `updateTask should call DAO update`() = runTest {
        // Given
        val updatedTask = testTask.copy(name = "Updated Task")
        coEvery { mockTaskDao.updateTask(updatedTask) } just Runs
        
        // When
        taskRepository.updateTask(updatedTask)
        
        // Then
        coVerify { mockTaskDao.updateTask(updatedTask) }
    }

    @Test
    fun `deleteTask should call DAO delete`() = runTest {
        // Given
        coEvery { mockTaskDao.deleteTask(testTask) } just Runs
        
        // When
        taskRepository.deleteTask(testTask)
        
        // Then
        coVerify { mockTaskDao.deleteTask(testTask) }
    }

    @Test
    fun `getTasksByUserAndStatus should return filtered tasks`() = runTest {
        // Given
        val userId = 100L
        val isCompleted = true
        val completedTasks = listOf(testTask.copy(isCompleted = true))
        val taskFlow = flow { emit(completedTasks) }
        
        every { mockTaskDao.getTasksByUserAndStatus(userId, isCompleted) } returns taskFlow
        
        // When
        val result = taskRepository.getTasksByUserAndStatus(userId, isCompleted).toList()
        
        // Then
        assertEquals("Should return filtered tasks", listOf(completedTasks), result)
        verify { mockTaskDao.getTasksByUserAndStatus(userId, isCompleted) }
    }

    @Test
    fun `repository should handle DAO exceptions gracefully`() = runTest {
        // Given
        val exception = Exception("Database error")
        coEvery { mockTaskDao.getTaskById(any()) } throws exception
        
        // When & Then
        try {
            taskRepository.getTaskById(1L)
            fail("Should have thrown exception")
        } catch (e: Exception) {
            assertEquals("Should propagate DAO exception", exception, e)
        }
        
        coVerify { mockTaskDao.getTaskById(1L) }
    }

    @Test
    fun `multiple repository operations should work independently`() = runTest {
        // Given
        val task1 = testTask.copy(id = 1L)
        val task2 = testTask.copy(id = 2L, name = "Second Task")
        
        coEvery { mockTaskDao.insertTask(task1) } returns 1L
        coEvery { mockTaskDao.insertTask(task2) } returns 2L
        coEvery { mockTaskDao.getTaskById(1L) } returns task1
        coEvery { mockTaskDao.getTaskById(2L) } returns task2
        
        // When
        val insertResult1 = taskRepository.insertTask(task1)
        val insertResult2 = taskRepository.insertTask(task2)
        val getResult1 = taskRepository.getTaskById(1L)
        val getResult2 = taskRepository.getTaskById(2L)
        
        // Then
        assertEquals("First insert should work", 1L, insertResult1)
        assertEquals("Second insert should work", 2L, insertResult2)
        assertEquals("First get should work", task1, getResult1)
        assertEquals("Second get should work", task2, getResult2)
        
        coVerify { mockTaskDao.insertTask(task1) }
        coVerify { mockTaskDao.insertTask(task2) }
        coVerify { mockTaskDao.getTaskById(1L) }
        coVerify { mockTaskDao.getTaskById(2L) }
    }
}