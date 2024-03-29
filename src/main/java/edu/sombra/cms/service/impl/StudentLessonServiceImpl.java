package edu.sombra.cms.service.impl;

import edu.sombra.cms.domain.dto.StudentLessonDTO;
import edu.sombra.cms.domain.entity.Lesson;
import edu.sombra.cms.domain.entity.Student;
import edu.sombra.cms.domain.entity.StudentLesson;
import edu.sombra.cms.domain.mapper.StudentLessonMapper;
import edu.sombra.cms.domain.payload.EvaluateLessonData;
import edu.sombra.cms.domain.payload.HomeworkData;
import edu.sombra.cms.messages.SomethingWentWrongException;
import edu.sombra.cms.repository.StudentLessonRepository;
import edu.sombra.cms.service.HomeworkUploadService;
import edu.sombra.cms.service.StudentLessonService;
import edu.sombra.cms.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static edu.sombra.cms.messages.StudentLessonMessage.*;

@Service
@Validated
@RequiredArgsConstructor
public class StudentLessonServiceImpl implements StudentLessonService {

    private final StudentLessonRepository studentLessonRepository;
    private final StudentService studentService;
    private final StudentLessonMapper studentLessonMapper;
    private final HomeworkUploadService homeworkUploadService;

    private static final Logger LOGGER = LoggerFactory.getLogger(StudentLessonServiceImpl.class);

    @Override
    @Transactional(rollbackFor = SomethingWentWrongException.class)
    public StudentLesson getByStudentAndLesson(Long studentId, Long lessonId) throws SomethingWentWrongException {
        return studentLessonRepository.findStudentLessonByStudentIdAndLessonId(studentId, lessonId)
                .orElseThrow(NOT_FOUND::ofException);
    }

    @Override
    @Transactional(rollbackFor = SomethingWentWrongException.class)
    public StudentLesson getByLessonId(Long lessonId) throws SomethingWentWrongException {
        var student = studentService.getLoggedStudent();

        return getByStudentAndLesson(student.getId(), lessonId);
    }

    @Override
    @Transactional(rollbackFor = SomethingWentWrongException.class)
    public StudentLessonDTO getDTOByLessonId(Long lessonId) throws SomethingWentWrongException {
        StudentLesson studentLesson = getByLessonId(lessonId);

        return studentLessonMapper.to(studentLesson.getStudent().getId(), studentLesson.getLesson().getId());
    }

    @Override
    @Transactional
    public void saveStudentLessons(List<Lesson> lessons, Student student) {
        var studentLessons = lessons.stream().map(l -> new StudentLesson(student, l)).collect(Collectors.toList());
        studentLessonRepository.saveAll(studentLessons);

        LOGGER.info("Created lessons for student with id: {}", student.getId());
    }

    @Override
    @Transactional
    public void saveStudentLessons(Lesson lesson, List<Student> students) {
        var studentLessons = students.stream().map(s -> new StudentLesson(s, lesson)).collect(Collectors.toList());
        studentLessonRepository.saveAll(studentLessons);

        LOGGER.info("Created lesson with id: {} for all related students", lesson.getId());
    }

    @Override
    @Transactional(rollbackFor = SomethingWentWrongException.class)
    public void evaluate(Long lessonId, @Valid EvaluateLessonData evaluateLessonData) throws SomethingWentWrongException {
        var studentLesson = Optional.of(getByStudentAndLesson(evaluateLessonData.getStudentId(), lessonId))
                .filter(StudentLesson::canBeEvaluated).orElseThrow(HOMEWORK_IS_NOT_UPLOADED::ofException);

        if(studentLesson.getMark() == null){
            studentLesson.setMark(evaluateLessonData.getMark());
            studentLesson.setFeedback(evaluateLessonData.getFeedback());
            studentLessonRepository.save(studentLesson);
        }

        LOGGER.info("Evaluated lesson with id: {} for student with id: {}, mark: {}", lessonId, evaluateLessonData.getStudentId(), evaluateLessonData.getMark());
    }

    @Override
    @Transactional(rollbackFor = SomethingWentWrongException.class)
    public void addHomework(Long lessonId, HomeworkData homeworkData, MultipartFile homeworkFile) throws SomethingWentWrongException {
        var studentLesson = getByLessonId(lessonId);
        var s3file = homeworkUploadService.uploadStudentHomework(studentLesson.getStudent(), homeworkFile).orElseThrow(UNABLE_TO_UPLOAD_HOMEWORK::ofException);

        studentLesson.addHomework(s3file);
        studentLesson.setNotes(homeworkData.getNote());

        studentLessonRepository.save(studentLesson);
        LOGGER.info("Added homework to lesson with id: {} for student with id: {}", lessonId, studentLesson.getStudent().getId());
    }
}
