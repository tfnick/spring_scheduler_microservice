package com.sidorii.scheduler;

import com.sidorii.scheduler.model.CustomHttpHeaders;
import com.sidorii.scheduler.model.exception.ConfigurationException;
import com.sidorii.scheduler.model.job.config.DefaultJobConfigurerImpl;
import com.sidorii.scheduler.model.job.config.JobConfiguration;
import com.sidorii.scheduler.model.service.ScheduleService;
import com.sidorii.scheduler.model.task.HttpTask;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

import static org.quartz.DateBuilder.dateOf;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ScheduleTest {

    @Autowired
    public DefaultJobConfigurerImpl jobConfigurer;

    @Autowired
    public ScheduleService service;

    private JobConfiguration jobConfiguration;

    @Before
    public void setUp() throws MalformedURLException {
        jobConfiguration = new JobConfiguration();

        HttpTask task = new HttpTask();
        Date time = Date.from(Instant.now());
        Date entTime = dateOf(1, 30, 30);
        task.setUrl(new URL("http://result.com"));
        task.setMethod(HttpMethod.PUT);


        CustomHttpHeaders customHttpHeaders = new CustomHttpHeaders();
        customHttpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        customHttpHeaders.setContentType(MediaType.APPLICATION_JSON);
        customHttpHeaders.setAuthorization("test authority");
        task.setHeaders(customHttpHeaders);


        jobConfiguration.setType("http");
        jobConfiguration.setTask(task);
        jobConfiguration.setEndTime(entTime);
        jobConfiguration.setStartTime(time);
        jobConfiguration.setScheduledAt("0/2 * * * * ?");
        jobConfiguration.setExecuteTimes(10);
        jobConfiguration.setTimeZone(TimeZone.getTimeZone("UA"));
    }

    @Test //Cron trigger triggered
    @Ignore
    public void testSchedule() throws ConfigurationException, InterruptedException {

        JobDetail detail = jobConfigurer.buildJob(jobConfiguration);
        Trigger trigger = jobConfigurer.buildTrigger(jobConfiguration);

        service.addJob(detail, trigger);

        Thread.sleep(1000);
    }

    @Test
    public void testScheduleSimpleTriger() throws ConfigurationException, InterruptedException {

        jobConfiguration.setScheduledAt(null);

        JobDetail detail = jobConfigurer.buildJob(jobConfiguration);
        Trigger trigger = jobConfigurer.buildTrigger(jobConfiguration);

        service.addJob(detail, trigger);

        Thread.sleep(30000); //Expected: execution time 10. Required time: 20 seconds
    }
}
