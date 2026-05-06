package com.example.coffee_shop.order.repository;

import com.example.coffee_shop.order.dto.PopularMenuProjection;
import com.example.coffee_shop.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(value = """
        SELECT o.menu_id AS menuId, m.name AS menuName, COUNT(*) AS orderCount
        FROM orders o
        JOIN menu m ON o.menu_id = m.id
        WHERE o.status = 'COMPLETED'
          AND o.ordered_at >= :since
        GROUP BY o.menu_id, m.name
        ORDER BY orderCount DESC
        LIMIT 3
        """, nativeQuery = true)
    List<PopularMenuProjection> findPopularMenus(@Param("since") LocalDateTime since);
}
