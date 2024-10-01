package com.example.LearningManagementSystem.service;

import com.example.LearningManagementSystem.bean.CourseBean;
import com.example.LearningManagementSystem.bean.CourseDetailsBean;
import com.example.LearningManagementSystem.bean.VideoBean;
import com.example.LearningManagementSystem.entity.*;
import com.example.LearningManagementSystem.exception.EntityDataNotFound;
import com.example.LearningManagementSystem.exception.LmsException;
import com.example.LearningManagementSystem.repository.*;

import com.example.LearningManagementSystem.utils.LearningManagementUtils;
import io.jsonwebtoken.io.IOException;
import org.springframework.http.HttpHeaders;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
    private CourseDetailsRepository courseDetailsRepository;
    
    @Autowired
    private VideoDetailsRepository videoDetailsRepository;
    
    @Autowired
    private VideoRepository   videoRepository;

	@Autowired
	private EnrollmentRepository enrollmentRepository;
    
    private  S3Client s3Client;
    
   // @Value("${aws.s3.bucketName}")
	private String bucketName;
    
    
    //@Value("${s3.video.link:false}")
  	private Boolean s3Link;

    @Override
    public List<CourseDetailsBean> getAllCourseDetails() throws LmsException {
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
                            if(userProfile!=null){
                                courseBean.setProfessorName(userProfile.getFirstname() + " " + userProfile.getLastname());
                                courseBean.setExperience(userProfile.getExperience());
                            }
                            CourseDetails courseDetails = getCourseDetails(course.getId().toString());
                            if(courseDetails!=null){
                                courseBean.setAbout(courseDetails.getAbout());
                                courseBean.setVideoLink(courseDetails.getVideolink());
                            }
                            return courseBean;
                        })
                        .collect(Collectors.toList());

                courseDetailsBean.setCourseDetailList(courseBeanList);
                return courseDetailsBean;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new LmsException("Exception occurred while getAllCourseDetails()", "LMS_002", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public CourseDetailsBean getCourseDetailsByCategory(Long catid) throws Exception{
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
                            CourseDetails courseDetails = getCourseDetails(course.getId().toString());
                            if(courseDetails!=null){
                                courseBean.setAbout(courseDetails.getAbout());
                                courseBean.setVideoLink(courseDetails.getVideolink());
                            }
                            return courseBean;
                        })
                        .collect(Collectors.toList());
                courseDetailsBean.setCourseDetailList(courseDetailList);
                return courseDetailsBean;
            } else {
                throw new EntityDataNotFound("Category doesn't exist");
            }
        } catch (Exception e) {
            throw new LmsException("Exception occurred while getCourseDetailsByCategory()", "LMS_002", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public CourseBean getCourseDetailsById(Long courseid) throws Exception{
        CourseBean courseBean = new CourseBean();
        try {
            Course course = courseRepository.findByIdAndIsactive(courseid, 1);
            if (course != null) {
                CourseDetails courseDetails = getCourseDetails(course.getId().toString());
                if(courseDetails!=null){
                    courseBean.setAbout(courseDetails.getAbout());
                    courseBean.setVideoLink(courseDetails.getVideolink());
                }
                courseBean.setCourseName(course.getCoursename());
                UserProfile userProfile = getUserDetails(course.getUserprofilekey());
                if (userProfile != null) {
                    courseBean.setProfessorName(userProfile.getFirstname() + " " + userProfile.getLastname());
                    courseBean.setExperience(userProfile.getExperience());
                }
            }
        } catch (Exception e) {
            throw new LmsException("Exception occurred while getCourseDetailsById()", "LMS_002", HttpStatus.INTERNAL_SERVER_ERROR);
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

    private CourseDetails getCourseDetails(String courseId){
        return courseDetailsRepository.findByCourseid(courseId);
    }
    
	@Override
	public CourseDetailsBean uploadVideoDetails(CourseDetailsBean courseDetailsBean) {
		List<CourseBean> courses = new ArrayList<>();
		CourseDetailsBean courseDetailsReponse = new CourseDetailsBean();
		try {
			Long userKey = 1L;
			UserProfile userProfile = userProfileRepository.findByUserkey(userKey);
			List<CourseCategory> courseCategories = courseCategoryRepository.findAll();
			List<String> categoryNameList = courseCategories.stream().map(category -> category.getCategoryname())
					.collect(Collectors.toList());
			if (categoryNameList.contains(courseDetailsBean.getCourseCategory())) {
				CourseCategory courseCategory = courseCategoryRepository
						.findByCategoryname(courseDetailsBean.getCourseCategory());
				courseDetailsReponse.setCourseCategory(courseCategory.getCategoryname());
				courseDetailsReponse.setCourseDetailList(courseDetailsBean.getCourseDetailList().stream().map(value -> {
					Course course = new Course();
					course.setCoursecategorykey(courseCategory.getId());
					course.setUserprofilekey(userProfile.getId() != null ? userProfile.getId() : null);
					course.setCoursedescription(
							value.getCoursedescription() != null ? value.getCoursedescription() : null);
					course.setCoursename(value.getCourseName());
					Course courseFromDB = courseRepository.save(course);
					CourseBean courseBean = new CourseBean();
					courseBean.setCoursedescription(courseFromDB.getCoursedescription());
					courseBean.setCourseId(courseFromDB.getId());
					courseBean.setCourseName(courseFromDB.getCoursename());
					courseBean.setExperience(userProfile.getExperience());
					courseBean.setUserprofilekey(userProfile.getId());
					courseBean.setProfessorName(userProfile.getFirstname() + " " + userProfile.getLastname());
					courseBean.setVideoBean(getVideoDetails(value.getVideoBean(), courseFromDB));

					return courseBean;
				}).collect(Collectors.toList()));
			} else {
				throw new EntityDataNotFound("Course not found for given courseKey");
			}

		} catch (Exception ex) {
			ex.getMessage();
		}
		return courseDetailsReponse;
	}

	private String uploadVideo(MultipartFile file) {
		String fileName = file.getOriginalFilename();
		try {
			s3Client.putObject(PutObjectRequest.builder().bucket(bucketName).key(fileName)
					.contentType(file.getContentType()).build(), RequestBody.fromBytes(file.getBytes()));
		} catch (Exception e) {
			e.getMessage();
		}

		return getVideoLink(bucketName, fileName);
	}

	private String getVideoLink(String bcktName, String fileName) {
		String url = "https://" + bcktName + ".s3.amazonaws.com/" + fileName;
		return url;
	}

	@Override
	public CourseBean updateVideoDetails(Long courseKey, Long videoId, CourseBean courseBean) {
		try {
			Long userKey = 1L;
			UserProfile userProfile = userProfileRepository.findByUserkey(userKey);
			Optional<Course> course = courseRepository.findById(courseKey);
			if (course.isPresent()) {
				course.get().setCoursedescription(courseBean.getCoursedescription());
				course.get().setCoursename(courseBean.getCourseName());
				Course courseFromDB = courseRepository.save(course.get());

				VideoBean videoBean = courseBean.getVideoBean().get(0);
				String url = "";
				if (videoBean != null) {
					if (s3Link) {
						try {
							url = uploadVideo(courseBean.getVideoBean().get(0).getVideoFile());
						} catch (Exception e) {
							e.getMessage();
						}
					}
					Video video = videoRepository.findByIdAndCourseid(videoId, courseKey);
					video.setVideoduration(videoBean.getVideoDuration());
					video.setVideolink(url);
					video.setVideotile(videoBean.getVideoTitle());
					Video videoFromDB = videoRepository.save(video);
					VideoDetails videoDetaisl = videoDetailsRepository.findByCourseIdAndVideoId(courseKey, videoId);
					VideoDetails videoDetails = new VideoDetails();
					videoDetails.setVideoDuration(videoBean.getVideoDuration());
					videoDetails.setVideoLink(url);
					videoDetails.setVideoTitle(videoBean.getVideoTitle());
					VideoDetails VideoDetailsFromDB = videoDetailsRepository.save(videoDetails);

					CourseBean courseResponse = mapCourse(courseFromDB);
					List<VideoBean> videobeans = new ArrayList<>();

					videobeans.add(mapVideoData(VideoDetailsFromDB));
					courseResponse.setVideoBean(videobeans);
				}
			}

		} catch (Exception ex) {
			ex.getMessage();
		}
		return courseBean;
	}

	private List<VideoBean> getVideoDetails(List<VideoBean> videoBean, Course course) {
		return videoBean.stream().map(videoDet -> {
			Video video = new Video();
			video.setCourseid(course.getId());
			video.setVideoduration(videoDet.getVideoDuration());
			String url = "";
			if (s3Link) {
				try {
					url = uploadVideo(videoDet.getVideoFile());
				} catch (Exception e) {
					e.getMessage();
				}
			}
			video.setVideolink(url);
			video.setVideotile(videoDet.getVideoTitle());
			Video videoFromDB = videoRepository.save(video);
			VideoDetails videoDetails = new VideoDetails();
			videoDetails.setCourseId(course.getId());
			videoDetails.setVideoDuration(videoDet.getVideoDuration());
			videoDetails.setCourseDescription(course.getCoursedescription());
			videoDetails.setVideoLink(url);
			videoDetails.setVideoTitle(videoDet.getVideoTitle());
			videoDetails.setVideoId(videoFromDB.getId());
			VideoDetails VideoDetailsFromDB = videoDetailsRepository.save(videoDetails);
			return mapVideoData(VideoDetailsFromDB);
		}).collect(Collectors.toList());
	}

	private CourseBean mapCourse(Course course) {
		CourseBean courseBean = new CourseBean();
		courseBean.setCoursedescription(course.getCoursedescription());
		courseBean.setCourseId(course.getId());
		courseBean.setUserprofilekey(course.getUserprofilekey());
		courseBean.setCourseName(course.getCoursename());
		return courseBean;
	}

	private VideoBean mapVideoData(VideoDetails videoDets) {
		VideoBean videoBean = new VideoBean();
		videoBean.setVideoDuration(videoDets.getVideoDuration());
		videoBean.setVideoId(videoDets.getVideoId());
		videoBean.setVideoLink(videoDets.getVideoLink());
		videoBean.setVideoTitle(videoDets.getVideoTitle());
		return videoBean;
	}

	@Override
	public List<CourseDetailsBean> getCoursesDetails() {
		List<CourseDetailsBean> courseDetailsBeans = new ArrayList<>();
		try {
			Long userKey = 1L;
			UserProfile userProfile = userProfileRepository.findByUserkey(userKey);
			List<Course> courses = courseRepository.findByUserprofilekey(userProfile.getId());
			Set<Long> categoryId = courses.stream().map(course -> course.getCoursecategorykey())
					.collect(Collectors.toSet());
			courseDetailsBeans = categoryId.stream().map(catId -> {
				CourseDetailsBean courseDetailsBean = new CourseDetailsBean();
				courseDetailsBean.setCourseCategory(courseCategoryRepository.findById(catId).get().getCategoryname());
				List<Course> coursess = courseRepository.findByCoursecategorykey(catId);
				if (!coursess.isEmpty()) {
					courseDetailsBean.setCourseDetailList(courses.stream().map(course -> {

						CourseBean courseBean = new CourseBean();
						courseBean.setCoursedescription(course.getCoursedescription());
						courseBean.setCourseName(course.getCoursename());
						courseBean.setExperience(userProfile.getExperience());
						courseBean.setUserprofilekey(userProfile.getId());
						courseBean.setVideoBean(videoRepository.findByCourseid(course.getId()).stream().map(video -> {
							VideoBean videoBean = new VideoBean();
							videoBean.setVideoId(video.getId());
							videoBean.setVideoLink(video.getVideolink());
							videoBean.setVideoTitle(video.getVideotile());
							return videoBean;
						}).collect(Collectors.toList()));
						return courseBean;
					}).collect(Collectors.toList()));
				}
				return courseDetailsBean;
			}).collect(Collectors.toList());

		} catch (Exception ex) {
			ex.getMessage();
		}
		return courseDetailsBeans;

	}

	@Override
	public List<CourseBean> getMyEnrolledCourses(HttpHeaders headers) throws Exception {

		List<CourseBean> courseBeanList = new ArrayList<>();

		try {
			Long userKey = LearningManagementUtils.getUserId();

			List<Enrollment> enrollmentList = enrollmentRepository.findByIdAndIsactive(userKey, 1);

			if (!CollectionUtils.isEmpty(enrollmentList)) {

				for (Enrollment enrollment : enrollmentList) {
					CourseBean courseBean = new CourseBean();
					Course course = courseRepository.findByIdAndIsactive(enrollment.getCourseid(), 1);
					UserProfile userProfile = userProfileRepository.findByIdAndIsactive(course.getUserprofilekey(), 1);
					courseBean.setCourseName(course.getCoursename());
					courseBean.setProfessorName(userProfile.getFirstname() + " " + userProfile.getLastname());
					courseBean.setExperience(userProfile.getExperience());
					courseBeanList.add(courseBean);
				}

			}

		} catch (Exception e) {

			throw new LmsException("Exception occur while fetching details", "LMS005",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return courseBeanList;
	}
}





