package com.example.LearningManagementSystem.service;

import java.io.IOException;
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
import com.example.LearningManagementSystem.bean.VideoDetailsBean;
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
	public VideoDetailsBean uploadVideoDetails(VideoDetailsBean videoDetailsBean) {
		VideoDetailsBean videoDetailsResponse = new VideoDetailsBean();
		try {
			// get userkey from header
			Long userKey = 1L;
			UserProfile userProfile = userProfileRepository.findByUserkey(userKey);
			Course course = courseDetails(videoDetailsBean, userProfile);
			videoDetailsResponse.setCourseCategory(videoDetailsBean.getCourseCategory());
			// save course data to DB
			videoDetailsResponse.setCourseBean(mapCourseData(course, userProfile));

			// link generation logic s3
			String url = null;
			if (s3Link) {
				if (videoDetailsBean.getVideoBean() != null) {
					url = uploadVideo(videoDetailsBean.getVideoBean().getVideoFile());
				}
			}
			// save video data into postgres
			videoDetails(course.getId(), videoDetailsBean.getVideoBean(), url, userProfile);
			// save video data to mongo
			VideoDetails videoDetails = mongoDetails(course.getId(), videoDetailsBean, userProfile, url);
			videoDetailsResponse.setVideoBean(mapMongoData(videoDetails));

		} catch (Exception ex) {
			ex.getMessage();
		}
		return videoDetailsResponse;
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
	public VideoDetailsBean updateVideoDetails(Long courseKey, VideoDetailsBean videoDetailsBean) {
		VideoDetailsBean videoDetailsResponse = new VideoDetailsBean();
		try {
			Long userKey = 1L;
			UserProfile userProfile = userProfileRepository.findByUserkey(userKey);
			Optional<Course> course = courseRepository.findById(courseKey);
			if (course.isPresent()) {
				if (videoDetailsBean != null) {
					Course courseFromDB = courseDetails(course.get(), videoDetailsBean, userProfile);
					videoDetailsResponse.setCourseBean(mapCourseData(courseFromDB,userProfile));
					videoDetailsResponse.setCoursedescription(courseFromDB.getCoursedescription());
					if (videoDetailsBean.getVideoBean() != null) {
						String url = null;
						if (s3Link) {
							if (videoDetailsBean.getVideoBean().getVideoFile() != null) {
								url = uploadVideo(videoDetailsBean.getVideoBean().getVideoFile());
							}
						}
						Video video = videoRepository.findByCourseid(courseKey);
						if (video != null) {
							videoDetails(video, videoDetailsBean.getVideoBean(), url, userProfile);
						}

						VideoDetails videoDets = videoDetailsRepository.findByCourseId(courseKey);
						if (videoDets != null) {
							VideoDetails videoDetails = mongoDetails(videoDets, videoDetailsBean, userProfile, url);
							videoDetailsResponse.setVideoBean(mapMongoData(videoDetails));
						}

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

	// save video Dets to postgres
	private void videoDetails(Long courseKey, VideoBean videoBean, String url, UserProfile userProfile) {
		Video video = new Video();
		video.setVideolink(url);
		video.setVideotile(videoBean.getVideoTitle() != null ? videoBean.getVideoTitle() : null);
		video.setVideoduration(videoBean.getVideoDuration() != null ? videoBean.getVideoDuration() : null);
		video.setCourseid(courseKey);
		videoRepository.save(video);
	}

	// update video dets to postgres
	private void videoDetails(Video video, VideoBean videoBean, String url, UserProfile userProfile) {
		if (!StringUtils.isEmpty(url)) {
			video.setVideolink(url);
		}
		video.setVideotile(videoBean.getVideoTitle() != null ? videoBean.getVideoTitle() : null);
		video.setVideoduration(videoBean.getVideoDuration() != null ? videoBean.getVideoDuration() : null);
		videoRepository.save(video);
	}

	// save mongo Dets
	private VideoDetails mongoDetails(Long courseKey, VideoDetailsBean VideoDetailsBean, UserProfile userProfile,
			String url) {
		VideoDetails videoDetails = new VideoDetails();
		videoDetails.setCourseId(courseKey);
		videoDetails.setProfName(userProfile.getFirstname() + " " + userProfile.getLastname());
		videoDetails.setVideoLink(url);
		if (VideoDetailsBean.getVideoBean() != null) {
			videoDetails.setVideoTitle(VideoDetailsBean.getVideoBean().getVideoTitle());
			videoDetails.setVideoDuration(VideoDetailsBean.getVideoBean().getVideoDuration());
			videoDetails.setCourseDescription(VideoDetailsBean.getCoursedescription());
		}
		return videoDetailsRepository.save(videoDetails);
	}

	// update mongo Dets
	private VideoDetails mongoDetails(VideoDetails videoDetails, VideoDetailsBean VideoDetailsBean,
			UserProfile userProfile, String url) {
		videoDetails.setProfName(userProfile.getFirstname() + " " + userProfile.getLastname());
		if (!StringUtils.isEmpty(url)) {
			videoDetails.setVideoLink(url);
		}
		if (VideoDetailsBean.getVideoBean() != null) {
			videoDetails.setVideoTitle(VideoDetailsBean.getVideoBean().getVideoTitle());
			videoDetails.setVideoDuration(VideoDetailsBean.getVideoBean().getVideoDuration());
			videoDetails.setCourseDescription(VideoDetailsBean.getCoursedescription());
		}
		return videoDetailsRepository.save(videoDetails);
	}

	private Course courseDetails(Course course, VideoDetailsBean videoDetailsBean, UserProfile userProfile) {
		course.setUserprofilekey(userProfile.getId() != null ? userProfile.getId() : null);
		course.setCoursedescription(
				videoDetailsBean.getCoursedescription() != null ? videoDetailsBean.getCoursedescription()
						: course.getCoursedescription());
		if (videoDetailsBean.getCourseCategory() != null) {
			CourseCategory courseCategory = courseCategoryRepository
					.findByCategoryname(videoDetailsBean.getCourseCategory());
			Long courseCategoryKey = courseCategory.getId() != null ? courseCategory.getId() : null;
			if (courseCategoryKey != null) {
				course.setCoursecategorykey(courseCategoryKey);
			}
		}
		if (videoDetailsBean.getCourseBean() != null) {
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
			course.setCoursedescription(
					videoDetailsBean.getCoursedescription() != null ? videoDetailsBean.getCoursedescription()
							: course.getCoursedescription());
			if (videoDetailsBean.getCourseBean().getCourseName() != null) {
				course.setCoursename(videoDetailsBean.getCourseBean().getCourseName());
			}
		}
		return courseRepository.save(course);
	}

	private CourseBean mapCourseData(Course course, UserProfile userProfile) {
		CourseBean courseBean = new CourseBean();
		courseBean.setCourseName(course.getCoursename());
		if (userProfile != null) {
			courseBean.setExperience(userProfile.getExperience());
			courseBean.setProfessorName(userProfile.getFirstname() + " " + userProfile.getLastname());
		}
		courseBean.setUserprofilekey(course.getUserprofilekey());
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
		courseBean.setCourseName(course.getCoursename());
		courseBean.setCourseId(course.getId());
		return courseBean;
	}
	
}





