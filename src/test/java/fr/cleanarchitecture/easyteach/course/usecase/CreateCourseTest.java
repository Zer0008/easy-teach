package fr.cleanarchitecture.easyteach.course.usecase;

import fr.cleanarchitecture.easyteach.authentication.application.ports.UserRepository;
import fr.cleanarchitecture.easyteach.authentication.domain.model.User;
import fr.cleanarchitecture.easyteach.authentication.infrastructure.persistence.inmemory.InMemoryUserRepository;
import fr.cleanarchitecture.easyteach.course.application.ports.CourseRepository;
import fr.cleanarchitecture.easyteach.course.application.usecases.commands.CreateCourseCommand;
import fr.cleanarchitecture.easyteach.course.application.usecases.handlers.CreateCourseCommandHandler;
import fr.cleanarchitecture.easyteach.course.domain.enums.CourseStatus;
import fr.cleanarchitecture.easyteach.course.domain.valueobject.Price;
import fr.cleanarchitecture.easyteach.course.infrastructure.persistence.inmemory.InMemoryCourseRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

public class CreateCourseTest {

    private final CourseRepository courseRepository = new InMemoryCourseRepository();
    private final UserRepository userRepository = new InMemoryUserRepository();

    private User user;

    @Before
    public void setUp() {
        user = new User();
        userRepository.save(user);
    }

    @Test
    public void shouldCreateCourse() {
        var createCourseCommand = createCommand();
        var createCourseCommandHandler = createHandler();

        var result = createCourseCommandHandler.handle(createCourseCommand);

        var newCourseCreated = courseRepository.findByCourseId(result.getCourse().getCourseId());

        Assert.assertTrue(newCourseCreated.isPresent());
        Assert.assertEquals(result.getCourse().getCourseId(), newCourseCreated.get().getCourseId());
        Assert.assertEquals(CourseStatus.DRAFT, newCourseCreated.get().getStatus());
    }

    @Test
    public void createCourseIfCourseAlreadyExists_shouldThrowException() {
        var createCourseCommand = createCommand();
        var createCourseCommandHandler = createHandler();

        createCourseCommandHandler.handle(createCourseCommand);

        var throwValue = Assert.assertThrows(
                IllegalArgumentException.class,
                () -> createCourseCommandHandler.handle(createCourseCommand)
        );
        Assert.assertEquals("Course already exists", throwValue.getMessage());
    }

    private CreateCourseCommand createCommand() {
        return new CreateCourseCommand(
                "course name",
                "course description",
                user.getUserId(),
                new Price(BigDecimal.valueOf(1000), "FCFA"));
    }

    private CreateCourseCommandHandler createHandler() {
        return new CreateCourseCommandHandler(courseRepository, userRepository);
    }
}
