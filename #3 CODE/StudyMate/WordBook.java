package StudyMate;

import javax.swing.*;
import javax.swing.plaf.basic.BasicOptionPaneUI;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

class WordBook extends ObjectPanel {
    JLabel labelExplanationSubjectSelection;
    String nameOfSubject;
    int indexOfSubject;
    JButton btnBack;
    JButton btnOK;
    JButton btnAdd;
    JButton btnEdit;
    JButton btnDelete;
    JButton btnReadWord;
    JButton btnWriteWord;

    JList listSubject;
    JScrollPane spList;
    Vector<Subject> subject;
    DefaultListModel listModel;


    //constructor
    public WordBook() {
        super(Screen.WORDBOOK);
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

        //과목선택설명
        labelExplanationSubjectSelection = new JLabel("과목을 선택해주세요.");
        labelExplanationSubjectSelection.setFont(new Font("D2Coding", Font.BOLD, 20));
        labelExplanationSubjectSelection.setHorizontalAlignment(SwingConstants.CENTER);
        panelTop.add(labelExplanationSubjectSelection, BorderLayout.CENTER);

        JPanel panelMain = new JPanel();
        panelMain.setLayout(new BorderLayout());


        //과목리스트
        listSubject = new JList(listModel);
        listSubject.setFont(new Font("D2Coding", Font.BOLD, 20));
        listSubject.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listSubject.addListSelectionListener(listListener);
        spList = new JScrollPane(listSubject);
        panelMain.add(spList, BorderLayout.CENTER);

        JPanel panelNorthOfMain = new JPanel();
        //챕터 확인
        btnOK = new JButton("확인");
        btnOK.setFont(new Font("D2Coding", Font.BOLD, 35));
        btnOK.addActionListener(btnListener);
        btnOK.setEnabled(false);
        panelNorthOfMain.add(btnOK);

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

        panelMain.add(panelNorthOfMain, BorderLayout.NORTH);

        leftPanel.add(panelTop, BorderLayout.NORTH);
        leftPanel.add(panelMain, BorderLayout.CENTER);
    }
    public void setRightPanel() {
        //rightPanel 레이아웃 설정
        rightPanel.setLayout(new GridLayout(2, 1));

        //단어장 선택
        btnReadWord = new JButton("단어 불러오기");
        btnReadWord.setFont(new Font("D2Coding", Font.BOLD, 35));
        btnReadWord.setForeground(Color.WHITE);
        btnReadWord.setBackground(NAVY);
        btnReadWord.setContentAreaFilled(false);
        btnReadWord.setOpaque(true);
        btnReadWord.addActionListener(btnListener);
        rightPanel.add(btnReadWord);

        //일정관리 선택
        btnWriteWord = new JButton("단어 내보내기");
        btnWriteWord.setFont(new Font("D2Coding", Font.BOLD, 35));
        btnWriteWord.setForeground(Color.WHITE);
        btnWriteWord.setBackground(NAVY);
        btnWriteWord.setContentAreaFilled(false);
        btnWriteWord.setOpaque(true);
        btnWriteWord.addActionListener(btnListener);
        rightPanel.add(btnWriteWord);
    }

   public void loadDatabase() {
       try {
           subject = db.getSubject();
       } catch (SQLException e) {
           e.printStackTrace();
       }
       listModel.clear();
       for (int i = 0; i < subject.toArray().length; ++i) {
           listModel.addElement(subject.get(i).subjectName);
       }
   }

    public Subject getSubject(){
        for(int i=0;i<subject.toArray().length;++i){
            if(subject.get(i).subjectName.equals(nameOfSubject)){
                return subject.get(i);
            }
        }
        return new Subject(0,"0");
    }
    public Vector<Subject> getSubjectVector(){
        return subject;
    }
    public void setOption(String nameOfSubject,int index) {
        this.nameOfSubject = nameOfSubject; //선택된 챕터
        this.indexOfSubject = index;
        btnEdit.setEnabled(true);
        btnDelete.setEnabled(true);
        btnOK.setEnabled(true);
    }

    public void addData() {
        String addSubject = JOptionPane.showInputDialog("과목 입력");
        try{
            if(addSubject.equals("null"));
        }catch(NullPointerException e){ return;}

        try {
            //중복 확인
            for (int i = 0; i < subject.toArray().length; ++i) {
                if (subject.get(i).subjectName.equals(addSubject)) {
                    JOptionPane.showMessageDialog(null, "중복되는 과목이름입니다.");
                    return;
                }
            }
            //중복 아닐 시
            db.insertNewSubject(addSubject);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        loadDatabase();
    }

    public void deleteData(int result) {
        if (result == JOptionPane.YES_OPTION) {
            try {
                db.deleteSubject(nameOfSubject);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            loadDatabase();
        }
    }

    public void updateData() {
        String editSubject = JOptionPane.showInputDialog("과목 이름 변경");
        try{
            if(editSubject.equals("null"));
        }catch(NullPointerException e){ return;}

        try {
            for (int i = 0; i < subject.toArray().length; ++i) {
                if (subject.get(i).subjectName.equals(editSubject)) {
                    JOptionPane.showMessageDialog(null, "중복되는 과목이름입니다.");
                    return;
                }
            }
            db.updateSubject(nameOfSubject, editSubject);
            nameOfSubject = editSubject;
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        loadDatabase();
    }
}