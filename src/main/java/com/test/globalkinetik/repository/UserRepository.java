package com.test.globalkinetik.repository;

import com.test.globalkinetik.dto.UserDto;
import com.test.globalkinetik.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * @author S'phokuhle on 9/13/2021
 */
public interface UserRepository extends JpaRepository<Users, Long>, JpaSpecificationExecutor<Users> {
    Optional<Users> findByUsername(String username);

    @Query("Select new com.test.globalkinetik.dto.UserDto(u) From Users u Order By u.id Asc")
    List<UserDto> findAllOrderById();

    List<Users> findByLoggedInTrue();
}
