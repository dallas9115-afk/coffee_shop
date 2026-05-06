package com.example.coffee_shop.outbox;

import com.example.coffee_shop.menu.entity.Menu;
import com.example.coffee_shop.menu.repository.MenuRepository;
import com.example.coffee_shop.order.dto.OrderRequest;
import com.example.coffee_shop.order.service.OrderService;
import com.example.coffee_shop.outbox.entity.OutboxEvent;
import com.example.coffee_shop.outbox.repository.OutboxEventRepository;
import com.example.coffee_shop.outbox.scheduler.OutboxScheduler;
import com.example.coffee_shop.point.entity.Point;
import com.example.coffee_shop.point.repository.PointRepository;
import com.example.coffee_shop.user.entity.User;
import com.example.coffee_shop.user.repository.UserRepository;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class OutboxIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OutboxScheduler outboxScheduler;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private MenuRepository menuRepository;

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(0);
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("data-platform.api.url",
                () -> "http://localhost:" + wireMockServer.port() + "/mock/data-platform");
    }

    @BeforeEach
    void setUp() {
        outboxEventRepository.deleteAll();
        pointRepository.deleteAll();
        menuRepository.deleteAll();
        userRepository.deleteAll();

        wireMockServer.resetAll();
    }

    @Test
    @DisplayName("주문 → Outbox PENDING → 스케줄러 전송 → SENT 전환")
    void orderAndOutboxSend_success() {
        // given
        User user = saveUser("테스트유저");
        savePoint(user.getId(), 50000L);
        Menu menu = saveMenu("아메리카노", 4500);

        wireMockServer.stubFor(post(urlEqualTo("/mock/data-platform"))
                .willReturn(aResponse().withStatus(200).withBody("received")));

        // when: 주문
        orderService.order(new OrderRequest(user.getId(), menu.getId()));

        // then: Outbox에 PENDING 이벤트 존재
        List<OutboxEvent> pendingEvents = outboxEventRepository.findByStatusOrderByCreatedAtAsc("PENDING");
        assertThat(pendingEvents).hasSize(1);
        assertThat(pendingEvents.get(0).getAggregateType()).isEqualTo("ORDER");

        // when: 스케줄러 수동 실행
        outboxScheduler.pollAndSend();

        // then: SENT로 전환
        List<OutboxEvent> sentEvents = outboxEventRepository.findByStatusOrderByCreatedAtAsc("SENT");
        assertThat(sentEvents).hasSize(1);
        assertThat(sentEvents.get(0).getSentAt()).isNotNull();

        // WireMock 검증
        wireMockServer.verify(1, postRequestedFor(urlEqualTo("/mock/data-platform")));
    }

    @Test
    @DisplayName("전송 실패 시 FAILED 마킹")
    void outboxSend_failure() {
        // given
        User user = saveUser("테스트유저");
        savePoint(user.getId(), 50000L);
        Menu menu = saveMenu("아메리카노", 4500);

        wireMockServer.stubFor(post(urlEqualTo("/mock/data-platform"))
                .willReturn(aResponse().withStatus(500)));

        orderService.order(new OrderRequest(user.getId(), menu.getId()));

        // when: 스케줄러 수동 실행
        outboxScheduler.pollAndSend();

        // then: FAILED로 전환
        List<OutboxEvent> failedEvents = outboxEventRepository.findByStatusOrderByCreatedAtAsc("FAILED");
        assertThat(failedEvents).hasSize(1);
    }

    private User saveUser(String name) {
        try {
            var constructor = User.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            User user = constructor.newInstance();
            var nameField = User.class.getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(user, name);
            return userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void savePoint(Long userId, Long balance) {
        try {
            var constructor = Point.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            Point point = constructor.newInstance();
            var userIdField = Point.class.getDeclaredField("userId");
            userIdField.setAccessible(true);
            userIdField.set(point, userId);
            var balanceField = Point.class.getDeclaredField("balance");
            balanceField.setAccessible(true);
            balanceField.set(point, balance);
            pointRepository.save(point);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Menu saveMenu(String name, int price) {
        try {
            var constructor = Menu.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            Menu menu = constructor.newInstance();
            var nameField = Menu.class.getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(menu, name);
            var priceField = Menu.class.getDeclaredField("price");
            priceField.setAccessible(true);
            priceField.set(menu, price);
            return menuRepository.save(menu);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
