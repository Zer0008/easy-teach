package fr.cleanarchitecture.easyteach.course.e2eTests;

import fr.cleanarchitecture.easyteach.EasyTeachIntegrationTests;
import fr.cleanarchitecture.easyteach.course.application.ports.CourseRepository;
import fr.cleanarchitecture.easyteach.course.domain.model.Course;
import fr.cleanarchitecture.easyteach.course.domain.model.Module;
import fr.cleanarchitecture.easyteach.course.domain.model.Teacher;
import fr.cleanarchitecture.easyteach.course.domain.valueobject.Price;
import fr.cleanarchitecture.easyteach.course.infrastructure.spring.RemoveModuleFromCourseDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

@RunWith(SpringRunner.class)
public class RemoveModuleFromCourseE2ETest extends EasyTeachIntegrationTests {

    @Autowired
    private CourseRepository courseRepository;

    @Test
    public void removeModuleFromCourseE2ETest() throws Exception {
        var course = new Course(
                "Title",
                "Description",
                new Teacher(),
                new Price(BigDecimal.ZERO, "FCFA")
        );
        var module = new Module(
                "title",
                "description",
                2
        );
        course.addModule(module);
        courseRepository.save(course);

        var dto = new RemoveModuleFromCourseDto(module.getModuleId());

        mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/courses/" +course.getCourseId()+ "/remove-module-from-course")
                                .content(objectMapper.writeValueAsBytes(dto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        var existingCourse = courseRepository.findByCourseId(course.getCourseId());

        Assert.assertTrue(existingCourse.isPresent());
        Assert.assertEquals(0, existingCourse.get().getModules().size());
    }

    @Test
    public void removeModuleFromNotExistCourseE2ETest_shouldThrowException() throws Exception {
        var course = new Course(
                "Title",
                "Description",
                new Teacher(),
                new Price(BigDecimal.ZERO, "FCFA")
        );

        var dto = new RemoveModuleFromCourseDto("moduleId");

        mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/courses/" +course.getCourseId()+ "/remove-module-from-course")
                                .content(objectMapper.writeValueAsBytes(dto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void removeNotLinkedModuleFromCourseE2ETest_shouldThrowException() throws Exception {
        var course = new Course(
                "Title",
                "Description",
                new Teacher(),
                new Price(BigDecimal.ZERO, "FCFA")
        );
        courseRepository.save(course);
        var module = new Module(
                "title",
                "description",
                1
        );
        var dto = new RemoveModuleFromCourseDto(module.getModuleId());

        mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/courses/" +course.getCourseId()+ "/remove-module-from-course")
                                .content(objectMapper.writeValueAsBytes(dto))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
