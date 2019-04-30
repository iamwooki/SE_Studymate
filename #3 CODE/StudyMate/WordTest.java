package StudyMate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.SQLException;
import java.util.*;

public class WordTest extends ObjectPanel implements Runnable{
    public enum Level{BASIC,HIGH};

    JLabel labelNameOfSubject;
    JLabel labelNameOfChapter;
    JLabel labelOfCurrentLevel;
    JLabel labelOfCurrentScore;
    int valueOfCurrentSccre=0;
    Level levelOfExam;
    String answerWord;
    JTextField labelNameOfWord;
    JButton[] basicMeaningOfWord = new JButton[4];
    JTextArea highMeaningOfWord;
    JButton btnBack;
    JButton btnResult;
    
    ArrayList<Vocabulary> arrlistWrongWord;
    boolean visited[];
    int visited_cnt = 0;
    int currentDisplayingWordIndex = -1;

    DefaultListModel listModel;
    Chapter chapter;
    Vector<Vocabulary> voca;
    JList listWord;
    JScrollPane spList;
    //constructor
    public WordTest(String nameOfSubject, Chapter chapter,Level levelOfExam){
        super(Screen.WORDTEST);
        arrlistWrongWord = new ArrayList<Vocabulary>();
        listModel = new DefaultListModel();
        this.chapter = chapter;
        this.levelOfExam = levelOfExam;
        labelNameOfSubject = new JLabel(nameOfSubject);
        labelNameOfChapter = new JLabel(chapter.chapterName);
        labelOfCurrentLevel = new JLabel("난이도 : "+levelOfExam);
        loadDatabase();
        super.display();
        if(levelOfExam==Level.HIGH){labelNameOfWord.requestFocus();}
    }
    
    public boolean hasNextProblem() {
    	if(visited_cnt != voca.toArray().length) {
    		return true;
    	}
    	
    	return false;
    }

