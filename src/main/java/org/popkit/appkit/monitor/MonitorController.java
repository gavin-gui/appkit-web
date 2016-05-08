package org.popkit.appkit.monitor;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.popkit.appkit.common.controller.BaseController;
import org.popkit.appkit.common.utils.ResponseUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Aborn Jiang
 * Mail aborn.jiang@gmail.com
 * 2016-05-07:21:34
 */
@Controller
@RequestMapping(value = "monitor")
public class MonitorController extends BaseController {
    private static final DateTimeFormatter DEFAULT_FORMAT = DateTimeFormat.forPattern("MM-dd HH:mm");
    private static final DateTimeFormatter SHORT_FORMAT = DateTimeFormat.forPattern("MM-dd HH:");
    private static final DateTimeFormatter LOG_DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"); //2015-09-11 01:17:51
    private static final String LOG_FILE_NAME = "/Users/aborn/github/appkit-web/shadowsocks.log";

    @RequestMapping(value = "ss.html")
    public String ss() {
        return "monitor/ss-monitor";
    }

    @RequestMapping(value = "ajaxss.json")
    public void ajaxss(Integer type, HttpServletResponse response) {
        List<String> labels = buildLabels("2015-09-11 01:17:51");
        List<Integer> data = new ArrayList<Integer>();
        for (String item : labels) {
            data.add(0);
        }

        EachLine eachLine = new EachLine(labels, data);
        List<EachLogItem> logItems = readLogFileContent(getDateTime("2015-09-11 01:17:51"));

        doStatistics(logItems, labels, data, 10);
        ResponseUtils.renderJson(response, eachLine.toString());
    }

    private void doStatistics(List<EachLogItem> logItems, List<String> labels, List<Integer> date, int step) {
        Map<String, Integer> indexMap = buildMap(labels);
        if (CollectionUtils.isNotEmpty(logItems)) {
            for (EachLogItem logItem : logItems) {
                DateTime dateTime = getDateTime(logItem.getTime());
                String shortString = dateTime.toString(SHORT_FORMAT);
                int minuteOfHour = dateTime.getMinuteOfHour();
                String time = shortString + (minuteOfHour / step) + "0";
                if (indexMap.containsKey(time)) {
                    Integer va = date.get(indexMap.get(time)) + 1;
                    date.set(indexMap.get(time), va);
                }
            }
        }
    }

    private Map<String, Integer> buildMap(List<String> labels) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        int index = 0;
        for (String item : labels) {
            map.put(item, index ++);
        }
        return map;
    }

    public List<EachLogItem> readLogFileContent(DateTime dateTime) {
        List<EachLogItem> result = new ArrayList<EachLogItem>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(LOG_FILE_NAME));
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                EachLogItem item = new EachLogItem(sCurrentLine.substring(0,19), sCurrentLine);
                DateTime thisLineDate = getDateTime(item.getTime());
                if (thisLineDate.getDayOfYear() == dateTime.getDayOfYear()) {
                    result.add(item);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    private DateTime getDateTime(String dateString) {
        return DateTime.parse(dateString, LOG_DATE_FORMAT);
    }

    List<String> buildLabels(String monitorDayString) {
        List<String> labels = new ArrayList<String>();
        DateTime monitorDay = DateTime.now();
        if (StringUtils.isNotBlank(monitorDayString)) {
            monitorDay = DateTime.parse(monitorDayString, LOG_DATE_FORMAT);
        }

        List<DateTime> stepList = buildStepDateTimeList(10, monitorDay);
        if (CollectionUtils.isNotEmpty(stepList)) {
            for (DateTime time : stepList) {
                labels.add(time.toString(DEFAULT_FORMAT));
            }
        }
        return labels;
    }

    List<DateTime> buildStepDateTimeList(int step, DateTime monitorDay) {
        List<DateTime> result = new ArrayList<DateTime>();
        DateTime todayStart = monitorDay.withTimeAtStartOfDay();
        DateTime tomorrowStart = monitorDay.plusDays( 1 ).withTimeAtStartOfDay();

        DateTime tmp = todayStart.plus(Period.minutes(step));
        while (tomorrowStart.isAfter(tmp)) {
            result.add(tmp);
            tmp = tmp.plus(Period.minutes(step));
        }

        return result;
    }
}
