package fr.cleanarchitecture.easyteach.course.usecase;

import fr.cleanarchitecture.easyteach.course.application.ports.CourseRepository;
import fr.cleanarchitecture.easyteach.course.application.usecases.commands.PublishCourseCommand;
import fr.cleanarchitecture.easyteach.course.application.usecases.handlers.PublishCourseCommandHandler;
import fr.cleanarchitecture.easyteach.course.domain.enums.CourseStatus;
import fr.cleanarchitecture.easyteach.course.domain.enums.ResourceType;
import fr.cleanarchitecture.easyteach.course.domain.model.Course;
import fr.cleanarchitecture.easyteach.course.domain.model.Lesson;
import fr.cleanarchitecture.easyteach.course.domain.model.Module;
import fr.cleanarchitecture.easyteach.course.domain.model.Teacher;
import fr.cleanarchitecture.easyteach.course.domain.valueobject.Price;
import fr.cleanarchitecture.easyteach.course.infrastructure.persistence.inmemory.InMemoryCourseRepository;
import fr.cleanarchitecture.easyteach.shared.domain.exceptions.NotFoundException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

public class PublishCourseTest {

    private CourseRepository courseRepository = new InMemoryCourseRepository();
    private Course course;

    @Before
    public void setUp() {
        course = new Course(
                "course title",
                "course description",
                new Teacher(),
                new Price(BigDecimal.valueOf(1000), "FCFA")
        );
    }

    @Test
    public void publishCourseTest() {
        var module = new Module("moduleTitle", "moduleDescription",1);
        course.addModule(module);
        course.addLessonToModule(
                module.getModuleId(),
                new Lesson("lessonTitle", ResourceType.IMAGES, "Images", "textContent", 1)
        );
        courseRepository.save(course);

        var publishCourseCommand = new PublishCourseCommand(course.getCourseId());
        var publishCourseCommandHandler = new PublishCourseCommandHandler(courseRepository);

        var result = publishCourseCommandHandler.handle(publishCourseCommand);

        Assert.assertEquals(CourseStatus.PUBLISHED, result.getData().getStatus());
    }

    @Test
    public void publishUnExistingCourseTest_shouldThrowException() {
        var publishCourseCommand = new PublishCourseCommand(course.getCourseId());
        var publishCourseCommandHandler = new PublishCourseCommandHandler(courseRepository);

        Assert.assertThrows(
                "Course not found",
                NotFoundException.class,
                () -> publishCourseCommandHandler.handle(publishCourseCommand)
        );
    }
}
