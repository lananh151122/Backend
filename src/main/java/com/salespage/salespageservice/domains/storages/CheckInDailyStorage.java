package com.salespage.salespageservice.domains.storages;

import com.salespage.salespageservice.domains.entities.CheckInDaily;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class CheckInDailyStorage extends BaseStorage {
  public void save(CheckInDaily checkInDaily) {
    checkInDailyRepository.save(checkInDaily);
  }

  public CheckInDaily findByUsernameAndDate(String username, String today) {
    return checkInDailyRepository.findByUsernameAndDate(username, today);
  }

  public List<CheckInDaily> findByDateAndCheckIn(Date date, boolean b) {
    return checkInDailyRepository.findByDateAndCheckInToday(date, b);
  }
}
