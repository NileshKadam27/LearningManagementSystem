package com.example.LearningManagementSystem.service;

import com.example.LearningManagementSystem.bean.UserCreationBean;
import com.example.LearningManagementSystem.bean.UserResponse;
import com.example.LearningManagementSystem.entity.Role;
import com.example.LearningManagementSystem.entity.User;
import com.example.LearningManagementSystem.entity.UserProfile;
import com.example.LearningManagementSystem.exception.LmsException;
import com.example.LearningManagementSystem.exception.UserNameAlreadyExist;
import com.example.LearningManagementSystem.repository.RoleRepository;
import com.example.LearningManagementSystem.repository.UserProfileRepository;
import com.example.LearningManagementSystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserProfileRepository userProfileRepository;

    @Override
    public UserResponse createUser(UserCreationBean userCreationBean) throws Exception {

        UserResponse userResponse = new UserResponse();
        try{
            User existingUser = userRepository.findByUsername(userCreationBean.getUsername());
            if(existingUser != null){
                throw new UserNameAlreadyExist("Username already exist in database.");
            }
            Role role = roleRepository.findByRolename(userCreationBean.getRoleCode());
            User user = saveUser(userCreationBean);
            saveUserprofile(userCreationBean, user);
            userResponse.setId(user.getId());
            userResponse.setRolekey(user.getRolekey());
            userResponse.setUsername(user.getUsername());

        }catch (LmsException ex){
            throw ex;
        }catch (Exception e){
            throw new LmsException("Exception occurred while register new user","LMS_001", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return userResponse;
    }

    private void saveUserprofile(UserCreationBean userCreationBean, User user) {
        UserProfile userProfile = new UserProfile();
        userProfile.setFirstname(userCreationBean.getFirstname());
        userProfile.setLastname(userCreationBean.getLastname());
        userProfile.setFirstname(userCreationBean.getFirstname());
        userProfile.setEmail(userCreationBean.getEmail());
        userProfile.setMobile(userCreationBean.getMobile());
        userProfile.setUserkey(user.getId());
        userProfile.setExperience(userCreationBean.getExperience());
        userProfileRepository.save(userProfile);
    }

    private User saveUser(UserCreationBean userCreationBean) {
        User user = new User();
        user.setUsername(userCreationBean.getUsername());
        user.setPassword(userCreationBean.getPassword());
        Role role = roleRepository.findByRolename(userCreationBean.getRoleCode());
        user.setRolekey(role.getId());
        user = userRepository.save(user);
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username);
        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(getRoles(user))
                .build();
    }

    private String[] getRoles(User user) {
        Optional<Role> optionalRole = roleRepository.findById(user.getRolekey());
        if (!optionalRole.isPresent()) {
            return new String[]{"user"};
        }
        return optionalRole.get().getRolename().split(",");
    }
}
