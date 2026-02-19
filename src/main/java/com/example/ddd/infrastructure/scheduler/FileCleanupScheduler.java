package com.example.ddd.infrastructure.scheduler;

import com.example.ddd.application.service.FileApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 文件清理定时任务
 * 每天02:00运行，清理过期的临时文件和已删除文件
 *
 * @author DDD Demo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileCleanupScheduler {

    private final FileApplicationService fileApplicationService;

    /**
     * 每天凌晨02:00执行，清理过期文件
     * cron表达式: 秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredFiles() {
        log.info("开始执行文件清理任务...");
        try {
            int count = fileApplicationService.cleanExpiredFiles();
            log.info("文件清理任务完成，共清理 {} 个过期文件", count);
        } catch (Exception e) {
            log.error("文件清理任务执行失败", e);
        }
    }
}
