package top.lazyr.notion.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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

    public Map<String, Object> getMovieNameLink(String keyword) {
        String data = HttpUtil.get("https://www.1905.com/search/?q=" + keyword);
        if (data == null) {
            return null;
        }
        Document document = Jsoup.parse(data);;
        Map<String, Object> nameLink = new HashMap<>();

        Elements movie_box = document.getElementsByClass("movie_box");
        for (Element movieBox : movie_box) {
            for (Element titleMV : movieBox.getElementsByClass("title-mv")) {
                Elements a = titleMV.getElementsByTag("a");
                nameLink.put(a.text(), a.get(0).attr("href"));
            }
        }
        List<String> movieNames = new ArrayList<>();
        for (String name : nameLink.keySet()) {
            movieNames.add(name);
        }
        nameLink.put("movie_name", movieNames);

        return nameLink;
    }

    public Map<String, String> getMovieTypeCover(String url) {
        String data = HttpUtil.get(url);
        if (data == null) {
            return null;
        }
        Map<String, String> movieInfo = new HashMap<>();
        Document document = Jsoup.parse(data);;
        Elements movie = document.getElementsByClass("movie");
        for (Element table : movie) { // 获取类型
            for (Element tr : table.getElementsByTag("tr")) {
                Elements tds = tr.getElementsByTag("td");
                for (Element td : tds) {
                    if (td.text().contains("类型：")) {
                        movieInfo.put("labels", td.text().split("：")[1].trim());
                    }
                }
            }
        }

        Elements imgs = document.getElementsByTag("img");
        for (Element img : imgs) { // 获取图片链接
            Elements movieImg = img.getElementsByClass("poster");
            if (movieImg != null) {
                if (!movieImg.attr("src").equals("")) {
                    movieInfo.put("imgUrl", movieImg.attr("src"));
                }

            }
        }

        return movieInfo;
    }

    public String createMovieItem(String movieName, String url, String status) {
        /*
            $iconUrl、$imgUrl、$labels、$status、$type、$movieName、$episodes、$pubYear
        */
        String body = "{\"parent\":{\"database_id\":\"55a0e6c5b83f48a5aee47b5812e16231\"},\"properties\":{\"Name\":{\"title\":[{\"type\":\"text\",\"text\":{\"content\":\"$movieName\",\"link\":null}}]},\"Type\":{\"type\":\"select\",\"select\":{\"name\":\"$type\"}},\"Status\":{\"type\":\"select\",\"select\":{\"name\":\"$status\"}},\"Label\":{\"type\":\"multi_select\",\"multi_select\":$labels},\"Episodes\":{\"type\":\"number\",\"number\":$episodes},\"PubYear\":{\"type\":\"number\",\"number\":$pubYear}},\"children\":[{\"type\":\"image\",\"image\":{\"type\":\"external\",\"external\":{\"url\":\"$imgUrl\"}}}],\"icon\":{\"type\":\"external\",\"external\":{\"url\":\"$iconUrl\"}}}";
        Map<String, String> movieTypeCover = getMovieTypeCover(url);
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("$imgUrl", movieTypeCover.get("imgUrl"));
        placeholders.put("$iconUrl", "https://gitee.com/lazy_r/typora/raw/master/img/projector.png");
        placeholders.put("$episodes", "1");

        String labels = movieTypeCover.get("labels");
        if (labels != null) {
            /*
                [{"name":"剧情"},{"name": "历史"}]
            */
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            String split = " ";
            if (labels.contains(" ")) {
                split = " ";
            }
            String[] labelArr = labels.split(split);
            for (int i = 0; i < labelArr.length; i++) {
                builder.append("{\"name\":\"" + labelArr[i] + "\"}");
                if (i != labelArr.length - 1) {
                    builder.append(",");
                }
            }
            builder.append("]");
            labels = builder.toString();
        } else {
            labels = "[]";
        }

        String[] namePubYear = movieName.split("\\(");
        placeholders.put("$labels", labels);
        placeholders.put("$type", "Film");
        placeholders.put("$movieName", namePubYear[0].trim());
        placeholders.put("$pubYear", namePubYear[1].substring(0, namePubYear[1].length() - 1));
        placeholders.put("$status", status);

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
