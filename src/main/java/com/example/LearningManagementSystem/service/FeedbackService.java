package com.example.LearningManagementSystem.service;


import com.example.LearningManagementSystem.bean.FeedbackBean;
import org.springframework.http.HttpHeaders;

import java.util.List;

public interface FeedbackService {



    public List<FeedbackBean> getFeedbackByCourseId(Long courseid, HttpHeaders headers) throws Exception;

    public String addFeedback(FeedbackBean feedbackBean, HttpHeaders headers) throws Exception;

}
