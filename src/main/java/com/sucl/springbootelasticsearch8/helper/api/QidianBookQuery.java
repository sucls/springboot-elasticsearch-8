package com.sucl.springbootelasticsearch8.helper.api;

import com.sucl.springbootelasticsearch8.document.Novel;
import com.sucl.springbootelasticsearch8.helper.NovelQuerySupport;
import com.sucl.springbootelasticsearch8.helper.NovelQuerySupport.BookQuery;
import com.sucl.springbootelasticsearch8.helper.NovelQuerySupport.RequestParam;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.concurrent.FutureUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 起点小说
 *  -主页
 *  - 分类页
 *    - 目录页
 *      - 内容页
 *
 * @author sucl
 * @since 1.0.0
 */
@Slf4j
public class QidianBookQuery implements BookQuery {

    private static final String QIDIAN_FREE_URL = "https://www.qidian.com/free/all/";

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    /**
     *
     * @param requestParam 请求参数
     * @return 返回获取到的小说
     */
    @Override
    public List<Novel> queryNovels(RequestParam requestParam){
        // 分类分页文档
        return doQueryNovels(requestParam, null)
                .map(novels->{log.info(">>> 完成加载页起点小说数据..."); return novels; })
                .reduce(new ArrayList<>(), this::addParts, (novels, parts)->null );
    }

    /**
     *
     * @param requestParam
     * @param pageConsumer
     */
    @Override
    public void queryNovels(RequestParam requestParam, Consumer<List<Novel>> pageConsumer){
        doQueryNovels(requestParam, pageConsumer);
    }

    private Stream<List<Novel>> doQueryNovels(RequestParam requestParam, Consumer<List<Novel>> pageConsumer){
        AtomicInteger page = new AtomicInteger(1);
        return IntStream.rangeClosed(requestParam.getPageStart(), requestParam.getPageIndex()) // 解析页码
                .mapToObj(index -> loadTypePageElements(requestParam.copy().setPageIndex(index))) // 按类型加载页文档
                .map(typePageElements -> {
                    log.info(">>> 开始加载第【{}】页数据", page.getAndIncrement());
                    List<Novel> novels = typePageElements
                            .stream()
                            .map(this::parsePageItemElem)
                            .map(this::getFutureValue)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    if( pageConsumer != null ){
                        pageConsumer.accept(novels);
                    }
                    return novels;
                });
    }
    public <V> V getFutureValue(Future<V> future){
        try {
            return future.get();
        } catch (InterruptedException|ExecutionException e) {
            log.warn("获取Future值出错：{}", e.getMessage());
            return null;
        }
    }

    public List addParts(List list, List part){
        if( list != null && part != null ){
            list.addAll(part);
        }
        return list;
    }

    private CompletableFuture<Novel> parsePageItemElem(Element pageItemElem) {
        return FutureUtils.callAsync(() -> { // 每页每项的内容
            Novel novel = newNovel(pageItemElem);
            log.info(">>> 开始加载小说【{}】数据", novel.getTitle());
            Elements chapterElems = loadChapterElements(pageItemElem); // 章节文档
            novel.setContent(loadChapterContent(chapterElems));
            return novel;
        }, executorService);
    }

    private String buildQueryUrl(RequestParam requestParam) {
        StringBuilder urlBuilder = new StringBuilder();
        Type type = Type.of(requestParam.getType());
        SubType subType = SubType.of(requestParam.getSubType());
        urlBuilder.append(QIDIAN_FREE_URL).append("/").append(type.code);
        if( subType != null ){
            urlBuilder.append("-").append(subType.code);
        }
        urlBuilder.append("-page").append(requestParam.getPageIndex());
        return urlBuilder.toString();
    }

    public Elements loadTypePageElements(RequestParam requestParam){
        log.info(">>> 开始加载第【{}】页起点小说数据...", requestParam.getPageIndex());

        String typePageUrl = requestParam.getType()!=null? buildQueryUrl(requestParam) : QIDIAN_FREE_URL;
        Document document = null;
        try {
            document = Jsoup.connect(typePageUrl).get();
        } catch (IOException e) {
            log.error("根据url【{}】获取分类页文档出错：{}", typePageUrl, e.getMessage());
        }
        if( document != null ){
            return document.select(".all-book-list ul>li");
        }
        return null;
    }

