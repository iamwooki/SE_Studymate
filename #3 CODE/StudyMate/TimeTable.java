package StudyMate;

import javafx.util.Pair;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.sql.SQLException;
import java.util.*;

class TimeTable extends ObjectPanel {
    JButton btnWordBook;
    JButton btnCalendar;

    final int ROW_LENGTH = 19;
    final int COL_LENGTH = 6;
    final int COURSE_BTN_WIDTH = 200;
    final int COURSE_BTN_HEIGHT = 80;

    String[] weekday = {"월요일", "화요일", "수요일", "목요일", "금요일"};
    String[] classTime = {"1A", "1B", "2A", "2B", "3A", "3B", "4A", "4B", "5A", "5B", "6A", "6B", "7A", "7B", "8A", "8B", "9A", "9B"};
    String[] schoolName = {"IT대학", "경상대학", "공과대학", "과학기술대학", "교육개발본부", "국제교류처", "대학원", "법과대학", "사범대학",
            "사회과학대학", "생태환경대학", "생활과학대학", "예술대학", "의과대학", "인문대학", "자연과학대학", "행정학부"};
    Vector<School> schoolVector = new Vector<School>();
    Vector<Course> courseVector = new Vector<Course>();
    Vector<Course> myCourseVector = new Vector<Course>();
    CourseButton[][] button;

    public TimeTable() {
        // 생성자
        super(Screen.TIMETABLE);
        // look and feel 설정


        leftPanel.setLayout(new BorderLayout(10, 10));

        // schoolVector에 학부 정보(name) 추가
        for (int i = 0; i < schoolName.length; ++i)
            schoolVector.add(new School(schoolName[i]));
        super.display();

        /*try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
        }*/
    }

    public void setLeftPanel() {
        // 상단 패널
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BorderLayout(0, 0));
        northPanel.setBackground(Color.WHITE);

        // 상단 제목
        JLabel title = new JLabel("    시간표");
        title.setFont(new Font("D2Coding", Font.BOLD, 30));
        northPanel.add(BorderLayout.WEST, title);
        // 상단 과목 추가 버튼
        JButton addButton = new JButton("과목 추가");
        addButton.setFont(D2Coding);
        addButton.addActionListener(new addCourseBtnListener());
        northPanel.add(BorderLayout.EAST, addButton);

        // 중간 시간표 패널
        JPanel timeTablePanel = new JPanel();
        timeTablePanel.setLayout(new GridLayout(ROW_LENGTH, COL_LENGTH, 0, 0));
        timeTablePanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        timeTablePanel.setBackground(Color.WHITE);

        // 중간 시간표 패널에 버튼 추가
        button = new CourseButton[ROW_LENGTH][COL_LENGTH];
        int xPos = 100, yPos;
        for (int i = 0; i < ROW_LENGTH; i++) {
            yPos = 100;
            for (int j = 0; j < COL_LENGTH; j++) {
                button[i][j] = new CourseButton("");

                if (i == 0 && j != 0)       // 첫 행에 요일 추가 - (0,0) 제외
                    button[i][j].setText(weekday[j - 1]);
                else if (i != 0 && j == 0)  // 첫 열에 시간 추가 - (0,0) 제외
                    button[i][j].setText(classTime[i - 1]);
                else
                    button[i][j].addActionListener(new courseBtnListener());

                timeTablePanel.add(button[i][j]);
                button[i][j].setLocation(xPos, yPos);
                yPos += COURSE_BTN_HEIGHT;
            }
            xPos += COURSE_BTN_WIDTH;
        }

        // DB에 저장된 수업들을 화면에 표시
        try {
            db = new DataBase();
            myCourseVector = db.getCourse();
            updateCoursesOnScreen(myCourseVector);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 패널에 컴포넌트 추가
        leftPanel.add(BorderLayout.NORTH, northPanel);
        leftPanel.add(BorderLayout.CENTER, timeTablePanel);
    }

    private void updateCoursesOnScreen(Vector<Course> myCourseVector) {

        for (int i = 0; i < myCourseVector.size(); ++i) {
            String courseName = myCourseVector.get(i).courseName;
            String classtime = myCourseVector.get(i).classtime;
            Vector<Pair<Integer, Integer>> coursePosVector = splitTime(classtime);
            Iterator it = coursePosVector.iterator();
            while (it.hasNext()) {
                Pair<Integer, Integer> item = ((Pair<Integer, Integer>) it.next());
                CourseButton currButton = button[item.getKey()][item.getValue()];
                currButton.setText(courseName);
                currButton.setCourse(myCourseVector.get(i));
            }
        }

    }

