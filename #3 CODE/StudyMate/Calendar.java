package StudyMate;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.Vector;

public class Calendar extends ObjectPanel {
    File path = new File("");
    ImageIcon check_clicked;
    ImageIcon check_normal;
    MemoCalendar calendar;
    int selectedYear;
    int selectedMonth;
    int selectedDayOfMonth;
    JLabel labelDday;
    JLabel labelContentOfDday;
    JButton btnBack;
    JLabel labelSelectedDate;
    JLabel labelMemoOfCalendar;
    JScrollPane spMemoOfCalendar;
    JPanel panelTopOfCalendar;
    JPanel panelMainCalendar;
    JButton btnMemoPlus;
    JButton btnMemoEdit;
    JPanel infoPanel;
    CheckBoxListener itemListener;
    Boolean editResult = false;

    //constructor
    public Calendar() {
        super(Screen.CALENDAR);
        db = new DataBase();
        check_clicked = new ImageIcon(path.getAbsolutePath() + "\\StudyMate\\src\\images\\" + "button_clicked.png");
        check_normal = new ImageIcon(path.getAbsolutePath() + "\\StudyMate\\src\\images\\" + "button_normal.png");
        calendar = new MemoCalendar();
        itemListener = new CheckBoxListener();
        setBackground(GREEN);
        super.display();
    }

    public void setLeftPanel() {
        //leftPanel 레이아웃 설정
        leftPanel.setLayout(new BorderLayout());
        leftPanel.add(panelTopOfCalendar, BorderLayout.PAGE_START);
        leftPanel.add(panelMainCalendar, BorderLayout.CENTER);
    }

    public void setRightPanel() {
        //init rightPanel
        rightPanel.setLayout(new GridLayout(2, 1));
        JPanel panelTopOfPanel = new JPanel();


        spMemoOfCalendar.setHorizontalScrollBar(null); //좌우 스크롤 없애기
        spMemoOfCalendar.getViewport().setBackground(NAVY); //scrollpane색상
        spMemoOfCalendar.setBorder(null); //sp 테두리 없애기

        panelTopOfPanel.setLayout(new BorderLayout());
        JPanel panelFuctionOfMemo = new JPanel();
        panelFuctionOfMemo.setBackground(NAVY);
        panelFuctionOfMemo.setLayout(new FlowLayout());

        panelFuctionOfMemo.add(btnMemoPlus);
        panelFuctionOfMemo.add(btnMemoEdit);
        JPanel panelBottomOfPanel = new JPanel();
        panelBottomOfPanel.setLayout(new BorderLayout());


        panelTopOfPanel.add(labelSelectedDate, BorderLayout.NORTH);
        panelTopOfPanel.add(spMemoOfCalendar, BorderLayout.CENTER);
        panelTopOfPanel.add(panelFuctionOfMemo, BorderLayout.SOUTH);

        //D-day
        labelDday = new JLabel("D-day");
        labelDday.setFont(new Font("D2Coding", Font.BOLD, 20));
        labelDday.setForeground(Color.WHITE);
        labelDday.setHorizontalAlignment(SwingConstants.CENTER); //가운데 정렬
        panelBottomOfPanel.add(labelDday, BorderLayout.PAGE_START);
        //D-day 내용

        labelContentOfDday = new JLabel("");
        labelContentOfDday.setFont(D2Coding);
        labelContentOfDday.setForeground(Color.WHITE);
        calendar.showDday();
        labelContentOfDday.setHorizontalAlignment(SwingConstants.LEFT);
        labelContentOfDday.setVerticalAlignment(SwingConstants.TOP);
        JScrollPane spContentOfDday = new JScrollPane(labelContentOfDday);
        spContentOfDday.setHorizontalScrollBar(null); //좌우 스크롤 없애기
        spContentOfDday.getViewport().setBackground(NAVY); //scrollpane색상
        spContentOfDday.setBorder(null); //sp 테두리 없애기
        panelBottomOfPanel.add(spContentOfDday, BorderLayout.CENTER);

        panelTopOfPanel.setBackground(NAVY);
        panelBottomOfPanel.setBackground(NAVY);
        rightPanel.add(panelTopOfPanel);
        rightPanel.add(panelBottomOfPanel);
    }

    public void setStatePanel() {
        announce.setText("Welcome to Calendar");
        statePanel.add(announce);
    }

    //달력 형식 구현
    class CalendarDataManager { // 6*7배열에 나타낼 달력 값을 구하는 class
        static final int CAL_WIDTH = 7;
        final static int CAL_HEIGHT = 6;
        int calDates[][] = new int[CAL_HEIGHT][CAL_WIDTH];

        final int calLastDateOfMonth[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int calLastDate;
        java.util.Calendar cal;

        public CalendarDataManager() {
            setToday();
        }

        public void setToday() {
            selectedYear = today.get(java.util.Calendar.YEAR);
            selectedMonth = today.get(java.util.Calendar.MONTH);
            selectedDayOfMonth = today.get(java.util.Calendar.DAY_OF_MONTH);
            makeCalendarData(today);
        }

        private void makeCalendarData(java.util.Calendar cal) {
            // 1일의 위치와 마지막 날짜를 구함
            int calStartingPos = (cal.get(java.util.Calendar.DAY_OF_WEEK) + 7 - (cal.get(java.util.Calendar.DAY_OF_MONTH)) % 7) % 7;
            if (selectedMonth == 1) calLastDate = calLastDateOfMonth[selectedMonth] + leapCheck(selectedYear);
            else calLastDate = calLastDateOfMonth[selectedMonth];
            // 달력 배열 초기화
            for (int i = 0; i < CAL_HEIGHT; i++) {
                for (int j = 0; j < CAL_WIDTH; j++) {
                    calDates[i][j] = 0;
                }
            }
            // 달력 배열에 값 채워넣기
            for (int i = 0, num = 1, k = 0; i < CAL_HEIGHT; i++) {
                if (i == 0) k = calStartingPos;
                else k = 0;
                for (int j = k; j < CAL_WIDTH; j++) {
                    if (num <= calLastDate) calDates[i][j] = num++;
                }
            }
        }

        private int leapCheck(int year) { // 윤년인지 확인하는 함수
            if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) return 1;
            else return 0;
        }

