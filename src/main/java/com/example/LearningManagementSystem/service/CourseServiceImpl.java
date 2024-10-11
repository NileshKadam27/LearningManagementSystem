package com.example.LearningManagementSystem.service;

import com.example.LearningManagementSystem.bean.*;
import com.example.LearningManagementSystem.entity.*;
import com.example.LearningManagementSystem.entity.Course;
import com.example.LearningManagementSystem.exception.EntityDataNotFound;
import com.example.LearningManagementSystem.exception.LmsException;
import com.example.LearningManagementSystem.repository.*;

import com.example.LearningManagementSystem.utils.LearningManagementUtils;
import jakarta.persistence.EntityNotFoundException;

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
	private VideoRepository videoRepository;

	@Autowired
	private EnrollmentRepository enrollmentRepository;

	@Autowired
	UserVideoprogressRepository userVideoprogressRepository;

	@Autowired
	private S3Client s3Client;

	@Value("${aws.s3.bucketName}")
	private String bucketName;


	@Value("${s3.video.link:true}")
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
							if (userProfile != null) {
								courseBean.setProfessorName(userProfile.getFirstname() + " " + userProfile.getLastname());
								courseBean.setExperience(userProfile.getExperience());
							}
							CourseDetails courseDetails = getCourseDetails(course.getId().toString());
							if (courseDetails != null) {
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
	public CourseDetailsBean getCourseDetailsByCategory(Long catid) throws Exception {
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
							if (courseDetails != null) {
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
	public CourseBean getCourseDetailsById(Long courseid) throws Exception {
		CourseBean courseBean = new CourseBean();
		try {
			Course course = courseRepository.findByIdAndIsactive(courseid, 1);
			if (course != null) {
				CourseDetails courseDetails = getCourseDetails(course.getId().toString());
				if (courseDetails != null) {
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

	private CourseDetails getCourseDetails(String courseId) {
		return courseDetailsRepository.findByCourseid(courseId);
	}


	@Override
	public List<CourseDetailsBean> getCoursesDetails(Long courseId) {
		List<CourseDetailsBean> courseDetailsBeans = new ArrayList<>();
		try {
			UserProfile userProfile = userProfileRepository.findByUserkey(LearningManagementUtils.getUserId());
			List<Course> courses = new ArrayList<>();
			if (courseId != null) {
				courses = courseRepository.findByUserprofilekeyAndId(userProfile.getId(), courseId);

			} else {
				courses = courseRepository.findByUserprofilekey(userProfile.getId());
			}
			Set<Long> categoryId = courses.stream().map(course -> course.getCoursecategorykey())
					.collect(Collectors.toSet());
			int count = 1;
			for (Long value : categoryId) {

				CourseDetailsBean courseDetailsBean = new CourseDetailsBean();
				Optional<CourseCategory> category = courseCategoryRepository.findById(value);
				if (category.isPresent()) {
					courseDetailsBean.setId(count);
					courseDetailsBean.setCourseCategory(category.get().getCategoryname());
					List<Course> coursess = courseRepository.findByCoursecategorykey(value);
					courseDetailsBean.setCourseDetailList(coursess.stream().map(course -> {
						CourseBean courseBean = new CourseBean();
						courseBean.setCourseId(course.getId());
						courseBean.setCourseName(course.getCoursename());
						courseBean.setExperience(userProfile.getExperience());
						courseBean.setCoursedescription(course.getCoursedescription());
						courseBean.setCourseImageLink(course.getCourseimagelink());
						List<Video> videos = videoRepository.findByCourseid(course.getId());
						List<VideoBean> vid = new ArrayList<>();
						if (!videos.isEmpty()) {
							for (Video video : videos) {
								VideoBean videoBean = new VideoBean();
								videoBean.setVideoId(video.getId());
								videoBean.setVideoLink(video.getVideolink());
								videoBean.setVideoTitle(video.getVideotitle());
								videoBean.setVideoDuration(video.getVideoduration());
								videoBean.setVideoDescription(video.getVideoDescription());
								vid.add(videoBean);
							}
						}
						courseBean.setVideoBean(vid);


						return courseBean;
					}).collect(Collectors.toList()));
				}

				courseDetailsBeans.add(courseDetailsBean);
				count++;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return courseDetailsBeans;
	}


	@Override
	public List<CourseBean> getMyEnrolledCourses() throws Exception {

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

	@Override
	public ProfDetBean uploadCourseDetails(ProfDetBean profDetBean) {
		ProfDetBean profDetResponse = new ProfDetBean();
		try {
			Long userKey = LearningManagementUtils.getUserId();
			UserProfile userProfile = userProfileRepository.findByUserkey(userKey);
			CourseCategory courseCat = null;
			CourseCategory courseCategory = new CourseCategory();
			if (courseCategoryRepository.findAll() != null) {
				Set<String> categories = courseCategoryRepository.findAll().stream().map(cat -> cat.getCategoryname())
						.collect(Collectors.toSet());
				if ((categories.contains(profDetBean.getCourseCategory()))
						&& profDetBean.getCourseCategory() != null) {
					courseCat = courseCategoryRepository.findByCategoryname(profDetBean.getCourseCategory());
				} else {
					courseCategory.setCategoryname(profDetBean.getCourseCategory());
					courseCat = courseCategoryRepository.save(courseCategory);
				}
			} else {
				courseCategory.setCategoryname(profDetBean.getCourseCategory());
				courseCat = courseCategoryRepository.save(courseCategory);
			}
			Course course = new Course();
			course.setCoursecategorykey(courseCat.getId());
			course.setUserprofilekey(userProfile.getId() != null ? userProfile.getId() : null);
			course.setCoursedescription(profDetBean.getCourseDescription());
			course.setCoursename(profDetBean.getCourseName());
			course.setIsactive(1);
			if (s3Link) {
				String url = uploadVideo(profDetBean.getCourseImage());
				course.setCourseimagelink(url);
			}
			Course courseFromDB = courseRepository.save(course);
			profDetResponse = mapUploadVidDets(courseFromDB, null, null, userProfile,
					courseCat.getCategoryname());

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return profDetResponse;
	}

	private ProfDetBean mapUploadVidDets(Course course, Video video, VideoDetails videoDets, UserProfile userProfile,
										 String category) {
		ProfDetBean profDet = new ProfDetBean();
		profDet.setCourseCategory(category);
		profDet.setCourseName(course.getCoursename());
		profDet.setExperience(userProfile.getExperience());
		profDet.setProfessorName(userProfile.getFirstname() + " " + userProfile.getLastname());
		profDet.setUserprofilekey(userProfile.getId());
		profDet.setCourseDescription(course.getCoursedescription());
		return profDet;
	}

	@Override
	public ProfDetBean updateVideoDetails(Long courseId, Long videoId, ProfDetBean profDetails) {
		ProfDetBean ProfDetails = new ProfDetBean();

		try {
			Long userKey = LearningManagementUtils.getUserId();
//			Long userKey =1l;
			UserProfile userProfile = userProfileRepository.findByUserkey(userKey);
			Optional<Course> courseById = courseRepository.findById(courseId);
			Course courseFromDB;
			CourseCategory courseCat = null;
			if (courseById.isPresent()) {
				Set<String> categories = courseCategoryRepository.findAll().stream().map(cat -> cat.getCategoryname())
						.collect(Collectors.toSet());

				if ((categories.contains(profDetails.getCourseCategory()))
						&& profDetails.getCourseCategory() != null) {
					courseCat = courseCategoryRepository.findByCategoryname(profDetails.getCourseCategory());
				} else {
					CourseCategory courseCategory = new CourseCategory();
					courseCategory.setCategoryname(profDetails.getCourseCategory());
					courseCat = courseCategoryRepository.save(courseCategory);
					courseById.get().setCoursecategorykey(courseCat.getId());
				}
				courseFromDB = updateCourseDetails(courseById.get(), userProfile, profDetails);
			} else {
				throw new EntityDataNotFound("Course Details not found");
			}

			Optional<Video> video = videoRepository.findById(videoId);
			Video videoFromDB;
			if (video.isPresent()) {
				video.get().setVideotitle(profDetails.getVideoTitle());
				video.get().setVideoDescription(profDetails.getVideoDescription());
				video.get().setVideoduration(profDetails.getVideoDuration());
				if (s3Link) {
					String url = uploadVideo(profDetails.getVideoFile());
					video.get().setVideolink(url);
				}
				videoFromDB = videoRepository.save(video.get());
			} else {
				throw new EntityDataNotFound("Video Details not found");
			}

			ProfDetails = mapUploadVidDets(courseFromDB, videoFromDB, null, userProfile,
					courseCat.getCategoryname());

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return ProfDetails;
	}


	private Video updateVideoDetails(Long id, Video video, ProfDetBean profDetBean) {
		video.setCourseid(id);
		if (profDetBean.getVideoFile() != null) {
			if (s3Link) {
				String url = uploadVideo(profDetBean.getVideoFile());
				video.setVideolink(url);
			}
		}
		if (profDetBean.getVideoDuration() != null) {
			video.setVideoduration(profDetBean.getVideoDuration());
		}
		if (profDetBean.getVideoTitle() != null) {
			video.setVideotitle(profDetBean.getVideoTitle());
		}
		return videoRepository.save(video);
	}

	private Course updateCourseDetails(Course course, UserProfile userProfile, ProfDetBean profDetBean) {
		course.setUserprofilekey(userProfile.getId() != null ? userProfile.getId() : null);
		if (profDetBean.getCourseDescription() != null) {
			course.setCoursedescription(profDetBean.getCourseDescription());
		}
		if (profDetBean.getCourseName() != null) {
			course.setCoursename(profDetBean.getCourseName());
		}
		return courseRepository.save(course);
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
	public List<CourseBean> getCourseDetailsByName(String coursename) {
		List<CourseBean> courseBeanList = new ArrayList<>();
		try {
			List<Course> courseList = courseRepository.findByCoursenameAndIsactive(coursename, 1);

			if (courseList != null && !courseList.isEmpty()) {
				courseBeanList = courseList.stream().map(course -> {
					CourseBean courseBean = new CourseBean();
					courseBean.setCourseName(course.getCoursename());
					courseBean.setCourseId(course.getId());
					Optional<UserProfile> optionalUserProfile;
					try {
						optionalUserProfile = Optional.ofNullable(getUserDetails(course.getUserprofilekey()));
						optionalUserProfile.ifPresent(userProfile -> {
							courseBean.setProfessorName(userProfile.getFirstname() + " " + userProfile.getLastname());
							courseBean.setExperience(userProfile.getExperience());
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
					return courseBean;
				}).collect(Collectors.toList());
			}
		} catch (Exception e) {
			e.getMessage();
		}
		return courseBeanList;
	}

	@Override
	public Enrollment saveEnrollment(Enrollment enrollment) throws Exception {
		try {
			if (enrollment != null) {
				Long id = enrollment.getCourseid();
				Optional<Course> course = Optional.ofNullable(courseRepository.findByIdAndIsactive(id, 1));
				if (course.isPresent()) {
					enrollment.setUserkey(course.get().getUserprofilekey());
					enrollment.setIsactive(1);
				}
			}
			return enrollmentRepository.save(enrollment);
		} catch (Exception e) {
			throw new LmsException("An unexpected error occurred, while enrolling into course", "LMS006",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public UserVideoprogress saveUserVideoProgress(UserVideoprogress userVideoprogress, Long courseid)
			throws Exception {
		if (courseid == null) {
			throw new IllegalArgumentException("Course ID cannot be null.");
		}

		try {
			Optional<Course> course = Optional.ofNullable(courseRepository.findByIdAndIsactive(courseid, 1));

			if (course.isPresent()) {
				userVideoprogress.setUserkey(course.get().getUserprofilekey());
				userVideoprogress.setIsactive(1);
			} else {
				throw new EntityNotFoundException("Course not found or inactive.");
			}

			return userVideoprogressRepository.save(userVideoprogress);
		} catch (Exception e) {
			e.getMessage();

			throw new LmsException("An unexpected error occurred, saving User Video Progress", "LMS006",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@Override
	public ProfDetBean addVideoDetails(Long courseKey, ProfDetBean profDetBean) throws EntityDataNotFound {
		ProfDetBean profDetResponse = new ProfDetBean();
		try {
			if (profDetBean.getVideoId() != null) {
				Video video = videoRepository.findByIdAndCourseid(profDetBean.getVideoId(), courseKey);
				video.setVideotitle(profDetBean.getVideoTitle());
				video.setVideoduration(profDetBean.getVideoDuration());
				video.setVideoDescription(profDetBean.getVideoDescription());
				if (s3Link) {
					String url = uploadVideo(profDetBean.getVideoFile());
					video.setVideolink(url);
				}
				Video videoFormDB = videoRepository.save(video);
				profDetResponse.setVideoTitle(videoFormDB.getVideotitle());
				profDetResponse.setVideoLink(videoFormDB.getVideolink());
				profDetResponse.setVideoDuration(videoFormDB.getVideoduration());
				profDetResponse.setVideoDescription(videoFormDB.getVideoDescription());

			} else {
				String url = null;
				if (s3Link) {
					url = uploadVideo(profDetBean.getVideoFile());
				}
				// save video det into postgres
				Video video = new Video();
				video.setCourseid(courseKey);
				video.setVideoduration(profDetBean.getVideoDuration());
				video.setVideolink(url);
				video.setVideotitle(profDetBean.getVideoTitle());
				video.setVideoduration(profDetBean.getVideoDuration());
				video.setVideoDescription(profDetBean.getVideoDescription());
				Video videoFromDB = videoRepository.save(video);


				profDetResponse.setVideoTitle(videoFromDB.getVideotitle());
				profDetResponse.setVideoLink(videoFromDB.getVideolink());
				profDetResponse.setVideoDuration(videoFromDB.getVideoduration());
				profDetResponse.setVideoDescription(videoFromDB.getVideoDescription());
				profDetResponse.setVideoLink(videoFromDB.getVideolink());
				// save video dets into mongo

			}
		} catch (Exception ex) {
			throw new EntityDataNotFound("video details not found in mongo");
		}
		return profDetResponse;
	}

	@Override
	public List<CourseCat> getAllCourseCategory() throws Exception {
		List<CourseCat> courseCatList = new ArrayList<>();
		try {

			List<CourseCategory> courseCategoryList = courseCategoryRepository.findAll();
			if (CollectionUtils.isEmpty(courseCategoryList)) {
				return List.of();
			}
			for (CourseCategory courseCategory : courseCategoryList) {
				CourseCat courseCat = new CourseCat();
				List<Course> courseList = courseRepository.findByCoursecategorykeyAndIsactive(courseCategory.getId(), 1);
				List<com.example.LearningManagementSystem.bean.Course> courseList1 = new ArrayList<>();
				if (!CollectionUtils.isEmpty(courseList)) {
					for (Course course : courseList) {
						com.example.LearningManagementSystem.bean.Course course1 = new com.example.LearningManagementSystem.bean.Course();
						courseCat.setCatgeoryName(courseCategory.getCategoryname());
						courseCat.setCatid(courseCategory.getId());
						course1.setCourseId(course.getId());
						course1.setCourseName(course.getCoursename());
						courseList1.add(course1);
					}
					courseCat.setCourseList(courseList1);
					courseCatList.add(courseCat);
				}
			}


		} catch (Exception e) {
			throw new LmsException("Exception occurred while getAllCourseDetails()", "LMS_002", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return courseCatList;

	}

}




