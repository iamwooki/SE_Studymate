package StudyMate;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import javax.swing.*;

class  VocaList extends ObjectPanel {
	

    String nameOfChapter;
    int indexOfChapter;
    JLabel labelExplanationSubjectSelection;
    JButton btnLearning;
    JButton btnTest;
    JButton btnAdd;
    JButton btnEdit;
    JButton btnDelete;
    JDialog dialogTestLevel;
    JLabel titleWordOfRecommandation; //단어추천
    JLabel labelWordOfRecommandation; //단어추천
    JLabel titleWordOfLongTermUnschooledStudy; //장기미학습
    JLabel labelWordOfLongTermUnschooledStudy; //장기미학습

    JButton btnBack;
    JPanel panelTop;
    JPanel panelMain;
    JList listChapter;
    JScrollPane spList;

    Vector<Chapter> chapter;
    DefaultListModel listModel;
    Subject subject;
    //constructor
    public VocaList(Subject subject){
        super(Screen.VOCALIST);
        this.subject= subject;
        listModel = new DefaultListModel();
        loadDatabase();
        super.display();
    }

    public void setLeftPanel(){
        leftPanel.setLayout(new BorderLayout());

        listChapter = new JList(listModel); //list등록
        listChapter.setFont(D2Coding);
        spList =new JScrollPane(listChapter); //리스트 스크롤등록

        panelTop = new JPanel();
        panelTop.setLayout(new BorderLayout());
        //뒤로가기
        btnBack = new JButton("뒤로가기");
        btnBack.setFont(D2Coding);
        btnBack.addActionListener(btnListener);
        panelTop.add(btnBack,BorderLayout.WEST);

        //과목선택설명
        labelExplanationSubjectSelection = new JLabel(subject.subjectName); //값 넘겨받기.
        labelExplanationSubjectSelection.setFont(new Font("D2Coding", Font.BOLD, 20));
        labelExplanationSubjectSelection.setHorizontalAlignment(SwingConstants.CENTER);
        panelTop.add(labelExplanationSubjectSelection,BorderLayout.CENTER);


        panelMain = new JPanel();
        panelMain.setLayout(new BorderLayout());
        //과목리스트
        listChapter.setFont(new Font("D2Coding", Font.BOLD, 20));
        listChapter.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listChapter.addListSelectionListener(listListener);
        panelMain.add(spList,BorderLayout.CENTER);

        JPanel panelNorthOfMain = new JPanel();

        //챕터 추가하기
        btnAdd = new JButton("추가");
        btnAdd.setFont(new Font("D2Coding", Font.BOLD, 35));
        btnAdd.addActionListener(btnListener);
        panelNorthOfMain.add(btnAdd);

        //챕터 이름수정하기
        btnEdit = new JButton("수정");
        btnEdit.setFont(new Font("D2Coding", Font.BOLD, 35));
        btnEdit.addActionListener(btnListener);
        btnEdit.setEnabled(false);
        panelNorthOfMain.add(btnEdit);

        //챕터 삭제하기
        btnDelete = new JButton("삭제");
        btnDelete.setFont(new Font("D2Coding", Font.BOLD, 35));
        btnDelete.addActionListener(btnListener);
        btnDelete.setEnabled(false);
        panelNorthOfMain.add(btnDelete);

        //과목 학습하기
        btnLearning = new JButton("학습");
        btnLearning.setFont(new Font("D2Coding", Font.BOLD, 35));
        btnLearning.addActionListener(btnListener);
        btnLearning.setEnabled(false);
        panelNorthOfMain.add(btnLearning);

        //과목 시험
        btnTest = new JButton("시험");
        btnTest.setFont(new Font("D2Coding", Font.BOLD, 35));
        btnTest.addActionListener(btnListener);
        btnTest.setEnabled(false);
        panelNorthOfMain.add(btnTest);
        panelMain.add(panelNorthOfMain,BorderLayout.NORTH);
        leftPanel.add(panelTop,BorderLayout.PAGE_START);
        leftPanel.add(panelMain,BorderLayout.CENTER);

    }
    public void setRightPanel(){
        rightPanel.setLayout(new GridLayout(2,1));
        JPanel panelTopOfRight = new JPanel();
        panelTopOfRight.setBackground(NAVY);
        panelTopOfRight.setLayout(new BorderLayout());

        //오늘의 단어
        titleWordOfRecommandation = new JLabel("오늘의 단어");
        titleWordOfRecommandation.setHorizontalAlignment(SwingConstants.CENTER);
        titleWordOfRecommandation.setFont(new Font("D2Coding", Font.BOLD, 20));
        titleWordOfRecommandation.setForeground(Color.WHITE);
        panelTopOfRight.add(titleWordOfRecommandation,BorderLayout.PAGE_START);

        labelWordOfRecommandation = new JLabel(""); //0번째가 제목



        Random rand = new Random();
        Vector<Vocabulary> todayVoca = null;
        Vector<Chapter> oldChapter = null;
        Vector<Vocabulary> oldVoca = null;
        
        
        try {
        	todayVoca = db.getVocabulary("SELECT * FROM Vocabulary");
        	oldChapter = db.getOldChapter();
        	oldVoca = db.getVocabularyFromChapter(oldChapter.get(0).chapterID);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        boolean todayVisited[] = new boolean[todayVoca.size()];
        boolean oldVisited[] = new boolean[oldChapter.size()];
        
        for(int i = 0; i < todayVisited.length;++i) {
        	todayVisited[i] = false;
        }
        for(int i=0;i<oldVisited.length;++i){
            oldVisited[i] = false;
        }

        StringBuffer todayWord = new StringBuffer("");
        todayWord.append("<html><table  style='width:100%;'>");
        for(int i=0;i<todayVoca.toArray().length;++i){
        	int randnum = rand.nextInt(todayVisited.length);
        	
        	while(todayVisited[randnum])
        		randnum = rand.nextInt(todayVisited.length);
        	
        	todayVisited[randnum] = true;
            todayWord.append("<tr><td style='font-size:18pt; width:200px;'>");
            todayWord.append("<font style='color:#00D86D;'>&nbsp ○ &nbsp</font>" + todayVoca.get(randnum).voca+"</td></tr>");
            todayWord.append("<tr><td style='font-size:12pt; width:200px;'>- "+todayVoca.get(randnum).mean+"</td></tr>");
        }
        todayWord.append("</table></html>");
        labelWordOfRecommandation.setFont(D2Coding);
        labelWordOfRecommandation.setText(todayWord.toString());
        labelWordOfRecommandation.setVerticalAlignment(SwingConstants.TOP);
        labelWordOfRecommandation.setForeground(Color.WHITE);
        JScrollPane spWordOfRecommandation = new JScrollPane(labelWordOfRecommandation);
        spWordOfRecommandation.setHorizontalScrollBar(null); //좌우 스크롤 없애기
        spWordOfRecommandation.getViewport().setBackground(NAVY); //scrollpane색상
        spWordOfRecommandation.setBorder(null); //sp 테두리 없애기
        panelTopOfRight.add(spWordOfRecommandation,BorderLayout.CENTER);



        JPanel panelBottomOfRight = new JPanel();
        panelBottomOfRight.setBackground(NAVY);
        panelBottomOfRight.setLayout(new BorderLayout());

        //장기미학습단어
        titleWordOfLongTermUnschooledStudy = new JLabel("장기미학습단어");
        titleWordOfLongTermUnschooledStudy.setHorizontalAlignment(SwingConstants.CENTER);
        titleWordOfLongTermUnschooledStudy.setFont(new Font("D2Coding", Font.BOLD, 20));
        titleWordOfLongTermUnschooledStudy.setForeground(Color.WHITE);
        panelBottomOfRight.add(titleWordOfLongTermUnschooledStudy,BorderLayout.PAGE_START);


        labelWordOfLongTermUnschooledStudy = new JLabel("");
        StringBuffer longtermWord = new StringBuffer("");
        longtermWord.append("<html><table style='width:100%;'>");

        for(int i=0;i<oldVoca.toArray().length;++i){
            longtermWord.append("<tr><td style='font-size:18pt; width:200px;'>");
            longtermWord.append("<font style='color:#00D86D;'>&nbsp ○ &nbsp</font>" + (oldVoca.get(i).voca)+"</td></tr>");
            longtermWord.append("<tr><td style='font-size:12pt; width:200px;'>- "+oldVoca.get(i).mean+"</td></tr>");
        }
        longtermWord.append("</table></html>");
        labelWordOfLongTermUnschooledStudy.setFont(D2Coding);
        labelWordOfLongTermUnschooledStudy.setText(longtermWord.toString());
        labelWordOfLongTermUnschooledStudy.setVerticalAlignment(SwingConstants.TOP);
        labelWordOfLongTermUnschooledStudy.setForeground(Color.WHITE);
        JScrollPane spWordOfLongTerm = new JScrollPane(labelWordOfLongTermUnschooledStudy);
        spWordOfLongTerm.setHorizontalScrollBar(null); //좌우 스크롤 없애기
        spWordOfLongTerm.getViewport().setBackground(NAVY); //scrollpane색상
        spWordOfLongTerm.setBorder(null); //sp 테두리 없애기
        panelBottomOfRight.add(spWordOfLongTerm,BorderLayout.CENTER);

        rightPanel.add(panelTopOfRight);
        rightPanel.add(panelBottomOfRight);

    }

    public void loadDatabase(){
        try {
            chapter = db.getChapter(subject.subjectID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        listModel.clear();
        for (int i = 0; i < chapter.toArray().length; ++i) {
            listModel.addElement(chapter.get(i).chapterName);
        }
    }

    public String getSubjectName(){
        return labelExplanationSubjectSelection.getText().toString();
    }
    public Chapter getChapter(){
        for(int i=0;i<chapter.toArray().length;++i){
            if(chapter.get(i).chapterName.equals(nameOfChapter)){
                return chapter.get(i);
            }
        }
        return new Chapter(0,"0","0",0);
    }

    public void setOption(String nameOfChapter,int index) {
        this.nameOfChapter = nameOfChapter; //선택된 챕터
        this.indexOfChapter = index;
        btnEdit.setEnabled(true);
        btnDelete.setEnabled(true);
        btnLearning.setEnabled(true);
        btnTest.setEnabled(true);
    }
    public String getChapterName(){
        return nameOfChapter;
    }

    public void runTestLevelDialog(){
        dialogTestLevel = new JDialog();
        dialogTestLevel.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); // 다이얼로그 종료버튼시 동작
        dialogTestLevel.setTitle("난이도를 선택하세요"); //다이얼로그 이름
        dialogTestLevel.setLayout(new GridLayout(1,2)); //다이얼로그 정렬
        dialogTestLevel.setSize(Constants.DIALOG_WIDTH,Constants.DIALOG_HEIGHT); //다이얼로그 사이즈
        JButton btnBasic = new JButton("초급");
        JButton btnHigh = new JButton("상급");
        btnBasic.addActionListener(btnListener);
        btnHigh.addActionListener(btnListener);
        dialogTestLevel.add(btnBasic);
        dialogTestLevel.add(btnHigh);
        dialogTestLevel.setLocationRelativeTo(this);//다이얼로그 실행 위치 - 화면 중앙
        dialogTestLevel.setModal(true); //실행중인 다이얼로그가 끝나야만 다른 작업을 할 수 있음
        dialogTestLevel.setVisible(true);
    }

    public void setDialogToDispose(){
        dialogTestLevel.dispose();
    }

    public void addData() {
        String addChapter = JOptionPane.showInputDialog("단원이름 입력");
        try{
            if(addChapter.equals("null"));
        }catch(NullPointerException e){ return;}

        try {
            //중복 확인
            for (int i = 0; i < chapter.toArray().length; ++i) {
                if (chapter.get(i).chapterName.equals(addChapter)) {
                    JOptionPane.showMessageDialog(null, "중복되는 단원이름입니다.");
                    return;
                }
            }
            //중복 아닐 시
            db.insertNewChapter(addChapter, subject.subjectID);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        loadDatabase();
        nameOfChapter = addChapter;
    }

    public void deleteData(int result) {
        if (result == JOptionPane.YES_OPTION) {
            try {
                db.deleteChapter(nameOfChapter,subject.subjectID);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            loadDatabase();
        }
    }

    public void updateData() {
        String editSubject = JOptionPane.showInputDialog("단원 이름 변경");
        try{
            if(editSubject.equals("null"));
        }catch(NullPointerException e){ return;}
        try {
            for (int i = 0; i < chapter.toArray().length; ++i) {
                if (chapter.get(i).chapterName.equals(editSubject)) {
                    JOptionPane.showMessageDialog(null, "중복되는 단원이름입니다.");
                    return;
                }
            }
            db.updateChapter(nameOfChapter, editSubject,subject.subjectID);
            nameOfChapter = editSubject;
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        loadDatabase();
    }
}

class Subject{
	int subjectID;
	String subjectName;
	
	public Subject(int subjectID, String subjectName){
		this.subjectID=subjectID;
		this.subjectName=subjectName;
	}
}

class Chapter{
	int chapterID;
	String chapterName;
	String timestamp;
	int subjectID;
	
	
	public Chapter(int chapterID, String chapterName, String timestamp, int subjectID){
		this.chapterID= chapterID;
		this.chapterName=chapterName;
		this.timestamp = timestamp;
		this.subjectID=subjectID;
	}
}

class Vocabulary{
	int vocaID;
	String voca;
	String mean;
	int chapterID;
	
	public Vocabulary(int vocaID, String voca, String mean, int chapterID){
		this.vocaID=vocaID;
		this.voca=voca;
		this.mean=mean;
		this.chapterID=chapterID;
	}
}