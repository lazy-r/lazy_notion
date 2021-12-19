package top.lazyr.notion;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.lazyr.notion.properties.Robot;

@SpringBootTest
class LazyNotionApplicationTests {
    @Autowired
    private Robot robot;
    @Test
    void contextLoads() {
        System.out.println(robot.getPrivateRobot());
    }

}
