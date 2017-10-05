package sample;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

public class Parameters {
    private int scale = 800;
    private int emuDays = 1;
    private int reportEveryDays = 1;

    public int getScale() {
        return scale;
    }
    public void setScale(int s) {
        if (s < 0) {
            if (scale == 1) {
                return;
            }
        } else {
            if (scale == 1000) {
                return;
            }
        }
        if (scale > 90) {
            scale += s * 100;
        } else if(scale > 9) {
            scale += s * 10;
        } else {
            scale += s;
        }

    }

    public int getEmuDays() {
        return emuDays;
    }
    public void setEmuDays(int ed) {
        if (ed < 0) {
            if (emuDays == 1) {
                return;
            }
        } else {
            if (emuDays == 365) {
                return;
            }
        }

        emuDays += ed;
        if (reportEveryDays > emuDays) {
            reportEveryDays = emuDays;
        }
    }

    public int getReportEveryDays() {
        return reportEveryDays;
    }
    public void setReportEveryDays(int red) {
        if (red < 0) {
            if (reportEveryDays == 1) {
                return;
            }
        } else {
            if (reportEveryDays == 365){
                return;
            }
        }

        reportEveryDays += red;
        if (emuDays < reportEveryDays) {
            emuDays = reportEveryDays;
        }
    }

    public int getMarkUp(LocalDateTime dt) {
        DayOfWeek day = dt.getDayOfWeek();
        int hour = dt.getHour();

        if (hour >= 18 && hour <= 20)
            return 8;

        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY)
            return 15;

        return 10;
    }

    public boolean isWorkTime(LocalDateTime time) {
        int hour = time.getHour();
        return hour >= 8 && hour <= 21;
    }
}
