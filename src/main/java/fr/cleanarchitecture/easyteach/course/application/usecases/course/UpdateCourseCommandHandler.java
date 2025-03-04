package fr.cleanarchitecture.easyteach.course.application.usecases.course;

import an.awesome.pipelinr.Command;
import fr.cleanarchitecture.easyteach.core.domain.exceptions.NotFoundException;
import fr.cleanarchitecture.easyteach.course.application.ports.CourseRepository;
import fr.cleanarchitecture.easyteach.course.domain.viewmodel.CourseViewModel;

public class UpdateCourseCommandHandler implements Command.Handler<UpdateCourseCommand, CourseViewModel> {

    private final CourseRepository courseRepository;

    public UpdateCourseCommandHandler(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public CourseViewModel handle(UpdateCourseCommand updateCourseCommand) {
        var existingCourse = courseRepository.findByCourseId(updateCourseCommand.getCourseId());
        if (existingCourse.isEmpty()) {
            throw new NotFoundException("Course not found");
        }
        existingCourse.get().changeTitle(updateCourseCommand.getCourseTitle());
        existingCourse.get().changeDescription(updateCourseCommand.getCourseDescription());
        var course = existingCourse.get().changePrice(updateCourseCommand.getPrice());
        this.courseRepository.save(course);
        return new CourseViewModel(
                "Your course was successfully updated",
                course
        );
    }
}
