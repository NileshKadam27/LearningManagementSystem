package com.example.LearningManagementSystem.service;

import com.example.LearningManagementSystem.bean.CourseBean;
import com.example.LearningManagementSystem.bean.CourseDetailsBean;
import com.example.LearningManagementSystem.entity.Course;
import com.example.LearningManagementSystem.entity.CourseCategory;
import com.example.LearningManagementSystem.entity.UserProfile;
import com.example.LearningManagementSystem.exception.EntityDataNotFound;
import com.example.LearningManagementSystem.repository.CourseCategoryRepository;
import com.example.LearningManagementSystem.repository.CourseRepository;
import com.example.LearningManagementSystem.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseCategoryRepository courseCategoryRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Override
    public List<CourseDetailsBean> getAllCourseDetails() {
        try {
            List<CourseCategory> courseCategoryList = courseCategoryRepository.findAll();
            if (CollectionUtils.isEmpty(courseCategoryList)) {
                return List.of();
            }

            List<Course> courseList = courseRepository.findByIsactive(1);

            Map<Long, UserProfile> userProfileMap = userProfileRepository.findAllById(
                    courseList.stream().map(Course::getUserprofilekey).collect(Collectors.toSet())
            ).stream().collect(Collectors.toMap(UserProfile::getId, userProfile -> userProfile));

            return courseCategoryList.stream().map(courseCategory -> {
                CourseDetailsBean courseDetailsBean = new CourseDetailsBean();
                courseDetailsBean.setCourseCategory(courseCategory.getCategoryname());

                List<CourseBean> courseBeanList = courseList.stream()
                        .filter(course -> course.getCoursecategorykey().equals(courseCategory.getId()))
                        .map(course -> {
                            CourseBean courseBean = new CourseBean();
                            UserProfile userProfile = userProfileMap.get(course.getUserprofilekey());
                            courseBean.setCourseId(course.getId());
                            courseBean.setCourseName(course.getCoursename());
                            courseBean.setProfessorName(
                                    userProfile != null ? userProfile.getFirstname() + " " + userProfile.getLastname() : "Unknown"
                            );
                            courseBean.setExperience(userProfile.getExperience());
                            return courseBean;
                        })
                        .collect(Collectors.toList());

                courseDetailsBean.setCourseDetailList(courseBeanList);
                return courseDetailsBean;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    @Override
    public CourseDetailsBean getCourseDetailsByCategory(Long catid) {
        try {
            Optional<CourseCategory> courseCategoryOptional = courseCategoryRepository.findById(catid);
            if (courseCategoryOptional.isPresent()) {
                List<Course> courseList = courseRepository.findByIsactive(1);

                Map<Long, UserProfile> userProfileMap = userProfileRepository.findAllById(
                        courseList.stream().map(Course::getUserprofilekey).collect(Collectors.toSet())
                ).stream().collect(Collectors.toMap(UserProfile::getId, userProfile -> userProfile));
                CourseCategory courseCategory = courseCategoryOptional.get();

                CourseDetailsBean courseDetailsBean = new CourseDetailsBean();
                courseDetailsBean.setCourseCategory(courseCategory.getCategoryname());
                List<CourseBean> courseDetailList = courseList.stream()
                        .filter(course -> course.getCoursecategorykey().equals(courseCategory.getId()))
                        .map(course -> {
                            CourseBean courseBean = new CourseBean();
                            UserProfile userProfile = userProfileMap.get(course.getUserprofilekey());
                            courseBean.setCourseId(course.getId());
                            courseBean.setCourseName(course.getCoursename());
                            courseBean.setExperience(userProfile.getExperience());
                            courseBean.setProfessorName(
                                    userProfile != null ? userProfile.getFirstname() + " " + userProfile.getLastname() : "Unknown"
                            );
                            return courseBean;
                        })
                        .collect(Collectors.toList());
                courseDetailsBean.setCourseDetailList(courseDetailList);
                return courseDetailsBean;
            } else {
                throw new EntityDataNotFound("Category doesn't exist");
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    @Override
    public CourseBean getCourseDetailsById(Long courseid) {
        CourseBean courseBean = new CourseBean();
        try {
            Course course = courseRepository.findByIdAndIsactive(courseid, 1);
            if (course != null) {
                courseBean.setCourseName(course.getCoursename());
                UserProfile userProfile = getUserDetails(course.getUserprofilekey());
                if (userProfile != null) {
                    courseBean.setProfessorName(userProfile.getFirstname() + " " + userProfile.getLastname());
                    courseBean.setExperience(userProfile.getExperience());
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return courseBean;
    }

    public UserProfile getUserDetails(Long userProfileKey) throws Exception {
        Optional<UserProfile> userProfileOptional = userProfileRepository.findById(userProfileKey);
        if (userProfileOptional.isPresent()) {
            return userProfileOptional.get();
        } else {
            throw new EntityDataNotFound("User doesn't exist");
        }
    }
}