    public void setRightPanel() {
        //rightPanel 레이아웃 설정
        rightPanel.setLayout(new GridLayout(2, 1));

        //단어장 선택
        btnWordBook = new JButton("단어장");
        btnWordBook.setFont(new Font("D2Coding", Font.BOLD, 35));
        btnWordBook.setForeground(Color.WHITE);
        btnWordBook.setBackground(NAVY);
        btnWordBook.setContentAreaFilled(false);
        btnWordBook.setOpaque(true);
        btnWordBook.addActionListener(btnListener);
        rightPanel.add(btnWordBook);

        //일정관리 선택
        btnCalendar = new JButton("일정관리");
        btnCalendar.setFont(new Font("D2Coding", Font.BOLD, 35));
        btnCalendar.setForeground(Color.WHITE);
        btnCalendar.setBackground(NAVY);
        btnCalendar.setContentAreaFilled(false);
        btnCalendar.setOpaque(true);
        btnCalendar.addActionListener(btnListener);
        rightPanel.add(btnCalendar);
    }

    private boolean readXLSCourses() {
        StringBuffer fileDirectoryName = new StringBuffer();
        try {
            Vector<FilePath> filePath = db.getFilePath("select * from file where name='TimeTable';");
            //경로가 존재하면
            if (!filePath.isEmpty()) {
                //예는 0, 아니오 1
                int ans = JOptionPane.showConfirmDialog(this, "기존의 파일을 사용하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (ans == 0) {
                    fileDirectoryName.append(filePath.get(0).path);
                } else { //기존 경로 미사용 시
                    FileDialog fileDialog = new FileDialog((Frame) null, "파일 변경", FileDialog.LOAD);
                    fileDialog.setFile("*.xls");
                    fileDialog.setVisible(true);
                    fileDirectoryName.append(fileDialog.getDirectory());
                    fileDirectoryName.append(fileDialog.getFile());
                    if(!fileDirectoryName.toString().equals("nullnull")){
                        db.updatePath(new FilePath(0,fileDirectoryName.toString(),"TimeTable"));
                    }
                }
            }
            //경로가 존재하지 않을 시
            else {
                FileDialog fileDialog = new FileDialog((Frame) null, "파일 열기", FileDialog.LOAD);
                fileDialog.setFile("*.xls");
                fileDialog.setVisible(true);
                fileDirectoryName.append(fileDialog.getDirectory());
                fileDirectoryName.append(fileDialog.getFile());
                db.insertFilePath(new FilePath(0, fileDirectoryName.toString(), "TimeTable"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // 1. courseVector에 추가
        // 2. schoolVector에 학과 정보(TreeSet) 추가
        String[] inputData = new String[11];


        try {
            FileInputStream fis = new FileInputStream(fileDirectoryName.toString());
            Workbook wb = new HSSFWorkbook(fis);
            int row = 0, col = 0;
            HSSFSheet sheet = ((HSSFWorkbook) wb).getSheetAt(0);
            int rowSize = sheet.getPhysicalNumberOfRows();

            for (row = 1; row < rowSize; row++) {
                HSSFRow hssfRow = sheet.getRow(row);

                if (hssfRow != null) {
                    int cells = hssfRow.getPhysicalNumberOfCells();
                    for (col = 0; col <= cells; col++) {
                        HSSFCell cell = hssfRow.getCell(col);

                        if (cell == null) continue;
                        else {
                            switch (cell.getCellType()) {
                                case HSSFCell.CELL_TYPE_FORMULA:
                                    inputData[col] = cell.getCellFormula();
                                    break;
                                case HSSFCell.CELL_TYPE_NUMERIC:
                                    inputData[col] = cell.getNumericCellValue() + "";
                                    break;
                                case HSSFCell.CELL_TYPE_STRING:
                                    inputData[col] = cell.getStringCellValue() + "";
                                    break;
                                case HSSFCell.CELL_TYPE_BLANK:
                                    inputData[col] = cell.getBooleanCellValue() + "";
                                    break;
                                case HSSFCell.CELL_TYPE_ERROR:
                                    inputData[col] = cell.getErrorCellValue() + "";
                                    break;
                            }
                        }
                    }

                    // 하나의 레코드 (한 행)를 다 입력받은 후 Course 객체 리스트에 추가한다.
                    String temp;
                    for (int i = 0; i < schoolVector.size(); ++i) {
                        temp = schoolVector.get(i).getName();

                        // 일치하는 단과대학을 찾으면
                        if (inputData[8].equals(temp)) {
                            // 해당 단과대학에 학과(inputData[9]) 삽입
                            schoolVector.get(i).addDept(inputData[9]);
                            break;
                        }
                    }
                    courseVector.add(new Course(inputData[0], inputData[1], inputData[2], inputData[3], inputData[4], inputData[5], inputData[6], inputData[7], inputData[8], inputData[9], inputData[10]));
                }
            }
        }catch(FileNotFoundException e0) {
            JOptionPane.showMessageDialog(null, "파일이 존재하지 않습니다.", "알림", JOptionPane.WARNING_MESSAGE);
            try{
                db.updateBy("DELETE FROM file WHERE name='TimeTable';");
            }catch(SQLException e){}
            return true;
        }catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private Vector<Pair<Integer, Integer>> splitTime(String time) {
        Vector<Pair<Integer, Integer>> times = new Vector<>();
        int row = 0, col = 0, num = 0;
        char token;

        for (int i = 0; i < time.length(); i++) {
            token = time.charAt(i);
            switch (token) {
                case '월':
                    col = 1;
                    break;
                case '화':
                    col = 2;
                    break;
                case '수':
                    col = 3;
                    break;
                case '목':
                    col = 4;
                    break;
                case '금':
                    col = 5;
                    break;
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    num = time.charAt(i) - '0';
                    row = num * 2 - 1;
                    break;
                case 'A':
                    times.add(new Pair(row, col));
                    break;
                case 'B':
                    times.add(new Pair(row + 1, col));
                    break;
            }
        }
        return times;
    }

    class CourseButton extends JButton {
        Course course;

        CourseButton() {
            super();
            course = null;
        }

        CourseButton(String btnName) {
            super(btnName);
            setContentAreaFilled(false);
            setBorder(new LineBorder(NAVY,1));
            setFont(new Font("D2Coding", Font.PLAIN, 15));
            course = null;
        }

        public void setCourse(Course c) {
            this.course = c;
        }

        public Course getCourse() {
            return course;
        }
    }

    class addCourseBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            new addCourseDialog();
        }
    }

    class courseBtnListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (((JButton) e.getSource()).getText().equals("")) {
                return;
            }
            new courseInfoDialog(e.getSource());
        }
    }

    class courseInfoDialog extends JDialog {
        private JPanel centerPane;
        private JPanel southPane;
        private JButton buttonOK;
        private JButton buttonCancel;

        public courseInfoDialog(Object source) {
            Course btnCourse = ((CourseButton) source).getCourse();

            // 기본 설정
            setTitle("과목 정보");
            setLayout(new BorderLayout());
            setModal(true);

            // 할당
            centerPane = new JPanel();
            centerPane.setLayout(new GridLayout(11, 2));
            southPane = new JPanel();
            buttonOK = new JButton("삭제");
            buttonCancel = new JButton("취소");

            // 수업 내용 표시 컴포넌트 추가
            add(BorderLayout.CENTER, centerPane);
            add(BorderLayout.SOUTH, southPane);

            centerPane.add(new JLabel("교과목번호"));
            centerPane.add(new JLabel(btnCourse.courseID));
            centerPane.add(new JLabel("학점"));
            centerPane.add(new JLabel(btnCourse.credit));
            centerPane.add(new JLabel("이론"));
            centerPane.add(new JLabel(btnCourse.credit_theory));
            centerPane.add(new JLabel("실습"));
            centerPane.add(new JLabel(btnCourse.credit_practice));
            centerPane.add(new JLabel("교과목명"));
            centerPane.add(new JLabel(btnCourse.courseName));
            centerPane.add(new JLabel("교과구분"));
            centerPane.add(new JLabel(btnCourse.classification));
            centerPane.add(new JLabel("시간"));
            centerPane.add(new JLabel(btnCourse.classtime));
            centerPane.add(new JLabel("강의실"));
            centerPane.add(new JLabel(btnCourse.classroom));
            centerPane.add(new JLabel("개설대학"));
            centerPane.add(new JLabel(btnCourse.school));
            centerPane.add(new JLabel("개설학과"));
            centerPane.add(new JLabel(btnCourse.department));
            centerPane.add(new JLabel("담당교수"));
            centerPane.add(new JLabel(btnCourse.professor));
            southPane.add(buttonOK);
            southPane.add(buttonCancel);

            // Button 리스너 추가
            //      1. 삭제
            buttonOK.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    // 1. MyCourseVector 에서 삭제
                    myCourseVector.removeElement(btnCourse);

                    Vector<Pair<Integer, Integer>> coursePosVector = splitTime(btnCourse.classtime);

                    // 2. GUI에서 삭제
                    // 해당 버튼의 1. 텍스트 삭제 2. Course 삭제
                    Iterator delIter = coursePosVector.iterator();
                    while (delIter.hasNext()) {
                        Pair<Integer, Integer> item = ((Pair<Integer, Integer>) delIter.next());
                        CourseButton cb = button[item.getKey()][item.getValue()];
                        cb.setCourse(null);
                        cb.setText("");
                    }

                    // 3. DB에서 삭제
                    try {
                        db = new DataBase();
                        db.removeCourse(btnCourse);
                    } catch (SQLException sqle) {
                        sqle.printStackTrace();
                    }

                    updateCoursesOnScreen(myCourseVector);
                    dispose();
                }
            });

