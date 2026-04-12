package com.budu.finance.repository;

import com.budu.finance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 如果之後需要自訂查詢，可以在這裡加方法
}