package StudyMate;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class TestResult extends ObjectPanel {
	
    JLabel labelNameOfSubject;
    JLabel labelNameOfChapter;
    JLabel labellevelOfTest;
    JLabel labelTotalScore;
    JButton btnBack;
    JButton btnCreateListOfWrongAnswers;
    JButton btnReTry;
    JButton btnWordList;

    JList listWrongAnswer;
    JScrollPane spList;
    JLabel labelListOfWrongAnswer;
    String[] testInformation;
    Chapter chapter;
    ArrayList<Vocabulary> arrlistWrongWords;
    DefaultListModel listModel;
    //constructor
    public TestResult(String[] testInformation, ArrayList<Vocabulary> wrongWords,Chapter chapter) {
        super(Screen.TESTRESULT);
        this.chapter=chapter;
        this.testInformation = testInformation; //시험정보 인덱스정보 : 0과목이름 1챕터이름 2난이도 3성적
        arrlistWrongWords = wrongWords;
        listModel = new DefaultListModel();
        loadDatabase();
        super.display();
    }

    public Chapter getChapter(){
        return this.chapter;
    }

    public void setLeftPanel() {
        leftPanel.setLayout(new BorderLayout());
        JPanel panelTop = new JPanel();
        panelTop.setLayout(new BorderLayout());
        
        JPanel panelBottom = new JPanel();
        panelTop.setLayout(new BorderLayout());

        //뒤로가기
        btnBack = new JButton("뒤로가기");
        btnBack.setFont(D2Coding);
        btnBack.addActionListener(btnListener);
        panelTop.add(btnBack,BorderLayout.WEST);

        //과목이름
        labelNameOfSubject = new JLabel(testInformation[0]);
        labelNameOfSubject.setFont(new Font("D2Coding", Font.BOLD, 20));
        labelNameOfSubject.setHorizontalAlignment(SwingConstants.CENTER);
        panelTop.add(labelNameOfSubject,BorderLayout.CENTER);

        //챕터이름
        labelNameOfChapter = new JLabel(chapter.chapterName);
        labelNameOfChapter.setFont(new Font("D2Coding", Font.BOLD, 35));
        labelNameOfChapter.setHorizontalAlignment(SwingConstants.CENTER);
        panelTop.add(labelNameOfChapter,BorderLayout.SOUTH);

        //시험난이도
        labellevelOfTest = new JLabel("난이도 : "+testInformation[2]);
        labellevelOfTest.setFont(new Font("D2Coding", Font.BOLD, 30));
        panelTop.add(labellevelOfTest,BorderLayout.EAST);

        JPanel panelMain = new JPanel();
        panelMain.setLayout(new BorderLayout());
        panelMain.setBackground(Color.WHITE);
        //총점수
        labelTotalScore = new JLabel("총점수 : "+testInformation[3]);
        labelTotalScore.setFont(new Font("D2Coding", Font.BOLD, 50));
        labelTotalScore.setHorizontalAlignment(SwingConstants.CENTER);
        panelMain.add(labelTotalScore,BorderLayout.CENTER);

        //재시험
        btnReTry = new JButton("재시험");
        btnReTry.setFont(new Font("D2Coding", Font.BOLD, 35));
        btnReTry.addActionListener(btnListener);
        panelBottom.add(btnReTry,BorderLayout.WEST);
        
        //단어목록으로
        btnWordList = new JButton("목록");
        btnWordList.setFont(new Font("D2Coding", Font.BOLD, 35));
        btnWordList.addActionListener(btnListener);
        panelBottom.add(btnWordList,BorderLayout.EAST);
        

        leftPanel.add(panelTop,BorderLayout.NORTH);
        leftPanel.add(panelMain,BorderLayout.CENTER);
        leftPanel.add(panelBottom, BorderLayout.SOUTH);
    }

    public void setRightPanel() {
        rightPanel.setLayout(new BorderLayout());
        //단어목록 이름
        labelListOfWrongAnswer = new JLabel("오답 단어");
        labelListOfWrongAnswer.setHorizontalAlignment(SwingConstants.CENTER);
        labelListOfWrongAnswer.setFont(new Font("D2Coding", Font.BOLD, 30));
        labelListOfWrongAnswer.setForeground(Color.WHITE);
        rightPanel.add(labelListOfWrongAnswer,BorderLayout.PAGE_START);

        //단어목록
        listWrongAnswer = new JList(listModel);
        listWrongAnswer.setFont(new Font("D2Coding", Font.BOLD, 20));
        listWrongAnswer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listWrongAnswer.addListSelectionListener(listListener);
        spList = new JScrollPane(listWrongAnswer);
        rightPanel.add(spList,BorderLayout.CENTER);

        //단어 추가
        btnCreateListOfWrongAnswers = new JButton("오답노트생성");
        btnCreateListOfWrongAnswers.setFont(new Font("D2Coding", Font.BOLD, 35));
        btnCreateListOfWrongAnswers.addActionListener(btnListener);
        rightPanel.add(btnCreateListOfWrongAnswers,BorderLayout.PAGE_END);
    }
    public void loadDatabase(){
        listModel.clear();
        for(int i=0;i<arrlistWrongWords.toArray().length;++i){
            listModel.addElement(arrlistWrongWords.get(i).voca);
        }
    }

    public ArrayList<Vocabulary> getWrongWords(){
        return arrlistWrongWords;
    }
    //재시험을 위한 시험정보
    public String[] getTestInformation(){
        //필요에 따라 맞은갯수 /틀린갯수도 같이 가지고옴
        return testInformation;
    }
}