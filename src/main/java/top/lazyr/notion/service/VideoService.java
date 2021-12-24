package top.lazyr.notion.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import top.lazyr.notion.util.HttpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lazyr
 * @created 2021/12/18
 */
@Service
public class VideoService {
    private Logger logger = LoggerFactory.getLogger(VideoService.class);

    public Map<String, Object> getVideoNameLink(String keyword) {
        String data = HttpUtil.get("https://www.douban.com/search?cat=1002&q=" + keyword);
        Map<String, Object> nameLink = new HashMap<>();
        if (data == null) {
            return nameLink;
        }
        Document document = Jsoup.parse(data);
        Elements results = document.getElementsByClass("result");
        List<String> movieNames = new ArrayList<>();
        for (Element result : results) {
            // 1、获取movie_name
            String title = result.getElementsByClass("title").get(0).getElementsByTag("a").text();
            String intro = result.getElementsByClass("subject-cast").text();
            // 2、获取电影link
            String link = result.getElementsByClass("title").get(0).getElementsByTag("a").get(0).attr("href");
            nameLink.put(title + " / " + intro, link);
            movieNames.add(title + " / " + intro);
        }
        nameLink.put("movie_name", movieNames);
        return nameLink;
    }

    public Map<String, Object> getVideoNameTypeCoverEpisodes(String url) {
        String data = HttpUtil.get(url);

        Map<String, Object> movieInfo = new HashMap<>();
        if (data == null) {
            return movieInfo;
        }

        Document document = Jsoup.parse(data);
        // 1、获取电影名
        Elements title = document.getElementsByTag("title");
        String movieName = title.text().split(" ")[0];
        movieInfo.put("$movieName", movieName);

        // 2、获取label
        Elements property = document.getElementsByAttributeValue("property", "v:genre");
        List<String> labels = new ArrayList<>();
        for (Element label : property) {
            labels.add(label.text());
        }
        movieInfo.put("$labels", labels);

        // 3、获取pubYear
        Elements pubYears = document.getElementsByAttributeValue("property", "v:initialReleaseDate");
        String pubYear = pubYears.get(0).text().split("-")[0];
        movieInfo.put("$pubYear", pubYear);

        // 4、获取imgUrl
        String imgUrl = document.getElementById("mainpic").getElementsByTag("img").get(0).attr("src");
        movieInfo.put("$imgUrl", imgUrl);

        // 5、获取episodes
        Element info = document.getElementById("info");
        Elements pls = info.getElementsByClass("pl");
        String episodes = "1";
        for (Element pl : pls) {
            if (pl.text().equals("集数:")) {
                episodes = pl.nextSibling().toString().trim();
            }
        }
        movieInfo.put("$episodes", episodes);
        return movieInfo;
    }

    public String createVideoItem(String url, String status, String type) {
        /*
            $iconUrl、$imgUrl、$labels、$status、$type、$movieName、$episodes、$pubYear
        */
        String body = "{\"parent\":{\"database_id\":\"55a0e6c5b83f48a5aee47b5812e16231\"},\"properties\":{\"Name\":{\"title\":[{\"type\":\"text\",\"text\":{\"content\":\"$movieName\",\"link\":null}}]},\"Type\":{\"type\":\"select\",\"select\":{\"name\":\"$type\"}},\"Status\":{\"type\":\"select\",\"select\":{\"name\":\"$status\"}},\"Label\":{\"type\":\"multi_select\",\"multi_select\":$labels},\"Episodes\":{\"type\":\"number\",\"number\":$episodes},\"PubYear\":{\"type\":\"number\",\"number\":$pubYear}},\"children\":[{\"type\":\"image\",\"image\":{\"type\":\"external\",\"external\":{\"url\":\"$imgUrl\"}}}],\"icon\":{\"type\":\"external\",\"external\":{\"url\":\"$iconUrl\"}}}";
        Map<String, Object> movieNameTypeCoverPubYearEpisodes = getVideoNameTypeCoverEpisodes(url);
        logger.info("search movie info => " + movieNameTypeCoverPubYearEpisodes);

        // 初始化iconMap
        Map<String, String> iconMap = new HashMap<>();
        iconMap.put("Film", "https://www.notion.so/image/https%3A%2F%2Fgitee.com%2Flazy_r%2Ftypora%2Fraw%2Fmaster%2Fimg%2Fprojector.png?table=block&id=9559f696-d63b-4bda-b870-630c596744b0&spaceId=67f4e34b-cceb-4e66-a61a-c72650af038e&width=250&userId=c60e8b5d-c151-4605-8f52-948dddd4d5d3&cache=v2");
        iconMap.put("Teleplay", "https://www.notion.so/image/https%3A%2F%2Fs3-us-west-2.amazonaws.com%2Fsecure.notion-static.com%2F6892a844-9d7e-48a7-b0c2-e227734276fc%2Ftv.png?table=block&id=e476a314-99f4-4033-a4ea-be4dd2e0f1e3&spaceId=67f4e34b-cceb-4e66-a61a-c72650af038e&width=250&userId=c60e8b5d-c151-4605-8f52-948dddd4d5d3&cache=v2");
        iconMap.put("Variety", "https://www.notion.so/image/https%3A%2F%2Fs3-us-west-2.amazonaws.com%2Fsecure.notion-static.com%2F0fa1bb0a-722c-49dd-8304-fe45ada6aa7c%2F%E7%BB%BC%E8%89%BA.png?table=block&id=824717ee-feff-409c-82da-c8c1f6259483&spaceId=67f4e34b-cceb-4e66-a61a-c72650af038e&width=250&userId=c60e8b5d-c151-4605-8f52-948dddd4d5d3&cache=v2");

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("$imgUrl", (String)movieNameTypeCoverPubYearEpisodes.get("$imgUrl"));
        placeholders.put("$iconUrl", iconMap.get(type));
        placeholders.put("$episodes", (String) movieNameTypeCoverPubYearEpisodes.get("$episodes"));

        List<String> labelList = (List<String>) movieNameTypeCoverPubYearEpisodes.get("$labels");
        String labels = "";
        if (labels != null) {
            /*
                [{"name":"剧情"},{"name": "历史"}]
            */
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            for (int i = 0; i < labelList.size(); i++) {
                builder.append("{\"name\":\"" + labelList.get(i) + "\"}");
                if (i != labelList.size() - 1) {
                    builder.append(",");
                }
            }
            builder.append("]");
            labels = builder.toString();
        } else {
            labels = "[]";
        }

        String pubYear = (String) movieNameTypeCoverPubYearEpisodes.get("$pubYear");
        String movieName = (String) movieNameTypeCoverPubYearEpisodes.get("$movieName");
        placeholders.put("$labels", labels);
        placeholders.put("$type", type);
        placeholders.put("$movieName", movieName);
        placeholders.put("$pubYear", pubYear);
        placeholders.put("$status", status);


        // 设置header
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer secret_4rt5CBFf72gfE4JujkaHxotXeAiKOwjXdGZsUtWrcQ3");
        headers.put("Content-Type", "application/json");
        headers.put("Notion-Version", "2021-08-16");


        for (String placeholder : placeholders.keySet()) {
            body = body.replace(placeholder, placeholders.get(placeholder));
        }
        return HttpUtil.post("https://api.notion.com/v1/pages", headers, body);
    }



}
