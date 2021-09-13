package com.test.globalkinetik.service;

import com.test.globalkinetik.model.Users;
import com.test.globalkinetik.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * This class uses cron job scheduled to run after every minute to
 * check for users who had their token expired and update their last logged-in date
 *
 * @author S'phokuhle on 9/13/2021
 */
@Service
@Slf4j
public class SchedulerService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Scheduled(cron = "0 */1 * * * ?")
    public void updateInActiveUsers() {
        List<Users> activeUsers = userRepository.findByLoggedInTrue();
        if(!activeUsers.isEmpty()) {
            for(Users user: activeUsers) {
                if(calculateLapsedMinutes(user) >= 3) {
                    log.info("User {}: token has expired", user.getUsername());
                    user.setActiveDate(new Date());
                    user.setLoggedIn(false);
                    userRepository.save(user);
                }
            }
        }
    }

    private long calculateLapsedMinutes(Users user) {
        if(user.getActiveDate() != null) {
            return TimeUnit.MILLISECONDS.toMinutes(new Date().getTime() - user.getActiveDate().getTime());
        }
        return 0;
    }
}
