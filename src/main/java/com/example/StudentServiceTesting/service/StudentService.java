package com.example.StudentServiceTesting.service;

import com.example.StudentServiceTesting.dto.StudentRequest;
import com.example.StudentServiceTesting.dto.StudentResponse;
import com.example.StudentServiceTesting.entity.Student;
import com.example.StudentServiceTesting.exception.DuplicateResourceException;
import com.example.StudentServiceTesting.exception.ResourceNotFoundException;
import com.example.StudentServiceTesting.repository.StudentRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // CREATE
    public StudentResponse createStudent(StudentRequest request) {

        if (studentRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException(
                    "Student already exists with email: " + request.email()
            );
        }

        Student student = Student.builder()
                .name(request.name())
                .email(request.email())
                .age(request.age())
                .course(request.course())
                .build();

        Student savedStudent = studentRepository.save(student);

        return mapToResponse(savedStudent);
    }

    // GET BY ID
    public StudentResponse getStudentById(Long id) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with id: " + id
                        )
                );

        return mapToResponse(student);
    }

    // GET ALL
    public List<StudentResponse> getAllStudents() {

        return studentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // UPDATE
    public StudentResponse updateStudent(
            Long id,
            StudentRequest request) {

        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with id: " + id
                        )
                );

        studentRepository.findByEmail(request.email())
                .filter(student -> !student.getId().equals(id))
                .ifPresent(student -> {
                    throw new DuplicateResourceException(
                            "Another student already exists with email: "
                                    + request.email()
                    );
                });

        existingStudent.setName(request.name());
        existingStudent.setEmail(request.email());
        existingStudent.setAge(request.age());
        existingStudent.setCourse(request.course());

        Student updatedStudent =
                studentRepository.save(existingStudent);

        return mapToResponse(updatedStudent);
    }

    // DELETE
    public void deleteStudent(Long id) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with id: " + id
                        )
                );

        studentRepository.delete(student);
    }

    // ENTITY -> RESPONSE DTO
    private StudentResponse mapToResponse(Student student) {

        return new StudentResponse(
                student.getId(),
                student.getName(),
                student.getEmail(),
                student.getAge(),
                student.getCourse()
        );
    }
}