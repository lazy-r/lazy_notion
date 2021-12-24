package top.lazyr.notion.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lazyr
 * @created 2021/12/19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieItem {
    private String url;
    private String status;
    private String type;
}