    public void setWord() {
    	Random generator = new Random();
    	Integer tmpScore = valueOfCurrentSccre;
    	labelOfCurrentScore.setText("현재점수: "+tmpScore.toString());
    	try {
            int tempRandNum = generator.nextInt(voca.toArray().length);
            while(visited[tempRandNum]) {
                tempRandNum = generator.nextInt(voca.toArray().length);
            }

            currentDisplayingWordIndex = tempRandNum;

            visited[currentDisplayingWordIndex] = true;
            visited_cnt++;

            //TODO
            setWordName(voca.get(currentDisplayingWordIndex).voca);
            setWordMeaning(voca.get(currentDisplayingWordIndex).mean);
        }catch(Exception e){

        }
    }
    //미구현
    @Override
    public void run() {
        while(true){
            try{
                Thread.sleep(1000);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void setLeftPanel(){
        leftPanel.setLayout(new BorderLayout());

        JPanel panelTop = new JPanel();
        panelTop.setLayout(new BorderLayout());
        //뒤로가기
        btnBack = new JButton("뒤로가기");
        btnBack.setFont(D2Coding);
        btnBack.addActionListener(btnListener);
        panelTop.add(btnBack,BorderLayout.WEST);

        //과목선택
        labelNameOfSubject.setFont(new Font("D2Coding", Font.BOLD, 20));
        labelNameOfSubject.setHorizontalAlignment(SwingConstants.CENTER);
        panelTop.add(labelNameOfSubject,BorderLayout.CENTER);

        //과목선택설명
        labelNameOfChapter.setFont(new Font("D2Coding", Font.BOLD, 35));
        labelNameOfChapter.setHorizontalAlignment(SwingConstants.CENTER);
        panelTop.add(labelNameOfChapter,BorderLayout.SOUTH);

        //현재난이도 나타내기
        labelOfCurrentLevel.setFont(new Font("D2Coding", Font.BOLD, 30));
        panelTop.add(labelOfCurrentLevel,BorderLayout.EAST);

        JPanel panelMain = new JPanel();
        panelMain.setLayout(new BorderLayout());

        //단어이름
        labelNameOfWord = new JTextField("");
        labelNameOfWord.setFont(new Font("D2Coding", Font.BOLD, 35));
        labelNameOfWord.setHorizontalAlignment(JTextField.CENTER);
        labelNameOfWord.setHorizontalAlignment(SwingConstants.CENTER);
        labelNameOfWord.setBackground(Color.WHITE);
        panelMain.add(labelNameOfWord,BorderLayout.NORTH);

        JPanel panelCenterOfMain = new JPanel();
        //단어뜻 - basic Level
        if(levelOfExam==Level.BASIC){
            labelNameOfWord.setEnabled(false);
            panelCenterOfMain.setLayout(new GridLayout(4,1));
            for(int i = 0;i<basicMeaningOfWord.length;++i){ //보기 4개 생성
                basicMeaningOfWord[i] = new JButton("" + i);
                basicMeaningOfWord[i].setFont(new Font("D2Coding", Font.BOLD, 20));
                basicMeaningOfWord[i].addActionListener(btnListener);
                panelCenterOfMain.add(basicMeaningOfWord[i]);
            }
        }
        if(levelOfExam==Level.HIGH){
            //단어뜻 - high Level
            highMeaningOfWord = new JTextArea();
            highMeaningOfWord.setFont(new Font("D2Coding", Font.BOLD, 20));
            labelNameOfWord.addKeyListener(keyListener);
            labelNameOfWord.setEnabled(true);
            highMeaningOfWord.setLineWrap(true);
            highMeaningOfWord.setColumns(95); //열의 크기(가로크기)
            highMeaningOfWord.setRows(25);
            highMeaningOfWord.setVisible(true);
            panelCenterOfMain.add(highMeaningOfWord);
        }
        panelMain.add(panelCenterOfMain,BorderLayout.CENTER);

        leftPanel.add(panelTop,BorderLayout.NORTH);
        leftPanel.add(panelMain,BorderLayout.CENTER);
    }

    public void addWrongWord(String wrongVoca) {
        for (int i = 0; i < voca.toArray().length; ++i) {
            if(voca.get(i).mean.equals(wrongVoca)){
                arrlistWrongWord.add(voca.get(i));
                return;
            }
        }
    }

    public void setRightPanel() {
        rightPanel.setLayout(new BorderLayout());

        //현재 점수 값
        Integer tmpScore = valueOfCurrentSccre; //박싱
        labelOfCurrentScore = new JLabel("현재점수: "+tmpScore.toString());
        labelOfCurrentScore.setHorizontalAlignment(SwingConstants.CENTER);
        labelOfCurrentScore.setFont(new Font("D2Coding", Font.BOLD, 30));
        labelOfCurrentScore.setForeground(Color.WHITE);
        rightPanel.add(labelOfCurrentScore,BorderLayout.PAGE_START);

        //단어목록
        listWord = new JList(listModel);
        listWord.setFont(new Font("D2Coding", Font.BOLD, 20));
        listWord.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listWord.addListSelectionListener(listListener);
        spList = new JScrollPane(listWord);
        //spList.setSize(Constants.LIST_WIDTH,Constants.LIST_HEIGHT);
        //spList.setLocation(Constants.FRAME_WIDTH * 5/8 - Constants.LIST_WIDTH/2, Constants.FRAME_HEIGHT * 1/8);
        rightPanel.add(spList,BorderLayout.CENTER);

        btnResult = new JButton("결과");
        btnResult.setFont(new Font("D2Coding", Font.BOLD, 35));
        btnResult.addActionListener(btnListener);
        rightPanel.add(btnResult,BorderLayout.PAGE_END);

    	visited = new boolean[voca.toArray().length];
    	
    	for(int d = 0; d < visited.length; d++) {
    		visited[d] = false;
    	}
    	setWord();

    }

    public void loadDatabase(){
        try {
            voca = db.getVocabulary("SELECT * FROM Vocabulary where chapterID='"+chapter.chapterID+"';");
        } catch (SQLException e) {  e.printStackTrace();}
        listModel.clear();
        for (int i = 0; i < voca.toArray().length; ++i) {
            listModel.addElement(voca.get(i).voca);
        }
    }
    public String getSubjectName(){
        return labelNameOfSubject.getText().toString();
    }

    public Chapter getChapter(){
        return this.chapter;
    }
    public void setWordName(String nameOfWord){
        for(int i=0;i<voca.toArray().length;++i){
            if(voca.get(i).voca.equals(nameOfWord)){
                setWordMeaning(voca.get(i).mean);
                break;
            }
        }
        if(levelOfExam==Level.BASIC){
            labelNameOfWord.setText(nameOfWord);
            labelNameOfWord.setVisible(true);
        }
        //Level.HIGH
        else{
            labelNameOfWord.setText("");
            labelNameOfWord.requestFocus();
        }

    }
    public void setWordMeaning(String meaningOfWord){
        answerWord = meaningOfWord; //정답 넣기
        if(levelOfExam==Level.BASIC){ //난이도가 초급일 때
            int indexOfAnswer = (int)(Math.random()*4); //0~3의 숫자
            String exampleOfWord;
            Vector<String> checkDuplication=new Vector<>(1); //중복체크 배열
            boolean dupOnOff=false;
            //비활성화 해제
            for(int i=0;i<basicMeaningOfWord.length;++i){
                basicMeaningOfWord[i].setEnabled(true);
            }
            basicMeaningOfWord[indexOfAnswer].setText("<html><table><tr><td style='width:100%;'>"+answerWord+"</td></tr></table><html>"); //사전에 답(단어의 뜻) 먼저 넣기
            basicMeaningOfWord[indexOfAnswer].setVisible(true);
            checkDuplication.add(answerWord); //중복체크 배열에 답 넣기
            //정답 단어의 뜻을 먼저 보기에 넣은 후, 나머지 자리에 넣는 동작
            for(int i=0,indexOfRandom=0;i<basicMeaningOfWord.length;++i){
                if(indexOfAnswer==i){ //사전에 넣은 난수인덱스와 현재 인덱스가 같을 경우
                    continue;
                }
                indexOfRandom=(int)(Math.random()*voca.toArray().length); //난수생성
                exampleOfWord = voca.get(indexOfRandom).mean; //난수 인덱스에 위치한 배열 값(hashmap의 key)
                //dic.get(haspmap의 key) => 그 단어의 뜻을 나타냄

                //check duplication
                for(int j=0;j<checkDuplication.size();++j){ //현재 vector에 들어있는 단어들과 비교해서
                    if(checkDuplication.get(j).equals(exampleOfWord)){ //랜덤으로 생성한 단어의 뜻이 정답 또는 이미 생성된 뜻이라면
                        dupOnOff=true;
                        break;
                    }
                }
                if(dupOnOff){
                    dupOnOff=false;
                    --i; //반복횟수를 줄이고 다시 한번 실행
                    continue;
                }
                //그렇지 않으면 보기로 넣기
                checkDuplication.add(exampleOfWord);
                basicMeaningOfWord[i].setText("<html><table><tr><td style='width:100%;'>"+exampleOfWord+"</td></tr></table><html>");
                basicMeaningOfWord[i].setVisible(true);
            }
        }//여기까지 Level.BASIC
        if(levelOfExam==Level.HIGH){ //난이도가 상급일 때
            highMeaningOfWord.setText(answerWord);
            highMeaningOfWord.setEnabled(false);
            highMeaningOfWord.setVisible(true);
            //highMeaningOfWord.requestFocus(); //포커스 요청
        }

    }

    public String getAnswer(){
        return answerWord; //뜻 리턴
    }

    public void addScore(int addedScore){
        Integer tmpScore = valueOfCurrentSccre;
        valueOfCurrentSccre+=addedScore;
        labelOfCurrentScore.setText("현재점수: "+tmpScore.toString());
        labelOfCurrentScore.setHorizontalAlignment(SwingConstants.CENTER);

        //성공이나 실패시 버튼 비활성화
        if(levelOfExam==Level.BASIC){
            for(int i=0;i<basicMeaningOfWord.length;++i){
                basicMeaningOfWord[i].setEnabled(false);
            }
        }
        if(levelOfExam==Level.HIGH){
            highMeaningOfWord.setEnabled(false);
        }
        labelOfCurrentScore.repaint();
    }

    public String getTextFieldofHighLevel(){
        return labelNameOfWord.getText();
    }

    public String[] getTestInformation(){
        //필요에 따라 맞은갯수 /틀린갯수도 같이 가지고옴
        String[] arrInforamtionOfTest = new String[4];
        arrInforamtionOfTest[0]=labelNameOfSubject.getText(); //과목이름
        arrInforamtionOfTest[1]=labelNameOfChapter.getText(); //챕터이름
        arrInforamtionOfTest[2]=levelOfExam.toString(); //난이도
        Integer tmpScore = valueOfCurrentSccre; //박싱
        arrInforamtionOfTest[3]=tmpScore.toString(); //현재 점수
        return arrInforamtionOfTest;
    }
}
