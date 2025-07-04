package com.springstudy.backend.API.Repository;

import com.springstudy.backend.API.Repository.Entity.Order;
import com.springstudy.backend.API.Repository.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * ✅ 주문 기본 키(ID)로 주문 정보 조회
     */
    Optional<Order> findById(Long id);  // ✅ `id` 기반 조회 추가


    /**
     * ✅ 주문 ID(uuid)로 주문 정보 조회
     */
    Optional<Order> findByUuid(String uuid);

    /**
     * 특정 사용자의 주문 정보 조회
     */
    List<Order> findByUserAndStatusNot(User user, String status);  // ✅ CANCELED 제외

    @Query("""
    SELECT o FROM Order o
    JOIN FETCH o.user
    JOIN FETCH o.screening s
    WHERE o.status = 'PAID'
      AND o.notified = false
      AND FUNCTION('TIMESTAMP', s.date, s.start) BETWEEN :now AND :cutoff
""")
    List<Order> findPaidOrdersToNotify(@Param("now") LocalDateTime now, @Param("cutoff") LocalDateTime cutoff);

    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING' AND o.createdAt <= :cutoff")
    List<Order> findPendingOrdersBefore(@Param("cutoff") LocalDateTime cutoff);

    /**
     * ✅ 특정 주문 상태인 주문이 존재하는지 확인 (PENDING, PAID)
     */
    boolean existsByUuidAndStatus(String uuid, String status);  // ✅ 수정됨

    /**
     * ✅ 특정 사용자의 특정 상태(PENDING, PAID)의 주문 존재 여부 확인
     */
    boolean existsByUserIdAndStatus(Long userId, String status);

    Optional<Object> findByStatus(String pending);

}
