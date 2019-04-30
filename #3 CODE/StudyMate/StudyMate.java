package StudyMate;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import static java.lang.System.exit;

public class StudyMate extends JFrame {
    ObjectPanel currentView;
    DataBase db = new DataBase();

    public StudyMate() {
        setTitle("StudyMate - Team : Schedule");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT);
        ObjectPanel.setBtnListener(new BtnListener());
        ObjectPanel.setListListener(new ListListener());
        ObjectPanel.setKeyListener(new KeyListener());
        currentView = new TimeTable();
        setContentPane(currentView);
        setVisible(true);
        //setResizable(false);
        setLocationRelativeTo(null);
        //new JOptionPane().showMessageDialog(null,"프레임크기 :"+this.getSize());
    }

    public void currentViewIsChangedTo(ObjectPanel newPanel) {
        currentView = newPanel;
        getContentPane().removeAll();
        setContentPane(currentView);
        revalidate();
        repaint();
        setTitle("curScreen = " + currentView.getScreen().toString());
        //System.gc(); //가비지 컬렉션 요청
    }

    class BtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton btnEvent = (JButton) e.getSource();
            //추가 팝업
            if (btnEvent.getText().equals("추가")) {
                switch (currentView.getScreen()) {
                    case WORDBOOK:
                        WordBook tmpWordbook = (WordBook) currentView;
                        tmpWordbook.addData();
                        break;
                    case WORDLEARNING:
                        WordLearning tmpWordlearning = (WordLearning) currentView;
                        tmpWordlearning.addData();
                        //p = new PopUp("New Word", 2, "", "");
                        //p.setChapterID(((WordLearning)currentView).getChapterID());
                        break;
                    case VOCALIST:
                        VocaList tmpVocalist = (VocaList) currentView;
                        tmpVocalist.addData();
                        break;
                }
            }
            //수정 팝업
            if (btnEvent.getText().equals("수정")) {
                switch (currentView.getScreen()) {
                    case WORDBOOK:
                        WordBook tempWordBook = (WordBook) currentView;
                        tempWordBook.updateData();
                        break;

                    case VOCALIST:
                        VocaList tempVocaList = (VocaList) currentView;
                        tempVocaList.updateData();
                        break;
                    case WORDLEARNING:
                        WordLearning tempWordlearning = (WordLearning) currentView;
                        tempWordlearning.updateData();
                        break;
                }

            }

            if (btnEvent.getText().equals("삭제")) {
                int reply = JOptionPane.showConfirmDialog(null, "삭제하시겠습니까?", "경고", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
                switch (currentView.getScreen()) {
                    case WORDBOOK:
                        WordBook tempWordBook = (WordBook) currentView;
                        tempWordBook.deleteData(reply);
                        break;

                    case VOCALIST:
                        VocaList tempVocaList = (VocaList) currentView;
                        tempVocaList.deleteData(reply);
                        break;
                    case WORDLEARNING:
                        WordLearning tempWordlearning = (WordLearning) currentView;
                        tempWordlearning.deleteData(reply);
                        break;
                }

            }

            //btnWordBook in TimeTable
            if (btnEvent.getText().equals("단어장")) {
                currentViewIsChangedTo(new WordBook());
                return;
            }
            //Calendar in TimeTable
            if (btnEvent.getText().equals("일정관리")) {
                currentViewIsChangedTo(new Calendar());
                return;
            }
            if (btnEvent.getText().equals("프로그램종료")) {
                JOptionPane alert = new JOptionPane();
                alert.showMessageDialog(null, "프로그램을 종료합니다");
                exit(0);
            }
            if (btnEvent.getText().equals("뒤로가기")) {
                switch (currentView.getScreen()) {
                    case WORDBOOK://WordBook
                    case CALENDAR:// or Calendar  에서Init으로 돌아갈 때
                        currentViewIsChangedTo(new TimeTable());
                        break;
                    case TESTRESULT:
                    case VOCALIST:
                        currentViewIsChangedTo(new WordBook());
                        break;
                    case WORDLEARNING:
                        WordLearning tmpWordLearning = (WordLearning) currentView;
                        currentViewIsChangedTo(new VocaList(new Subject(tmpWordLearning.getChapter().subjectID, tmpWordLearning.getSubjectName())));
                        break;
                    case WORDTEST:
                        WordTest tmpWordTest = (WordTest) currentView;
                        currentViewIsChangedTo(new VocaList(new Subject(tmpWordTest.getChapter().subjectID, tmpWordTest.getSubjectName())));
                        break;
                }
            }
            //btnTest in VocaList
            if (btnEvent.getText().equals("시험")) {
                VocaList tmpVocaList = (VocaList) currentView;
                tmpVocaList.runTestLevelDialog();
                return;
            }
            //btnBasic in Dialog
            if (btnEvent.getText().equals("초급")) {
                VocaList tmpVocaList = (VocaList) currentView;
                tmpVocaList.setDialogToDispose();
                currentViewIsChangedTo(new WordTest(tmpVocaList.getSubjectName(), tmpVocaList.getChapter(), WordTest.Level.BASIC)); //단어장으로 넘어가게됨
                return;
            }
            //btnHigh in Dialog
            if (btnEvent.getText().equals("상급")) {
                VocaList tmpVocaList = (VocaList) currentView;
                tmpVocaList.setDialogToDispose();
                currentViewIsChangedTo(new WordTest(tmpVocaList.getSubjectName(), tmpVocaList.getChapter(), WordTest.Level.HIGH)); //단어장으로 넘어가게됨
                return;
            }
            //btnLearning in VocaList
            if (btnEvent.getText().equals("학습")) {
                VocaList tmpVocaList = (VocaList) currentView;
                currentViewIsChangedTo(new WordLearning(tmpVocaList.getSubjectName(), tmpVocaList.getChapter())); //단어장으로 넘어가게됨
            }
            //btnOK in WordBook
            if (btnEvent.getText().equals("확인")) {
                WordBook tmpWordBook = (WordBook) currentView;
                currentViewIsChangedTo(new VocaList(tmpWordBook.getSubject())); //단어장으로 넘어가게됨
                return;
            }
            //임시버튼 추후에 시간이 지나면 자동으로 넘어가게 바꿔야함.
            if (btnEvent.getText().equals("결과")) {
                WordTest tmpWordTest = (WordTest) currentView;
                currentViewIsChangedTo(new TestResult(tmpWordTest.getTestInformation(), tmpWordTest.arrlistWrongWord, tmpWordTest.getChapter())); //총 점수를 가져와 결과창으로 넘겨줌
                return;
            }
            //Screen.WORDTEST의 경우
            if (currentView.getScreen() == ObjectPanel.Screen.WORDTEST) {
                //basic 정답체크
                WordTest tmpWordTest = (WordTest) currentView;
                String answer = "";
                //temp,후에 db로 변경
                {
                    StringTokenizer stk = new StringTokenizer(btnEvent.getText(), ">");
                    for (int i = 0; stk.hasMoreTokens(); ++i) {
                        answer = stk.nextToken();
                        if (i == 4) break;
                    }
                    stk = new StringTokenizer(answer, "<");
                    answer = stk.nextToken();
                }
                if (answer.equals(tmpWordTest.getAnswer())) {
                    tmpWordTest.addScore(5);
                    new JOptionPane().showMessageDialog(null, "정답입니다.");
                    if (tmpWordTest.hasNextProblem())
                        tmpWordTest.setWord();
                    else {
                        currentViewIsChangedTo(new TestResult(tmpWordTest.getTestInformation(), tmpWordTest.arrlistWrongWord, tmpWordTest.getChapter())); //총 점수를 가져와 결과창으로 넘겨줌
                    }
                } else {
                    tmpWordTest.addScore(-5);
                    tmpWordTest.addWrongWord(answer);
                    new JOptionPane().showMessageDialog(null, "틀렸습니다.");

                    if (tmpWordTest.hasNextProblem())
                        tmpWordTest.setWord();
                    else {
                        currentViewIsChangedTo(new TestResult(tmpWordTest.getTestInformation(), tmpWordTest.arrlistWrongWord, tmpWordTest.getChapter())); //총 점수를 가져와 결과창으로 넘겨줌
                    }
                }
                return;
            }
            if(currentView.getScreen()==ObjectPanel.Screen.TESTRESULT){
                TestResult tmpTestResult = (TestResult) currentView;
                if (btnEvent.getText().equals("재시험")) {
                    String[] tmpTestInforamtion = tmpTestResult.getTestInformation();
                    if (tmpTestInforamtion[2].equals(WordTest.Level.BASIC.toString())) { //초급난이도
                        currentViewIsChangedTo(new WordTest(tmpTestInforamtion[0], tmpTestResult.getChapter(), WordTest.Level.BASIC)); //시험 정보를 담은 자료와 함께 재시험
                    } else if (tmpTestInforamtion[2].equals(WordTest.Level.HIGH.toString())) { //상급난이도
                        currentViewIsChangedTo(new WordTest(tmpTestInforamtion[0], tmpTestResult.getChapter(), WordTest.Level.HIGH)); //시험 정보를 담은 자료와 함께 재시험
                    }
                }
                if(btnEvent.getText().equals("오답노트생성")){
                    boolean dupCheck = true;
                    try {
                        /*오답노트생성*/
                        //중복 확인
                        Vector<Subject> subject = new WordBook().getSubjectVector();
                        Subject tmpSubject = new Subject(0,"");
                        for (int i = 0; i < subject.toArray().length; ++i) {
                            if (subject.get(i).subjectName.equals("오답노트")) {
                                dupCheck = false;
                                break;
                            }
                        }
                        //중복 아닐 시
                        if(dupCheck){ db.insertNewSubject("오답노트"); }
                        //오답노트 서브젝트 불러오기
                        subject = new WordBook().getSubjectVector();
                        for (int i = 0; i < subject.toArray().length; ++i) {
                            if (subject.get(i).subjectName.equals("오답노트")) {
                                tmpSubject = subject.get(i);
                                break;
                            }
                        }
                        /*단원생성*/
                        VocaList tmpVocalist = new VocaList(tmpSubject);
                        tmpVocalist.addData();

                        /*단어추가*/
                        if(tmpVocalist.getChapter().chapterID!=0){
                            WordLearning tmpWordLearning = new WordLearning(tmpSubject.subjectName,tmpVocalist.getChapter());
                            tmpWordLearning.addWrongWord(((TestResult) currentView).getWrongWords());
                        }
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }

                }
            }
        }
    }

    //ListEvent구현
    class ListListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            JList listSubject = (JList) e.getSource();
            if (!listSubject.getValueIsAdjusting()) { //클릭시 한번만 실행되도록
                try {
                    switch (currentView.getScreen()) {
                        case WORDBOOK:
                            WordBook tmpWordBook = (WordBook) currentView;
                            tmpWordBook.setOption(listSubject.getSelectedValue().toString(), listSubject.getSelectedIndex());
                            break;
                        case VOCALIST:
                            VocaList tmpVocaList = (VocaList) currentView;
                            tmpVocaList.setOption(listSubject.getSelectedValue().toString(), listSubject.getSelectedIndex());
                            break; //누르면 팝업창이 떠서 시험칠지 학습할지
                        case WORDLEARNING:
                            WordLearning tmpWordLearning = (WordLearning) currentView;
                            tmpWordLearning.setWord(listSubject.getSelectedValue().toString(), listSubject.getSelectedIndex());
                            break;
                        case WORDTEST:
                            WordTest tmpWordTest = (WordTest) currentView;
                            tmpWordTest.setWordName(listSubject.getSelectedValue().toString());
                            break;

                    }
                } catch (Exception e2) {
                }
            }

        }
    }



    class KeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            super.keyPressed(e);
            if (e.getKeyCode() == KeyEvent.VK_ENTER) { //엔터를 눌렀을 시
                WordTest tmpWordTest = (WordTest) currentView;
                if (tmpWordTest.getTextFieldofHighLevel().equals(tmpWordTest.getAnswer())) {
                    tmpWordTest.addScore(5);
                    new JOptionPane().showMessageDialog(null, "정답입니다.");

                    if (tmpWordTest.hasNextProblem())
                        tmpWordTest.setWord();
                    else {
                        currentViewIsChangedTo(new TestResult(tmpWordTest.getTestInformation(), tmpWordTest.arrlistWrongWord, tmpWordTest.getChapter())); //총 점수를 가져와 결과창으로 넘겨줌
                    }
                } else {
                    tmpWordTest.addScore(-5);
                    tmpWordTest.addWrongWord(tmpWordTest.getAnswer());
                    new JOptionPane().showMessageDialog(null, "틀렸습니다.");
                    if (tmpWordTest.hasNextProblem())
                        tmpWordTest.setWord();
                    else {
                        currentViewIsChangedTo(new TestResult(tmpWordTest.getTestInformation(), tmpWordTest.arrlistWrongWord, tmpWordTest.getChapter())); //총 점수를 가져와 결과창으로 넘겨줌
                    }
                }
            }


        }
    }

    public static void main(String[] args) {
        new StudyMate();
    }

}