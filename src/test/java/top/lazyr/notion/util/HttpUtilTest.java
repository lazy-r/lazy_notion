package top.lazyr.notion.util;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class HttpUtilTest {

    @Test
    public void post() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer secret_3nJWtCo64J89MMZezGFxPxSxHLj2wUGEY39EsCSTXFI");
        headers.put("Content-Type", "application/json");
        headers.put("Notion-Version", "2021-08-16");
        String body = "{\"parent\":{\"database_id\":\"bf556e2d-91b0-4be7-bf41-412ed4231142\"},\"properties\":{\"Day\":{\"title\":[{\"type\":\"mention\",\"mention\":{\"type\":\"date\",\"date\":{\"start\":\"2021-12-15\",\"end\":null,\"time_zone\":null}}}]}},\"children\":[{\"type\":\"heading_3\",\"heading_3\":{\"text\":[{\"type\":\"text\",\"text\":{\"content\":\"Why\",\"link\":null},\"annotations\":{\"bold\":true,\"italic\":false,\"strikethrough\":false,\"underline\":false,\"code\":false,\"color\":\"default\"}}]}},{\"type\":\"quote\",\"quote\":{\"text\":[{\"type\":\"text\",\"text\":{\"content\":\"Why内容\"},\"annotations\":{\"bold\":true,\"italic\":false,\"strikethrough\":false,\"underline\":false,\"code\":false,\"color\":\"default\"}}]}},{\"object\":\"block\",\"type\":\"heading_3\",\"heading_3\":{\"text\":[{\"type\":\"text\",\"text\":{\"content\":\"Thought\",\"link\":null},\"annotations\":{\"bold\":true,\"italic\":false,\"strikethrough\":false,\"underline\":false,\"code\":false,\"color\":\"default\"}}]}},{\"type\":\"quote\",\"quote\":{\"text\":[{\"type\":\"text\",\"text\":{\"content\":\"Thought内容\"},\"annotations\":{\"bold\":false,\"italic\":false,\"strikethrough\":false,\"underline\":false,\"code\":false,\"color\":\"default\"}}]}}],\"icon\":{\"type\":\"emoji\",\"emoji\":\"\uD83E\uDD14\"}}";
        HttpUtil.post("https://api.notion.com/v1/pages", headers, body);
    }
}