    public Elements loadChapterElements(Element novelElement){
        // 小说明细
        Element bookInfoElem = novelElement.selectFirst(".book-mid-info");
        String bookUrl = bookInfoElem.selectFirst("a").attr("href");
        // 查询小说章节目录
        try {
            return Jsoup.connect(wrapUrl(bookUrl) + "#Catalog").get().select(".catalog-content-wrap .book_name");
        } catch (IOException e) {
            log.error("根据url【{}】获取章节文档出错：{}", bookUrl, e.getMessage());
        }
        return null;
    }

    public String loadChapterContent(Elements chapterElems){
        Map<Integer, String> result = new ConcurrentHashMap<>();
        AtomicInteger index = new AtomicInteger(1);
        List<CompletableFuture> futures = new ArrayList<>();
        chapterElems.forEach(chapterElem->{
            futures.add(CompletableFuture.runAsync(()->{
                result.put(index.getAndIncrement(), resolveContentFromElement(loadItemContentElement(chapterElem)));
            }));
        });
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return new ArrayList<>(result.keySet()).stream().sorted().map(result::get).filter(Objects::nonNull).collect(Collectors.joining(""));
    }

    public Element loadItemContentElement(Element element){
        String catalogHref = element.firstChild().attr("href");
        try {
            Document document = Jsoup.connect(wrapUrl(catalogHref)).get();
            String catalogId = findCatalogId(catalogHref);
            return document.selectFirst("[data-id='" + catalogId + "']");
        } catch (IOException e) {
            log.error("通过地址【{}】获取章节内容失败：{}", catalogHref, e.getMessage());
        }
        return null;
    }

    private Novel newNovel(Element novelElement){
        Novel novel = new Novel();
        // 封面元素
        Element imgElem = novelElement.selectFirst(".book-img-box");
        novel.setCoverUrl(imgElem.selectFirst("img").attr("src"));
        // 小说明细
        Element bookInfoElem = novelElement.selectFirst(".book-mid-info");
        novel.setTitle(bookInfoElem.firstElementChild().text());
        novel.setStatus(bookInfoElem.selectFirst(".author>span").text());

        Elements elems = bookInfoElem.select(".author>a");
        novel.setAuthor(elems.get(0).text());
        novel.setType(elems.get(1).text());
        novel.setCategory(new String[]{elems.get(2).text()});
        novel.setDescription(bookInfoElem.select(".intro").text());
        return novel.genId();
    }

    private String resolveContentFromElement(Element elem) {
        StringBuilder content = new StringBuilder();
        elem.children().forEach(element -> {
            // review
            // main解析时把最后一个div加进去了
            if("h1".equals(element.tagName()) || "main".equals(element.tagName())){
                content.append(element.text());
            }
        });
        return content.toString();
    }

    private String findCatalogId(String catalogHref) {
        String value = catalogHref.substring(0, catalogHref.length() - 1);
        return value.substring(value.lastIndexOf("/")+1);
    }

    private String wrapUrl(String url){
        if( url.startsWith("//") ){
            return "https:" + url;
        }
        return url;
    }

    public enum Type{
        /**
         * 玄幻
         */
        XH("chanId21", "玄幻"),
        QH("chanId1", "奇幻"),
        WX("chanId2", "武侠"),
        XX("chanId22", "仙侠"),
        DS("chanId4", "都市"),
        XS("chanId15", "现实"),
        JS("chanId6", "军事"),
        LS("chanId5", "历史"),
        YX("chanId7", "游戏"),
        TY("chanId8", "体育"),
        KH("chanId9", "科幻"),
        ZTWX("chanId20109", "诸天无限"),
        XY("chanId10", "悬疑"),
        QXS("chanId12", "轻小说"),
        DP("chanId20076", "短篇"),
        ;

        private String code;

        private String caption;
        Type(String code, String caption){
            this.code = code;
            this.caption = caption;
        }

        public static Type of(String name){
            try {
                return name == null? null : valueOf(name);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    public enum SubType{
        /**
         * 东方玄幻
         */
        DFXH(Type.XH, "subCateId8","东方玄幻");

        private Type parentType;
        private String code;
        private String caption;
        SubType(Type parentType,String code, String caption){
            this.parentType = parentType;
            this.code = code;
            this.caption = caption;
        }

        public static SubType of(String name){
            try {
                return name == null? null : valueOf(name);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    public static void main(String[] args) {
        QidianBookQuery qidianBookQuery = new QidianBookQuery();
        List<Novel> novels = qidianBookQuery.queryNovels(NovelQuerySupport.RequestParam.of().setType(Type.YX.name()).setPageIndex(1));
        System.out.println( novels );
    }
}
