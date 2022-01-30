package edu.sombra.cms.service.impl;

import edu.sombra.cms.domain.dto.StudentDTO;
import edu.sombra.cms.domain.entity.Student;
import edu.sombra.cms.domain.entity.User;
import edu.sombra.cms.domain.mapper.StudentMapper;
import edu.sombra.cms.domain.payload.StudentData;
import edu.sombra.cms.repository.StudentRepository;
import edu.sombra.cms.service.StudentService;
import edu.sombra.cms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final UserService userService;

    @Override
    public Student getById(Long id) {
        return studentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Student not found"));
    }

    @Override
    public List<Student> getByIdList(List<Long> ids) {
        return Optional.of(studentRepository.findAllById(ids)).filter(o -> !o.isEmpty())
                .orElseThrow(() -> new IllegalArgumentException("Students not found"));
    }

    @Override
    public StudentDTO create(StudentData studentData, Long userId) {
        var user = getStudentUser(userId);

        if(user.getStudent() != null){
            throw new IllegalArgumentException("Student info is already created");
        }

        Student student = new Student();

        student.setFirstName(studentData.getFirstName());
        student.setLastName(studentData.getLastName());
        student.setEmail(studentData.getEmail());
        student.setUser(user);

        return studentMapper.to(studentRepository.save(student));
    }

    private User getStudentUser(Long userId) {
        if(userService.getLoggedUser().isAdmin()){
            if(userId == null)
                throw new IllegalArgumentException("Unable to create Student, userId is null");

            return userService.findUserById(userId);
        }

        return userService.getLoggedUser();
    }
}
