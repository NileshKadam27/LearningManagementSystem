package com.example.LearningManagementSystem.service;

import com.example.LearningManagementSystem.bean.CourseBean;
import com.example.LearningManagementSystem.bean.CourseDetailsBean;
import com.example.LearningManagementSystem.bean.VideoBean;
import com.example.LearningManagementSystem.bean.VideoDetailsBean;
import com.example.LearningManagementSystem.entity.Course;
import com.example.LearningManagementSystem.entity.CourseCategory;
import com.example.LearningManagementSystem.entity.UserProfile;
import com.example.LearningManagementSystem.entity.VideoDetails;
import com.example.LearningManagementSystem.exception.EntityDataNotFound;
import com.example.LearningManagementSystem.repository.CourseCategoryRepository;
import com.example.LearningManagementSystem.repository.CourseRepository;
import com.example.LearningManagementSystem.repository.UserProfileRepository;
import com.example.LearningManagementSystem.repository.VideoDetailsRepository;

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
    
    @Autowired
    private VideoDetailsRepository videoDetailsRepository;

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

	@Override
	public VideoDetailsBean uploadVideoDetails(VideoDetailsBean videoDetailsBean) {
		VideoDetailsBean videoDetailsResponse = new VideoDetailsBean();
		try {
			// get userkey from header
			Long userKey = 1L;
			UserProfile userProfile = userProfileRepository.findByUserkey(userKey);
			Course course = courseDetails(videoDetailsBean, userProfile);
			videoDetailsResponse.setCourseCategory(videoDetailsBean.getCourseCategory());
			videoDetailsResponse.setCourseBean(mapCourseData(course, userProfile));

			// link generation logic s3
			String url = "link";

			VideoDetails videoDetails = mongoDetails(userKey, videoDetailsBean.getVideoBean(), url, userProfile);
			videoDetailsResponse.setVideoBean(mapMongoData(videoDetails));

		} catch (Exception ex) {
			ex.getMessage();
		}
		return videoDetailsResponse;
	}

	@Override
	public VideoDetailsBean updateVideoDetails(Long courseKey, VideoDetailsBean videoDetailsBean) {
		VideoDetailsBean videoDetailsResponse = new VideoDetailsBean();
		try {
			Optional<Course> course = courseRepository.findById(courseKey);
			if (course.isPresent()) {
				if (videoDetailsBean != null) {
					Course courseFromDB = courseDetails(course.get(), videoDetailsBean);
					videoDetailsResponse.setCourseBean(mapCourseData(courseFromDB));
					if (videoDetailsBean.getVideoBean() != null) {
						VideoDetails videoDetails = mongoDetails(courseKey, videoDetailsBean.getVideoBean());
						videoDetailsResponse.setVideoBean(mapMongoData(videoDetails));
					}
				}
			} else {
				throw new EntityDataNotFound("Course not found for given courseKey");
			}

		} catch (Exception ex) {
			ex.getMessage();
		}
		return videoDetailsResponse;
	}

	private VideoDetails mongoDetails(Long courseKey, VideoBean videoBean, String url, UserProfile userProfile) {
		VideoDetails videoDetails = new VideoDetails();
		videoDetails.setProfName(userProfile.getFirstname() + " " + userProfile.getLastname());
		videoDetails.setVideoLink(url);
		videoDetails.setVideoTitle(videoBean.getVideoTitle() != null ? videoBean.getVideoTitle() : null);
		videoDetails.setVideoDuration(videoBean.getVideoDuration() != null ? videoBean.getVideoTitle() : null);
		videoDetails.setCourseId(courseKey);
		return videoDetailsRepository.save(videoDetails);
	}

	private VideoDetails mongoDetails(Long courseKey, VideoBean videoBean) {
		VideoDetails videoDetails = videoDetailsRepository.findByCourseId(courseKey);
		if (videoDetails != null) {
			if (videoBean.getVideoLink() != null)
				videoDetails.setVideoDuration(videoBean.getVideoLink());
		}
		if (videoBean.getVideoTitle() != null) {
			videoDetails.setVideoTitle(videoBean.getVideoTitle());
		}
		return videoDetailsRepository.save(videoDetails);
	}

	private Course courseDetails(Course course, VideoDetailsBean videoDetailsBean) {
		if (videoDetailsBean.getCourseCategory() != null) {
			CourseCategory courseCategory = courseCategoryRepository
					.findByCategoryname(videoDetailsBean.getCourseCategory());
			Long courseCategoryKey = courseCategory.getId() != null ? courseCategory.getId() : null;
			if (courseCategoryKey != null) {
				course.setCoursecategorykey(courseCategoryKey);
			}
		}
		if (videoDetailsBean.getCourseBean() != null) {
			course.setCoursedescription(videoDetailsBean.getCourseBean().getCoursedescription() != null
					? videoDetailsBean.getCourseBean().getCoursedescription()
					: course.getCoursedescription());
			if (videoDetailsBean.getCourseBean().getCourseName() != null) {
				course.setCoursename(videoDetailsBean.getCourseBean().getCourseName());
			}
		}
		return courseRepository.save(course);
	}

	private Course courseDetails(VideoDetailsBean videoDetailsBean, UserProfile userProfile) {
		Course course = new Course();
		course.setUserprofilekey(userProfile.getId() != null ? userProfile.getId() : null);

		if (videoDetailsBean.getCourseCategory() != null) {
			CourseCategory courseCategory = courseCategoryRepository
					.findByCategoryname(videoDetailsBean.getCourseCategory());
			Long courseCategoryKey = courseCategory.getId() != null ? courseCategory.getId() : null;
			if (courseCategoryKey != null) {
				course.setCoursecategorykey(courseCategoryKey);
			}
		}
		if (videoDetailsBean.getCourseBean() != null) {
			course.setCoursedescription(videoDetailsBean.getCourseBean().getCoursedescription() != null
					? videoDetailsBean.getCourseBean().getCoursedescription()
					: course.getCoursedescription());
			if (videoDetailsBean.getCourseBean().getCourseName() != null) {
				course.setCoursename(videoDetailsBean.getCourseBean().getCourseName());
			}
		}
		return courseRepository.save(course);
	}

	private CourseBean mapCourseData(Course course, UserProfile userProfile) {
		CourseBean courseBean = new CourseBean();
		courseBean.setCoursedescription(course.getCoursedescription());
		courseBean.setCourseName(course.getCoursename());
		if (userProfile != null) {
			courseBean.setExperience(userProfile.getExperience());
			courseBean.setProfessorName(userProfile.getFirstname() + " " + userProfile.getLastname());
		}
		courseBean.setCourseId(course.getId());
		return courseBean;
	}

	private VideoBean mapMongoData(VideoDetails videoDets) {
		VideoBean videoBean = new VideoBean();
		videoBean.setVideoDuration(videoDets.getVideoDuration());
		videoBean.setVideoLink(videoDets.getVideoLink());
		videoBean.setVideoTitle(videoDets.getVideoTitle());
		return videoBean;
	}

	private CourseBean mapCourseData(Course course) {
		CourseBean courseBean = new CourseBean();
		courseBean.setCoursedescription(course.getCoursedescription());
		courseBean.setCourseName(course.getCoursename());
		courseBean.setCourseId(course.getId());
		return courseBean;
	}

	
	
}





