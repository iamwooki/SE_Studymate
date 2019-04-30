package StudyMate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

public class WordLearning extends ObjectPanel {
    JLabel labelNameOfSubject;
    JLabel labelNameOfChapter;
    JLabel labelListOfWord;
    JTextField labelNameOfWord;
    JTextPane labelMeaningOfWord;
    int indexOfWord;
    JButton btnStart;
    JButton btnStop;
    boolean th_switch = true; // 스레드 작동여부
    boolean thOnOff = true;
    JTextField wpmField = new JTextField("10", 3);
    JLabel labelWPM;
    int wpmNum = 10; //wpm 기본설정 값
    String wpmNumCheck; //숫자체크
    Boolean wpmConfirm; //숫자입력 판별
    int indexCheck = 0; //배열의 현재위치
    float progressNum; //진행값

    JButton btnBack;
    JButton btnEdit;
    JButton btnDelete;
    JButton btnAdd;
    JButton btnPrevWord;
    JButton btnNextWord;
    Thread th;
    JList listWord;
    JScrollPane spList;

    DefaultListModel listModel;
    String nameOfSubject;
    Chapter chapter;
    Vector<Vocabulary> voca;

    //constructor
    public WordLearning(String nameOfSubject, Chapter chapter) {
        super(Screen.WORDLEARNING);
        this.chapter = chapter;
        this.nameOfSubject = nameOfSubject;
        listModel = new DefaultListModel();
        loadDatabase();
        super.display();

    }

