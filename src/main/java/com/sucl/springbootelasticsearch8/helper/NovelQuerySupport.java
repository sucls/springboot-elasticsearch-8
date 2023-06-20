package com.sucl.springbootelasticsearch8.helper;

import com.sucl.springbootelasticsearch8.document.Novel;
import com.sucl.springbootelasticsearch8.helper.api.QidianBookQuery;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author sucl
 * @date 2023/6/8 18:38
 * @since 1.0.0
 */
public class NovelQuerySupport {

    /**
     *
     */
    public interface BookQuery{
        List<Novel> queryNovels(RequestParam requestParam);

        void queryNovels(RequestParam requestParam, Consumer<List<Novel>> pageConsumer);
    }


    @Data(staticConstructor="of")
    @Accessors(chain = true)
    public static class RequestParam{
        private String type;
        private String subType;
        private int pageStart = 1;
        private int pageIndex;

        public RequestParam copy() {
            RequestParam requestParam = new RequestParam();
            requestParam.setType(this.type).setSubType(this.subType).setPageIndex(this.pageIndex);
            return requestParam;
        }
    }

    public static BookQuery provide(){
        return new QidianBookQuery();
    }

    /**
     *
     * @param bookQuery
     * @param contentResolver
     * @return
     */
    public static List<Novel> queryWithOptions(RequestParam requestParam){
        return provide().queryNovels(requestParam);
    }

}
