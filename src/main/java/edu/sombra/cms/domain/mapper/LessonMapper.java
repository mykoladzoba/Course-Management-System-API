package edu.sombra.cms.domain.mapper;

import edu.sombra.cms.domain.dto.LessonDTO;
import edu.sombra.cms.domain.entity.Lesson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LessonMapper extends AbstractMapper<Lesson, LessonDTO> {

    private final StudentHomeworkMapper studentHomeworkMapper;

    public LessonDTO to(Lesson lesson){
        LessonDTO lessonDTO = new LessonDTO();

        lessonDTO.setId(lesson.getId());
        lessonDTO.setName(lesson.getName());
        lessonDTO.setDescription(lesson.getDescription());
        lessonDTO.setHomework(lesson.getHomework());

        lessonDTO.setStudentHomework(studentHomeworkMapper.toList(lesson.getStudentLessons()));

        return lessonDTO;
    }
}