    public void setLeftPanel() {
        leftPanel.setLayout(new BorderLayout());
        JPanel panelTop = new JPanel();
        panelTop.setLayout(new BorderLayout());
        //뒤로가기
        btnBack = new JButton("뒤로가기");
        btnBack.setFont(D2Coding);
        btnBack.addActionListener(btnListener);
        panelTop.add(btnBack, BorderLayout.WEST);

        //과목선택
        labelNameOfSubject = new JLabel(nameOfSubject);
        labelNameOfSubject.setFont(new Font("D2Coding", Font.BOLD, 20));
        labelNameOfSubject.setHorizontalAlignment(SwingConstants.CENTER);
        panelTop.add(labelNameOfSubject, BorderLayout.CENTER);

        //과목선택설명
        labelNameOfChapter = new JLabel(chapter.chapterName);
        labelNameOfChapter.setFont(new Font("D2Coding", Font.BOLD, 35));
        labelNameOfChapter.setHorizontalAlignment(SwingConstants.CENTER);
        panelTop.add(labelNameOfChapter, BorderLayout.SOUTH);

        JPanel panelNorthOfMain = new JPanel();
        labelWPM = new JLabel("분당 갯수");
        labelWPM.setFont(new Font("D2Coding", Font.BOLD, 35));
        panelNorthOfMain.add(labelWPM);
        wpmField.setFont(new Font("D2Coding", Font.BOLD, 35));
        panelNorthOfMain.add(wpmField);
        //thread 시작
        btnStart = new JButton("시작");
        btnStart.setFont(new Font("D2Coding", Font.BOLD, 35));
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                wpmNumCheck = wpmField.getText();
                if (Pattern.matches("^[0-9]+$", wpmNumCheck)) {
                    wpmNum = Integer.parseInt(wpmNumCheck);
                    wpmConfirm = true;
                } else {
                    JOptionPane.showMessageDialog(null, "wpm란에 올바른 숫자를 입력해주세요.", "입력오류", JOptionPane.ERROR_MESSAGE);
                    wpmConfirm = false;
                }
                if (wpmConfirm) {
                    btnStart.setEnabled(false);
                    btnStop.setEnabled(true);
                    btnNextWord.setEnabled(false);
                    btnPrevWord.setEnabled(false);
                    if (th_switch == true) {  //SWITCH가 TRUE면 새로운 스레드 실행
                        th = new TimeThread();
                        th.start();
                    } else { //정지를 눌렀을 경우 SWITCH가 FALSE로 바뀌고 새로운 스레드가 아닌 기존 스레드 재개
                        th.resume();
                        th_switch = true;
                        btnStart.setText("시작");
                    }
                    announce.setText("실행합니다.");
                } else announce.setText("입력 오류");

            }
        });
        panelNorthOfMain.add(btnStart);

        //thread 정지
        btnStop = new JButton("정지");
        btnStop.setEnabled(false);
        btnStop.setFont(new Font("D2Coding", Font.BOLD, 35));
        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnNextWord.setEnabled(true);
                btnPrevWord.setEnabled(true);
                btnEdit.setEnabled(true);
                btnDelete.setEnabled(true);
                announce.setText("정지합니다.");
                th.suspend(); // 스레드 일시 정지
                th_switch = false;
                btnStart.setText("재개");
                btnStart.setEnabled(true);
            }
        });
        panelNorthOfMain.add(btnStop);

        //특정단어 수정
        btnEdit = new JButton("수정");
        btnEdit.setFont(new Font("D2Coding", Font.BOLD, 35));
        btnEdit.addActionListener(btnListener);
        btnEdit.setEnabled(false);
        panelNorthOfMain.add(btnEdit);

        //특정단어 삭제
        btnDelete = new JButton("삭제");
        btnDelete.setFont(new Font("D2Coding", Font.BOLD, 35));
        btnDelete.addActionListener(btnListener);
        btnDelete.setEnabled(false);
        panelNorthOfMain.add(btnDelete);

        JPanel panelMain = new JPanel();
        panelMain.setLayout(new BorderLayout());
        JPanel panelCenterOfMain = new JPanel();
        panelCenterOfMain.setLayout(new BorderLayout());

        //단어이름
        labelNameOfWord = new JTextField("");
        labelNameOfWord.setFont(new Font("D2Coding", Font.BOLD, 35));
        labelNameOfWord.setHorizontalAlignment(JTextField.CENTER);
        labelNameOfWord.setEnabled(false); //수정불가
        labelNameOfWord.setBackground(Color.WHITE);
        panelCenterOfMain.add(labelNameOfWord, BorderLayout.NORTH);

        //단어뜻
        labelMeaningOfWord = new JTextPane();
        labelMeaningOfWord.setFont(new Font("D2Coding", Font.BOLD, 20));
        labelMeaningOfWord.setForeground(Color.WHITE);
        labelMeaningOfWord.setEnabled(false);
        labelMeaningOfWord.setBackground(Color.WHITE);
        panelCenterOfMain.add(labelMeaningOfWord, BorderLayout.CENTER);

        //이전 단어
        btnPrevWord = new JButton("이전");
        btnPrevWord.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                indexCheck--; //인덱스 위치
                setVocaText();
                statusDraw();
            }
        });
        panelCenterOfMain.add(btnPrevWord, BorderLayout.WEST);

        //다음 단어
        btnNextWord = new JButton("다음");
        btnNextWord.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                indexCheck++;
                setVocaText();
                statusDraw();
            }
        });
        panelCenterOfMain.add(btnNextWord, BorderLayout.EAST);

        panelMain.add(panelNorthOfMain, BorderLayout.NORTH);
        panelMain.add(panelCenterOfMain, BorderLayout.CENTER);

        leftPanel.add(panelTop, BorderLayout.NORTH);
        leftPanel.add(panelMain, BorderLayout.CENTER);
    }

    public void setRightPanel() {
        rightPanel.setLayout(new BorderLayout());
        //단어목록 이름
        labelListOfWord = new JLabel("단어 목록");
        labelListOfWord.setHorizontalAlignment(SwingConstants.CENTER);
        labelListOfWord.setFont(new Font("D2Coding", Font.BOLD, 20));
        labelListOfWord.setForeground(Color.WHITE);
        rightPanel.add(labelListOfWord, BorderLayout.PAGE_START);

        //단어목록
        listWord = new JList(listModel);
        listWord.setFont(new Font("D2Coding", Font.BOLD, 20));
        listWord.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listWord.addListSelectionListener(listListener);
        spList = new JScrollPane(listWord);
        rightPanel.add(spList, BorderLayout.CENTER);

        //단어 추가
        btnAdd = new JButton("추가");
        btnAdd.setFont(new Font("D2Coding", Font.BOLD, 35));
        btnAdd.addActionListener(btnListener);
        rightPanel.add(btnAdd, BorderLayout.PAGE_END);

    }

    public void setStatePanel() {
        announce.setText("WPM을 설정하고 시작버튼을 누르세요 (WPM수=1분당 보여질 단어갯수 ex) 10이면 1분동안 10개의 단어가 보여질 빠르기)");
    }

    public void setVocaText() { //그리는 부분
        try {
            //체크하기
            if (indexCheck <= 0) indexCheck = 0; //0보다 작을 경우 보정 - (size-level_num) 보정
            else if (indexCheck >= voca.toArray().length) indexCheck = voca.toArray().length - 1; //배열보다 클 경우 보정
            labelNameOfWord.setText(voca.get(indexCheck).voca);
            labelMeaningOfWord.setText(voca.get(indexCheck).mean);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "단어장에 단어가 존재하지 않습니다.", "실행오류", JOptionPane.ERROR_MESSAGE);
            thOnOff = false;
        }
    }

    public void statusDraw() {
        progressNum = ((float) indexCheck / (float) (voca.toArray().length - 1)) * 100;
        if (progressNum >= 95) announce.setText("■■■■■■■■■■ " + (int) progressNum + "%");
        else if (progressNum >= 90) announce.setText("■■■■■■■■■□ " + (int) progressNum + "%");
        else if (progressNum >= 80) announce.setText("■■■■■■■■□□ " + (int) progressNum + "%");
        else if (progressNum >= 70) announce.setText("■■■■■■■□□□ " + (int) progressNum + "%");
        else if (progressNum >= 60) announce.setText("■■■■■■□□□□ " + (int) progressNum + "%");
        else if (progressNum >= 50) announce.setText("■■■■■□□□□□ " + (int) progressNum + "%");
        else if (progressNum >= 40) announce.setText("■■■■□□□□□□ " + (int) progressNum + "%");
        else if (progressNum >= 30) announce.setText("■■■□□□□□□□ " + (int) progressNum + "%");
        else if (progressNum >= 20) announce.setText("■■□□□□□□□□ " + (int) progressNum + "%");
        else if (progressNum >= 10) announce.setText("■□□□□□□□□□ " + (int) progressNum + "%");
        else if (progressNum >= 0) announce.setText("□□□□□□□□□□ " + (int) progressNum + "%");
    }

    public void loadDatabase() {
        try {
            voca = db.getVocabulary("SELECT * FROM Vocabulary where chapterID='" + chapter.chapterID + "';");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        listModel.clear();
        for (int i = 0; i < voca.toArray().length; ++i) {
            listModel.addElement(voca.get(i).voca);
        }
    }

    public String getSubjectName() {
        return nameOfSubject;
    }

    class DialogWord extends JDialog {
        JLabel text;
        JTextField voca;
        JTextPane mean;
        JButton btnOK;
        JButton btnCancel;

        public DialogWord(String title, String voca, String mean) {
            setTitle(title);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            addComponent(voca, mean);
            setSize(300, 300);
            setLocationRelativeTo(this);//다이얼로그 실행 위치 - 화면 중앙
            setResizable(false); //창 크기 조절X
            setModal(true); //실행중인 다이얼로그가 끝나야만 다른 작업을 할 수 있음
            setVisible(true);
        }

        public void addComponent(String voca1, String mean1) {
            text = new JLabel("단어 추가");
            this.voca = new JTextField(voca1, 20);
            this.mean = new JTextPane();
            this.mean.setText(mean1);
            btnOK = new JButton("확인");
            btnOK.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (voca.getText().trim().equals("")) {
                        JOptionPane.showMessageDialog(null, "단어 이름을 입력하세요");
                        voca.requestFocus();
                        return;
                    }
                    if (voca.getText().length() > 50) {
                        JOptionPane.showMessageDialog(null, "단어 길이초과. 현재길이" + voca.getText().length());
                        voca.requestFocus();
                        return;
                    }
                    if (mean.getText().trim().equals("")) {
                        JOptionPane.showMessageDialog(null, "단어 뜻을 입력하세요");
                        mean.requestFocus();
                        return;
                    }
                    if (mean.getText().length() > 100) {
                        JOptionPane.showMessageDialog(null, "내용 길이초과. 현재길이" + mean.getText().length());
                        mean.requestFocus();
                        return;
                    }
                    dispose();
                }
            });
            btnCancel = new JButton("취소");
            btnCancel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
            JPanel panelBase = new JPanel();
            panelBase.setLayout(new BorderLayout());
            panelBase.add(text, BorderLayout.PAGE_START);

            JPanel panelMain = new JPanel();
            panelMain.setLayout(new BorderLayout());
            panelMain.add(this.voca, BorderLayout.NORTH);
            panelMain.add(this.mean, BorderLayout.CENTER);
            JPanel panelBottom = new JPanel();
            panelBottom.setLayout(new FlowLayout(FlowLayout.CENTER));
            panelBottom.add(btnOK);
            panelBottom.add(btnCancel);
            panelMain.add(panelBottom, BorderLayout.SOUTH);

            panelBase.add(panelMain, BorderLayout.CENTER);
            setContentPane(panelBase);

        }

        public void setWord(String[] word) {
            word[0] = voca.getText();
            word[1] = mean.getText();
        }

    }

    public void addData() {
        String[] addWord = new String[2];
        new DialogWord("단어생성", "", "").setWord(addWord);
        try {
            if (addWord[0].trim().equals("") || addWord[1].trim().equals("")) {
                return;
            }
        } catch (NullPointerException e) {
            return;
        }

        try {
            //중복 확인
            for (int i = 0; i < voca.toArray().length; ++i) {
                if (voca.get(i).voca.equals(addWord[0]) && voca.get(i).mean.equals(addWord[1])) {
                    JOptionPane.showMessageDialog(null, "중복되는 단어입니다.");
                    return;
                }
            }
            //중복 아닐 시
            db.insertNewWord(addWord[0], addWord[1], chapter.chapterID);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        loadDatabase();
    }

    public void addWrongWord(ArrayList<Vocabulary> word) {
        try {
            for (int i = 0; i < word.toArray().length; ++i) {
                db.insertNewWord(word.get(i).voca, word.get(i).mean, chapter.chapterID);
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        JOptionPane.showMessageDialog(null, "오답노트 생성완료");
    }

    public void deleteData(int result) {
        if (result == JOptionPane.YES_OPTION) {
            try {
                listModel.removeElement(labelNameOfWord.getText());
                db.deleteVoca(labelNameOfWord.getText(), labelMeaningOfWord.getText(), chapter.chapterID);
                labelNameOfWord.setText("");
                labelMeaningOfWord.setText("");
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            loadDatabase();
        }
    }

    public void updateData() {
        String[] editWord = new String[2];
        new DialogWord("단어변경", labelNameOfWord.getText(), labelMeaningOfWord.getText()).setWord(editWord);
        try {
            if (editWord[0].trim().equals("") || editWord[1].trim().equals("")) {
                return;
            }
        } catch (NullPointerException e) {
            return;
        }
        try {
            for (int i = 0; i < voca.toArray().length; ++i) {
                if (i == indexOfWord) continue;
                if (voca.get(i).voca.equals(editWord[0]) && voca.get(i).mean.equals(editWord[1])) {
                    JOptionPane.showMessageDialog(null, "중복되는 단어입니다.");
                    return;
                }
            }
            db.updateVoca(labelNameOfWord.getText(), editWord[0], labelMeaningOfWord.getText(), editWord[1], chapter.chapterID);
            labelNameOfWord.setText(editWord[0]);
            labelMeaningOfWord.setText(editWord[1]);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        loadDatabase();
    }

    public void setWord(String nameOfWord, int indexOfWord) {
        labelNameOfWord.setText(nameOfWord);
        for (int i = 0; i < voca.toArray().length; ++i) {
            if (voca.get(i).voca.equals(nameOfWord)) {
                labelMeaningOfWord.setText(voca.get(i).mean);
            }
        }
        this.indexCheck = this.indexOfWord = indexOfWord;
        btnEdit.setEnabled(true);
        btnDelete.setEnabled(true);
    }

    public Chapter getChapter() {
        return this.chapter;
    }

    class TimeThread extends Thread {
        public void run() {
            setVocaText();
            try {
                while (thOnOff) {
                    wpmNumCheck = wpmField.getText();
                    //숫자판별
                    if (Pattern.matches("^[0-9]+$", wpmNumCheck)) wpmNum = Integer.parseInt(wpmNumCheck);
                    else {
                        JOptionPane.showMessageDialog(null, "숫자를 입력해주세요.(학습 실행중)", "입력오류", JOptionPane.ERROR_MESSAGE);
                        th_switch = false;
                        btnStart.setText("재개");
                        btnStart.setEnabled(true);
                        btnNextWord.setEnabled(true);
                        btnPrevWord.setEnabled(true);
                        th.suspend();
                    }

                    sleep(60000 / wpmNum); //wpm조절
                    ++indexCheck;
                    setVocaText();
                    statusDraw();
                    if (indexCheck >= voca.toArray().length - 1) {
                        announce.setText("");
                        break; // 스레드 종료
                    }
                }
            } catch (Exception e) {
                return;
            }
            announce.setText("학습 종료");
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
            indexCheck = 0;
        }
    }
}