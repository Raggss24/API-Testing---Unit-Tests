package com.example.StudentServiceTesting.service;

import com.example.StudentServiceTesting.dto.StudentRequest;
import com.example.StudentServiceTesting.dto.StudentResponse;
import com.example.StudentServiceTesting.entity.Student;
import com.example.StudentServiceTesting.exception.DuplicateResourceException;
import com.example.StudentServiceTesting.exception.ResourceNotFoundException;
import com.example.StudentServiceTesting.repository.StudentRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    @DisplayName("Should create student successfully when email does not exist")
    void shouldCreateStudentSuccessfully() {

        // Arrange
        StudentRequest request = new StudentRequest(
                "Rahul",
                "rahul@gmail.com",
                22,
                "Computer Science"
        );

        Student savedStudent = Student.builder()
                .id(1L)
                .name("Rahul")
                .email("rahul@gmail.com")
                .age(22)
                .course("Computer Science")
                .build();

        when(studentRepository.existsByEmail(request.email()))
                .thenReturn(false);

        when(studentRepository.save(any(Student.class)))
                .thenReturn(savedStudent);

        // Act
        StudentResponse response =
                studentService.createStudent(request);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Rahul", response.name());
        assertEquals("rahul@gmail.com", response.email());
        assertEquals(22, response.age());
        assertEquals("Computer Science", response.course());

        verify(studentRepository).existsByEmail(request.email());
        verify(studentRepository).save(any(Student.class));
    }
    
    @Test
    @DisplayName("Should throw DuplicateResourceException when creating student with existing email")
    void shouldThrowExceptionWhenCreatingStudentWithDuplicateEmail() {

        // Arrange
        StudentRequest request = new StudentRequest(
                "Rahul",
                "rahul@gmail.com",
                22,
                "Computer Science"
        );

        when(studentRepository.existsByEmail(request.email()))
                .thenReturn(true);

        // Act
        DuplicateResourceException exception =
                assertThrows(
                        DuplicateResourceException.class,
                        () -> studentService.createStudent(request)
                );

        // Assert
        assertEquals(
                "Student already exists with email: rahul@gmail.com",
                exception.getMessage()
        );

        verify(studentRepository).existsByEmail(request.email());

        verify(studentRepository, never())
                .save(any(Student.class));
    }
    
    @Test
    @DisplayName("Should return student when valid ID is provided")
    void shouldReturnStudentWhenValidIdProvided() {

        // Arrange
        Long studentId = 1L;

        Student student = Student.builder()
                .id(studentId)
                .name("Rahul")
                .email("rahul@gmail.com")
                .age(22)
                .course("Computer Science")
                .build();

        when(studentRepository.findById(studentId))
                .thenReturn(Optional.of(student));

        // Act
        StudentResponse response =
                studentService.getStudentById(studentId);

        // Assert
        assertNotNull(response);
        assertEquals(studentId, response.id());
        assertEquals("Rahul", response.name());
        assertEquals("rahul@gmail.com", response.email());

        verify(studentRepository).findById(studentId);
    }
    @Test
    @DisplayName("Should throw ResourceNotFoundException when student ID does not exist")
    void shouldThrowExceptionWhenStudentIdDoesNotExist() {

        // Arrange
        Long studentId = 100L;

        when(studentRepository.findById(studentId))
                .thenReturn(Optional.empty());

        // Act
        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> studentService.getStudentById(studentId)
                );

        // Assert
        assertEquals(
                "Student not found with id: 100",
                exception.getMessage()
        );

        verify(studentRepository).findById(studentId);
    }
    
    @Test
    @DisplayName("Should return all students successfully")
    void shouldReturnAllStudentsSuccessfully() {

        // Arrange
        Student student1 = Student.builder()
                .id(1L)
                .name("Rahul")
                .email("rahul@gmail.com")
                .age(22)
                .course("Computer Science")
                .build();

        Student student2 = Student.builder()
                .id(2L)
                .name("Priya")
                .email("priya@gmail.com")
                .age(21)
                .course("Information Science")
                .build();

        when(studentRepository.findAll())
                .thenReturn(List.of(student1, student2));

        // Act
        List<StudentResponse> responses =
                studentService.getAllStudents();

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());

        assertEquals("Rahul", responses.get(0).name());
        assertEquals("Priya", responses.get(1).name());

        verify(studentRepository).findAll();
    }
    
    @Test
    @DisplayName("Should return empty list when no students exist")
    void shouldReturnEmptyListWhenNoStudentsExist() {

        // Arrange
        when(studentRepository.findAll())
                .thenReturn(List.of());

        // Act
        List<StudentResponse> responses =
                studentService.getAllStudents();

        // Assert
        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(studentRepository).findAll();
    }
    @Test
    @DisplayName("Should update student successfully")
    void shouldUpdateStudentSuccessfully() {

        // Arrange
        Long studentId = 1L;

        Student existingStudent = Student.builder()
                .id(studentId)
                .name("Rahul")
                .email("rahul@gmail.com")
                .age(22)
                .course("Computer Science")
                .build();

        StudentRequest request = new StudentRequest(
                "Rahul Kumar",
                "rahulkumar@gmail.com",
                23,
                "Information Science"
        );

        when(studentRepository.findById(studentId))
                .thenReturn(Optional.of(existingStudent));

        when(studentRepository.findByEmail(request.email()))
                .thenReturn(Optional.empty());

        when(studentRepository.save(existingStudent))
                .thenReturn(existingStudent);

        // Act
        StudentResponse response =
                studentService.updateStudent(studentId, request);

        // Assert
        assertNotNull(response);

        assertEquals("Rahul Kumar", response.name());
        assertEquals("rahulkumar@gmail.com", response.email());
        assertEquals(23, response.age());
        assertEquals("Information Science", response.course());

        verify(studentRepository).findById(studentId);
        verify(studentRepository).findByEmail(request.email());
        verify(studentRepository).save(existingStudent);
    }
    
    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating nonexistent student")
    void shouldThrowExceptionWhenUpdatingNonexistentStudent() {

        // Arrange
        Long studentId = 100L;

        StudentRequest request = new StudentRequest(
                "Rahul",
                "rahul@gmail.com",
                22,
                "Computer Science"
        );

        when(studentRepository.findById(studentId))
                .thenReturn(Optional.empty());

        // Act
        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> studentService.updateStudent(
                                studentId,
                                request
                        )
                );

        // Assert
        assertEquals(
                "Student not found with id: 100",
                exception.getMessage()
        );

        verify(studentRepository).findById(studentId);

        verify(studentRepository, never())
                .save(any(Student.class));
    }
    @Test
    @DisplayName("Should throw DuplicateResourceException when updating with another student's email")
    void shouldThrowExceptionWhenUpdatingWithAnotherStudentsEmail() {

        // Arrange
        Long studentId = 1L;

        Student existingStudent = Student.builder()
                .id(1L)
                .name("Rahul")
                .email("rahul@gmail.com")
                .age(22)
                .course("Computer Science")
                .build();

        Student anotherStudent = Student.builder()
                .id(2L)
                .name("Priya")
                .email("priya@gmail.com")
                .age(21)
                .course("Information Science")
                .build();

        StudentRequest request = new StudentRequest(
                "Rahul",
                "priya@gmail.com",
                22,
                "Computer Science"
        );

        when(studentRepository.findById(studentId))
                .thenReturn(Optional.of(existingStudent));

        when(studentRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(anotherStudent));

        // Act
        DuplicateResourceException exception =
                assertThrows(
                        DuplicateResourceException.class,
                        () -> studentService.updateStudent(
                                studentId,
                                request
                        )
                );

        // Assert
        assertEquals(
                "Another student already exists with email: priya@gmail.com",
                exception.getMessage()
        );

        verify(studentRepository).findById(studentId);
        verify(studentRepository).findByEmail(request.email());

        verify(studentRepository, never())
                .save(any(Student.class));
    }
    
    @Test
    @DisplayName("Should allow student to keep same email during update")
    void shouldAllowStudentToKeepSameEmailDuringUpdate() {

        // Arrange
        Long studentId = 1L;

        Student existingStudent = Student.builder()
                .id(studentId)
                .name("Rahul")
                .email("rahul@gmail.com")
                .age(22)
                .course("Computer Science")
                .build();

        StudentRequest request = new StudentRequest(
                "Rahul Updated",
                "rahul@gmail.com",
                23,
                "Computer Science"
        );

        when(studentRepository.findById(studentId))
                .thenReturn(Optional.of(existingStudent));

        when(studentRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(existingStudent));

        when(studentRepository.save(existingStudent))
                .thenReturn(existingStudent);

        // Act
        StudentResponse response =
                studentService.updateStudent(studentId, request);

        // Assert
        assertNotNull(response);

        assertEquals(studentId, response.id());
        assertEquals("Rahul Updated", response.name());
        assertEquals("rahul@gmail.com", response.email());
        assertEquals(23, response.age());

        verify(studentRepository).save(existingStudent);
    }
    
    @Test
    @DisplayName("Should delete student successfully when student exists")
    void shouldDeleteStudentSuccessfully() {

        // Arrange
        Long studentId = 1L;

        Student student = Student.builder()
                .id(studentId)
                .name("Rahul")
                .email("rahul@gmail.com")
                .age(22)
                .course("Computer Science")
                .build();

        when(studentRepository.findById(studentId))
                .thenReturn(Optional.of(student));

        // Act
        studentService.deleteStudent(studentId);

        // Assert
        verify(studentRepository).findById(studentId);
        verify(studentRepository).delete(student);
    }
    
    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting nonexistent student")
    void shouldThrowExceptionWhenDeletingNonexistentStudent() {

        // Arrange
        Long studentId = 100L;

        when(studentRepository.findById(studentId))
                .thenReturn(Optional.empty());

        // Act
        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> studentService.deleteStudent(studentId)
                );

        // Assert
        assertEquals(
                "Student not found with id: 100",
                exception.getMessage()
        );

        verify(studentRepository).findById(studentId);

        verify(studentRepository, never())
                .delete(any(Student.class));
    }
}