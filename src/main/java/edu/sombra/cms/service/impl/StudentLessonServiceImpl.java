package edu.sombra.cms.service.impl;

import edu.sombra.cms.domain.dto.StudentLessonDTO;
import edu.sombra.cms.domain.entity.Lesson;
import edu.sombra.cms.domain.entity.Student;
import edu.sombra.cms.domain.entity.StudentLesson;
import edu.sombra.cms.domain.entity.User;
import edu.sombra.cms.domain.mapper.StudentLessonMapper;
import edu.sombra.cms.domain.payload.EvaluateLessonData;
import edu.sombra.cms.messages.SomethingWentWrongException;
import edu.sombra.cms.repository.StudentLessonRepository;
import edu.sombra.cms.service.StudentLessonService;
import edu.sombra.cms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static edu.sombra.cms.messages.StudentLessonMessage.NOT_FOUND;
import static edu.sombra.cms.messages.StudentMessage.USER_NOT_STUDENT;

@Service
@Validated
@RequiredArgsConstructor
public class StudentLessonServiceImpl implements StudentLessonService {

    private final StudentLessonRepository studentLessonRepository;
    private final UserService userService;
    private final StudentLessonMapper lessonMapper;

    @Override
    public StudentLesson getByStudentAndLesson(Long studentId, Long lessonId) throws SomethingWentWrongException {
        return studentLessonRepository.findStudentLessonByStudentIdAndLessonId(studentId, lessonId)
                .orElseThrow(NOT_FOUND::ofException);
    }

    @Override
    public StudentLessonDTO getDTOByLessonId(Long lessonId) throws SomethingWentWrongException {
        var student = Optional.of(userService.getLoggedUser())
                .filter(User::isStudent).map(User::getStudent).orElseThrow(USER_NOT_STUDENT::ofException);

        var studentLesson = getByStudentAndLesson(student.getId(), lessonId);

        return lessonMapper.to(studentLesson);
    }

    @Override
    public void saveStudentLessons(List<Lesson> lessons, Student student) {
        var studentLessons = lessons.stream().map(l -> new StudentLesson(student, l)).collect(Collectors.toList());
        studentLessonRepository.saveAll(studentLessons);
    }

    @Override
    public void saveStudentLessons(Lesson lesson, List<Student> students) {
        var studentLessons = students.stream().map(s -> new StudentLesson(s, lesson)).collect(Collectors.toList());
        studentLessonRepository.saveAll(studentLessons);
    }

    @Override
    public void evaluate(Long lessonId, @Valid EvaluateLessonData evaluateLessonData) throws SomethingWentWrongException {
        var studentLesson = getByStudentAndLesson(evaluateLessonData.getStudentId(), lessonId);

        if(studentLesson.getMark() == null && studentLesson.getHomeworkFile() != null){
            studentLesson.setMark(evaluateLessonData.getMark());
            studentLesson.setFeedback(evaluateLessonData.getFeedback());
            studentLessonRepository.save(studentLesson);
        }
    }

}
