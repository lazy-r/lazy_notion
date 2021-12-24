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
        iconMap.put("Film", "https://gitee.com/lazy_r/typora/raw/master/img/film.png");
        iconMap.put("Teleplay", "https://gitee.com/lazy_r/typora/raw/master/img/tv.png");
        iconMap.put("Variety", "https://gitee.com/lazy_r/typora/raw/master/img/variety.png");

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
            System.out.println(body);
        }
        return HttpUtil.post("https://api.notion.com/v1/pages", headers, body);
    }


}
