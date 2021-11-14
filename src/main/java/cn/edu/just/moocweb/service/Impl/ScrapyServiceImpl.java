package cn.edu.just.moocweb.service.Impl;

import cn.edu.just.moocweb.service.ScrapyService;
import cn.edu.just.moocweb.utils.RunScrapy;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ScrapyServiceImpl extends BaseService implements ScrapyService {

    @Override
    public String spiderCourse(Long userID) {
        String threadId = UUID.randomUUID().toString();
        new RunScrapy.ScrapyThread(threadId,"mooc","u="+userID).start();
        return threadId;
    }

    @Override
    public String spiderClassroom(Long userID , Integer classroomId) {
        String threadId = UUID.randomUUID().toString();
        new RunScrapy.ScrapyThread(threadId,"course","u="+userID,"c="+classroomId).start();
        return threadId;
    }

    @Override
    public Integer checkThread(String threadId) {
        try {
            return RunScrapy.pools.get(threadId).getStatus();
        }
        catch (Exception ex){
            return -100;
        }
    }
}
