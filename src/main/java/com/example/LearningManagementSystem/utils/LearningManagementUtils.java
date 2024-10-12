package com.example.LearningManagementSystem.utils;

import com.example.LearningManagementSystem.bean.CustomerUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class LearningManagementUtils {

    public static Long getUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication!=null){
            CustomerUserDetails userDetails = (CustomerUserDetails) authentication.getPrincipal();
            return  userDetails.getUser().getId();
        }
        return null;
    }

    public static String getUserRole(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication!=null){
            CustomerUserDetails userDetails = (CustomerUserDetails) authentication.getPrincipal();
            return  userDetails.getRole();
        }
        return null;
    }
}
