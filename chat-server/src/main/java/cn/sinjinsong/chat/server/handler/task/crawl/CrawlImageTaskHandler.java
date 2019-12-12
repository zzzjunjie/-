package cn.sinjinsong.chat.server.handler.task.crawl;

import cn.sinjinsong.chat.server.handler.task.BaseTaskHandler;
import cn.sinjinsong.chat.server.task.TaskManagerThread;
import cn.sinjinsong.chat.server.util.ImageURLCrawlerUtil;
import cn.sinjinsong.chat.server.util.RequestParser;
import cn.sinjinsong.common.domain.MessageHeader;
import cn.sinjinsong.common.domain.Request;
import cn.sinjinsong.common.domain.Response;
import cn.sinjinsong.common.domain.ResponseHeader;
import cn.sinjinsong.common.enumeration.ResponseType;
import cn.sinjinsong.common.util.ZipUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by SinjinSong on 2017/5/25.
 */
@Component("BaseTaskHandler.crawl_image")
@Scope("prototype")
@Slf4j
public class CrawlImageTaskHandler extends BaseTaskHandler {
    private ExecutorService crawlerPool;

    /**
     * 当向Executor提交批处理任务时，并且希望在它们完成后获得结果，如果用FutureTask，
     * 你可以循环获取task，并用future.get()去获取结果，但是如果这个task没有完成，你就得阻塞在这里，
     * 这个实效性不高，其实在很多场合，其实你拿第一个任务结果时，此时结果并没有生成并阻塞，
     * 其实在阻塞在第一个任务时，第二个task的任务已经早就完成了，显然这种情况用future task不合适的，效率也不高。
     *
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected Response process() throws IOException, InterruptedException {
        MessageHeader header = info.getMessage().getHeader();
        Request request = RequestParser.parse(info.getDesc());
        List<String> urls = ImageURLCrawlerUtil.crawl(request);
        urls.forEach(System.out::println);
        String suffix = null;
        if (urls.size() > 0) {
            suffix = urls.get(0).substring(urls.get(0).lastIndexOf('.') + 1);
        }
        CompletionService<byte[]> completionService = new ExecutorCompletionService<>(crawlerPool);
        //先提交任务
        for (String url : urls) {
            completionService.submit(new ImageThread(url, manager));
        }
        List<byte[]> result = new ArrayList<>();
        //取出完成了的任务结果
        byte[] image;
        for (int i = 0; i < urls.size(); i++) {
            Future<byte[]> future = completionService.take();
            try {
                image = future.get();
                result.add(image);
            } catch (ExecutionException e) {
                //即使有下载任务失败，也不影响，继续下载
                log.info("有图片下载失败");
            }
        }
        result.forEach((bytes) -> System.out.println(bytes.length));
        return new Response(ResponseHeader.builder()
                .type(ResponseType.FILE)
                .sender(header.getSender())
                .timestamp(header.getTimestamp())
                .build(),
                ZipUtil.zipCompress(result,suffix));
    }

    @Override
    protected void init(TaskManagerThread parentThread) {
        this.crawlerPool = parentThread.getCrawlerPool();
    }

}