            //      2. 취소 버튼
            buttonCancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });

            pack();
            setSize(400, 400);
            setLocationRelativeTo(this);
            setVisible(true);
        }

    }

    // 과목 추가 대화상자
    class addCourseDialog extends JDialog {
        private JPanel northPane;
        private JScrollPane centerPane;
        private JPanel southPane;
        private JButton buttonOK;
        private JButton buttonCancel;
        private JComboBox<String> schoolComboBox;
        private JComboBox<String> deptComboBox;
        private JTable table;
        private String[] attribute = {"교과목번호", "학점", "이론", "실습", "교과목명", "교과구분", "시간", "강의실", "개설대학", "개설학과", "담당교수"};
        private DefaultTableModel model;

        public addCourseDialog() {
            // 기본 설정
            setTitle("과목 추가");
            setLayout(new BorderLayout());
            setModal(true);

            if(readXLSCourses()){
                return;
            }
            // 할당
            northPane = new JPanel();
            northPane.setLayout(new GridLayout(1, 5));
            centerPane = new JScrollPane(table);
            southPane = new JPanel();
            buttonOK = new JButton("확인");
            buttonOK.setFont(D2Coding);
            buttonCancel = new JButton("취소");
            buttonCancel.setFont(D2Coding);
            schoolComboBox = new JComboBox<String>(schoolName);
            deptComboBox = new JComboBox<String>(schoolVector.get(0).depts.toArray(new String[schoolVector.get(0).depts.size()]));
            model = new DefaultTableModel(attribute, 0){
              public boolean isCellEditable(int row, int column){
                  return false;
              }
            };
            table = new JTable(model);

            // 테이블에 내용 추가
            for (int i = 0; i < courseVector.size(); ++i) {
                Course item = courseVector.get(i);
                model.addRow(new Object[]{item.courseID, item.credit, item.credit_theory, item.credit_practice, item.courseName, item.classification, item.classtime, item.classroom, item.school, item.department, item.professor});
            }
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            centerPane.setViewportView(table);

            // 컴포넌트 추가
            add(BorderLayout.NORTH, northPane);
            add(BorderLayout.CENTER, centerPane);
            add(BorderLayout.SOUTH, southPane);
            northPane.add(new JLabel("개설대학"));
            northPane.add(schoolComboBox);
            northPane.add(new JLabel("개설학과"));
            northPane.add(deptComboBox);
            southPane.add(buttonOK);
            southPane.add(buttonCancel);

            // Combobox 리스너 추가
            //      1. 개설대학
            schoolComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // 개설학과에 표시되는 부분 갱신
                    String selected = (String) schoolComboBox.getSelectedItem();
                    int i;
                    for (i = 0; i < schoolName.length; ++i)
                        if (selected.equals(schoolName[i])) break;

                    String[] result = schoolVector.get(i).depts.toArray(new String[schoolVector.get(i).depts.size()]);
                    deptComboBox.removeAllItems();

                    for (i = 0; i < result.length; ++i)
                        deptComboBox.addItem(result[i]);

                    // 테이블 부분 갱신
                    model.setNumRows(0);
                    for (i = 0; i < courseVector.size(); ++i) {
                        Course item = courseVector.get(i);
                        if (item.school.equals(selected))
                            model.addRow(new Object[]{item.courseID, item.credit, item.credit_theory, item.credit_practice, item.courseName, item.classification, item.classtime, item.classroom, item.school, item.department, item.professor});
                    }
                }
            });

            //      2. 개설학과
            deptComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String selected = (String) deptComboBox.getSelectedItem();
                    model.setNumRows(0);
                    for (int i = 0; i < courseVector.size(); ++i) {
                        Course item = courseVector.get(i);
                        if (item.department.equals(selected))
                            model.addRow(new Object[]{item.courseID, item.credit, item.credit_theory, item.credit_practice, item.courseName, item.classification, item.classtime, item.classroom, item.school, item.department, item.professor});
                    }
                }
            });

            // Button 리스너 추가
            //      1. 확인
            buttonOK.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int row = table.getSelectedRow();
                    if(row==-1){
                        JOptionPane.showMessageDialog(null, "선택된 수업이 없습니다", "알림", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    String courseID = table.getValueAt(row, 0).toString();
                    String credit = table.getValueAt(row, 1).toString();
                    String credit_theory = table.getValueAt(row, 2).toString();
                    String credit_practice = table.getValueAt(row, 3).toString();
                    String courseName = table.getValueAt(row, 4).toString();
                    String classification = table.getValueAt(row, 5).toString();
                    String classtime = table.getValueAt(row, 6).toString();
                    String classroom = table.getValueAt(row, 7).toString();
                    String school = table.getValueAt(row, 8).toString();
                    String department = table.getValueAt(row, 9).toString();
                    String professor = table.getValueAt(row, 10).toString();

                    // 시간표가 추가될 위치 계산
                    Vector<Pair<Integer, Integer>> coursePosVector = splitTime(classtime);

                    // 시간 중복 검사
                    Iterator timecheckIter = coursePosVector.iterator();
                    while (timecheckIter.hasNext()) {
                        Pair<Integer, Integer> item = ((Pair<Integer, Integer>) timecheckIter.next());
                        if (!button[item.getKey()][item.getValue()].getText().equals("")) {
                            JOptionPane.showMessageDialog(null, "이미 수업이 등록된 시간입니다.", "알림", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }

                    // 시간표 추가 (1. myCourseVector 2. 화면 3. DB)
                    Course newCourse = new Course(courseID, credit, credit_theory, credit_practice, courseName, classification, classtime, classroom, school, department, professor);
                    myCourseVector.add(newCourse);
                    updateCoursesOnScreen(myCourseVector);
                    try {
                        db.addCourse(newCourse);
                    } catch (SQLException sqle) {
                        sqle.printStackTrace();
                    }

                    dispose();
                }
            });

            //      2. 취소 버튼
            buttonCancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });

            pack();
            setSize(1000, 720);
            setLocationRelativeTo(this);
            setVisible(true);
        }
    }
}

class Course {
    String courseID;
    String credit;
    String credit_theory;
    String credit_practice;
    String courseName;
    String classification;
    String classtime;
    String classroom;
    String school;
    String department;
    String professor;

    public Course(String courseID,
                  String credit,
                  String credit_theory,
                  String credit_practice,
                  String courseName,
                  String classification,
                  String classtime,
                  String classroom,
                  String school,
                  String department,
                  String professor) {
        this.courseID = courseID;
        this.credit = credit;
        this.credit_theory = credit_theory;
        this.credit_practice = credit_practice;
        this.courseName = courseName;
        this.classification = classification;
        this.classtime = classtime;
        this.classroom = classroom;
        this.school = school;
        this.department = department;
        this.professor = professor;
    }
}

class School {
    String name;
    TreeSet<String> depts;

    School(String name) {
        this.name = name;
        depts = new TreeSet<>();
    }

    String getName() {
        return name;
    }

    void addDept(String deptName) {
        this.depts.add(deptName);
    }
}

