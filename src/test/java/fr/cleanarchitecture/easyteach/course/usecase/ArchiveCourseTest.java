package fr.cleanarchitecture.easyteach.course.usecase;

import fr.cleanarchitecture.easyteach.authentication.application.ports.UserRepository;
import fr.cleanarchitecture.easyteach.authentication.domain.model.User;
import fr.cleanarchitecture.easyteach.authentication.infrastructure.persistence.inmemory.InMemoryUserRepository;
import fr.cleanarchitecture.easyteach.core.domain.exceptions.BadRequestException;
import fr.cleanarchitecture.easyteach.core.domain.exceptions.NotFoundException;
import fr.cleanarchitecture.easyteach.course.application.ports.CourseRepository;
import fr.cleanarchitecture.easyteach.course.application.usecases.commands.ArchiveCourseCommand;
import fr.cleanarchitecture.easyteach.course.application.usecases.handlers.ArchiveCourseCommandHandler;
import fr.cleanarchitecture.easyteach.course.domain.enums.ResourceType;
import fr.cleanarchitecture.easyteach.course.domain.model.Course;
import fr.cleanarchitecture.easyteach.course.domain.model.Lesson;
import fr.cleanarchitecture.easyteach.course.domain.model.Module;
import fr.cleanarchitecture.easyteach.course.domain.model.Teacher;
import fr.cleanarchitecture.easyteach.course.domain.valueobject.Price;
import fr.cleanarchitecture.easyteach.course.infrastructure.persistence.inmemory.InMemoryCourseRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

public class ArchiveCourseTest {

    private final CourseRepository courseRepository = new InMemoryCourseRepository();
    private final UserRepository userRepository = new InMemoryUserRepository();
    private Course course;
    private User user;
    private Lesson lesson;

    @Before
    public void setUp() throws Exception {
        user = new User();
        userRepository.save(user);
        course = new Course(
                "Course title",
                "Course description",
                new Teacher(),
                new Price(BigDecimal.ZERO, "FCFA")
        );
        lesson = new Lesson("lessonTitle", ResourceType.IMAGES, null, "textContent", 1);
    }

    @Test
    public void archiveCourseTest() {
        var module = new Module("moduleTitle", "moduleDescriptio", 1);
        course.addModule(module);
        course.addLessonToModule(module.getModuleId(), lesson);
        course.publish();
        courseRepository.save(course);

        var archiveCourseCommand = new ArchiveCourseCommand(course.getCourseId());
        var archiveCourseCommandHandler = new ArchiveCourseCommandHandler(courseRepository);

        var result = archiveCourseCommandHandler.handle(archiveCourseCommand);

        var archivedCourse = courseRepository.findByCourseId(course.getCourseId());

        Assert.assertTrue(archivedCourse.isPresent());
        Assert.assertEquals(result.getCourse().getStatus(), archivedCourse.get().getStatus());
    }

    @Test
    public void archiveUnExistingCourseTest_shouldThrowException() {
        var archiveCourseCommand = new ArchiveCourseCommand(course.getCourseId());
        var archiveCourseCommandHandler = new ArchiveCourseCommandHandler(courseRepository);
        Assert.assertThrows(
                "Course not found",
                NotFoundException.class,
                () -> archiveCourseCommandHandler.handle(archiveCourseCommand)
        );
    }

    @Test
    public void archiveNotPublishedCourseTest_shouldThrowException() {
        courseRepository.save(course);
        var archiveCourseCommand = new ArchiveCourseCommand(course.getCourseId());
        var archiveCourseCommandHandler = new ArchiveCourseCommandHandler(courseRepository);
        Assert.assertThrows(
                "The status of the course is not published",
                BadRequestException.class,
                () -> archiveCourseCommandHandler.handle(archiveCourseCommand)
        );
    }
}
