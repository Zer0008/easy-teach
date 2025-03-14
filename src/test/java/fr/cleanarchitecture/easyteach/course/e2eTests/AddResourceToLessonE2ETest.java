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
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class AddResourceToLessonE2ETest extends EasyTeachIntegrationTests {

    private static final String UPLOAD_DIR = "/Users/nazam/Desktop/Projects/easy-teach/uploaded-resources";

    @Autowired
    private CourseRepository courseRepository;

    private Course course;
    private Module module;
    private Lesson lesson;

    @Before
    public void setUp() throws Exception {
        course = new Course(
                "courseTitle",
                "courseDescription",
                new Teacher(),
                new Price(BigDecimal.ZERO, "FCFA"));
        module = new Module("moduleTitle", "moduleDescription", 1);
        lesson = new Lesson("lessonTitle", ResourceType.IMAGES, null, "textContent", 1);
        course.addModule(module);
        course.addLessonToModule(module.getModuleId(), lesson);
        courseRepository.save(course);
    }

    @Test
    public void shouldAddResourceToLessonTest() throws Exception {
        MockMultipartFile pdfFile = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "PDF content".getBytes()
        );

        mockMvc.perform(multipart(
                    HttpMethod.POST,
                    "/courses/{courseId}/modules/{moduleId}/lessons/{lessonId}/resources",
                    course.getCourseId(), module.getModuleId(), lesson.getLessonId())
                        .file(pdfFile)
                        .param("type", "DOCUMENTS"))
                .andExpect(status().isOk());

        var course = courseRepository.findByCourseId(this.course.getCourseId()).orElseThrow();
        var resources = course.getModules()
                .stream()
                .filter(module1 -> module1.getModuleId().equals(module.getModuleId()))
                .findFirst()
                .orElseThrow()
                .getLessons()
                .stream()
                .filter(lesson1 -> lesson1.getLessonId().equals(lesson.getLessonId()))
                .findFirst()
                .orElseThrow()
                .getResources();
        var newResource = resources.get(0);
        Assert.assertEquals(
                UPLOAD_DIR + "/documents/" + newResource.getResourceName(),
                newResource.getResourceUrl());
    }

    @Test
    public void addResourceWithNoFileToLessonTest_shouldThrowException() throws Exception {
        mockMvc.perform(multipart(
                        HttpMethod.POST,
                        "/courses/{courseId}/modules/{moduleId}/lessons/{lessonId}/resources",
                        course.getCourseId(), module.getModuleId(), lesson.getLessonId()))
                .andExpect(status().isBadRequest());
    }
}
