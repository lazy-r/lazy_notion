package top.lazyr.notion.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * @author lazyr
 * @created 2021/12/16
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "robot")
public class Robot {
    private String privateRobot;
}
