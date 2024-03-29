package edu.sombra.cms.service.impl;

import edu.sombra.cms.domain.dto.LessonDTO;
import edu.sombra.cms.domain.dto.OverviewPageDTO;
import edu.sombra.cms.domain.entity.Course;
import edu.sombra.cms.domain.entity.Instructor;
import edu.sombra.cms.domain.entity.Lesson;
import edu.sombra.cms.domain.mapper.ComingLessonMapper;
import edu.sombra.cms.domain.mapper.LessonMapper;
import edu.sombra.cms.domain.payload.LessonData;
import edu.sombra.cms.messages.SomethingWentWrongException;
import edu.sombra.cms.repository.LessonRepository;
import edu.sombra.cms.service.CourseService;
import edu.sombra.cms.service.InstructorService;
import edu.sombra.cms.service.LessonService;
import edu.sombra.cms.service.StudentLessonService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static edu.sombra.cms.messages.LessonMessage.NOT_COURSE_INSTRUCTOR;
import static edu.sombra.cms.messages.LessonMessage.NOT_FOUND;

@Service
@Validated
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;
    @Lazy
    private final CourseService courseService;
    private final InstructorService instructorService;
    private final StudentLessonService saveStudentLessons;
    private final ComingLessonMapper comingLessonMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(LessonServiceImpl.class);

    @Override
    @Transactional(rollbackFor = SomethingWentWrongException.class)
    public Lesson getById(Long id) throws SomethingWentWrongException {
        return lessonRepository.findById(id).orElseThrow(NOT_FOUND::ofException);
    }

    @Override
    @Transactional(rollbackFor = SomethingWentWrongException.class)
    public LessonDTO getDTOById(Long id) throws SomethingWentWrongException {
        var lesson = getById(id);

        return lessonMapper.to(lesson.getId());
    }

    @Override
    @Transactional(rollbackFor = SomethingWentWrongException.class)
    public LessonDTO create(@Valid LessonData lessonData) throws SomethingWentWrongException {
        Lesson lesson = new Lesson();

        lesson.setName(lessonData.getName());
        lesson.setDescription(lessonData.getDescription());
        lesson.setHomework(lessonData.getHomework());
        lesson.setHomeworkFinishDate(lessonData.getHomeworkDate());
        lesson.setLessonDate(lessonData.getDate());

        lesson.setCourse(courseService.getById(lessonData.getCourseId()));
        lesson.setInstructor(getInstructor(lessonData.getCourseId(), lessonData.getInstructorId()));

        lessonRepository.save(lesson);
        saveStudentLessons.saveStudentLessons(lesson, lesson.getCourse().getStudents());

        LOGGER.info("Created Lesson with id: {}", lesson.getId());
        return lessonMapper.to(lesson.getId());
    }

    @Override
    @Transactional(rollbackFor = SomethingWentWrongException.class)
    public LessonDTO update(Long id, @Valid LessonData lessonData) {
        return null;
    }

    public Instructor getInstructor(Long courseId, Long instructorId) throws SomethingWentWrongException {
        Course course = courseService.getById(courseId);
        Instructor instructor = instructorService.getById(instructorId);

        if(!course.getInstructors().contains(instructor)){
            throw NOT_COURSE_INSTRUCTOR.ofException();
        }

        return instructor;
    }

    @Override
    @Transactional(rollbackFor = SomethingWentWrongException.class)
    public List<OverviewPageDTO.ComingLessonDTO> comingLessons() throws SomethingWentWrongException {
        var comingLessons = lessonRepository.findAll().stream().filter(Lesson::isComing).collect(Collectors.toList());

        return comingLessonMapper.toList(comingLessons);
    }

}
