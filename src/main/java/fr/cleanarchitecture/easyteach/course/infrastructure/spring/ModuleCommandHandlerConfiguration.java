package fr.cleanarchitecture.easyteach.course.infrastructure.spring;

import fr.cleanarchitecture.easyteach.course.application.ports.CourseRepository;
import fr.cleanarchitecture.easyteach.course.application.ports.ModuleRepository;
import fr.cleanarchitecture.easyteach.course.application.usecases.module.LinkModuleToCourseCommandHandler;
import fr.cleanarchitecture.easyteach.course.application.usecases.module.UnLinkModuleToCourseCommandHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModuleCommandHandlerConfiguration {

    @Bean
    public LinkModuleToCourseCommandHandler linkModuleToCourseCommandHandler(CourseRepository courseRepository, ModuleRepository moduleRepository) {
        return new LinkModuleToCourseCommandHandler(courseRepository, moduleRepository);
    }

    @Bean
    public UnLinkModuleToCourseCommandHandler unLinkModuleToCourseCommandHandler(CourseRepository courseRepository, ModuleRepository moduleRepository) {
        return new UnLinkModuleToCourseCommandHandler(courseRepository, moduleRepository);
    }
}
