package cn.edu.just.moocweb.service;

public interface ScrapyService {
    String spiderCourse(Long userId);
    String spiderClassroom(Long userId,Integer classroomId);
    Integer checkThread(String threadId);
}
