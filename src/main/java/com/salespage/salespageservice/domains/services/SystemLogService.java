package com.salespage.salespageservice.domains.services;

import com.salespage.salespageservice.domains.entities.SystemLog;
import com.salespage.salespageservice.domains.entities.types.LogType;
import com.salespage.salespageservice.domains.utils.DateUtils;
import org.springframework.stereotype.Service;

@Service
public class SystemLogService {
  public void createSystemLog(String createdBy, String message, String trace, LogType logType) {
    SystemLog systemLog = new SystemLog();
    systemLog.setLogType(logType);
    systemLog.setMessage(message);
    systemLog.setTrace(trace);
    systemLog.setCreatedBy(createdBy);
    systemLog.setCreatedAt(DateUtils.now());
  }
}
