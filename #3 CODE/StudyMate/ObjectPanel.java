package StudyMate;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.EventListener;

public abstract class ObjectPanel extends JPanel {
    //local var
    public enum Screen{TIMETABLE,WORDBOOK,VOCALIST,WORDTEST,WORDLEARNING,TESTRESULT,CALENDAR};
    java.util.Calendar today = java.util.Calendar.getInstance();
    DataBase db;
    Screen screen;
    JPanel leftPanel;
    JPanel rightPanel;
    JPanel statePanel;
    JLabel announce;
    Color NAVY;
    Color GREEN;
    Font D2Coding;

    static ActionListener btnListener = new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e) {
            //don't touch ,temp anonymous
        }
    };
    static ListSelectionListener listListener = new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            //don't touch ,temp anonymous
        }
    };
    static KeyListener keyListener = new KeyAdapter(){
        @Override
        public void keyPressed(KeyEvent e) {
            super.keyPressed(e);
            //don't touch, temp anonymouse
        }
    };
    //constructor
    ObjectPanel(Screen screen){
        this.screen = screen; //열거형 불러오기
        db = new DataBase();
        D2Coding = new Font("D2Coding",Font.PLAIN,15);
        announce = new JLabel("Welcome to StudyMate");
        GREEN = new Color(0, 216, 109);
        NAVY = new Color(42, 42, 56);
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");//LookAndFeel Windows 스타일 적용
        } catch (Exception e) {
            e.printStackTrace();
        }
        leftPanel= new JPanel(); //메인 PANEL
        leftPanel.setBackground(Color.WHITE);//임시 크기 판단 추후 삭제
        rightPanel = new JPanel(); //알림 PANEL
        rightPanel.setPreferredSize(new Dimension(Constants.RIGHTPANEL_WIDTH,Constants.FRAME_HEIGHT));
        rightPanel.setBackground(NAVY); //임시 크기 판단 추후 삭제
        statePanel = new JPanel(); //상태 PANEL
        statePanel.setBackground(Color.WHITE); //임시 크기 판단 추후 삭제
        statePanel.add(announce);
        setBackground(GREEN);
        setLayout(new BorderLayout(5,5));

        //Thread 작동(시계, bottomMsg 일정시간후 삭제)
        AnnounceTimer timer = new AnnounceTimer();
        timer.start();
    }
    //method
    public Screen getScreen(){
        return this.screen;
    }

    public static void setBtnListener(StudyMate.BtnListener listener){
        btnListener = listener;
    }
    public static void setListListener(StudyMate.ListListener listener){
        listListener = listener;
    }
    public static void setKeyListener(StudyMate.KeyListener listener){ keyListener = listener;}
    public void setLeftPanel(){};
    public void setRightPanel(){};
    public void setStatePanel(){};
    public void display(){
        setLeftPanel();
        setRightPanel();
        setStatePanel();
        add(leftPanel,BorderLayout.CENTER); //main창
        add(rightPanel,BorderLayout.EAST); //알림창
        add(statePanel,BorderLayout.PAGE_END); //상태창
    }

    private class AnnounceTimer extends Thread {
        public void run() {
            boolean msgCntFlag = false;
            int num = 0;
            String curStr = new String();
            while (true) {
                try {
                    today = java.util.Calendar.getInstance();
                    String amPm = (today.get(java.util.Calendar.AM_PM) == 0 ? "AM" : "PM");
                    String hour;
                    if (today.get(java.util.Calendar.HOUR) == 0) hour = "12";
                    else if (today.get(java.util.Calendar.HOUR) == 12) hour = " 0";
                    else
                        hour = (today.get(java.util.Calendar.HOUR) < 10 ? " " : "") + today.get(java.util.Calendar.HOUR);
                    String min = (today.get(java.util.Calendar.MINUTE) < 10 ? "0" : "") + today.get(java.util.Calendar.MINUTE);
                    String sec = (today.get(java.util.Calendar.SECOND) < 10 ? "0" : "") + today.get(java.util.Calendar.SECOND);

                    sleep(1000);
                    String infoStr = announce.getText();

                    if (infoStr != " " && (msgCntFlag == false || curStr != infoStr)) {
                        num = 5;
                        msgCntFlag = true;
                        curStr = infoStr;
                    } else if (infoStr != " " && msgCntFlag == true) {
                        if (num > 0) num--;
                        else {
                            msgCntFlag = false;
                            announce.setText(" ");
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

class FilePath{
    int ID;
    String path;
    String name;
    public FilePath(int id,String path,String name){
        this.ID= id;
        this.path = path;
        this.name = name;
    }
}