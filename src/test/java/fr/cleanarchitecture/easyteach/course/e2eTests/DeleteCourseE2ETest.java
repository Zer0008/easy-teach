package fr.cleanarchitecture.easyteach.course.e2eTests;

import fr.cleanarchitecture.easyteach.EasyTeachIntegrationTests;
import fr.cleanarchitecture.easyteach.course.application.ports.CourseRepository;
import fr.cleanarchitecture.easyteach.course.domain.enums.ResourceType;
import fr.cleanarchitecture.easyteach.course.domain.model.Course;
import fr.cleanarchitecture.easyteach.course.domain.model.Lesson;
import fr.cleanarchitecture.easyteach.course.domain.model.Module;
import fr.cleanarchitecture.easyteach.course.domain.model.Teacher;
import fr.cleanarchitecture.easyteach.course.domain.valueobject.Price;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

@RunWith(SpringRunner.class)
public class DeleteCourseE2ETest extends EasyTeachIntegrationTests {
    @Autowired
    private CourseRepository courseRepository;
    private Course course;

    @Before
    public void setUp() {
        course = new Course(
                "title",
                "description",
                new Teacher(),
                new Price(BigDecimal.ZERO, "FCFA")
        );
        courseRepository.save(course);
    }

    @Test
    public void deleteCourseTest() throws Exception {
        mockMvc
            .perform(MockMvcRequestBuilders.delete("/courses/" + course.getCourseId()))
            .andExpect(MockMvcResultMatchers.status().isNoContent());
        var deletedCourse = courseRepository.findByCourseId(course.getCourseId());
        Assert.assertTrue(deletedCourse.isEmpty());
    }

    @Test
    public void deleteUnexistingCourseTest_shouldThrow() throws Exception {
        mockMvc
            .perform(MockMvcRequestBuilders.delete("/courses/Garbage"))
            .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void deleteExistingCourseWithStatusPublishedTest_shouldThrow() throws Exception {
        var module = new Module("moduleTitle", "moduleDescription",1);
        course.addModule(module);
        course.addLessonToModule(
                module.getModuleId(),
                new Lesson("lessonTitle", ResourceType.IMAGES, "Images", "textContent", 1));
        course.publish();
        courseRepository.save(course);
        mockMvc
            .perform(MockMvcRequestBuilders.delete("/courses/" + course.getCourseId()))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
