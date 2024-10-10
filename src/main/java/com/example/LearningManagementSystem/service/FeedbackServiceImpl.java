package com.example.LearningManagementSystem.service;

import com.example.LearningManagementSystem.bean.FeedbackBean;
import com.example.LearningManagementSystem.entity.Feedback;
import com.example.LearningManagementSystem.exception.LmsException;
import com.example.LearningManagementSystem.repository.FeedbackRepository;
import com.example.LearningManagementSystem.utils.LearningManagementUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService  {


    @Autowired
    FeedbackRepository feedbackRepository;

    public List<FeedbackBean> getFeedbackByCourseId(Long courseid, HttpHeaders headers) throws Exception {
        List<FeedbackBean> feedbackBeanList = new ArrayList<>();
        try {
            List<Feedback> feedbackList = feedbackRepository.findByIdAndIsactive(courseid, 1);
            if (!CollectionUtils.isEmpty(feedbackList)) {
                for (Feedback feedback : feedbackList) {
                    FeedbackBean feedbackBean = new FeedbackBean();
                    feedbackBean.setComments(feedback.getFeedbackcomments());
                    feedbackBean.setRating(feedback.getRating());
                    feedbackBean.setUserkey(feedback.getUserkey());
                    feedbackBeanList.add(feedbackBean);
                }

            }

        } catch (Exception e) {

            throw new LmsException("Exception occured while getting feedback details", "LMS_003",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return feedbackBeanList;
    }


    @Override

    public String addFeedback(FeedbackBean feedbackBean, HttpHeaders headers) throws Exception {
        String response = null;
        try {

            Long userKey= LearningManagementUtils.getUserId();

            Feedback feedback = new Feedback();
            feedback.setRating(feedbackBean.getRating());
            feedback.setFeedbackcomments(feedbackBean.getComments());
            feedback.setCourseid(feedbackBean.getCourseId());
            feedback.setUserkey(userKey);
            feedback.setIsactive(1);
            feedbackRepository.save(feedback);
            response = "Feedback saved successfully";
        } catch (Exception e) {
            throw new LmsException("Exception occured while saving details", "LMS_004",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;

    }


}