        public void moveMonth(int mon) { // 현재달로 부터 n달 전후를 받아 달력 배열을 만드는 함수(1년은 +12, -12달로 이동 가능)
            selectedMonth += mon;
            if (selectedMonth > 11) while (selectedMonth > 11) {
                selectedYear++;
                selectedMonth -= 12;
            }
            else if (selectedMonth < 0) while (selectedMonth < 0) {
                selectedYear--;
                selectedMonth += 12;
            }
            cal = new GregorianCalendar(selectedYear, selectedMonth, selectedDayOfMonth);
            makeCalendarData(cal);
        }
    }


    public class CheckBoxListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            JCheckBox tmp = (JCheckBox) e.getItem();
            try {
                if (e.getStateChange() == 1) {
                    db.updateBy("UPDATE schedule SET checkState='Y' WHERE scheduleID=" + tmp.getName() + ";");
                } else {
                    db.updateBy("UPDATE schedule set checkState='N' WHERE scheduleID=" + tmp.getName() + ";");
                }
                calendar.showCalendar();

            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        }
    }

    public class MemoCalendar extends CalendarDataManager { // CalendarDataManager의 GUI + 메모기능 + 시계
        // 창 구성요소와 배치도
        JButton btnToday;
        JButton btnPreYear;
        JButton btnPreMonth;
        JLabel labelselectedMonth;
        JLabel labelselectedYear;
        JButton btnNextMonth;
        JButton btnNextYear;
        JButton btnLoadSchedule;
        CalendarBtnListener calendarBtnListener;
        MemoPanelListener memoOfSelectedDay;
        JButton btnWeekDaysName[];
        JButton btnDate[][] = new JButton[6][7];
        JScrollPane spDate[][] = new JScrollPane[6][7];
        JLabel infoClock;

        BtnMemoListener btnMemoListener;
        DialogLoadSchedule dialogSchedule;
        DialogMemo dialogMemo;

        //상수, 메세지
        final String[] WEEK_DAY_NAME = {"SUN", "MON", "TUE", "WED", "THR", "FRI", "SAT"};
        final String[] MONTH = {"JANUARY", "FEBRUARAY", "MARCH", "APRIL", "MAY", "JUN", "JULY", "AUGUEST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"}; //0~11

        public MemoCalendar() { //구성요소 순으로 정렬되어 있음. 각 판넬 사이에 빈줄로 구별
            memoOfSelectedDay = new MemoPanelListener();
            calendarBtnListener = new CalendarBtnListener();
            btnMemoListener = new BtnMemoListener();
            addComponent();

        }

        public void refresh() {
            showDday();
            showMemo();
            showCalendar();
        }

        public void addComponent() {
            spMemoOfCalendar = new JScrollPane
                    (new Panelmemo(java.util.Calendar.getInstance().get(java.util.Calendar.YEAR), java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1, java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH)));
            //캘린더 윗 기능버튼
            panelTopOfCalendar = new JPanel();
            btnToday = new JButton("Today");
            btnToday.setFont(D2Coding);
            btnToday.setToolTipText("Today");
            btnToday.addActionListener(calendarBtnListener);
            btnPreYear = new JButton("<<");
            btnPreYear.setFont(D2Coding);
            btnPreYear.setToolTipText("Previous Year");
            btnPreYear.addActionListener(calendarBtnListener);
            btnPreMonth = new JButton("◀");
            btnPreMonth.setFont(D2Coding);
            btnPreMonth.setToolTipText("Previous Month");
            btnPreMonth.addActionListener(calendarBtnListener);
            labelselectedMonth = new JLabel("<html><table  style='width:180;'><tr><td style='font-size:25pt; text-align:center;'>" + MONTH[selectedMonth] + "</td></tr></table></html>");
            labelselectedMonth.setFont(D2Coding);
            labelselectedYear = new JLabel("<html><table  style='width:100%;'><tr><td style='font-size:25pt; text-align:center;'>" + selectedYear + "</td></tr></table></html>");
            labelselectedYear.setFont(D2Coding);
            btnNextMonth = new JButton("▶");
            btnNextMonth.setFont(D2Coding);
            btnNextMonth.setToolTipText("Next Month");
            btnNextMonth.addActionListener(calendarBtnListener);
            btnNextYear = new JButton(">>");
            btnNextYear.setFont(D2Coding);
            btnNextYear.setToolTipText("Next Year");
            btnNextYear.addActionListener(calendarBtnListener);
            btnBack = new JButton("뒤로가기");
            btnBack.setFont(D2Coding);
            btnBack.addActionListener(btnListener);
            btnLoadSchedule = new JButton("남아 있는 일정 확인");
            btnLoadSchedule.setFont(D2Coding);
            btnLoadSchedule.setToolTipText("현재 체크되지 않은 모든 일정을 불러옵니다.");
            btnLoadSchedule.addActionListener(calendarBtnListener);
            panelTopOfCalendar.setLayout(new BorderLayout());
            //NORTH
            JPanel panelNorthOfTopOfCalendar = new JPanel();
            panelNorthOfTopOfCalendar.setLayout(new BorderLayout());
            JPanel panelNorthWestOfTopCalendar = new JPanel();
            panelNorthWestOfTopCalendar.add(btnBack);
            panelNorthWestOfTopCalendar.add(btnLoadSchedule); //Flow VS Border
            JPanel panelNorthEastOfTopCalendar = new JPanel();
            panelNorthEastOfTopCalendar.add(btnPreYear);
            panelNorthEastOfTopCalendar.add(labelselectedYear);
            panelNorthEastOfTopCalendar.add(btnNextYear);
            panelNorthOfTopOfCalendar.add(panelNorthWestOfTopCalendar, BorderLayout.WEST);
            panelNorthOfTopOfCalendar.add(panelNorthEastOfTopCalendar, BorderLayout.EAST);

            JPanel panelCenterOfTopCalendar = new JPanel();
            panelCenterOfTopCalendar.add(btnPreMonth);
            panelCenterOfTopCalendar.add(labelselectedMonth);
            panelCenterOfTopCalendar.add(btnNextMonth);
            panelTopOfCalendar.add(panelNorthOfTopOfCalendar, BorderLayout.NORTH);
            panelTopOfCalendar.add(panelCenterOfTopCalendar, BorderLayout.CENTER);
            panelTopOfCalendar.add(btnToday, BorderLayout.WEST);

            /*위의 내용 배치
            //////////////////////////////////////////////////////////////////////
            ///BACK LOADSCHEDULE                                     < YEAR > ////
            ///TODAY              <   MONTH   >                              ////
            /////////////////////////////////////////////////////////////////////
            */

            panelMainCalendar = new JPanel();
            panelMainCalendar.setBackground(Color.WHITE);
            btnWeekDaysName = new JButton[7];
            for (int i = 0; i < CAL_WIDTH; i++) {
                //  요일 타이틀
                btnWeekDaysName[i] = new JButton(WEEK_DAY_NAME[i]);
                btnWeekDaysName[i].setFont(D2Coding);
                btnWeekDaysName[i].setBorderPainted(false);
                btnWeekDaysName[i].setContentAreaFilled(false);
                btnWeekDaysName[i].setForeground(Color.WHITE);
                if (i == 0) btnWeekDaysName[i].setBackground(new Color(200, 50, 50));
                else if (i == 6) btnWeekDaysName[i].setBackground(new Color(50, 100, 200));
                else btnWeekDaysName[i].setBackground(new Color(150, 150, 150));
                btnWeekDaysName[i].setOpaque(true);
                btnWeekDaysName[i].setFocusPainted(false);
                panelMainCalendar.add(btnWeekDaysName[i]);
            }
            for (int i = 0; i < CAL_HEIGHT; i++) {
                for (int j = 0; j < CAL_WIDTH; j++) {
                    //각 날짜 속성
                    btnDate[i][j] = new JButton();
                    btnDate[i][j].setBorderPainted(false);
                    btnDate[i][j].setContentAreaFilled(false);
                    btnDate[i][j].setBackground(Color.WHITE);
                    btnDate[i][j].setHorizontalAlignment(SwingConstants.LEFT);
                    btnDate[i][j].setVerticalAlignment(SwingConstants.TOP);
                    btnDate[i][j].setOpaque(true);
                    btnDate[i][j].addActionListener(memoOfSelectedDay);
                    spDate[i][j] = new JScrollPane(btnDate[i][j]);
                    spDate[i][j].setHorizontalScrollBar(null); //좌우 스크롤 없애기
                    spDate[i][j].getViewport().setBackground(Color.WHITE); //scrollpane색상
                    spDate[i][j].setBorder(null); //sp 테두리 없애기

                    panelMainCalendar.add(spDate[i][j]);
                }
            }
            panelMainCalendar.setLayout(new GridLayout(0, 7, 2, 2));
            //양 옆 테두리 칸 설정
            panelMainCalendar.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            showCalendar(); // 달력을 표시

            infoPanel = new JPanel();
            infoPanel.setLayout(new BorderLayout());
            infoClock = new JLabel("", SwingConstants.RIGHT);
            infoClock.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            infoPanel.add(infoClock, BorderLayout.NORTH);

            //오늘 날짜
            labelSelectedDate = new JLabel("<html>" +
                    "<table style='width:100%;'><tr><td style='font-size:60px; text-align:center; width:250px;'>" + today.get(java.util.Calendar.DAY_OF_MONTH) + "</b></td></tr>" +
                    "<tr><td style='font-size:15px; font-weight:bold; text-align:center;'>TODAY</td></tr>" +
                    "<tr><td style='font-size:10px; text-align:center;'>&nbsp</tr></td>" + //&nbsp =공백을 뜻하는 html 용어
                    "<tr><td style='font-size:10px; text-align:center;'>&nbsp</tr></td>" +
                    "<tr><td style='font-size:10px; text-align:center;'>&nbsp</tr></td>" +
                    "</table></html>");

            labelSelectedDate.setForeground(Color.WHITE);
            labelSelectedDate.setFont(D2Coding);
            labelSelectedDate.setHorizontalAlignment(SwingConstants.CENTER);

            //메모 내용
            labelMemoOfCalendar = new JLabel("");
            labelMemoOfCalendar.setHorizontalAlignment(SwingConstants.CENTER);
            labelMemoOfCalendar.setVerticalAlignment(SwingConstants.TOP);
            labelMemoOfCalendar.setForeground(Color.WHITE);

            focusToday(); //현재 날짜에 focus를 줌 (mainFrame.setVisible(true) 이후에 배치해야함)


        }

        private void focusToday() {
            if (today.get(java.util.Calendar.DAY_OF_WEEK) == 1)
                btnDate[today.get(java.util.Calendar.WEEK_OF_MONTH)][today.get(java.util.Calendar.DAY_OF_WEEK) - 1].requestFocusInWindow();
            else
                btnDate[today.get(java.util.Calendar.WEEK_OF_MONTH) - 1][today.get(java.util.Calendar.DAY_OF_WEEK) - 1].requestFocusInWindow();
        }

        //일정 메모보여주기(오른쪽 패널쪽)
        public void showMemo() {
            spMemoOfCalendar.setViewportView(new Panelmemo(selectedYear, selectedMonth + 1, selectedDayOfMonth));
        }

        //D-day 일정보여주기
        public void showDday() {
            StringBuffer tmpSb = new StringBuffer("");
            try {
                Vector<Dday> days = db.getDdayOfSchedule(); //dDay자료 vector형식으로 불러오기
                tmpSb.append("<html><table style='width:100%;'>");
                tmpSb.append("<tr><td style='font-size:18pt; width:220px;'>");
                //색 ; #00D86D
                for (int i = 0; i < days.toArray().length; ++i) {
                    tmpSb.append("<font style='color:#00D86D;'>&nbsp ○ &nbsp</font>D-" + days.get(i).dDay + " : " + days.get(i).scheduleName);
                    tmpSb.append("<br>");
                    //JLabel에서 문자 개행을 위해 <html>html형식 사용 <br>: 한줄뛰우기 </html>:html형식 사용종료
                }
                tmpSb.append("</html>");
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            labelContentOfDday.setText(tmpSb.toString());
        }

        //캘린더 표시(새로고침)
        public void showCalendar() {

            for (int i = 0; i < CAL_HEIGHT; i++) {
                for (int j = 0; j < CAL_WIDTH; j++) {
                    String fontColor = "black"; //평일
                    if (j == 0) fontColor = "red"; //일요일
                    else if (j == 6) fontColor = "blue"; //토요일
                    StringBuffer theDay = new StringBuffer("<html><table style='width:100%;'><tr><td style='width:90px;'><b><font color=" + fontColor + ">" + calDates[i][j] + "</b></font>");
                    //일정이 있으면 보여주기
                    try {
                        Vector<Schedule> schedule = db.getSchedule("SELECT * FROM schedule order by startTime;"); //Schedule 자료 vector형식으로 불러오기
                        //vector 순회
                        for (int k = 0; k < schedule.toArray().length; ++k) {
                            StringTokenizer stk = new StringTokenizer(schedule.get(k).date.toString(), "-");
                            String arrDate[] = new String[3]; //index 비교, 0=년 1=월 2=일
                            for (int l = 0; stk.hasMoreTokens(); ++l) {
                                arrDate[l] = stk.nextToken(); //날짜 쪼개서 넣기
                            }
                            //데이터가 현재 년도의 현재 날짜면.
                            if (Integer.parseInt(arrDate[0]) == selectedYear && Integer.parseInt(arrDate[1]) == (selectedMonth + 1) && Integer.parseInt(arrDate[2]) == calDates[i][j]) {
                                theDay.append("<font style='color:"+schedule.get(k).color+"+;'>");
                                if (schedule.get(k).curState.equals("Y")) {
                                    theDay.append("<br><s>- " + schedule.get(k).scheduleName + "</s>");
                                } else {
                                    theDay.append("<br>- " + schedule.get(k).scheduleName);
                                }
                                theDay.append("</font>");
                            }
                        }

                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                    theDay.append("</td></tr></table></html>");
                    //날짜 표기
                    btnDate[i][j].setText(theDay.toString());
                    btnDate[i][j].setFont(new Font("D2Coding",Font.PLAIN,13));
                    //오늘 날짜 표기
                    btnDate[i][j].removeAll();
                    btnDate[i][j].setBorderPainted(false);
                    if (selectedMonth == today.get(java.util.Calendar.MONTH) &&
                            selectedYear == today.get(java.util.Calendar.YEAR) &&
                            calDates[i][j] == today.get(java.util.Calendar.DAY_OF_MONTH)) {
                        //오늘 날짜면 테두리 설정
                        btnDate[i][j].setBorderPainted(true);
                        btnDate[i][j].setBorder(new LineBorder(GREEN, 2,true));
                        btnDate[i][j].setToolTipText("Today");
                    }
                    //X월 0일이면 보여지면 안됨
                    if (calDates[i][j] == 0) btnDate[i][j].setVisible(false);
                        // 그렇지 않으면 보여줌
                    else btnDate[i][j].setVisible(true);
                }
            }
        }

        private class CalendarBtnListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == btnLoadSchedule) {
                    new DialogLoadSchedule("all");
                    return;
                }
                if (e.getSource() == btnToday) {
                    setToday();
                    memoOfSelectedDay.actionPerformed(e);
                    focusToday();
                    announce.setText("오늘로 이동합니다");
                } else if (e.getSource() == btnPreYear) moveMonth(-12);
                else if (e.getSource() == btnPreMonth) moveMonth(-1);
                else if (e.getSource() == btnNextMonth) moveMonth(1);
                else if (e.getSource() == btnNextYear) moveMonth(12);
                labelselectedMonth.setText("<html><table  style='width:180;'><tr><td style='font-size:25pt; text-align:center;'>" + MONTH[selectedMonth] + "</td></tr></table></html>");
                labelselectedYear.setText("<html><table  style='width:100%'><tr><td style='font-size:25pt; text-align:center;'>" + selectedYear + "</td></tr></table></html>");
                refresh();
            }
        }

        private class MemoPanelListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                String tmpDay;
                int k = 0, l = 0;
                for (int i = 0; i < CAL_HEIGHT; i++) {
                    for (int j = 0; j < CAL_WIDTH; j++) {
                        if (e.getSource() == btnDate[i][j]) {
                            k = i;
                            l = j;
                        }
                    }
                }
                if (!(k == 0 && l == 0)) {
                    selectedDayOfMonth = calDates[k][l]; //today버튼을 눌렀을때도 이 actionPerformed함수가 실행되기 때문에 넣은 부분
                    tmpDay = WEEK_DAY_NAME[l];
                } else {
                    tmpDay = WEEK_DAY_NAME[today.get(java.util.Calendar.DAY_OF_WEEK) - 1];
                    //selectedDayOfMonth = today.get(java.util.Calendar.DAY_OF_MONTH);
                }
                cal = new GregorianCalendar(selectedYear, selectedMonth, selectedDayOfMonth);

                String dDayString = new String();
                int dDay = ((int) ((cal.getTimeInMillis() - today.getTimeInMillis()) / 1000 / 60 / 60 / 24));
                //오늘 일경우
                if (dDay == 0 && (cal.get(java.util.Calendar.YEAR) == today.get(java.util.Calendar.YEAR))
                        && (cal.get(java.util.Calendar.MONTH) == today.get(java.util.Calendar.MONTH))
                        && (cal.get(java.util.Calendar.DAY_OF_MONTH) == today.get(java.util.Calendar.DAY_OF_MONTH)))
                    dDayString = "TODAY";
                    //오늘이 아닌 다른 날들의 경우
                else if (dDay >= 0) dDayString = (dDay + 1) + "일 후";
                else if (dDay < 0) dDayString = (dDay) * (-1) + "일 전";

                //다른 날짜
                //labelSelectedDate = new JLabel("<Html><font size=20>"+(today.get(java.util.Calendar.DAY_OF_MONTH)+"<br>"+"&nbsp;(Today)</html>"), SwingConstants.CENTER);
                labelSelectedDate.setText("<html><table style='width:100%;'><tr><td style='font-size:60px; text-align:center; width:250px;'>" + selectedDayOfMonth + "</b></td></tr>" +
                        "<tr><td style='font-size:15px; font-weight:bold; text-align:center;'>" + tmpDay + "</tr></td>" +
                        "<tr><td style='font-size:10px; text-align:center;'>" + (selectedMonth + 1) + "/" + selectedYear + "</tr></td>" +
                        "<tr><td style='font-size:10px; text-align:center;'>" + "( " + dDayString + " )" + "</tr></td>" +
                        "<tr><td style='font-size:10px; text-align:center;'>&nbsp</tr></td>" +
                        "</table></html>");

                //
                calendar.showMemo();
            }
        }



        public class DialogLoadSchedule extends JDialog {
            //모든 일정불러오기, 그 날의 일정불러오기(편집 버튼시)
            JCheckBox[] cbMemoList;
            JButton[] btnEdit;
            JButton[] btnDelete;
            Vector<Schedule> schedule;
            String yyyymmdd;
            JPanel panelMain;
            JScrollPane spMain;
            JPanel[] panelContent; //날짜, 제목, 시작시간, 종료시간, 내용등이 panelmemo창에 center에 잡힘.
            JPanel[] panelEastOfContent;  //버튼들이 오른쪽에 들어감.
            int SIZE;

            public DialogLoadSchedule(String yyyymmdd) { //그 날의 일정만 불러올 때
                this.yyyymmdd = yyyymmdd;
                setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                panelMain = new JPanel();
                panelMain.setBackground(NAVY);
                setSize(600, 600);
                loadDatabase();
                addComponent();
                spMain = new JScrollPane(panelMain);
                panelMain.setPreferredSize(new Dimension(500, (int) (72.5 * SIZE)));
                setContentPane(spMain);
                setLocationRelativeTo(this);//다이얼로그 실행 위치 - 화면 중앙
                setResizable(false); //창 크기 조절X
                setModal(true); //실행중인 다이얼로그가 끝나야만 다른 작업을 할 수 있음
                setVisible(true);
            }

            public void loadDatabase() {
                try {
                    if (yyyymmdd.equals("all")) { //모든 일정을 불러올 때
                        schedule = db.getSchedule("SELECT * FROM schedule WHERE checkState='N' order by date,startTime;");
                        this.setTitle("남아있는 모든 일정");
                    } else { //그날의 일정을 불러올 때
                        schedule = db.getSchedule("SELECT * FROM schedule WHERE date='" + yyyymmdd + "'order by startTime;");
                        this.setTitle(yyyymmdd + " 일정");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            public void addComponent() { //
                SIZE = schedule.toArray().length;
                cbMemoList = new JCheckBox[SIZE]; //동적 할당
                btnEdit = new JButton[SIZE];
                btnDelete = new JButton[SIZE];
                panelContent = new JPanel[SIZE];
                panelEastOfContent = new JPanel[SIZE];

                for (int i = 0; i < cbMemoList.length; ++i) {
                    cbMemoList[i] = new JCheckBox("", check_normal);
                    cbMemoList[i].setFont(D2Coding);
                    btnEdit[i] = new JButton("수정");
                    btnEdit[i].setFont(D2Coding);
                    btnEdit[i].setName(schedule.get(i).id.toString()); //scheduleID 버튼에 설정
                    btnEdit[i].addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            JButton btnEdit = (JButton) e.getSource();
                            new DialogMemo(Integer.parseInt(btnEdit.getName())); //이 부분에 체크박스 값 체크해서 수정할 수 있도록 호출.
                            if (editResult) {
                                editResult = false;
                                dispose();
                            }
                        }
                    });
                    btnDelete[i] = new JButton("삭제");
                    btnDelete[i].setFont(D2Coding);
                    btnDelete[i].setName(schedule.get(i).id.toString()); //scheduleID 버튼에 설정
                    btnDelete[i].addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            JButton btnDelete = (JButton) e.getSource();
                            db.removeSchedule(btnDelete.getName());
                            calendar.refresh();
                            dispose();
                            announce.setText("일정을 삭제합니다");
                        }
                    });

                    panelContent[i] = new JPanel();
                    panelContent[i].setLayout(new BorderLayout());
                    panelEastOfContent[i] = new JPanel();
                    panelEastOfContent[i].setPreferredSize(new Dimension(100, 50));
                    panelEastOfContent[i].setBackground(NAVY);
                    cbMemoList[i].setFont(D2Coding);
                    cbMemoList[i].setText("<html>" +
                            "<table border='2' style='width:430pt; margin-left:5pt'><tr><td>" + schedule.get(i).date + "</td><td>" + schedule.get(i).scheduleName + "</td><td>" +
                            schedule.get(i).startTime + "</td><td>" + schedule.get(i).endTime + "</td></tr><tr><td colspan='4'>" + schedule.get(i).content + "</td></tr></table></html>");
                    cbMemoList[i].setSelectedIcon(check_clicked);
                    cbMemoList[i].setBorder(null);
                    cbMemoList[i].setBorderPainted(false);
                    cbMemoList[i].setBackground(NAVY);
                    cbMemoList[i].setForeground(Color.WHITE);
                    cbMemoList[i].setFocusPainted(false);
                    cbMemoList[i].setBorderPaintedFlat(false);
                    cbMemoList[i].addItemListener(itemListener);
                    cbMemoList[i].setName(schedule.get(i).id.toString());

                    //add components
                    panelEastOfContent[i].add(btnEdit[i]);
                    panelEastOfContent[i].add(btnDelete[i]);
                    panelContent[i].add(cbMemoList[i], BorderLayout.CENTER);
                    panelContent[i].add(panelEastOfContent[i], BorderLayout.EAST);
                    panelMain.add(panelContent[i]);
                }
            }
        }

        public class DialogMemo extends JDialog {
            JTextField scheduleName;
            JTextField content;
            Integer[] hour;
            String selectedColor = "#000000";
            JComboBox<Integer> cbSHour;
            JComboBox<Integer> cbEHour;
            JComboBox<Integer> cbSMin;
            JComboBox<Integer> cbEMin;
            JCheckBox cbCurState;
            JCheckBox cbDday;
            Integer[] min;
            JButton btnOK;
            JButton[] btnColor;
            JButton btnCancel;
            Integer scheduleID;
            JLabel labelTitle;
            JLabel labelStartTime;
            JLabel labelEndTime;
            JLabel labelCheck;
            JLabel labelDday;
            Vector<Schedule> schedule;
            //test
            Integer[] color = {0x000000,0xFE0001,0xFE9C00,0x9C01FF,0x98CB00,0xFF00FE,0x6AA549,0xCE0000,0x1099CB};

            public DialogMemo(int scheduleID) { //schedule : -1 생성시 , schedule > 0 기존 것
                this.scheduleID = scheduleID;
                setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); // 다이얼로그 종료버튼시 동작
                setLayout(new GridLayout(12, 2)); //다이얼로그 정렬
                setSize(500, 650); //다이얼로그 사이즈
                loadDatabase();
                addComponent();
                setLocationRelativeTo(this);//다이얼로그 실행 위치 - 화면 중앙
                setModal(true); //실행중인 다이얼로그가 끝나야만 다른 작업을 할 수 있음
                setVisible(true);
                setResizable(false);
            }

            public void loadDatabase() {
                //편집 시
                if (scheduleID != -1) {
                    try {
                        schedule = db.getSchedule("SELECT * FROM schedule WHERE scheduleID='" + scheduleID + "';");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }


            public void addComponent() {
                //init
                String strDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDayOfMonth;
                setTitle(strDate + " 일정 생성");
                labelTitle = new JLabel("일정 생성");
                labelTitle.setFont(new Font("D2Coding", Font.BOLD, 35));
                scheduleName = new JTextField("제목을 입력하세요", 20);
                scheduleName.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        super.mouseClicked(e);
                        if (scheduleName.getText().equals("제목을 입력하세요")) {
                            scheduleName.setText("");
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        super.mouseExited(e);
                        if (scheduleName.getText().equals("")) {
                            scheduleName.setText("제목을 입력하세요");
                        }
                    }
                });
                scheduleName.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        super.keyPressed(e);
                        if(e.getKeyCode()==KeyEvent.VK_ESCAPE){
                            scheduleName.setText("제목을 입력하세요");
                            return;
                        }
                        if(scheduleName.getText().equals("제목을 입력하세요")){
                            scheduleName.setText("");
                            return;
                        }
                    }
                });

                btnColor = new JButton[color.length];
                for (int i = 0; i < btnColor.length; ++i) {
                    btnColor[i] = new JButton("");
                    btnColor[i].setForeground(Color.WHITE);
                    btnColor[i].setFont(D2Coding);
                    btnColor[i].setContentAreaFilled(false);
                    btnColor[i].setOpaque(true);
                    btnColor[i].setBackground(new Color(color[i]));
                    btnColor[i].setName(color[i].toString());
                    btnColor[i].addActionListener(new ActionListener(){
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            for(int i =0;i<btnColor.length;++i){
                                btnColor[i].setText("");
                            }
                            JButton btnColor = (JButton)e.getSource();
                            selectedColor ="#"+Integer.toHexString(Integer.parseInt(btnColor.getName()));
                            btnColor.setText("선택");
                        }
                    });
                }

                labelStartTime = new JLabel("시작 시간");
                labelStartTime.setFont(new Font("D2Coding", Font.BOLD, 20));
                labelEndTime = new JLabel("종료 시간");
                labelEndTime.setFont(new Font("D2Coding", Font.BOLD, 20));
                labelCheck = new JLabel("일정완료여부");
                labelCheck.setFont(new Font("D2Coding", Font.BOLD, 20));
                labelCheck.setHorizontalAlignment(SwingConstants.CENTER);
                labelDday = new JLabel("D-day표시여부");
                labelDday.setFont(new Font("D2Coding", Font.BOLD, 20));
                labelDday.setHorizontalAlignment(SwingConstants.CENTER);
                content = new JTextField("내용을 입력하세요", 20);
                content.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        super.mouseClicked(e);
                        if (content.getText().equals("내용을 입력하세요")) {
                            content.setText("");
                        }
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        super.mouseExited(e);
                        if (content.getText().equals("")) {
                            content.setText("내용을 입력하세요");
                        }
                    }
                });
                content.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        super.keyPressed(e);
                        if(content.getText().equals("내용을 입력하세요")){
                            content.setText("");
                            return;
                        }
                        if(e.getKeyCode()==KeyEvent.VK_ESCAPE){
                            content.setText("내용을 입력하세요");
                            return;
                        }
                    }
                });
                hour = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24};
                cbSHour = new JComboBox<Integer>(hour);
                cbSHour.setBackground(Color.WHITE);
                cbSHour.setPreferredSize(new Dimension(100, 30));
                cbEHour = new JComboBox<Integer>(hour);
                cbEHour.setBackground(Color.WHITE);
                cbEHour.setPreferredSize(new Dimension(100, 30));
                min = new Integer[]{0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55};
                cbSMin = new JComboBox<Integer>(min);
                cbSMin.setBackground(Color.WHITE);
                cbSMin.setPreferredSize(new Dimension(100, 30));
                cbEMin = new JComboBox<Integer>(min);
                cbEMin.setBackground(Color.WHITE);
                cbEMin.setPreferredSize(new Dimension(100, 30));

                cbCurState = new JCheckBox();
                cbCurState.setHorizontalAlignment(SwingConstants.CENTER);
                cbCurState.setPreferredSize(new Dimension(50, 50));
                cbDday = new JCheckBox();
                cbDday.setHorizontalAlignment(SwingConstants.CENTER);
                cbDday.setPreferredSize(new Dimension(50, 50));
                btnOK = new JButton("확인");
                btnOK.setFont(new Font("D2Coding", Font.BOLD, 20));
                btnOK.setContentAreaFilled(false);
                btnOK.setOpaque(true);
                btnOK.setBackground(Color.BLUE);
                btnOK.setForeground(Color.WHITE);
                btnOK.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (scheduleName.getText().trim().equals("") || scheduleName.getText().equals("제목을 입력하세요")) {
                            JOptionPane.showMessageDialog(null, "일정 제목을 입력해주세요");
                            scheduleName.requestFocus();
                            return;
                        }
                        if (scheduleName.getText().length() > 50) {
                            JOptionPane.showMessageDialog(null, "제목 길이초과. 현재길이" + scheduleName.getText().length());
                            scheduleName.requestFocus();
                            return;
                        }
                        if (content.getText().trim().equals("") || content.getText().equals("내용을 입력하세요")) {
                            content.setText("등록된 내용이 없습니다.");
                        }
                        if (content.getText().length() > 100) {
                            JOptionPane.showMessageDialog(null, "내용 길이초과. 현재길이" + content.getText().length());
                            content.requestFocus();
                            return;
                        }
                        //시간체크
                        //시작시간(HOUR) 종료시간(HOUR)보다 크면
                        if (Integer.parseInt(cbSHour.getSelectedItem().toString()) > Integer.parseInt(cbEHour.getSelectedItem().toString())) {
                            JOptionPane.showMessageDialog(null, "시작시간(시)을 확인해주세요");
                            cbSHour.requestFocus();
                            return;
                        }
                        //시작시간=종료시간이고 시작 분>종료 분
                        if (Integer.parseInt(cbSHour.getSelectedItem().toString()) == Integer.parseInt(cbEHour.getSelectedItem().toString()) &&
                                Integer.parseInt(cbSMin.getSelectedItem().toString()) > Integer.parseInt(cbEMin.getSelectedItem().toString())) {
                            JOptionPane.showMessageDialog(null, "시작시간(분)을 확인해주세요");
                            cbSMin.requestFocus();
                            return;
                        }


                        try {
                            if (scheduleID != -1) { //편집
                                //편집부분
                                db.updateBy("UPDATE schedule SET scheduleName='" + scheduleName.getText() + "',date='" + strDate + "',startTime='" + cbSHour.getSelectedItem() + ":" + cbSMin.getSelectedItem() + ":00" +
                                        "',endTime='" + cbEHour.getSelectedItem() + ":" + cbEMin.getSelectedItem() + ":00" + "',checkState='" + (cbCurState.isSelected() ? "Y" : "N") + "',comment='" + content.getText() + "',d_day='" + (cbDday.isSelected() ? "Y" : "N") + "',color='"+selectedColor+"' WHERE scheduleID=" + scheduleID + ";");
                                editResult = true;
                                announce.setText("일정을 수정했습니다");
                            } else { //새로생성
                                db.insertSchedule(new Schedule(0, scheduleName.getText(), strDate, cbSHour.getSelectedItem() + ":" + cbSMin.getSelectedItem() + ":00",
                                        cbEHour.getSelectedItem() + ":" + cbEMin.getSelectedItem() + ":00", cbCurState.isSelected() ? "Y" : "N", content.getText(), cbDday.isSelected() ? "Y" : "N",selectedColor));
                                announce.setText("일정을 생성했습니다");
                            }
                        } catch (SQLException e2) {
                            e2.printStackTrace();
                        }
                        calendar.refresh();
                        dispose();
                    }
                });
                btnCancel = new JButton("취소");
                btnCancel.setFont(new Font("D2Coding", Font.BOLD, 20));
                btnCancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dispose();
                    }
                });
                //value setting , 편집 시
                if (scheduleID != -1)
                {
                    String strTime = schedule.get(0).startTime;
                    String endTime = schedule.get(0).endTime;
                    StringTokenizer stkSTime = new StringTokenizer(strTime, ":");
                    StringTokenizer stkETime = new StringTokenizer(endTime, ":");
                    Integer[] sTime = new Integer[3];
                    Integer[] eTime = new Integer[3];
                    for (int i = 0; stkSTime.hasMoreTokens(); ++i) {
                        sTime[i] = Integer.parseInt(stkSTime.nextToken());
                    }
                    for (int i = 0; stkETime.hasMoreTokens(); ++i) {
                        eTime[i] = Integer.parseInt(stkETime.nextToken());
                    }
                    setTitle(selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDayOfMonth + " = " + schedule.get(0).scheduleName + " 일정 수정");
                    labelTitle.setText("일정 수정");
                    scheduleName.setText(schedule.get(0).scheduleName);
                    content.setText(schedule.get(0).content);
                    cbSHour.setSelectedIndex(sTime[0]);
                    cbSMin.setSelectedIndex(sTime[1] / 5);
                    cbEHour.setSelectedIndex(eTime[0]);
                    cbEMin.setSelectedIndex(eTime[1] / 5); //현재시간 /5하면 몇번째 인덱스인지 나옴.
                    cbCurState.setSelected((schedule.get(0).curState.equals("Y")) ? true : false);
                    cbDday.setSelected((schedule.get(0).d_day.equals("Y") ? true : false));
                    selectedColor = schedule.get(0).color;
                    String[] split =selectedColor.split("#");
                    for(int i=0;i<btnColor.length;++i){
                        if(split[1].equals(Integer.toHexString(Integer.parseInt(btnColor[i].getName())))){
                            btnColor[i].setText("선택");
                        }
                    }
                }

                JPanel panelBase = new JPanel();
                panelBase.setLayout(new BorderLayout(10, 10));
                JPanel panelNorthOfBase = new JPanel();
                JPanel panelCenterOfBase = new JPanel();
                panelCenterOfBase.setLayout(new GridLayout(9, 1, 0, 5));

                //일정 수정 및 변경
                panelNorthOfBase.add(labelTitle);
                panelNorthOfBase.setBackground(new Color(241, 241, 241));
                panelBase.add(panelNorthOfBase, BorderLayout.NORTH);

                panelCenterOfBase.add(scheduleName);
                JPanel panelColor = new JPanel();
                panelColor.setLayout(new GridLayout(1, color.length));
                for (
                        JButton btnColor : btnColor)

                {
                    panelColor.add(btnColor);
                }
                panelCenterOfBase.add(panelColor);
                panelCenterOfBase.add(labelStartTime);

                JPanel panelStartTime = new JPanel();
                panelStartTime.add(cbSHour);
                panelStartTime.add(new JLabel(":"));
                panelStartTime.add(cbSMin);
                panelCenterOfBase.add(panelStartTime);

                panelCenterOfBase.add(labelEndTime);
                JPanel panelEndTime = new JPanel();
                panelEndTime.add(cbEHour);
                panelEndTime.add(new JLabel(":"));
                panelEndTime.add(cbEMin);
                panelCenterOfBase.add(panelEndTime);
                panelCenterOfBase.add(content);

                JPanel panelCheck = new JPanel();
                panelCheck.setLayout(new GridLayout(2, 2, 10, 10));
                panelCheck.add(labelCheck);
                panelCheck.add(labelDday);
                panelCheck.add(cbCurState);
                panelCheck.add(cbDday);
                panelCenterOfBase.add(panelCheck);

                JPanel panelBtn = new JPanel();
                panelBtn.setLayout(new BorderLayout());
                panelBtn.add(btnOK, BorderLayout.WEST);
                panelBtn.add(btnCancel, BorderLayout.EAST);
                panelCenterOfBase.add(panelBtn);
                panelBase.add(panelCenterOfBase, BorderLayout.CENTER);
                panelBase.setPreferredSize(new Dimension(460, 560));
                this.setLayout(new FlowLayout(FlowLayout.CENTER));
                this.add(panelBase);
                //this.pack();

            }
        }

        public class Panelmemo extends JPanel {
            Vector<Schedule> schedule;
            int year;
            int month;
            int day;

            public Panelmemo(int year, int month, int day) {
                this.year = year;
                this.month = month;
                this.day = day;
                this.setBackground(NAVY);
                loadDatabase();
                this.setLayout(new GridLayout(schedule.toArray().length, 1));
                setMemoList();
                btnMemoPlus = new JButton("+");
                btnMemoPlus.setFont(D2Coding);
                btnMemoEdit = new JButton("/");
                btnMemoEdit.setFont(D2Coding);
                btnMemoPlus.addActionListener(btnMemoListener);
                btnMemoEdit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dialogSchedule = new DialogLoadSchedule(selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDayOfMonth);
                    }
                });
            }

            public void loadDatabase() {
                try {
                    schedule = db.getSchedule("SELECT * FROM schedule WHERE date='" + year + "-" + month + "-" + day + "'order by startTime;"); //dDay자료 vector형식으로 불러오기
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            public void setMemoList() {
                JCheckBox[] cbMemoList = new JCheckBox[schedule.toArray().length]; //동적 할당
                for (int i = 0; i < cbMemoList.length; ++i) {
                    cbMemoList[i] = new JCheckBox("", check_normal);
                    cbMemoList[i].setFont(new Font("D2Coding", Font.PLAIN, 15));
                    cbMemoList[i].setText("<html>" +
                            "<table style='width:100%;'><tr><td style='font-size:18pt; width:190px;'>" + schedule.get(i).scheduleName + "</td></tr>" +
                            "<tr><td style='font-size:12pt; width:190px;'>" + schedule.get(i).startTime + "&nbsp - " + schedule.get(i).content + "</td></tr></table></html>");

                    cbMemoList[i].setSelectedIcon(check_clicked);
                    cbMemoList[i].setBorder(null);
                    cbMemoList[i].setBorderPainted(false);
                    cbMemoList[i].setBackground(NAVY);
                    cbMemoList[i].setForeground(Color.WHITE);
                    cbMemoList[i].setFocusPainted(false);
                    cbMemoList[i].setBorderPaintedFlat(false);
                    cbMemoList[i].addItemListener(itemListener);
                    cbMemoList[i].setName(schedule.get(i).id.toString());
                    if (schedule.get(i).curState.equals("Y")) {
                        cbMemoList[i].setSelected(true);
                    }
                    this.add(cbMemoList[i]);
                }
            }

        }


        public class BtnMemoListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                JButton btn = (JButton) e.getSource();
                if (btn.getText().equals("+")) {
                    dialogMemo = new DialogMemo(-1);
                    return;
                }
                if (btn.getText().equals("/")) { //Edit부분
                    dialogSchedule = new DialogLoadSchedule(selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDayOfMonth);
                    return;
                }
            }
        }
    }


}


class Dday {
    int dDay;
    String scheduleName;

    public Dday(int dDay, String scheduleName) {
        this.dDay = dDay;
        this.scheduleName = scheduleName;
    }
}

class Schedule {
    Integer id;
    String scheduleName;
    String date;
    String startTime;
    String endTime;
    String curState;
    String content;
    String d_day;
    String color;

    public Schedule(int id, String scheduleName, String date, String startTime, String endTime, String curState, String content, String d_day,String color) {
        this.id = id;
        this.scheduleName = scheduleName;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.curState = curState;
        this.content = content;
        this.d_day = d_day;
        this.color = color;
    }
}