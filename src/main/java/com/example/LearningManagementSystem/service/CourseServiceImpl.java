package com.example.LearningManagementSystem.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.LearningManagementSystem.bean.CourseBean;
import com.example.LearningManagementSystem.bean.CourseDetailsBean;
import com.example.LearningManagementSystem.bean.VideoBean;
import com.example.LearningManagementSystem.entity.Course;
import com.example.LearningManagementSystem.entity.CourseCategory;
import com.example.LearningManagementSystem.entity.UserProfile;
import com.example.LearningManagementSystem.entity.Video;
import com.example.LearningManagementSystem.entity.VideoDetails;
import com.example.LearningManagementSystem.exception.EntityDataNotFound;
import com.example.LearningManagementSystem.repository.CourseCategoryRepository;
import com.example.LearningManagementSystem.repository.CourseRepository;
import com.example.LearningManagementSystem.repository.UserProfileRepository;
import com.example.LearningManagementSystem.repository.VideoDetailsRepository;
import com.example.LearningManagementSystem.repository.VideoRepository;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

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
    
    @Autowired
    private VideoRepository   videoRepository;
    
    private  S3Client s3Client;
    
    @Value("${aws.s3.bucketName}")
	private String bucketName;
    
    
    @Value("${s3.video.link:false}")
  	private Boolean s3Link;
    

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
					course.setCoursedescription(value.getCoursedescription() != null ? value.getCoursedescription()
							: null);
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


	private String uploadVideo(MultipartFile file) throws IOException {
		String fileName = file.getOriginalFilename();
		s3Client.putObject(
				PutObjectRequest.builder().bucket(bucketName).key(fileName).contentType(file.getContentType()).build(),
				RequestBody.fromBytes(file.getBytes()));

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

	private CourseDetailsBean mapcourseDetails(VideoDetails videoDets, Course course) {
		CourseDetailsBean courseDetailsBean = new CourseDetailsBean();
		Optional<CourseCategory> courseCategory = courseCategoryRepository.findById(course.getId());
		if (courseCategory.isPresent()) {
			courseDetailsBean.setCourseCategory(courseCategory.get().getCategoryname());
		}

		return null;
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

}





